package com.example.bridge

import android.util.Log
import com.example.blockchain.KotlinCoreEngine

object RustBridge {
    private var isNativeLoaded = false

    init {
        try {
            System.loadLibrary("aurumgrid")
            isNativeLoaded = true
            Log.d("RustBridge", "Native aurumgrid cargo JNI library successfully loaded.")
        } catch (e: UnsatisfiedLinkError) {
            isNativeLoaded = false
            Log.w("RustBridge", "Native aurumgrid shared library not found. Falling back to robust Kotlin Core SDK.")
        }
    }

    fun createWallet(): String {
        if (isNativeLoaded) {
            try {
                return nativeCreateWallet()
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeCreateWallet link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.createWallet()
    }

    fun importWallet(seed: String): String {
        if (isNativeLoaded) {
            try {
                return nativeImportWallet(seed)
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeImportWallet link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.importWallet(seed)
    }

    fun getBalance(address: String): Long {
        if (isNativeLoaded) {
            try {
                return nativeGetBalance(address)
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeGetBalance link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.getBalance(address)
    }

    fun buildTransaction(from: String, to: String, amount: Long, fee: Long): String {
        if (isNativeLoaded) {
            try {
                return nativeBuildTransaction(from, to, amount, fee)
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeBuildTransaction link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.buildTransaction(from, to, amount, fee)
    }

    fun signTransaction(txJson: String): String {
        if (isNativeLoaded) {
            try {
                return nativeSignTransaction(txJson)
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeSignTransaction link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.signTransaction(txJson)
    }

    fun submitTransaction(signedTxJson: String): Boolean {
        if (isNativeLoaded) {
            try {
                return nativeSubmitTransaction(signedTxJson)
            } catch (e: UnsatisfiedLinkError) {
                Log.w("RustBridge", "nativeSubmitTransaction link failed, calling fallbacks.")
            }
        }
        return KotlinCoreEngine.submitTransaction(signedTxJson)
    }

    // Original external JNI functions requested by the platform structure:
    private external fun nativeCreateWallet(): String
    private external fun nativeImportWallet(seed: String): String
    private external fun nativeGetBalance(address: String): Long
    private external fun nativeBuildTransaction(from: String, to: String, amount: Long, fee: Long): String
    private external fun nativeSignTransaction(txJson: String): String
    private external fun nativeSubmitTransaction(signedTxJson: String): Boolean
}
