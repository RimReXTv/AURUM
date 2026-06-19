package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SoftMutedText
import com.example.vm.MainViewModel

@Composable
fun AppNav(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val wallet by viewModel.walletState.collectAsState()
    val notification by viewModel.notification.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(ObsidianDark)) {
        // Safe navigation animation content switching
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "screen_navigation"
        ) { screen ->
            when (screen) {
                "wallet" -> WalletScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    onSend = { viewModel.setScreen("send") },
                    onReceive = { viewModel.setScreen("receive") },
                    onMining = { viewModel.setScreen("mining") },
                    onStake = { viewModel.setScreen("stake") },
                    onSettings = { viewModel.setScreen("settings") }
                )
                "send" -> SendScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.setScreen("wallet") }
                )
                "receive" -> ReceiveScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    onBack = { viewModel.setScreen("wallet") }
                )
                "mining" -> MiningScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.setScreen("wallet") }
                )
                "stake" -> StakeScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.setScreen("wallet") }
                )
                "settings" -> SettingsScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    onBack = { viewModel.setScreen("wallet") }
                )
            }
        }

        // Custom premium floating notification card
        AnimatedVisibility(
            visible = notification != null,
            enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -50 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            notification?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                        .testTag("notification_toast"),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Alert",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                msg,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }
                        TextButton(
                            onClick = { viewModel.dismissNotification() },
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("OK", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
