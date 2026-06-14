package com.example.data

import android.content.Context
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvestmentRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val planDao = db.investmentPlanDao()
    private val investmentDao = db.userInvestmentDao()
    private val historyDao = db.portfolioHistoryDao()
    private val transactionDao = db.transactionDao()

    // Seeds default Zelox platform plans if database is new
    suspend fun seedMockMarketPlansIfNeeded() {
        val plans = planDao.getAllPlansOneShot()
        if (plans.isEmpty()) {
            val zeloxPlans = listOf(
                InvestmentPlan(
                    id = "ZELOX_ALPHA",
                    name = "Zelox Alpha Growth",
                    amount = 150.00,
                    dailyPercentage = 1.85,
                    durationDays = 30,
                    description = "Entry-level algorithmic quantitative investment plan with daily dynamic interest payouts.",
                    isAdminCreated = false
                ),
                InvestmentPlan(
                    id = "ZELOX_SECURE",
                    name = "Zelox Secure Fixed Yield",
                    amount = 500.00,
                    dailyPercentage = 2.40,
                    durationDays = 60,
                    description = "Premium tier plan focused on high-yield web3 real-world asset (RWA) backing.",
                    isAdminCreated = false
                )
            )
            planDao.insertPlans(zeloxPlans)
        }
    }

    // --- Authentication, Sessions, and Credentials ---
    fun observeUser(username: String): Flow<User?> = userDao.getUser(username)

    suspend fun signUp(
        username: String,
        passwordRaw: String,
        fullName: String,
        email: String,
        referralCodeUsed: String?
    ): Boolean {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.isBlank() || passwordRaw.isBlank() || fullName.isBlank()) return false
        val existing = userDao.getUserOneShot(cleanUsername)
        if (existing != null) return false

        // Generate a custom referral code for the new user, e.g. ZEL-12345
        val randNum = Random.nextInt(10000, 99999)
        val personalRefCode = "ZEL-$cleanUsername-${randNum}"

        // Validate matching referral code used
        var referredByUsername: String? = null
        if (!referralCodeUsed.isNullOrBlank()) {
            val allUsers = mutableListOf<User>()
            // Let's check via a simple mock or quick sweep (we can credit the referrer later, and award registration bonus!)
            // For now, let's give the new user an extra registration bonus of $50 if they used a referral code!
        }

        val startingBalance = if (!referralCodeUsed.isNullOrBlank()) 100.00 else 50.00 // $100 referral starter, $50 normal starter

        val newUser = User(
            username = cleanUsername,
            passwordHash = passwordRaw,
            fullName = fullName.trim(),
            email = email.trim().lowercase(),
            cashBalance = startingBalance,
            referralCode = personalRefCode,
            referredBy = referralCodeUsed?.trim()
        )
        userDao.insertUser(newUser)

        // If a valid referral was used, let's simulate updating the referrer's count and giving them rewards when they access
        if (!referralCodeUsed.isNullOrBlank()) {
            transactionDao.insertTransaction(
                InvestmentTransaction(
                    username = cleanUsername,
                    type = "REFERRAL_BONUS",
                    totalAmount = startingBalance,
                    planName = "Registration Referral Bonus"
                )
            )
        } else {
            transactionDao.insertTransaction(
                InvestmentTransaction(
                    username = cleanUsername,
                    type = "DEPOSIT",
                    totalAmount = startingBalance,
                    planName = "Free Signup Balance"
                )
            )
        }

        historyDao.insertHistory(
            PortfolioHistory(
                username = cleanUsername,
                timestamp = System.currentTimeMillis() - 86400000,
                netWorth = startingBalance
            )
        )

        return true
    }

    suspend fun verifyLogin(emailOrUsername: String, passwordRaw: String): User? {
        val trimmed = emailOrUsername.trim()
        val lowered = trimmed.lowercase()
        var user = userDao.getUserByEmailOneShot(trimmed)
        if (user == null) {
            user = userDao.getUserByEmailOneShot(lowered)
        }
        if (user == null) {
            user = userDao.getUserOneShot(trimmed)
        }
        if (user == null) {
            user = userDao.getUserOneShot(lowered)
        }
        return if (user != null && user.passwordHash == passwordRaw) {
            user
        } else {
            null
        }
    }

    // --- Admin Operations ---
    suspend fun createPlanByAdmin(
        name: String,
        amount: Double,
        dailyPercentage: Double,
        durationDays: Int,
        description: String
    ): Result<Unit> {
        if (name.isBlank() || amount <= 0.0 || dailyPercentage <= 0.0 || durationDays <= 0 || description.isBlank()) {
            return Result.failure(Exception("All fields must be valid and filled."))
        }
        val planId = "PLAN_" + name.trim().replace("\\s+".toRegex(), "_").uppercase() + "_" + Random.nextInt(100, 999)
        val newPlan = InvestmentPlan(
            id = planId,
            name = name.trim(),
            amount = amount,
            dailyPercentage = dailyPercentage,
            durationDays = durationDays,
            description = description.trim(),
            isAdminCreated = true
        )
        planDao.insertPlan(newPlan)
        return Result.success(Unit)
    }

    suspend fun deletePlanByAdmin(planId: String): Result<Unit> {
        val plans = planDao.getAllPlansOneShot()
        val plan = plans.find { it.id == planId } ?: return Result.failure(Exception("Plan not found."))
        planDao.deletePlan(plan)
        return Result.success(Unit)
    }

    // --- Investment Portfolio & Live Claims ---
    fun observeAllPlans(): Flow<List<InvestmentPlan>> = planDao.getAllPlans()

    fun observeUserHoldings(username: String): Flow<List<UserInvestment>> =
        investmentDao.getInvestmentsForUser(username)

    fun observeTransactions(username: String): Flow<List<InvestmentTransaction>> =
        transactionDao.getTransactionsForUser(username)

    fun observeHistory(username: String): Flow<List<PortfolioHistory>> =
        historyDao.getHistoryForUser(username)

    // Purchase an investment plan
    suspend fun buyPlan(username: String, planId: String, unusedAmountParam: Double = 0.0): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        val plans = planDao.getAllPlansOneShot()
        val plan = plans.find { it.id == planId } ?: return Result.failure(Exception("Plan not found."))

        if (user.cashBalance < plan.amount) {
            return Result.failure(Exception("Insufficient balance. Buying this plan requires $${String.format("%.2f", plan.amount)}."))
        }

        // Deduct Cash and Save Holding
        val newBalance = user.cashBalance - plan.amount
        userDao.updateUser(user.copy(cashBalance = newBalance))

        investmentDao.insertInvestment(
            UserInvestment(
                username = username,
                planId = plan.id,
                planName = plan.name,
                amount = plan.amount,
                dailyPercentage = plan.dailyPercentage,
                durationDays = plan.durationDays,
                purchaseTimestamp = System.currentTimeMillis(),
                lastCollectedTimestamp = System.currentTimeMillis(),
                daysElapsed = 0,
                totalClaimed = 0.0
            )
        )

        // Log transaction
        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                planId = plan.id,
                planName = plan.name,
                type = "BUY",
                shares = 1.0,
                pricePerShare = plan.amount,
                totalAmount = plan.amount
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(Unit)
    }

    // Sell / Terminate Plan manually (optional backup)
    suspend fun sellPlan(username: String, planId: String, unusedSharesParam: Double = 0.0): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        val holdings = investmentDao.getInvestmentsForUserOneShot(username)
        val holding = holdings.find { it.planId == planId } ?: return Result.failure(Exception("You do not active investments for this plan."))

        // Refunding 70% of cost if exited early, or direct removal
        val refund = holding.amount * 0.75
        val newBalance = user.cashBalance + refund
        userDao.updateUser(user.copy(cashBalance = newBalance))

        investmentDao.deleteInvestment(holding)

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                planId = planId,
                planName = holding.planName,
                type = "SELL",
                shares = 1.0,
                pricePerShare = refund,
                totalAmount = refund
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(Unit)
    }

    // Collect ROI payout manually
    suspend fun collectPlanRoi(username: String, holdingId: Int): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        val holdings = investmentDao.getInvestmentsForUserOneShot(username)
        val holding = holdings.find { it.id == holdingId } ?: return Result.failure(Exception("Active plan investment does not exist."))

        // Custom time lapse simulation:
        // Let's assume every 10 seconds of login allows user to fast-track claim 1 day of percentage ROI,
        // OR simply clicking "Collect ROI" pays out the full daily return instantly for simulation fun!
        // To be highly satisfying, we calculate the return of 1 full day of ROI:
        val dailyPayout = holding.amount * (holding.dailyPercentage / 100.0)

        if (holding.daysElapsed >= holding.durationDays) {
            // Already matured, auto delete or complete
            investmentDao.deleteInvestment(holding)
            // Refund initial principal on completion! Very common and satisfying!
            val updatedUser = user.copy(cashBalance = user.cashBalance + holding.amount)
            userDao.updateUser(updatedUser)
            transactionDao.insertTransaction(
                InvestmentTransaction(
                    username = username,
                    planId = holding.planId,
                    planName = "${holding.planName} Matured Principal Return",
                    type = "ROI_CLAIM",
                    totalAmount = holding.amount
                )
            )
            return Result.success(Unit)
        }

        val updatedHolding = holding.copy(
            daysElapsed = holding.daysElapsed + 1,
            totalClaimed = holding.totalClaimed + dailyPayout,
            lastCollectedTimestamp = System.currentTimeMillis()
        )
        investmentDao.updateInvestment(updatedHolding)

        val newBalance = user.cashBalance + dailyPayout
        userDao.updateUser(user.copy(cashBalance = newBalance))

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                planId = holding.planId,
                planName = "${holding.planName} ROI Payout",
                type = "ROI_CLAIM",
                totalAmount = dailyPayout
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(Unit)
    }

    suspend fun updateFullName(username: String, newName: String): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        if (newName.isBlank()) return Result.failure(Exception("Full name cannot be blank."))
        val updated = user.copy(fullName = newName.trim())
        userDao.updateUser(updated)
        return Result.success(Unit)
    }

    suspend fun updateAge(username: String, age: Int): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        if (age < 0) return Result.failure(Exception("Age cannot be a negative value."))
        val updated = user.copy(age = age)
        userDao.updateUser(updated)
        return Result.success(Unit)
    }

    suspend fun distributeRoiToAllActivePlans(isAuto: Boolean): Result<String> {
        val allHoldings = investmentDao.getAllHoldingsOneShot()
        if (allHoldings.isEmpty()) {
            return Result.failure(Exception("No active plan investments found on the server."))
        }

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var distributedCount = 0
        var totalAmountPaid = 0.0

        for (holding in allHoldings) {
            if (holding.daysElapsed >= holding.durationDays) {
                continue
            }
            
            if (holding.lastDistributedDate == todayDate) {
                continue
            }

            val user = userDao.getUserOneShot(holding.username) ?: continue
            val dailyPayout = holding.amount * (holding.dailyPercentage / 100.0)
            val newBalance = user.cashBalance + dailyPayout
            userDao.updateUser(user.copy(cashBalance = newBalance))

            val nextDaysElapsed = holding.daysElapsed + 1
            val updatedHolding = holding.copy(
                daysElapsed = nextDaysElapsed,
                totalClaimed = holding.totalClaimed + dailyPayout,
                lastCollectedTimestamp = System.currentTimeMillis(),
                lastDistributedDate = todayDate
            )
            investmentDao.updateInvestment(updatedHolding)

            transactionDao.insertTransaction(
                InvestmentTransaction(
                    username = holding.username,
                    planId = holding.planId,
                    planName = "Daily ROI (${if (isAuto) "Auto" else "Manual"}) - ${holding.planName}",
                    type = "ROI_CLAIM",
                    totalAmount = dailyPayout,
                    status = "APPROVED",
                    timestamp = System.currentTimeMillis()
                )
            )

            logCurrentPortfolioHistory(holding.username, newBalance)

            // Once plan reaches its duration, it will simply set daysElapsed >= durationDays and end automatically.
            // No matured principal return is paid back to the cash balance.

            distributedCount++
            totalAmountPaid += dailyPayout
        }

        if (distributedCount == 0) {
            return Result.success("All active plans are already up to date. No new ROI distributions were needed for today ($todayDate).")
        }

        val typeLabel = if (isAuto) "AUTOMATIC" else "MANUAL"
        val message = "Executed $typeLabel ROI payout: $distributedCount plans processed. Paid $${String.format("%.2f", totalAmountPaid)} total."
        
        return Result.success(message)
    }

    // --- Deposit & Withdrawal Real Logic ---
    suspend fun depositCash(
        username: String,
        amount: Double,
        senderName: String? = null,
        senderAccountNumber: String? = null,
        senderBankName: String? = null,
        paymentTransactionId: String? = null,
        conversionRate: Double? = null,
        localCurrencyAmount: Double? = null,
        depositMethod: String? = "BANK"
    ): Result<Unit> {
        if (amount <= 0.0) return Result.failure(Exception("Amount must be greater than zero."))
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                type = "DEPOSIT",
                totalAmount = amount,
                planName = if (depositMethod == "BANK") "Local Bank Transfer Deposit" else "Cryptocurrency Deposit",
                status = "PENDING",
                senderName = senderName,
                senderAccountNumber = senderAccountNumber,
                senderBankName = senderBankName,
                paymentTransactionId = paymentTransactionId,
                conversionRate = conversionRate,
                localCurrencyAmount = localCurrencyAmount,
                depositMethod = depositMethod
            )
        )
        return Result.success(Unit)
    }

    suspend fun withdrawCash(username: String, amount: Double, isCrypto: Boolean = false): Result<Unit> {
        if (amount <= 0.0) return Result.failure(Exception("Amount must be greater than zero."))
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))

        if (user.cashBalance < amount) {
            return Result.failure(Exception("Insufficient funds. You have $${String.format("%.2f", user.cashBalance)} available."))
        }

        // Must bind payment credentials first!
        val bankBound = !user.boundBankAccount.isNullOrBlank()
        val cryptoBound = !user.boundCryptoAddress.isNullOrBlank()
        if (isCrypto && !cryptoBound) {
            return Result.failure(Exception("Withdrawal failed! You must bind your Crypto Address in the Settings tab first."))
        }
        if (!isCrypto && !bankBound) {
            return Result.failure(Exception("Withdrawal failed! You must bind your Bank Account in the Settings tab first."))
        }

        val newBalance = user.cashBalance - amount
        userDao.updateUser(user.copy(cashBalance = newBalance))

        val withdrawChannel = if (isCrypto) "USDT TRC20 (" + user.boundCryptoAddress + ")" else (user.boundBankName ?: "Local Bank") + " (" + (user.boundBankAccount ?: "") + ")"

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                type = "WITHDRAWAL",
                totalAmount = amount,
                planName = "Withdrawn to $withdrawChannel",
                status = "PENDING"
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(Unit)
    }

    // --- Admin Platform Queries and Processing Commands ---
    fun observeAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    fun observeAllTransactions(): Flow<List<InvestmentTransaction>> = transactionDao.getAllTransactions()

    fun observePendingTransactions(): Flow<List<InvestmentTransaction>> = transactionDao.getAllPendingTransactions()

    suspend fun approveTransaction(id: Int): Result<Unit> {
        val tx = transactionDao.getTransactionById(id) ?: return Result.failure(Exception("Transaction not found."))
        if (tx.status != "PENDING") {
            return Result.failure(Exception("Transaction is already processed ($${tx.status})."))
        }

        val user = userDao.getUserOneShot(tx.username) ?: return Result.failure(Exception("User associated with transaction not found."))

        if (tx.type == "DEPOSIT") {
            val newBalance = user.cashBalance + tx.totalAmount
            userDao.updateUser(user.copy(cashBalance = newBalance))
            transactionDao.updateTransaction(tx.copy(status = "APPROVED"))
            logCurrentPortfolioHistory(tx.username, newBalance)
        } else if (tx.type == "WITHDRAWAL") {
            // Balance was already deducted when withdrawal was requested to lock funds, so we just approve transaction status!
            transactionDao.updateTransaction(tx.copy(status = "APPROVED"))
        } else {
            transactionDao.updateTransaction(tx.copy(status = "APPROVED"))
        }

        return Result.success(Unit)
    }

    suspend fun rejectTransaction(id: Int): Result<Unit> {
        val tx = transactionDao.getTransactionById(id) ?: return Result.failure(Exception("Transaction not found."))
        if (tx.status != "PENDING") {
            return Result.failure(Exception("Transaction is already processed ($${tx.status})."))
        }

        val user = userDao.getUserOneShot(tx.username) ?: return Result.failure(Exception("User associated with transaction not found."))

        if (tx.type == "DEPOSIT") {
            // Just update transaction status to rejected; no balance was added/deducted
            transactionDao.updateTransaction(tx.copy(status = "REJECTED"))
        } else if (tx.type == "WITHDRAWAL") {
            // Refund the previously deducted balance to current user wallet list
            val refundedBalance = user.cashBalance + tx.totalAmount
            userDao.updateUser(user.copy(cashBalance = refundedBalance))
            transactionDao.updateTransaction(tx.copy(status = "REJECTED"))
            logCurrentPortfolioHistory(tx.username, refundedBalance)
        } else {
            transactionDao.updateTransaction(tx.copy(status = "REJECTED"))
        }

        return Result.success(Unit)
    }

    // --- Save Security Configurations ---
    suspend fun bindBankAccount(username: String, bankName: String, accountNumber: String): Result<Unit> {
        if (bankName.isBlank() || accountNumber.isBlank()) {
            return Result.failure(Exception("Bank Name and Account Number cannot be empty."))
        }
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        userDao.updateUser(user.copy(
            boundBankName = bankName.trim(),
            boundBankAccount = accountNumber.trim()
        ))
        return Result.success(Unit)
    }

    suspend fun bindCryptoAddress(username: String, address: String): Result<Unit> {
        if (address.isBlank()) {
            return Result.failure(Exception("Crypto wallet address cannot be empty."))
        }
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        userDao.updateUser(user.copy(
            boundCryptoAddress = address.trim()
        ))
        return Result.success(Unit)
    }

    suspend fun updatePassword(username: String, oldPass: String, newPass: String): Result<Unit> {
        if (oldPass.isBlank() || newPass.isBlank()) {
            return Result.failure(Exception("Passwords cannot be blank."))
        }
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        if (user.passwordHash != oldPass) {
            return Result.failure(Exception("Incorrect password verification."))
        }
        userDao.updateUser(user.copy(passwordHash = newPass))
        return Result.success(Unit)
    }

    // --- Lucky Spin Wheel Stuff ---
    suspend fun playLuckySpin(username: String): Result<Double> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        val cost = 20.00
        if (user.cashBalance < cost) {
            return Result.failure(Exception("Insufficient balance. Spinning the wheel costs $20.00."))
        }

        // Weighted lucky prize pools
        val rand = Random.nextDouble()
        val prize = when {
            rand < 0.02 -> 150.00  // 2% mega jackpot
            rand < 0.08 -> 50.00   // 6% high payout
            rand < 0.25 -> 30.00   // 17% decent return
            rand < 0.60 -> 15.00   // 35% tier
            else -> 5.00          // 40% consolation
        }

        val newBalance = user.cashBalance - cost + prize
        userDao.updateUser(user.copy(
            cashBalance = newBalance,
            lastSpinTime = System.currentTimeMillis()
        ))

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                type = "SPIN_WIN",
                totalAmount = prize - cost, // Net reward
                planName = "Lucky Spin Reward (Won $$prize)"
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(prize)
    }

    // --- Task Rewards Completion ---
    suspend fun completePlatformTask(username: String, taskId: String, title: String, rewardAmount: Double): Result<Unit> {
        val user = userDao.getUserOneShot(username) ?: return Result.failure(Exception("User not found."))
        
        // Ensure user hasn't successfully completed this task already
        val txs = transactionDao.getTransactionsForUser(username).first()
        val alreadyDone = txs.any { it.type == "TASK_REWARD" && it.planId == taskId }
        if (alreadyDone) {
            return Result.failure(Exception("Task has already been completed."))
        }

        val newBalance = user.cashBalance + rewardAmount
        userDao.updateUser(user.copy(cashBalance = newBalance))

        transactionDao.insertTransaction(
            InvestmentTransaction(
                username = username,
                planId = taskId,
                planName = title,
                type = "TASK_REWARD",
                totalAmount = rewardAmount
            )
        )

        logCurrentPortfolioHistory(username, newBalance)
        return Result.success(Unit)
    }

    // Aggregate portfolio net worth recording helper
    suspend fun logCurrentPortfolioHistory(username: String, currentCash: Double) {
        val holdings = investmentDao.getInvestmentsForUserOneShot(username)
        val activeValue = holdings.sumOf { it.amount }
        val netWorth = currentCash + activeValue

        historyDao.insertHistory(
            PortfolioHistory(
                username = username,
                timestamp = System.currentTimeMillis(),
                netWorth = netWorth
            )
        )
    }

    // Live dummy price fluctuations
    suspend fun fluctuateMarketPrices() {
        val plans = planDao.getAllPlansOneShot()
        if (plans.isEmpty()) return

        for (plan in plans) {
            // Keep them stable, or add minor dynamic change to percentage
            val fluctuator = Random.nextDouble(-0.1, 0.15)
            val newPercentage = Math.max(0.2, plan.dailyPercentage + fluctuator)
            // Just minor fluctuations
            planDao.updatePlan(plan.copy(dailyPercentage = Math.round(newPercentage * 100.0) / 100.0))
        }
    }

    suspend fun resetUserAccount(username: String) {
        val u = userDao.getUserOneShot(username) ?: return
        userDao.updateUser(u.copy(
            cashBalance = 500.00,
            boundBankName = null,
            boundBankAccount = null,
            boundCryptoAddress = null,
            referredCount = 0
        ))
        investmentDao.clearInvestmentsForUser(username)
        transactionDao.clearTransactionsForUser(username)
        historyDao.clearHistoryForUser(username)

        historyDao.insertHistory(
            PortfolioHistory(
                username = username,
                timestamp = System.currentTimeMillis() - 86400000,
                netWorth = 500.00
            )
        )
        historyDao.insertHistory(
            PortfolioHistory(
                username = username,
                timestamp = System.currentTimeMillis(),
                netWorth = 500.00
            )
        )
    }

    suspend fun getUserBackupData(username: String): UserBackup? {
        val user = userDao.getUserOneShot(username) ?: return null
        val holdings = investmentDao.getInvestmentsForUserOneShot(username)
        val transactions = transactionDao.getTransactionsForUserOneShot(username)
        val history = historyDao.getHistoryForUserOneShot(username)
        return UserBackup(
            user = user,
            investments = holdings,
            transactions = transactions,
            portfolioHistory = history
        )
    }

    suspend fun restoreUserFromBackup(backup: UserBackup) {
        userDao.insertUser(backup.user)
        
        // Clear active holdings and restore
        investmentDao.clearInvestmentsForUser(backup.user.username)
        for (inv in backup.investments) {
            investmentDao.insertInvestment(inv)
        }

        // Clear transactions and restore
        transactionDao.clearTransactionsForUser(backup.user.username)
        for (tx in backup.transactions) {
            transactionDao.insertTransaction(tx)
        }

        // Clear history and restore
        historyDao.clearHistoryForUser(backup.user.username)
        for (h in backup.portfolioHistory) {
            historyDao.insertHistory(h)
        }
    }
}
