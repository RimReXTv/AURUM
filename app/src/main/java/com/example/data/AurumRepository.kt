package com.example.data

import kotlinx.coroutines.flow.Flow

class AurumRepository(private val database: AppDatabase) {
    val walletDao = database.walletDao()
    val transactionDao = database.transactionDao()
    val stakeDao = database.stakeDao()

    fun getActiveWallet(): Flow<WalletAccount?> = walletDao.getActiveWallet()

    suspend fun getWalletByAddress(address: String): WalletAccount? =
        walletDao.getWalletByAddress(address)

    suspend fun saveWallet(wallet: WalletAccount) = walletDao.saveWallet(wallet)

    suspend fun updateBalance(address: String, newBalance: Long) =
        walletDao.updateBalance(address, newBalance)

    suspend fun updateNonce(address: String, newNonce: Long) =
        walletDao.updateNonce(address, newNonce)

    suspend fun clearWallets() {
        walletDao.clearWallets()
        transactionDao.clearTransactions()
        stakeDao.clearStakes()
    }

    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactionsFlow()

    suspend fun insertTransaction(tx: TransactionEntity) =
        transactionDao.insertTransaction(tx)

    suspend fun updateTransactionStatus(txId: String, status: String) =
        transactionDao.updateTransactionStatus(txId, status)

    fun getStakesByOwnerFlow(owner: String): Flow<List<StakeEntity>> =
        stakeDao.getStakesByOwnerFlow(owner)

    suspend fun insertStake(stake: StakeEntity) = stakeDao.insertStake(stake)

    suspend fun updateStakeStatus(id: Int, locked: Boolean, claimed: Boolean) =
        stakeDao.updateStakeStatus(id, locked, claimed)
}
