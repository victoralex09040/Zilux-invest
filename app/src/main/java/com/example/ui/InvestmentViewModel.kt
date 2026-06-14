package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

class InvestmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InvestmentRepository
    private val supabaseClient = SupabaseClient()
    private val sharedPrefs = application.getSharedPreferences("zelox_prefs", android.content.Context.MODE_PRIVATE)

    private val _distributionHistory = MutableStateFlow<List<String>>(emptyList())
    val distributionHistory: StateFlow<List<String>> = _distributionHistory.asStateFlow()

    private val _actionResult = MutableSharedFlow<Result<String>>()
    val actionResult = _actionResult.asSharedFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = InvestmentRepository(database)
        
        // Seed default index funds and assets on startup
        viewModelScope.launch {
            repository.seedMockMarketPlansIfNeeded()
        }

        // Start real-time live market ticker service
        startMarketTicker()

        loadDistributionHistory()
        initializeAutoDistributor()
    }

    // --- Supabase Cloud Database Sync Syncing Helpers ---
    fun getSupabaseUrl(): String {
        val saved = sharedPrefs.getString("supabase_url", "") ?: ""
        val disconnected = sharedPrefs.getBoolean("supabase_disconnected", false)
        if (saved.isBlank() && !disconnected) {
            val buildConfigUrl = try { com.example.BuildConfig.SUPABASE_URL } catch (e: Exception) { "" }
            if (buildConfigUrl.isNotBlank() && !buildConfigUrl.startsWith("MY_")) {
                return buildConfigUrl
            }
            return "https://fpnsgxtrsiegntteqkhw.supabase.co"
        }
        return saved
    }

    fun getSupabaseKey(): String {
        val saved = sharedPrefs.getString("supabase_key", "") ?: ""
        val disconnected = sharedPrefs.getBoolean("supabase_disconnected", false)
        if (saved.isBlank() && !disconnected) {
            val buildConfigKey = try { com.example.BuildConfig.SUPABASE_KEY } catch (e: Exception) { "" }
            if (buildConfigKey.isNotBlank() && !buildConfigKey.startsWith("MY_")) {
                return buildConfigKey
            }
            return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZwbnNneHRyc2llZ250dGVxa2h3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODEzOTU4OTksImV4cCI6MjA5Njk3MTg5OX0.UioaRLJND7uQxlU-_3fm0c9WOtSIvMDa4UvhHpSVDWo"
        }
        return saved
    }

    fun saveSupabaseCredentials(url: String, key: String) {
        sharedPrefs.edit()
            .putString("supabase_url", url.trim())
            .putString("supabase_key", key.trim())
            .putBoolean("supabase_disconnected", false)
            .apply()
    }

    fun disconnectSupabase() {
        sharedPrefs.edit()
            .putString("supabase_url", "")
            .putString("supabase_key", "")
            .putBoolean("supabase_disconnected", true)
            .apply()
        viewModelScope.launch {
            _actionResult.emit(Result.success("Disconnected. App is now running in offline local mode."))
        }
    }

    fun syncCurrentUserDataToCloud() {
        val username = _currentUsername.value ?: return
        val url = getSupabaseUrl()
        val key = getSupabaseKey()
        if (url.isBlank() || key.isBlank()) return

        viewModelScope.launch {
            val backup = repository.getUserBackupData(username)
            if (backup != null) {
                val res = supabaseClient.saveBackup(url, key, username, backup)
                val email = backup.user.email.trim().lowercase()
                if (email.isNotBlank() && email != username) {
                    supabaseClient.saveBackup(url, key, email, backup)
                }
                if (res.isSuccess) {
                    _actionResult.emit(Result.success("Success: Saved all metrics directly to Supabase Cloud!"))
                } else {
                    _actionResult.emit(Result.failure(res.exceptionOrNull() ?: Exception("Sync failed.")))
                }
            }
        }
    }

    fun backupActiveUserToSupabase() {
        val username = _currentUsername.value ?: return
        val url = getSupabaseUrl()
        val key = getSupabaseKey()
        if (url.isBlank() || key.isBlank()) return

        viewModelScope.launch {
            val backup = repository.getUserBackupData(username)
            if (backup != null) {
                val res = supabaseClient.saveBackup(url, key, username, backup)
                val email = backup.user.email.trim().lowercase()
                if (email.isNotBlank() && email != username) {
                    supabaseClient.saveBackup(url, key, email, backup)
                }
                if (res.isSuccess) {
                    android.util.Log.d("ZeloxSupabase", "Cloud backup saved successfully.")
                } else {
                    val err = res.exceptionOrNull()?.message ?: "Unknown error"
                    android.util.Log.e("ZeloxSupabase", "Cloud backup failed: $err")
                }
            }
        }
    }

    fun testSupabaseAndSync(url: String, key: String) {
        if (url.isBlank() || key.isBlank()) {
            viewModelScope.launch {
                _actionResult.emit(Result.failure(Exception("Supabase URL and API Key cannot be blank.")))
            }
            return
        }
        viewModelScope.launch {
            val res = supabaseClient.testConnection(url, key)
            if (res.isSuccess) {
                saveSupabaseCredentials(url, key)
                _actionResult.emit(Result.success("Successfully Connected to Supabase Cloud!"))
                
                // If there's an active user sessions, immediately upload their local data!
                val active = _currentUsername.value
                if (active != null) {
                    val backup = repository.getUserBackupData(active)
                    if (backup != null) {
                        supabaseClient.saveBackup(url, key, active, backup)
                        val email = backup.user.email.trim().lowercase()
                        if (email.isNotBlank() && email != active) {
                            supabaseClient.saveBackup(url, key, email, backup)
                        }
                        _actionResult.emit(Result.success("Connected & synchronized local active portfolio to the cloud!"))
                    }
                }
            } else {
                val err = res.exceptionOrNull()?.message ?: ""
                if (err == "TABLE_MISSING") {
                    _actionResult.emit(Result.failure(Exception("Connected to Supabase, but the 'zelox_cloud_sync' table was not found. Please run the SQL setup script first in Supabase.")))
                } else {
                    _actionResult.emit(Result.failure(Exception("Connection failed: $err")))
                }
            }
        }
    }

    // --- Active User Session ---
    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<User?> = _currentUsername
        .flatMapLatest { username ->
            if (username == null) flowOf(null)
            else repository.observeUser(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Market Streams & History ---
    val allPlans: StateFlow<List<InvestmentPlan>> = repository.observeAllPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userHoldings: StateFlow<List<UserInvestment>> = _currentUsername
        .flatMapLatest { username ->
            if (username == null) flowOf(emptyList())
            else repository.observeUserHoldings(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userTransactions: StateFlow<List<InvestmentTransaction>> = _currentUsername
        .flatMapLatest { username ->
            if (username == null) flowOf(emptyList())
            else repository.observeTransactions(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val portfolioHistory: StateFlow<List<PortfolioHistory>> = _currentUsername
        .flatMapLatest { username ->
            if (username == null) flowOf(emptyList())
            else repository.observeHistory(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Safe aggregated calculation of Total Asset Holdings Valuation
    val totalHoldingsValue: StateFlow<Double> = userHoldings.map { holdings ->
        holdings.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalNetWorth: StateFlow<Double> = combine(currentUser, totalHoldingsValue) { user, holdingsVal ->
        val cash = user?.cashBalance ?: 500.00
        cash + holdingsVal
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 500.00)

    // --- Action flows / state triggers ---
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signUpState: StateFlow<AuthState> = _signUpState.asStateFlow()

    // --- Remember Me Preferences ---

    fun getSavedLogin(): Pair<String, String>? {
        val remember = sharedPrefs.getBoolean("remember_me", false)
        if (!remember) return null
        val identifier = sharedPrefs.getString("saved_identifier", "") ?: ""
        val pass = sharedPrefs.getString("saved_pass", "") ?: ""
        return if (identifier.isNotBlank() && pass.isNotBlank()) {
            Pair(identifier, pass)
        } else {
            null
        }
    }

    fun saveLogin(identifier: String, pass: String, remember: Boolean) {
        sharedPrefs.edit()
            .putBoolean("remember_me", remember)
            .putString("saved_identifier", if (remember) identifier else "")
            .putString("saved_pass", if (remember) pass else "")
            .apply()
    }

    fun clearSavedLogin() {
        sharedPrefs.edit()
            .putBoolean("remember_me", false)
            .putString("saved_identifier", "")
            .putString("saved_pass", "")
            .apply()
    }

    // --- Core Operations ---
    fun login(username: String, passwordRaw: String, rememberMe: Boolean = false) {
        if (username.isBlank() || passwordRaw.isBlank()) {
            _loginState.value = AuthState.Error("Email/Username and password cannot be empty.")
            return
        }
        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            val cleanUsername = username.trim().lowercase()
            val url = getSupabaseUrl()
            val key = getSupabaseKey()
            
            // Try automatic cloud check and restoration before local verification
            var restoredFromCloud = false
            if (url.isNotBlank() && key.isNotBlank()) {
                android.util.Log.d("ZeloxSupabase", "Checking cloud restore for username/email: $cleanUsername")
                val remoteBackupRes = supabaseClient.downloadBackup(url, key, cleanUsername)
                if (remoteBackupRes.isSuccess) {
                    val backup = remoteBackupRes.getOrNull()
                    if (backup != null) {
                        repository.restoreUserFromBackup(backup)
                        restoredFromCloud = true
                        android.util.Log.d("ZeloxSupabase", "Successfully downloaded cloud backup and restored user local data.")
                    }
                } else {
                    val err = remoteBackupRes.exceptionOrNull()?.message ?: ""
                    android.util.Log.e("ZeloxSupabase", "Failed to check cloud restoration: $err")
                }
            }

            val user = repository.verifyLogin(cleanUsername, passwordRaw)
            if (user != null) {
                saveLogin(cleanUsername, passwordRaw, rememberMe)
                _loginState.value = AuthState.Success
                _currentUsername.value = user.username
                repository.logCurrentPortfolioHistory(user.username, user.cashBalance)
                if (restoredFromCloud) {
                    _actionResult.emit(Result.success("Success: Your profile has been restored from your online Supabase database!"))
                }
                backupActiveUserToSupabase()
            } else {
                _loginState.value = AuthState.Error("Incorrect email/username or password.")
            }
        }
    }

    fun setSignUpError(message: String) {
        _signUpState.value = AuthState.Error(message)
    }

    fun signUp(
        username: String,
        passwordRaw: String,
        confirmPasswordRaw: String,
        fullName: String,
        email: String,
        agreedToTerms: Boolean,
        referralCodeUsed: String?
    ) {
        if (fullName.isBlank()) {
            _signUpState.value = AuthState.Error("Please enter your Full Identification Name.")
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _signUpState.value = AuthState.Error("Please enter a valid Email Address Protocol.")
            return
        }
        if (username.isBlank()) {
            _signUpState.value = AuthState.Error("Please enter a System Link Username.")
            return
        }
        if (passwordRaw.isBlank()) {
            _signUpState.value = AuthState.Error("Please enter a Password Vault Token.")
            return
        }
        if (passwordRaw != confirmPasswordRaw) {
            _signUpState.value = AuthState.Error("Passwords do not match. Please re-type your secure password.")
            return
        }
        if (!agreedToTerms) {
            _signUpState.value = AuthState.Error("You must agree to the Zelox terms of service to deploy a new node.")
            return
        }
        _signUpState.value = AuthState.Loading
        viewModelScope.launch {
            val success = repository.signUp(username, passwordRaw, fullName, email, referralCodeUsed)
            if (success) {
                _signUpState.value = AuthState.Success
                _currentUsername.value = username.trim().lowercase()
                backupActiveUserToSupabase()
            } else {
                _signUpState.value = AuthState.Error("Username already exists or contains invalid characters.")
            }
        }
    }

    fun logout() {
        _currentUsername.value = null
        _loginState.value = AuthState.Idle
        _signUpState.value = AuthState.Idle
        clearSavedLogin()
    }

    // --- Client Transactions ---
    fun buyPlan(planId: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.buyPlan(username, planId)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Successfully purchased investment plan! Status is active."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Purchase failed.")))
            }
        }
    }

    // Sell / Revoke active investment
    fun sellPlan(planId: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.sellPlan(username, planId)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Position liquidated. Balance refunded."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Liquidation failed.")))
            }
        }
    }

    // Collect daily ROI
    fun collectRoi(holdingId: Int) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.collectPlanRoi(username, holdingId)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Succesfully collected daily platform ROI dividends!"))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("ROI collection failed.")))
            }
        }
    }

    // --- Admin Platform Settings ---
    fun getAdminNairaRate(): Double {
        return sharedPrefs.getFloat("admin_naira_rate", 1500.0f).toDouble()
    }

    fun getAdminWithdrawalNairaRate(): Double {
        return sharedPrefs.getFloat("admin_withdrawal_naira_rate", 1450.0f).toDouble()
    }

    fun getAdminBankName(): String {
        return sharedPrefs.getString("admin_bank_name", "Access Bank PLC") ?: "Access Bank PLC"
    }

    fun getAdminBankAccountNumber(): String {
        return sharedPrefs.getString("admin_bank_account_number", "0498175491") ?: "0498175491"
    }

    fun getAdminBankAccountName(): String {
        return sharedPrefs.getString("admin_bank_account_name", "Zelox Invest Corp (Nigeria)") ?: "Zelox Invest Corp (Nigeria)"
    }

    fun saveAdminPlatformSettings(depositRate: Double, withdrawalRate: Double, bankName: String, accountNumber: String, accountName: String) {
        sharedPrefs.edit()
            .putFloat("admin_naira_rate", depositRate.toFloat())
            .putFloat("admin_withdrawal_naira_rate", withdrawalRate.toFloat())
            .putString("admin_bank_name", bankName.trim())
            .putString("admin_bank_account_number", accountNumber.trim())
            .putString("admin_bank_account_name", accountName.trim())
            .apply()
        viewModelScope.launch {
            _actionResult.emit(Result.success("Platform financial parameters updated successfully!"))
        }
    }

    // Deposit cash into wallet
    fun deposit(
        amount: Double,
        senderName: String? = null,
        senderAccountNumber: String? = null,
        senderBankName: String? = null,
        paymentTransactionId: String? = null,
        conversionRate: Double? = null,
        localCurrencyAmount: Double? = null,
        depositMethod: String? = "BANK"
    ) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.depositCash(
                username = username,
                amount = amount,
                senderName = senderName,
                senderAccountNumber = senderAccountNumber,
                senderBankName = senderBankName,
                paymentTransactionId = paymentTransactionId,
                conversionRate = conversionRate,
                localCurrencyAmount = localCurrencyAmount,
                depositMethod = depositMethod
            )
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Deposit request of $$amount dispatched successfully for audit."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Deposit failed.")))
            }
        }
    }

    // Withdraw cash
    fun withdraw(amount: Double, isCrypto: Boolean = false) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.withdrawCash(username, amount, isCrypto)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Withdrawal of $$amount processed successfully! Pending review."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Withdrawal failed.")))
            }
        }
    }

    // Bindings
    fun bindBank(bankName: String, accountNumber: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.bindBankAccount(username, bankName, accountNumber)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Success: Bank account details updated."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Task failed.")))
            }
        }
    }

    fun bindCrypto(address: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.bindCryptoAddress(username, address)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Success: Crypto wallet address bound."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Task failed.")))
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.updatePassword(username, oldPass, newPass)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Success: Security password altered."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Security update failed.")))
            }
        }
    }

    // Lucky Wheel Spin
    fun spinLuckyWheel() {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.playLuckySpin(username)
            if (result.isSuccess) {
                val prize = result.getOrNull() ?: 0.0
                _actionResult.emit(Result.success("Lucky Wheel: You won $$prize cash!"))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Lucky spin failed.")))
            }
        }
    }

    // Complete Task
    fun completeTask(taskId: String, title: String, rewardAmount: Double) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val result = repository.completePlatformTask(username, taskId, title, rewardAmount)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Task completed: +$$rewardAmount credited!"))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Task completion failed.")))
            }
        }
    }

    // --- Admin Panel ---
    fun addAdminPlan(name: String, amount: Double, dailyPercentage: Double, durationDays: Int, description: String) {
        viewModelScope.launch {
            val result = repository.createPlanByAdmin(name, amount, dailyPercentage, durationDays, description)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Admin Panel: Successfully published custom plan '$name'!"))
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Plan creation failed.")))
            }
        }
    }

    fun deleteAdminPlan(planId: String) {
        viewModelScope.launch {
            val result = repository.deletePlanByAdmin(planId)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Admin Panel: Terminated investment plan."))
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Plan deletion failed.")))
            }
        }
    }

    fun resetDemoAccount() {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            repository.resetUserAccount(username)
            _actionResult.emit(Result.success("Account portfolio has been safely reset to $500.00 cash!"))
            backupActiveUserToSupabase()
        }
    }

    // --- Admin Platform Actions and Subscriptions ---
    val allRegisteredUsers: StateFlow<List<User>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGlobalTransactions: StateFlow<List<InvestmentTransaction>> = repository.observeAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTransactions: StateFlow<List<InvestmentTransaction>> = repository.observePendingTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveTransaction(id: Int) {
        viewModelScope.launch {
            val result = repository.approveTransaction(id)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Success: Approved and finalized transaction!"))
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Failed to approve transaction.")))
            }
        }
    }

    fun rejectTransaction(id: Int) {
        viewModelScope.launch {
            val result = repository.rejectTransaction(id)
            if (result.isSuccess) {
                _actionResult.emit(Result.success("Success: Rejected transaction and processed any payouts refund."))
            } else {
                _actionResult.emit(Result.failure(result.exceptionOrNull() ?: Exception("Failed to reject transaction.")))
            }
        }
    }

    fun clearAuthStates() {
        _loginState.value = AuthState.Idle
        _signUpState.value = AuthState.Idle
    }

    // --- Internal Live Fluctuation Loop ---
    private fun startMarketTicker() {
        viewModelScope.launch {
            while (isActive) {
                delay(12000) // minor ROI percentage drift every 12 seconds
                repository.fluctuateMarketPrices()
                
                val activeUser = _currentUsername.value
                if (activeUser != null) {
                    val userObj = repository.observeUser(activeUser).first()
                    if (userObj != null) {
                        repository.logCurrentPortfolioHistory(activeUser, userObj.cashBalance)
                    }
                }
            }
        }
    }

    // --- ROI Distribution Configuration & Logging ---
    private fun loadDistributionHistory() {
        val raw = sharedPrefs.getString("roi_distribution_history", "") ?: ""
        if (raw.isNotBlank()) {
            _distributionHistory.value = raw.split("\n")
        } else {
            _distributionHistory.value = emptyList()
        }
    }

    private fun saveDistributionHistory(newEvent: String) {
        val currentList = _distributionHistory.value.toMutableList()
        currentList.add(0, newEvent)
        val text = currentList.take(30).joinToString("\n")
        sharedPrefs.edit().putString("roi_distribution_history", text).apply()
        _distributionHistory.value = currentList.take(30)
    }

    fun isAutoRoiEnabled(): Boolean = sharedPrefs.getBoolean("roi_auto_enabled", false)
    fun getAutoRoiTime(): String = sharedPrefs.getString("roi_auto_time", "12:00") ?: "12:00"

    fun saveAutoRoiSettings(enabled: Boolean, time: String) {
        sharedPrefs.edit()
            .putBoolean("roi_auto_enabled", enabled)
            .putString("roi_auto_time", time)
            .apply()
    }

    fun distributeUserRoiManually() {
        viewModelScope.launch {
            val res = repository.distributeRoiToAllActivePlans(isAuto = false)
            if (res.isSuccess) {
                val logMsg = "[${SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())}] " + res.getOrThrow()
                saveDistributionHistory(logMsg)
                _actionResult.emit(Result.success(res.getOrThrow()))
            } else {
                _actionResult.emit(Result.failure(res.exceptionOrNull() ?: Exception("Manual distribution failed.")))
            }
        }
    }

    private fun initializeAutoDistributor() {
        viewModelScope.launch {
            while (isActive) {
                delay(15000) // check every 15 seconds
                if (isAutoRoiEnabled()) {
                    val schedTime = getAutoRoiTime()
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    if (currentTime == schedTime) {
                        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val lastRanAutoDay = sharedPrefs.getString("last_ran_auto_day", "") ?: ""
                        if (lastRanAutoDay != todayDate) {
                            sharedPrefs.edit().putString("last_ran_auto_day", todayDate).apply()
                            val res = repository.distributeRoiToAllActivePlans(isAuto = true)
                            if (res.isSuccess) {
                                val logMsg = "[${SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())}] " + res.getOrThrow()
                                saveDistributionHistory(logMsg)
                                _actionResult.emit(Result.success(res.getOrThrow()))
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateFullName(newName: String) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val res = repository.updateFullName(username, newName)
            if (res.isSuccess) {
                _actionResult.emit(Result.success("Success: Full name updated."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(res.exceptionOrNull() ?: Exception("Failed to update name.")))
            }
        }
    }

    fun updateAge(newAge: Int) {
        val username = _currentUsername.value ?: return
        viewModelScope.launch {
            val res = repository.updateAge(username, newAge)
            if (res.isSuccess) {
                _actionResult.emit(Result.success("Success: Profile age updated."))
                backupActiveUserToSupabase()
            } else {
                _actionResult.emit(Result.failure(res.exceptionOrNull() ?: Exception("Failed to update age.")))
            }
        }
    }
}
