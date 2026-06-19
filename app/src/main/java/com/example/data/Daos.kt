package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_accounts LIMIT 1")
    fun getActiveWallet(): Flow<WalletAccount?>

    @Query("SELECT * FROM wallet_accounts WHERE address = :address")
    suspend fun getWalletByAddress(address: String): WalletAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWallet(wallet: WalletAccount)

    @Query("UPDATE wallet_accounts SET balance = :newBalance WHERE address = :address")
    suspend fun updateBalance(address: String, newBalance: Long)

    @Query("UPDATE wallet_accounts SET nonce = :newNonce WHERE address = :address")
    suspend fun updateNonce(address: String, newNonce: Long)

    @Query("DELETE FROM wallet_accounts")
    suspend fun clearWallets()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE txId = :txId")
    suspend fun getTransactionById(txId: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)

    @Query("UPDATE transactions SET status = :status WHERE txId = :txId")
    suspend fun updateTransactionStatus(txId: String, status: String)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}

@Dao
interface StakeDao {
    @Query("SELECT * FROM stakes WHERE owner = :owner ORDER BY startEpoch DESC")
    fun getStakesByOwnerFlow(owner: String): Flow<List<StakeEntity>>

    @Query("SELECT * FROM stakes WHERE id = :id")
    suspend fun getStakeById(id: Int): StakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStake(stake: StakeEntity)

    @Query("UPDATE stakes SET locked = :locked, rewardClaimed = :claimed WHERE id = :id")
    suspend fun updateStakeStatus(id: Int, locked: Boolean, claimed: Boolean)

    @Query("DELETE FROM stakes")
    suspend fun clearStakes()
}
