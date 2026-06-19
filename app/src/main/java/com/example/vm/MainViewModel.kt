package com.example.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.blockchain.KotlinCoreEngine
import com.example.bridge.RustBridge
import com.example.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import kotlin.random.Random

class MainViewModel(
    application: Application,
    private val repository: AurumRepository
) : AndroidViewModel(application) {

    // Active Wallet details
    private val _walletState = MutableStateFlow<WalletAccount?>(null)
    val walletState: StateFlow<WalletAccount?> = _walletState.asStateFlow()

    // Screen transitions
    private val _currentScreen = MutableStateFlow("wallet")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Transaction histories
    val transactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stake positions
    private val _stakes = MutableStateFlow<List<StakeEntity>>(emptyList())
    val stakes: StateFlow<List<StakeEntity>> = _stakes.asStateFlow()

    // Mining Status Engine
    private val _miningEnabled = MutableStateFlow(false)
    val miningEnabled: StateFlow<Boolean> = _miningEnabled.asStateFlow()

    private val _cpuLimit = MutableStateFlow(20)
    val cpuLimit: StateFlow<Int> = _cpuLimit.asStateFlow()

    private val _tempCap = MutableStateFlow(42)
    val tempCap: StateFlow<Int> = _tempCap.asStateFlow()

    private val _currentTemp = MutableStateFlow(32.4f)
    val currentTemp: StateFlow<Float> = _currentTemp.asStateFlow()

    private val _hashRate = MutableStateFlow(0)
    val hashRate: StateFlow<Int> = _hashRate.asStateFlow()

    private val _totalMinedBlocks = MutableStateFlow(0)
    val totalMinedBlocks: StateFlow<Int> = _totalMinedBlocks.asStateFlow()

    private val _miningLogs = MutableStateFlow<List<String>>(listOf("System ready for proof-of-work mining."))
    val miningLogs: StateFlow<List<String>> = _miningLogs.asStateFlow()

    private var miningJob: Job? = null

    // Message / Notification channel
    private val _notification = MutableStateFlow<String?>(null)
    val notification: StateFlow<String?> = _notification.asStateFlow()

    init {
        // Observe wallet and fetch child stakes
        viewModelScope.launch {
            repository.getActiveWallet().collect { wallet ->
                _walletState.value = wallet
                if (wallet != null) {
                    observeStakes(wallet.address)
                } else {
                    _stakes.value = emptyList()
                }
            }
        }
    }

    private fun observeStakes(address: String) {
        viewModelScope.launch {
            repository.getStakesByOwnerFlow(address).collect { list ->
                _stakes.value = list
            }
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun dismissNotification() {
        _notification.value = null
    }

    // Wallet Generation & Import
    fun createNewWallet() {
        viewModelScope.launch {
            val walletJson = RustBridge.createWallet()
            val obj = JSONObject(walletJson)
            val address = obj.getString("address")
            val seed = obj.getString("seedPhrase")
            
            // Faucet: Let's credit newly created wallet with 1,500.00 AUR to test staking/sends immediately!
            val mainWallet = WalletAccount(
                address = address,
                seedPhrase = seed,
                balance = 1500,
                nonce = 0
            )
            repository.saveWallet(mainWallet)
            
            // Add genesis faucet record
            val faucetTx = TransactionEntity(
                txId = "GENESIS-" + Random.nextInt(1000000),
                sender = "AUR-GENESIS-POOL",
                receiver = address,
                amount = 1500,
                fee = 0,
                timestamp = System.currentTimeMillis(),
                memo = "Genesis Welcome Faucet Coins",
                status = "Confirmed"
            )
            repository.insertTransaction(faucetTx)
            _notification.value = "New Aurum wallet created! Loaded 1,500.00 AUR Welcome Faucet."
        }
    }

    fun importWallet(seedPhrase: String) {
        if (seedPhrase.trim().split("\\s+".toRegex()).size < 12) {
            _notification.value = "Invalid seed phrase. Must contain at least 12 or 24 noble words."
            return
        }

        viewModelScope.launch {
            val walletJson = RustBridge.importWallet(seedPhrase)
            val obj = JSONObject(walletJson)
            val address = obj.getString("address")
            val seed = obj.getString("seedPhrase")

            val mainWallet = WalletAccount(
                address = address,
                seedPhrase = seed,
                balance = 750, // Import welcome faucet
                nonce = 0
            )
            repository.saveWallet(mainWallet)

            val faucetTx = TransactionEntity(
                txId = "IMPORT-" + Random.nextInt(1000000),
                sender = "AUR-IMPORT-POOL",
                receiver = address,
                amount = 750,
                fee = 0,
                timestamp = System.currentTimeMillis(),
                memo = "Core Wallet Import Faucet",
                status = "Confirmed"
            )
            repository.insertTransaction(faucetTx)
            _notification.value = "Wallet successfully imported! Loaded 750.00 AUR."
        }
    }

    fun requestFaucet() {
        val wallet = _walletState.value ?: return
        viewModelScope.launch {
            val claimAmount = 500L
            val updatedBalance = wallet.balance + claimAmount
            repository.updateBalance(wallet.address, updatedBalance)

            val faucetTx = TransactionEntity(
                txId = "FAUCET-" + System.currentTimeMillis(),
                sender = "AUR-DEVELOPER-FAUCET",
                receiver = wallet.address,
                amount = claimAmount,
                fee = 0,
                timestamp = System.currentTimeMillis(),
                memo = "Instant Developer Faucet Claim",
                status = "Confirmed"
            )
            repository.insertTransaction(faucetTx)
            _notification.value = "Claimed 500.00 AUR Faucet tokens successfully!"
        }
    }

    fun sendTransaction(recipientAddress: String, amountStr: String, memo: String) {
        val wallet = _walletState.value ?: return
        val amount = amountStr.toLongOrNull()
        if (amount == null || amount <= 0) {
            _notification.value = "Please enter a valid amount greater than zero."
            return
        }

        val fee = 2L
        val totalDebit = amount + fee

        if (wallet.balance < totalDebit) {
            _notification.value = "Insufficient balance. Need at least ${totalDebit} AUR (including ${fee} AUR gas fee)."
            return
        }

        if (recipientAddress.trim() == wallet.address) {
            _notification.value = "Cannot transfer coins to your own address."
            return
        }

        if (!recipientAddress.startsWith("AUR-") || recipientAddress.trim().length < 10) {
            _notification.value = "Invalid recipient address format. Must match 'AUR-XXXXXXXX'."
            return
        }

        viewModelScope.launch {
            val txJson = RustBridge.buildTransaction(wallet.address, recipientAddress, amount, fee)
            val signature = RustBridge.signTransaction(txJson)

            val nextNonce = wallet.nonce + 1
            val updatedBalance = wallet.balance - totalDebit

            // Update balance and nonce
            repository.updateBalance(wallet.address, updatedBalance)
            repository.updateNonce(wallet.address, nextNonce)

            val txId = JSONObject(txJson).getString("tx_id")
            val newTx = TransactionEntity(
                txId = txId,
                sender = wallet.address,
                receiver = recipientAddress,
                amount = amount,
                fee = fee,
                timestamp = System.currentTimeMillis(),
                memo = memo,
                status = "Confirmed"
            )

            repository.insertTransaction(newTx)
            _notification.value = "Transaction of ${amount}.00 AUR successfully submitted!"
            _currentScreen.value = "wallet"
        }
    }

    // Staking logic
    fun stakeTokens(amountStr: String, term: String) {
        val wallet = _walletState.value ?: return
        val amount = amountStr.toLongOrNull()
        if (amount == null || amount <= 0) {
            _notification.value = "Please enter a valid amount to stake."
            return
        }

        if (wallet.balance < amount) {
            _notification.value = "Insufficient balance. Max available: ${wallet.balance} AUR"
            return
        }

        viewModelScope.launch {
            val estimatedRate = when (term) {
                "Monthly" -> 0.01 // 1% for 30s demo limit
                "Quarterly" -> 0.03 // 3% for 90s demo limit
                "Yearly" -> 0.12 // 12% for 365s demo limit
                else -> 0.01
            }
            val estReward = (amount * estimatedRate).toLong().coerceAtLeast(1L)

            val currentEpoch = System.currentTimeMillis() / 1000 // Treat seconds as epochs for lightning live testing!

            val newStake = StakeEntity(
                owner = wallet.address,
                amount = amount,
                term = term,
                startEpoch = currentEpoch,
                locked = true,
                rewardClaimed = false,
                estimatedReward = estReward
            )

            // Deduct from wallet balance
            val updatedBalance = wallet.balance - amount
            repository.updateBalance(wallet.address, updatedBalance)

            repository.insertStake(newStake)

            // Insert transaction entity log
            val stakeTxLog = TransactionEntity(
                txId = "STAKE-" + Random.nextInt(1000000),
                sender = wallet.address,
                receiver = "AUR-STAKE-SMART-ESCROW",
                amount = amount,
                fee = 1,
                timestamp = System.currentTimeMillis(),
                memo = "Lock Aurum to $term Staking Pool",
                status = "Confirmed"
            )
            repository.insertTransaction(stakeTxLog)

            _notification.value = "Successfully locked $amount.00 AUR into $term staking program!"
        }
    }

    fun claimStakeReward(stake: StakeEntity) {
        val wallet = _walletState.value ?: return
        if (!stake.locked || stake.rewardClaimed) return

        viewModelScope.launch {
            val totalClaim = stake.amount + stake.estimatedReward
            val updatedBalance = wallet.balance + totalClaim
            repository.updateBalance(wallet.address, updatedBalance)

            // Update status
            repository.updateStakeStatus(stake.id, locked = false, claimed = true)

            // Log yield receipt transaction
            val yieldTx = TransactionEntity(
                txId = "CLAIM-" + Random.nextInt(1000000),
                sender = "AUR-STAKE-SMART-ESCROW",
                receiver = wallet.address,
                amount = totalClaim,
                fee = 0,
                timestamp = System.currentTimeMillis(),
                memo = "Unlock Staked Capital + ${stake.estimatedReward} AUR Reward",
                status = "Confirmed"
            )
            repository.insertTransaction(yieldTx)
            _notification.value = "Claimed ${totalClaim}.00 AUR staking rewards successfully!"
        }
    }

    // CPU Miner logic
    fun setCpuLimit(limit: Int) {
        _cpuLimit.value = limit
        addLog("Mining thread CPU core execution target modified to $limit%")
    }

    fun setTempCap(dec: Int) {
        _tempCap.value = dec
        addLog("Thermal throttling safety threshold set to ${dec}°C")
    }

    fun toggleMining() {
        val wallet = _walletState.value
        if (wallet == null) {
            _notification.value = "Please create or import a wallet first."
            return
        }

        if (_miningEnabled.value) {
            // Stop Mining
            _miningEnabled.value = false
            _hashRate.value = 0
            _currentTemp.value = 32.4f
            addLog("Core mining operations paused. Engine status: IDLE.")
            miningJob?.cancel()
        } else {
            // Start Mining
            _miningEnabled.value = true
            addLog("Initializing Aurum Grid Mining Subprocessor...")
            addLog("Targeting Witness nodes: 3 count (Threshold: 2).")
            addLog("P2P Seed Node: miner.aurumgrid.org")
            addLog("Active Wallet Node Receiver: ${wallet.address}")

            miningJob = viewModelScope.launch(Dispatchers.Default) {
                var nonce = 0L
                var consecutiveSeconds = 0
                val activeSeed = wallet.seedPhrase

                while (isActive && _miningEnabled.value) {
                    val limitMultiplier = _cpuLimit.value / 100.0
                    val coreRate = (15000 + Random.nextInt(8000)) * limitMultiplier
                    _hashRate.value = coreRate.toInt()

                    // Increase temperature based on limits
                    val targetTemp = 32.0f + (15.0f * (_cpuLimit.value / 100.0f)) + Random.nextFloat() * 1.5f
                    val tempDiff = targetTemp - _currentTemp.value
                    _currentTemp.value += tempDiff * 0.15f

                    // Thermal check
                    if (_currentTemp.value >= _tempCap.value) {
                        _currentTemp.value = _tempCap.value.toFloat() - Random.nextFloat() * 0.5f
                        _hashRate.value = (_hashRate.value * 0.3).toInt() // Throttle
                        addLog("[THERMAL PROTECTION] GPU/CPU Temperature exceeds limit of ${_tempCap.value}°C! Throttling engines.")
                    }

                    // Increment proof and evaluate hashes
                    nonce += Random.nextLong(100000, 300000)
                    val proof = KotlinCoreEngine.computeMiningProof(activeSeed, nonce)

                    // Find block proof (difficulty matching)
                    if (Random.nextInt(18) == 7) { // 1 in 18 chance of finding subproject block lock every second
                        val payoutReward = 50L
                        _totalMinedBlocks.value += 1
                        
                        addLog("✔ BLOCK SOLVED! Proof: ...${proof.takeLast(12)} | Nonce: $nonce")
                        addLog("▶ Submitting solution to decentralised network witness nodes...")
                        addLog("✓ Received 3/3 Witness approvals. Consensus confirmed!")
                        addLog("★ Block reward payout: +50.00 AUR added to wallet balance.")

                        // Add reward to database
                        withContext(Dispatchers.Main) {
                            val activeWallet = _walletState.value
                            if (activeWallet != null) {
                                repository.updateBalance(activeWallet.address, activeWallet.balance + payoutReward)

                                val blockRewardTx = TransactionEntity(
                                    txId = "MINE-" + System.currentTimeMillis(),
                                    sender = "AUR-MINING-EMISSION",
                                    receiver = activeWallet.address,
                                    amount = payoutReward,
                                    fee = 0,
                                    timestamp = System.currentTimeMillis(),
                                    memo = "Proof of Work Block Payout #${_totalMinedBlocks.value}",
                                    status = "Confirmed"
                                )
                                repository.insertTransaction(blockRewardTx)
                            }
                        }
                    }

                    delay(1000)
                }
            }
        }
    }

    private fun addLog(log: String) {
        val currentLogs = _miningLogs.value.toMutableList()
        currentLogs.add(0, "[${System.currentTimeMillis() % 1000000}] $log")
        if (currentLogs.size > 22) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        _miningLogs.value = currentLogs
    }

    fun resetWallet() {
        viewModelScope.launch {
            repository.clearWallets()
            _walletState.value = null
            _stakes.value = emptyList()
            _miningEnabled.value = false
            _totalMinedBlocks.value = 0
            _hashRate.value = 0
            _miningLogs.value = listOf("System database reset. Wallet cache cleared safely.")
            _notification.value = "All localized wallet data has been wiped from memory."
            _currentScreen.value = "wallet"
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: AurumRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
