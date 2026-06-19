package com.example.blockchain

import android.util.Log
import java.security.MessageDigest
import kotlin.random.Random

object KotlinCoreEngine {

    private val wordlist = listOf(
        "aurum", "grid", "blockchain", "node", "crypto", "secure", "ledger", "witness", "vault", "seed",
        "private", "public", "consensus", "reward", "token", "mint", "proof", "work", "stake", "epoch",
        "block", "chain", "address", "wallet", "mine", "compute", "limit", "core", "network", "client",
        "hardware", "cpu", "temp", "algorithm", "cipher", "matrix", "vector", "cluster", "solar", "merkle",
        "faucet", "balance", "transfer", "lock", "annual", "yield", "validator", "byzantine", "attested", "signature",
        "verify", "submit", "compile", "native", "bridge", "rust", "kotlin", "dalek", "sha", "noble",
        "golden", "digital", "quantum", "ledger", "mining", "hazard", "simulation", "hardware", "terminal", "genesis",
        "reward", "decay", "halving", "treasury", "witness", "sign", "verify", "difficulty", "nonce", "stable"
    )

    fun generate24WordSeedPhrase(): String {
        val pickedList = mutableListOf<String>()
        val size = wordlist.size
        for (i in 1..24) {
            val idx = Random.nextInt(size)
            pickedList.add(wordlist[idx])
        }
        return pickedList.joinToString(" ")
    }

    fun addressFromSeedPhrase(seedPhrase: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val entropy = digest.digest(seedPhrase.trim().toByteArray(Charsets.UTF_8))
        
        // Suffix salt as in keys.rs
        val saltHash = MessageDigest.getInstance("SHA-256")
        saltHash.update(entropy)
        saltHash.update("AurumGridMasterKeyV1".toByteArray(Charsets.UTF_8))
        val masterKey = saltHash.digest()

        // Get Address
        val addressHash = MessageDigest.getInstance("SHA-256")
        addressHash.update(masterKey)
        addressHash.update("AURUM_ADDRESS_V1".toByteArray(Charsets.UTF_8))
        val addressBytes = addressHash.digest()

        // Match first 10 hex bytes representation in uppercase
        val hexAddress = addressBytes.take(10).joinToString("") { String.format("%02X", it) }
        return "AUR-$hexAddress"
    }

    fun createWallet(): String {
        val seed = generate24WordSeedPhrase()
        val address = addressFromSeedPhrase(seed)
        return """{"address":"$address", "seedPhrase":"$seed"}"""
    }

    fun importWallet(seed: String): String {
        val cleanSeed = seed.trim()
        val words = cleanSeed.split("\\s+".toRegex())
        val address = addressFromSeedPhrase(cleanSeed)
        return """{"address":"$address", "seedPhrase":"$cleanSeed"}"""
    }

    fun getBalance(address: String): Long {
        // Fallback or static balance, but ViewModel retrieves balance from Room database reactively
        return 0L
    }

    fun buildTransaction(from: String, to: String, amount: Long, fee: Long, nonce: Long = 0, timestamp: Long = System.currentTimeMillis(), memo: String = ""): String {
        val txId = calculateTxId(from, to, amount, fee, nonce, timestamp, memo)
        return """{
            "tx_id": "$txId",
            "from": "$from",
            "to": "$to",
            "amount": $amount,
            "fee": $fee,
            "nonce": $nonce,
            "timestamp": $timestamp,
            "memo": "$memo"
        }""".trimIndent()
    }

    fun calculateTxId(from: String, to: String, amount: Long, fee: Long, nonce: Long, timestamp: Long, memo: String): String {
        val canonicalStr = "$from|$to|$amount|$fee|$nonce|$timestamp|$memo"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(canonicalStr.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { String.format("%02x", it) }
    }

    fun signTransaction(txJson: String): String {
        // Simply return a dynamic signature based on SHA-256 of the JSON text + "AUR_SIG"
        val digest = MessageDigest.getInstance("SHA-256")
        val sigBytes = digest.digest((txJson + "AUR_SIG").toByteArray(Charsets.UTF_8))
        val sigHex = sigBytes.joinToString("") { String.format("%02x", it) }
        return sigHex
    }

    fun submitTransaction(signedTxJson: String): Boolean {
        // Will be validated and injected directly into Room by ViewModel
        return true
    }

    // Proof calculation matching mining.rs
    fun computeMiningProof(seed: String, nonce: Long): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(seed.toByteArray(Charsets.UTF_8))
        
        // Nonce to Big-Endian bytes emulation
        val nonceBytes = ByteArray(8) { i -> (nonce ushr (56 - i * 8)).toByte() }
        digest.update(nonceBytes)
        
        val done = digest.digest()
        return done.joinToString("") { String.format("%02x", it) }
    }
}
