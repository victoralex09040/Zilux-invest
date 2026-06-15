package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// 1. Entities
// ==========================================

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val fullName: String,
    val email: String = "",
    val cashBalance: Double = 500.00, // Starts with a realistic $500 balance for investment games
    val boundBankName: String? = null,
    val boundBankAccount: String? = null,
    val boundCryptoAddress: String? = null,
    val referralCode: String = "",
    val referredBy: String? = null,
    val referredCount: Int = 0,
    val lastSpinTime: Long = 0L,
    val age: Int = 0,
    val lastArrivalClaimTime: Long = 0L,
    val arrivalStreak: Int = 0
)

@Entity(tableName = "investment_plans")
data class InvestmentPlan(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Double,         // Cost of the plan
    val dailyPercentage: Double, // ROI percentage per day
    val durationDays: Int,       // Duration of the plan
    val description: String,
    val isAdminCreated: Boolean = true
)

@Entity(tableName = "user_investments")
data class UserInvestment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val planId: String,
    val planName: String,
    val amount: Double,
    val dailyPercentage: Double,
    val durationDays: Int,
    val purchaseTimestamp: Long = System.currentTimeMillis(),
    val lastCollectedTimestamp: Long = System.currentTimeMillis(),
    val daysElapsed: Int = 0,
    val totalClaimed: Double = 0.0,
    val lastDistributedDate: String = ""
)

@Entity(tableName = "portfolio_history")
data class PortfolioHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val timestamp: Long,
    val netWorth: Double // cash + current assets valuation
)

@Entity(tableName = "investment_transactions")
data class InvestmentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val planId: String = "",
    val planName: String = "",
    val type: String, // "BUY", "DEPOSIT", "WITHDRAWAL", "TASK_REWARD", "SPIN_WIN", "REFERRAL_BONUS", "ROI_CLAIM"
    val shares: Double = 1.0,
    val pricePerShare: Double = 0.0,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val senderName: String? = null,
    val senderAccountNumber: String? = null,
    val senderBankName: String? = null,
    val paymentTransactionId: String? = null,
    val conversionRate: Double? = null,
    val localCurrencyAmount: Double? = null,
    val depositMethod: String? = null // "BANK" or "CRYPTO"
)

// ==========================================
// 2. DAOs
// ==========================================

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUser(username: String): Flow<User?>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserOneShot(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmailOneShot(email: String): User?

    @Query("SELECT * FROM users WHERE referralCode = :referralCode LIMIT 1")
    suspend fun getUserByReferralCode(referralCode: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOneShot(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface InvestmentPlanDao {
    @Query("SELECT * FROM investment_plans")
    fun getAllPlans(): Flow<List<InvestmentPlan>>

    @Query("SELECT * FROM investment_plans")
    suspend fun getAllPlansOneShot(): List<InvestmentPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: InvestmentPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<InvestmentPlan>)

    @Delete
    suspend fun deletePlan(plan: InvestmentPlan)

    @Update
    suspend fun updatePlan(plan: InvestmentPlan)
}

@Dao
interface UserInvestmentDao {
    @Query("SELECT * FROM user_investments WHERE username = :username")
    fun getInvestmentsForUser(username: String): Flow<List<UserInvestment>>

    @Query("SELECT * FROM user_investments WHERE username = :username")
    suspend fun getInvestmentsForUserOneShot(username: String): List<UserInvestment>

    @Query("SELECT * FROM user_investments")
    fun observeAllHoldings(): Flow<List<UserInvestment>>

    @Query("SELECT * FROM user_investments")
    suspend fun getAllHoldingsOneShot(): List<UserInvestment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: UserInvestment)

    @Update
    suspend fun updateInvestment(investment: UserInvestment)

    @Delete
    suspend fun deleteInvestment(investment: UserInvestment)

    @Query("DELETE FROM user_investments WHERE username = :username")
    suspend fun clearInvestmentsForUser(username: String)
}

@Dao
interface PortfolioHistoryDao {
    @Query("SELECT * FROM portfolio_history WHERE username = :username ORDER BY timestamp ASC")
    fun getHistoryForUser(username: String): Flow<List<PortfolioHistory>>

    @Query("SELECT * FROM portfolio_history WHERE username = :username ORDER BY timestamp ASC")
    suspend fun getHistoryForUserOneShot(username: String): List<PortfolioHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PortfolioHistory)

    @Query("DELETE FROM portfolio_history WHERE username = :username")
    suspend fun clearHistoryForUser(username: String)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM investment_transactions WHERE username = :username ORDER BY timestamp DESC")
    fun getTransactionsForUser(username: String): Flow<List<InvestmentTransaction>>

    @Query("SELECT * FROM investment_transactions WHERE username = :username ORDER BY timestamp DESC")
    suspend fun getTransactionsForUserOneShot(username: String): List<InvestmentTransaction>

    @Query("SELECT * FROM investment_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<InvestmentTransaction>>

    @Query("SELECT * FROM investment_transactions WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getAllPendingTransactions(): Flow<List<InvestmentTransaction>>

    @Query("SELECT * FROM investment_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): InvestmentTransaction?

    @Update
    suspend fun updateTransaction(transaction: InvestmentTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: InvestmentTransaction)

    @Query("DELETE FROM investment_transactions WHERE username = :username")
    suspend fun clearTransactionsForUser(username: String)
}

// ==========================================
// 3. Database Singleton
// ==========================================

@Database(
    entities = [
        User::class,
        InvestmentPlan::class,
        UserInvestment::class,
        PortfolioHistory::class,
        InvestmentTransaction::class
    ],
    version = 7, // Upgraded database version to 7 for lastArrivalClaimTime & arrivalStreak fields
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun investmentPlanDao(): InvestmentPlanDao
    abstract fun userInvestmentDao(): UserInvestmentDao
    abstract fun portfolioHistoryDao(): PortfolioHistoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zelox_exclusive_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
