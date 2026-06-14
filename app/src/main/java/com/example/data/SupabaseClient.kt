package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

@JsonClass(generateAdapter = true)
data class UserBackup(
    val user: User,
    val investments: List<UserInvestment>,
    val transactions: List<InvestmentTransaction>,
    val portfolioHistory: List<PortfolioHistory>
)

@JsonClass(generateAdapter = true)
data class SupabaseRow(
    val key: String,
    val value: String
)

class SupabaseClient {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val backupAdapter = moshi.adapter(UserBackup::class.java)
    private val rowListAdapter = moshi.adapter<List<SupabaseRow>>(
        com.squareup.moshi.Types.newParameterizedType(List::class.java, SupabaseRow::class.java)
    )
    private val rowAdapter = moshi.adapter(SupabaseRow::class.java)

    companion object {
        const val TABLE_NAME = "zelox_cloud_sync"
    }

    suspend fun testConnection(url: String, key: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanUrl = url.trim().removeSuffix("/")
        val requestUrl = "$cleanUrl/rest/v1/$TABLE_NAME?limit=1"
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key.trim())
            .addHeader("Authorization", "Bearer ${key.trim()}")
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                when {
                    response.isSuccessful -> Result.success("CONNECTED")
                    response.code == 404 -> Result.failure(Exception("TABLE_MISSING"))
                    else -> Result.failure(Exception("HTTP Error ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBackup(url: String, key: String, username: String, backup: UserBackup): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanUrl = url.trim().removeSuffix("/")
        val jsonValue = backupAdapter.toJson(backup)
        val row = SupabaseRow(key = "user_$username", value = jsonValue)
        val requestBody = rowAdapter.toJson(row).toRequestBody("application/json".toMediaType())

        val exists = checkRowExists(cleanUrl, key, "user_$username")
        val request = if (exists) {
            Request.Builder()
                .url("$cleanUrl/rest/v1/$TABLE_NAME?key=eq.user_$username")
                .addHeader("apikey", key.trim())
                .addHeader("Authorization", "Bearer ${key.trim()}")
                .addHeader("Content-Type", "application/json")
                .patch(requestBody)
                .build()
        } else {
            Request.Builder()
                .url("$cleanUrl/rest/v1/$TABLE_NAME")
                .addHeader("apikey", key.trim())
                .addHeader("Authorization", "Bearer ${key.trim()}")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
        }

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 201 || response.code == 204) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to save cloud backup (HTTP ${response.code}): ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadBackup(url: String, key: String, username: String): Result<UserBackup?> = withContext(Dispatchers.IO) {
        val cleanUrl = url.trim().removeSuffix("/")
        val requestUrl = "$cleanUrl/rest/v1/$TABLE_NAME?key=eq.user_$username&select=key,value"
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key.trim())
            .addHeader("Authorization", "Bearer ${key.trim()}")
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to download backup (HTTP ${response.code})"))
                }
                val bodyText = response.body?.string() ?: ""
                val rows = rowListAdapter.fromJson(bodyText) ?: emptyList()
                if (rows.isEmpty()) {
                    return@withContext Result.success(null)
                }
                val matchedRow = rows.first()
                val backup = backupAdapter.fromJson(matchedRow.value)
                Result.success(backup)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun checkRowExists(url: String, key: String, rowKey: String): Boolean {
        val requestUrl = "$url/rest/v1/$TABLE_NAME?key=eq.$rowKey&select=key"
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key.trim())
            .addHeader("Authorization", "Bearer ${key.trim()}")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyText = response.body?.string() ?: ""
                    bodyText.contains(rowKey)
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }
}
