package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "wallet_accounts")
data class WalletAccount(
    @PrimaryKey val address: String,
    val seedPhrase: String,
    val balance: Long,
    val nonce: Long
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val txId: String,
    val sender: String,
    val receiver: String,
    val amount: Long,
    val fee: Long,
    val timestamp: Long,
    val memo: String,
    val status: String // "Pending", "Confirmed", "Rejected"
)

@Entity(tableName = "stakes")
data class StakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val owner: String,
    val amount: Long,
    val term: String, // "Monthly", "Quarterly", "Yearly"
    val startEpoch: Long,
    val locked: Boolean = true,
    val rewardClaimed: Boolean = false,
    val estimatedReward: Long
)
