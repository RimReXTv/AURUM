package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.theme.*
import com.example.vm.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    wallet: WalletAccount?,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onMining: () -> Unit,
    onStake: () -> Unit,
    onSettings: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_aurum_logo),
                            contentDescription = "Aurum Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "AURUM GRID",
                            color = AurumGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (wallet == null) {
                item {
                    OnboardingCard(
                        onCreateWallet = { viewModel.createNewWallet() },
                        onImportWallet = { viewModel.setScreen("settings") }
                    )
                }
            } else {
                item {
                    // Premium Gold Card with generated background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .shadow(12.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Brush.linearGradient(listOf(AurumGold, Color.Transparent)), RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_aurum_banner),
                            contentDescription = "Card Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            ObsidianDark.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "MAIN STAKE NODE WALLET",
                                    color = SoftMutedText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "ONLINE",
                                    color = StatusGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "${wallet.balance}.00",
                                    color = AurumGold,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "AUR",
                                        color = AurumLight,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "(= \$${wallet.balance * 12}.00 USD)",
                                        color = SoftMutedText,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(wallet.address))
                                        // Trigger feedback
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Safe Icon",
                                        tint = AurumBronze,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        wallet.address,
                                        color = SoftMutedText,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 220.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Copy Address",
                                    tint = AurumGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Grid Actions Row
                item {
                    Text(
                        "GRID DECENTRALIZED CORE FUNCTIONS",
                        color = SoftMutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ActionGridCard(
                                title = "Transfer Aurum",
                                subtitle = "Send coins securely",
                                icon = Icons.AutoMirrored.Filled.Send,
                                color = AurumGold,
                                modifier = Modifier.weight(1f).testTag("send_action"),
                                onClick = onSend
                            )
                            ActionGridCard(
                                title = "Receive Address",
                                subtitle = "Show public keys",
                                icon = Icons.Default.AccountBox,
                                color = NeonSliver,
                                modifier = Modifier.weight(1f).testTag("receive_action"),
                                onClick = onReceive
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ActionGridCard(
                                title = "CPU Mining",
                                subtitle = "Earn network rewards",
                                icon = Icons.Default.Build,
                                color = StatusGreen,
                                modifier = Modifier.weight(1f).testTag("mining_action"),
                                onClick = onMining
                            )
                            ActionGridCard(
                                title = "Staking Nodes",
                                subtitle = "Escrow locks up to 12% yield",
                                icon = Icons.Default.Star,
                                color = AurumBronze,
                                modifier = Modifier.weight(1f).testTag("staking_action"),
                                onClick = onStake
                            )
                        }
                        ActionSingleCard(
                            title = "Advanced Node Settings",
                            subtitle = "Private keys, backing stats, wipe ledger state",
                            icon = Icons.Default.Settings,
                            color = SoftMutedText,
                            onClick = onSettings
                        )
                    }
                }

                // Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PEER LEDGER TRANSACTION LOGS",
                            color = SoftMutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "${transactions.size} records",
                            color = SoftMutedText,
                            fontSize = 11.sp
                        )
                    }
                }

                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Empty",
                                    tint = SoftMutedText,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "No transaction history in this epoch.",
                                    color = SoftMutedText,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(items = transactions, key = { it.txId }) { tx ->
                        TxRow(tx, wallet.address)
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun OnboardingCard(
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkCardSurface)
            .border(1.dp, BorderSlate, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_aurum_logo),
            contentDescription = "Aurum Core Initial Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Text(
            "Welcome to Aurum Grid",
            color = AurumGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Text(
            "Secure multi-witness blockchain ledger node. Generate a new seed phrase wallet with immediate faucet tokens or import an existing wallet keyset.",
            color = SoftMutedText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCreateWallet,
            colors = ButtonDefaults.buttonColors(containerColor = AurumGold, contentColor = ObsidianDark),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("create_wallet_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Create Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Node Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        OutlinedButton(
            onClick = onImportWallet,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AurumGold),
            border = BorderStroke(1.dp, AurumGold),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("import_wallet_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Import Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import Seed Keys", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun ActionGridCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardSurface)
            .clickable(onClick = onClick)
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .height(100.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
        }

        Column {
            Text(title, color = AurumLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                color = SoftMutedText,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionSingleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardSurface)
            .clickable(onClick = onClick)
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = AurumLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    color = SoftMutedText,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(Icons.Default.PlayArrow, contentDescription = "Go", tint = SoftMutedText, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun TxRow(tx: TransactionEntity, activeAddress: String) {
    val isIncoming = tx.receiver == activeAddress
    val displayColor = if (isIncoming) StatusGreen else AurumGold
    val displayPrefix = if (isIncoming) "+" else "-"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardSurface)
            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(displayColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncoming) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isIncoming) "Incoming" else "Outgoing",
                    tint = displayColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    if (isIncoming) "Incoming Block Transfer" else "Outgoing Transfer",
                    color = AurumLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    tx.memo.ifEmpty { "Aurum consensus gas tx" },
                    color = SoftMutedText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$displayPrefix${tx.amount}.00 AUR",
                color = displayColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            val dateStr = remember(tx.timestamp) {
                val sdf = SimpleDateFormat("HH:mm:ss dd MMM", Locale.getDefault())
                sdf.format(Date(tx.timestamp))
            }
            Text(
                dateStr,
                color = SoftMutedText,
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Block Coins", color = AurumGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "ENTER TRANSFER DETAILS",
                        color = SoftMutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it },
                        label = { Text("Recipient Node Public Address") },
                        placeholder = { Text("AUR-XXXXXXXXXXXXXXXXXXXX") },
                        modifier = Modifier.fillMaxWidth().testTag("recipient_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AurumGold,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = AurumLight
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount of AUR") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("amount_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AurumGold,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = AurumLight
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = memo,
                        onValueChange = { memo = it },
                        label = { Text("Memo / Message (Optional)") },
                        placeholder = { Text("Aurum node validation payload") },
                        modifier = Modifier.fillMaxWidth().testTag("memo_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AurumGold,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = AurumLight
                        )
                    )
                }
            }

            Text(
                "Gas Fees is set dynamically by consensus witnesses at fixed rate of 2.00 AUR. Solutions will propagate through P2P validation nodes instantly.",
                color = SoftMutedText,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.sendTransaction(recipient, amount, memo) },
                colors = ButtonDefaults.buttonColors(containerColor = AurumGold, contentColor = ObsidianDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .testTag("confirm_transfer_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Blockchain Transfer", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveScreen(
    viewModel: MainViewModel,
    wallet: WalletAccount?,
    onBack: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receive Cryptography Keys", color = AurumGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (wallet != null) {
                Text(
                    "NODE INBOUND ADRESS",
                    color = SoftMutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                // High fidelity QR pattern drawing inside a Box
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSlate)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cellSize = size.width / 13f
                        // Generate beautiful pseudorandom blocks based on seed for consistency
                        val random = Random(wallet.address.hashCode())

                        for (row in 0 until 13) {
                            for (col in 0 until 13) {
                                // Draw main corner anchors typical for QR codes
                                val isInCornerAnchor = (row < 4 && col < 4) ||
                                        (row > 8 && col < 4) ||
                                        (row < 4 && col > 8)

                                val shouldDraw = if (isInCornerAnchor) {
                                    // Visual representation of standard QR corner patterns
                                    val isCenter = (row == 1 && col == 1) || (row == 11 && col == 1) || (row == 1 && col == 11)
                                    val isBorder = row == 0 || row == 3 || col == 0 || col == 3 ||
                                            row == 12 || row == 9 || col == 0 || col == 3 ||
                                            row == 0 || row == 3 || col == 12 || col == 9
                                    isCenter || isBorder
                                } else {
                                    random.nextBoolean()
                                }

                                if (shouldDraw) {
                                    drawRect(
                                        color = if (isInCornerAnchor) ObsidianDark else Color.DarkGray,
                                        topLeft = Offset(col * cellSize, row * cellSize),
                                        size = androidx.compose.ui.geometry.Size(cellSize - 1.dp.toPx(), cellSize - 1.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    "Securely backed by Ed25519 asymmetric signature. Scan QR or copy Node Public key address below to request inbound grid solution payouts.",
                    color = SoftMutedText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("YOUR COMPILING ADDRESS", color = SoftMutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                wallet.address,
                                color = AurumGold,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(wallet.address)) }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Copy", tint = AurumGold)
                        }
                    }
                }

                // Interactive Developer Welcome Request
                Divider(color = BorderSlate, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                Button(
                    onClick = { viewModel.requestFaucet() },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("faucet_request_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Faucet Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Faucet +500.00 AUR", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val enabled by viewModel.miningEnabled.collectAsState()
    val limit by viewModel.cpuLimit.collectAsState()
    val cap by viewModel.tempCap.collectAsState()
    val temp by viewModel.currentTemp.collectAsState()
    val rHashes by viewModel.hashRate.collectAsState()
    val blocks by viewModel.totalMinedBlocks.collectAsState()
    val logs by viewModel.miningLogs.collectAsState()

    // Smooth pulsing glow scale
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsing"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Core Subprocessor Mining", color = AurumGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status visualizers
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Glowing mining radar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .drawBehind {
                                if (enabled) {
                                    drawCircle(
                                        color = StatusGreen.copy(alpha = 0.15f),
                                        radius = size.width / 2 * pulseGlow
                                    )
                                    drawCircle(
                                        color = StatusGreen,
                                        radius = size.width / 2,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                } else {
                                    drawCircle(
                                        color = BorderSlate,
                                        radius = size.width / 2,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Mining Active",
                            tint = if (enabled) StatusGreen else SoftMutedText,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ENGINE STATUS", color = SoftMutedText, fontSize = 10.sp)
                            Text(
                                if (enabled) "MINING" else "IDLE",
                                color = if (enabled) StatusGreen else SoftMutedText,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HASHRATE", color = SoftMutedText, fontSize = 10.sp)
                            Text(
                                if (enabled) "${rHashes / 1000f} Kh/s" else "0.0 H/s",
                                color = AurumGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SOLVED", color = SoftMutedText, fontSize = 10.sp)
                            Text(
                                "$blocks blocks",
                                color = StatusGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Controllers Slider Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("ENGINE CONTROLLER & THROTTLES", color = AurumLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    // CPU Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CPU Cores Limit Targets", color = SoftMutedText, fontSize = 12.sp)
                            Text("$limit%", color = AurumGold, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = limit.toFloat(),
                            onValueChange = { viewModel.setCpuLimit(it.toInt()) },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = AurumGold,
                                activeTrackColor = AurumGold,
                                inactiveTrackColor = BorderSlate
                            ),
                            modifier = Modifier.testTag("cpu_limit_slider")
                        )
                    }

                    // Thermal Cap Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Thermal Limit Protection Cap", color = SoftMutedText, fontSize = 12.sp)
                            Text("${cap}°C", color = ErrorRuby, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = cap.toFloat(),
                            onValueChange = { viewModel.setTempCap(it.toInt()) },
                            valueRange = 40f..90f,
                            colors = SliderDefaults.colors(
                                thumbColor = ErrorRuby,
                                activeTrackColor = ErrorRuby,
                                inactiveTrackColor = BorderSlate
                            ),
                            modifier = Modifier.testTag("temp_cap_slider")
                        )
                    }

                    // Thermal Gauge visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianDark)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Core Temperature", color = SoftMutedText, fontSize = 12.sp)
                        Text(
                            String.format(Locale.US, "%.1f°C", temp),
                            color = if (temp > 50f) ErrorRuby else StatusGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Live compilation logs console
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CONECTIONS & VALIDATOR LOGS",
                    color = SoftMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCardSurface)
                        .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(
                                log,
                                color = if (log.contains("✔") || log.contains("✓") || log.contains("★")) StatusGreen else if (log.contains("THERMAL")) ErrorRuby else SoftMutedText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Start/Stop compilation Switch
            Button(
                onClick = { viewModel.toggleMining() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) ErrorRuby else StatusGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(12.dp))
                    .testTag("toggle_mining_button")
            ) {
                Icon(
                    imageVector = if (enabled) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (enabled) "Stop" else "Start"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (enabled) "HALT CORE MINING" else "INITIALIZE MINING SUBPROCESSOR",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StakeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val stakes by viewModel.stakes.collectAsState()
    val wallet by viewModel.walletState.collectAsState()
    var stakeAmount by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf("Monthly") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escrow Staking Nodes", color = AurumGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("LOCK STAKING CAPITAL", color = SoftMutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = stakeAmount,
                            onValueChange = { stakeAmount = it },
                            label = { Text("Amount of AUR to Lock") },
                            placeholder = { Text("Max available: ${wallet?.balance ?: 0}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("stake_amount_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AurumGold,
                                unfocusedBorderColor = BorderSlate,
                                focusedTextColor = AurumLight
                            )
                        )

                        Text("Select Staking Lock Term Program", color = SoftMutedText, fontSize = 12.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TermSelectionChip(
                                term = "Monthly",
                                yieldStr = "12% APY",
                                selected = selectedTerm == "Monthly",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedTerm = "Monthly" }
                            )
                            TermSelectionChip(
                                term = "Quarterly",
                                yieldStr = "12% APY",
                                selected = selectedTerm == "Quarterly",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedTerm = "Quarterly" }
                            )
                            TermSelectionChip(
                                term = "Yearly",
                                yieldStr = "12% APY",
                                selected = selectedTerm == "Yearly",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedTerm = "Yearly" }
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.stakeTokens(stakeAmount, selectedTerm)
                                stakeAmount = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AurumGold, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("stake_lock_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Initialize Staking Escrow Lock", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    "Note: Program times are accelerated for developer testing. Locks mature instantly to allow easy live cycle testing! Hover reward click claims return stakes + interest payout immediately.",
                    color = SoftMutedText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            // Staked lockups header
            item {
                Text(
                    "YOUR ESCROW CONTRACT POSITIONS",
                    color = SoftMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (stakes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "You have no locked staking programs currently.",
                            color = SoftMutedText,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(stakes) { stake ->
                    StakeRow(stake, onClaim = { viewModel.claimStakeReward(stake) })
                }
            }
        }
    }
}

@Composable
fun TermSelectionChip(
    term: String,
    yieldStr: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) AurumGold.copy(alpha = 0.12f) else ObsidianDark)
            .clickable(onClick = onClick)
            .border(1.dp, if (selected) AurumGold else BorderSlate, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(term, color = if (selected) AurumGold else SoftMutedText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(yieldStr, color = if (selected) AurumLight else SoftMutedText, fontSize = 10.sp)
    }
}

@Composable
fun StakeRow(stake: StakeEntity, onClaim: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardSurface)
            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (stake.locked && !stake.rewardClaimed) AurumBronze.copy(alpha = 0.12f) else StatusGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (stake.locked && !stake.rewardClaimed) Icons.Default.Lock else Icons.Default.CheckCircle,
                    contentDescription = "Lock",
                    tint = if (stake.locked && !stake.rewardClaimed) AurumGold else StatusGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    "${stake.amount}.00 AUR Locked (${stake.term})",
                    color = AurumLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Est Accrued Reward: +${stake.estimatedReward}.00 AUR",
                    color = SoftMutedText,
                    fontSize = 12.sp
                )
            }
        }

        if (stake.locked && !stake.rewardClaimed) {
            Button(
                onClick = onClaim,
                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Unlock Claims", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                "CLAIMED",
                color = StatusGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    wallet: WalletAccount?,
    onBack: () -> Unit
) {
    var seedFieldText by remember { mutableStateOf("") }
    var showSeedPhrase by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asymmetric Node Engine", color = AurumGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (wallet == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("IMPORT MASTER NODE KEYS", color = AurumLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Input your 12 or 24 BIP39 / Aurum standard seed words sequence to construct compiler address details offline.",
                            color = SoftMutedText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        OutlinedTextField(
                            value = seedFieldText,
                            onValueChange = { seedFieldText = it },
                            label = { Text("Seed Phrase (spaces separated)") },
                            placeholder = { Text("aurum grid secure node decentralized ledger seed...") },
                            modifier = Modifier.fillMaxWidth().height(110.dp).testTag("seed_import_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AurumGold,
                                unfocusedBorderColor = BorderSlate,
                                focusedTextColor = AurumLight
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.importWallet(seedFieldText)
                                seedFieldText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AurumGold, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_import_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Load")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compile Imported Keys", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("ACTIVE KEY MATERIAL DETIALS", color = AurumLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Active Node Address:", color = SoftMutedText, fontSize = 12.sp)
                            Text("AUR-CONNECTED", color = StatusGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            wallet.address,
                            color = AurumGold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Divider(color = BorderSlate, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reveal Seed Recovery Phrase", color = SoftMutedText, fontSize = 13.sp)
                            Switch(
                                checked = showSeedPhrase,
                                onCheckedChange = { showSeedPhrase = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AurumGold,
                                    checkedTrackColor = AurumGold.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.testTag("reveal_seed_switch")
                            )
                        }

                        if (showSeedPhrase) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianDark)
                                    .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    wallet.seedPhrase,
                                    color = AurumLight,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // About Program details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                     Text("ABOUT AURUM SYSTEM PROTOCOL", color = AurumLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                     Text(
                         "Aurum Grid is an offline capable consensus-driven cryptographic asset manager. Harness computer system limits and temperature caps to solve mathematical blocks interactively. Witness validations confirm client states locally for absolute security.",
                         color = SoftMutedText,
                         fontSize = 12.sp,
                         lineHeight = 18.sp
                     )
                     Text(
                         "Version: SDK v1.0.8-Core\nEnvironment Target: Android API 36 (Kotlin Compiled)\nJNI Bridge Engine: Fallback Emulator Core active",
                         color = SoftMutedText,
                         fontSize = 11.sp,
                         fontFamily = FontFamily.Monospace,
                         lineHeight = 16.sp
                     )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Wipe ledger safely
            OutlinedButton(
                onClick = { viewModel.resetWallet() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRuby),
                border = BorderStroke(1.dp, ErrorRuby),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("wipe_db_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Wipe icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wipe Node Database Safely", fontWeight = FontWeight.Bold)
            }
        }
    }
}
