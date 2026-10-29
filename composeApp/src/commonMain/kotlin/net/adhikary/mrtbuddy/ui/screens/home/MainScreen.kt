package net.adhikary.mrtbuddy.ui.screens.home

import MoreScreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.transactions
import net.adhikary.mrtbuddy.ui.components.AppsIcon
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.HistoryIcon
import net.adhikary.mrtbuddy.ui.theme.glassSurface
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreen
import net.adhikary.mrtbuddy.ui.screens.licenses.OpenSourceLicensesScreen
import net.adhikary.mrtbuddy.ui.screens.stationmap.StationMapScreen
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import mrtbuddy.composeapp.generated.resources.recentActivity
import mrtbuddy.composeapp.generated.resources.recharge
import mrtbuddy.composeapp.generated.resources.noTransactionsInCard
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.model.TransactionType


enum class Screen(val title: StringResource) {
    Home(title = Res.string.balance),
    Calculator(title = Res.string.fare),
    More(title = Res.string.more),
    History(title = Res.string.historyTab),
    TransactionList(title = Res.string.transactions),
    StationMap(title = Res.string.stationMap),
    Licenses(title = Res.string.openSourceLicenses)
}

@Composable
@Preview
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
        val uiState by viewModel.state.collectAsState()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Home.name
        )
        var selectedCardIdm by remember { mutableStateOf<String?>(null) }
        val hasTransactions = uiState.transaction.isNotEmpty()

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                if (currentScreen != Screen.StationMap) {
                    ModernNavigationBar(currentScreen, navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = Screen.Home.name) {
                    ModernHomeScreen(
                        uiState = uiState,
                        hasTransactions = hasTransactions,
                        paddingValues = paddingValues
                    )
                }

                composable(route = Screen.Calculator.name) {
                    FareCalculatorScreen(
                        cardState = uiState.cardState,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable(route = Screen.More.name) {
                    MoreScreen(
                        onNavigateToStationMap = {
                            navController.navigate(Screen.StationMap.name)
                        },
                        onNavigateToLicenses = {
                            navController.navigate(Screen.Licenses.name)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable(route = Screen.History.name) {
                    HistoryScreen(
                        onCardSelected = { cardIdm ->
                            selectedCardIdm = cardIdm
                            navController.navigate(Screen.TransactionList.name)
                        },
                        modifier = Modifier.padding(paddingValues),
                        refreshKey = uiState.historyRevision
                    )
                }

                composable(route = Screen.TransactionList.name) {
                    selectedCardIdm?.let { cardIdm ->
                        TransactionListScreen(
                            cardIdm = cardIdm,
                            onBack = {
                                navController.navigateUp()
                            },
                            paddingValues = paddingValues
                        )
                    }
                }

                composable(route = Screen.StationMap.name) {
                    StationMapScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                    )
                }

                composable(route = Screen.Licenses.name) {
                    OpenSourceLicensesScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                        paddingValues = paddingValues
                    )
                }
            }
        }
        }
}

@Composable
private fun ModernNavigationBar(
    currentScreen: Screen,
    navController: NavHostController
) {
    val shape = RoundedCornerShape(36.dp)
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (dark) 0.42f else 0.54f),
                        MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.18f else 0.16f),
                        Color.Transparent
                    )
                ),
                shape
            )
    ) {
        // A soft specular sweep gives the floating bar a glass rim/highlight instead of a flat tint.
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (dark) 0.26f else 0.34f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp).height(66.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassNavigationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.fare),
                selected = currentScreen == Screen.Calculator,
                icon = { CalculatorIcon() },
                onClick = { navigateToScreen(navController, Screen.Calculator) }
            )
            GlassNavigationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.balance),
                selected = currentScreen == Screen.Home,
                icon = { CardIcon() },
                onClick = { navigateToScreen(navController, Screen.Home) }
            )
            GlassNavigationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.historyTab),
                selected = currentScreen == Screen.History || currentScreen == Screen.TransactionList,
                icon = { HistoryIcon() },
                onClick = { navigateToScreen(navController, Screen.History) }
            )
            GlassNavigationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.more),
                selected = currentScreen == Screen.More,
                icon = { AppsIcon() },
                onClick = { navigateToScreen(navController, Screen.More) }
            )
        }
    }
}

@Composable
private fun GlassNavigationItem(
    modifier: Modifier,
    title: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val selectedColor = Color(0xFFD32F3F)
    val iconColor = if (selected) selectedColor else if (dark) MaterialTheme.colorScheme.onSurface else Color(0xFF17191B)
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "navigation_icon_scale"
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .semantics {
                contentDescription = title
                role = Role.Tab
                this.selected = selected
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(38.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                Box(Modifier.size(24.dp).scale(iconScale), contentAlignment = Alignment.Center) { icon() }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = iconColor
        )
    }
}

@Composable
private fun ModernHomeScreen(
    uiState: MainScreenState,
    hasTransactions: Boolean,
    paddingValues: PaddingValues,

) {
    val validTransactions = remember(uiState.transactionWithAmount) {
        uiState.transactionWithAmount
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 15.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section with modern styling - Fixed at top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = "MRT Buddy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Quick stats card
            Card(
                modifier = Modifier
                    .height(60.dp)
                    .width(120.dp)
                    .glassSurface(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${uiState.transaction.size}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }

        BalanceCard(
            cardState = uiState.cardState,
            cardName = uiState.cardName,
            cardIdm = uiState.cardIdm,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f)
                )
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        if (hasTransactions) {
            // Recent Transactions section header - Fixed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.recentActivity),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.clickable {
                        // Handle click here

                    }
                ) {
//                    Text(
//                        text = "View All",
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 6.dp),
//                        style = MaterialTheme.typography.labelMedium.copy(
//                            fontWeight = FontWeight.Medium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//
//                    )
                }
            }

            // Scrollable Recent Activity Section - Only this part scrolls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .glassSurface(RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = validTransactions,
                        key = { item ->
                            "${item.transaction.timestamp}-${item.transaction.fixedHeader}-${item.transaction.fromStation}-${item.transaction.toStation}"
                        }
                    ) { transactionWithAmount ->
                        ModernTransactionItem(
                            transaction = transactionWithAmount,
                            showDivider = transactionWithAmount != validTransactions.lastOrNull()
                        )
                    }
                }
            }
        } else if (uiState.hasCurrentScanResult && uiState.cardIdm != null) {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).glassSurface(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(Res.string.noTransactionsInCard),
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ModernTransactionItem(
    transaction: net.adhikary.mrtbuddy.model.TransactionWithAmount,
    showDivider: Boolean
) {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (TransactionType.isRecharge(
                            transaction.transaction.fixedHeader,
                            transaction.transaction.fromStation,
                            transaction.transaction.toStation
                        )
                    ) {
                        stringResource(Res.string.recharge)
                    } else if (transaction.transaction.fromStation.isNotEmpty())
                        "${StationService.translate(transaction.transaction.fromStation)} → ${
                            StationService.translate(transaction.transaction.toStation)
                        }"
                    else stringResource(Res.string.recharge),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = net.adhikary.mrtbuddy.nfc.service.TimestampService.formatDateTime(transaction.transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Light
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                val amount = transaction.amount
                // Recharges and balance updates share one transaction category.
                val recharge = TransactionType.isRecharge(
                    transaction.transaction.fixedHeader,
                    transaction.transaction.fromStation,
                    transaction.transaction.toStation
                )
                Text(
                    text = amount?.let { "৳ ${translateNumber(it)}" } ?: "N/A",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        recharge -> if (dark) DarkPositiveGreen else LightPositiveGreen
                        amount == null -> MaterialTheme.colorScheme.onSurfaceVariant
                        amount > 0 -> if (dark) DarkPositiveGreen else LightPositiveGreen
                        amount < 0 -> if (dark) DarkNegativeRed else LightNegativeRed
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        if (showDivider) {
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

private fun navigateToScreen(navController: NavHostController, screen: Screen) {
    navController.navigate(screen.name) {
        popUpTo(navController.graph.findStartDestination().route ?: Screen.Home.name) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}
