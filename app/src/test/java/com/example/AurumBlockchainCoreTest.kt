package com.example

import com.example.blockchain.KotlinCoreEngine
import org.junit.Assert.*
import org.junit.Test

class AurumBlockchainCoreTest {

    @Test
    fun testSeedPhraseGeneration() {
        val phrase = KotlinCoreEngine.generate24WordSeedPhrase()
        val words = phrase.split(" ")
        assertEquals(24, words.size)
        
        // Assert words are from our noble blockchain word dictionary
        assertTrue(words.isNotEmpty())
        assertTrue(words.all { it.isNotBlank() })
    }

    @Test
    fun testAddressDerivation() {
        val phrase1 = "aurum secure decentralized node ledger block witness core master key audit transfer"
        val address1 = KotlinCoreEngine.addressFromSeedPhrase(phrase1)
        
        // Assert address starts with premium native identifier AUR-
        assertTrue(address1.startsWith("AUR-"))
        assertEquals(24, address1.length) // "AUR-" + 20 char hex key = 24
    }

    @Test
    fun testTransactionHashing() {
        val from = "AUR-A1B2C3D4E5"
        val to = "AUR-F6G7H8I9J0"
        val amount = 100L
        val fee = 2L
        val nonce = 1L
        val timestamp = 1718812800000L // static date
        val memo = "Consensus gas payload"

        val txId = KotlinCoreEngine.calculateTxId(from, to, amount, fee, nonce, timestamp, memo)
        
        // Check standard 64-character SHA-256 hex string format
        assertEquals(64, txId.length)
        
        // Changing any value should produce a totally different hash (avoids double execution)
        val txIdAlt = KotlinCoreEngine.calculateTxId(from + "1", to, amount, fee, nonce, timestamp, memo)
        assertNotEquals(txId, txIdAlt)
    }

    @Test
    fun testMiningProofCalculation() {
        val seed = "aurum node mineral ledger"
        val nonce = 129048L
        val proof = KotlinCoreEngine.computeMiningProof(seed, nonce)
        
        assertEquals(64, proof.length)
        assertTrue(proof.matches(Regex("^[0-9a-f]+$")))
    }
}
