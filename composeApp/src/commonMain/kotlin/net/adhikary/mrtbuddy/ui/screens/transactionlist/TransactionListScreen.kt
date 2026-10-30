package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.amount
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.back
import mrtbuddy.composeapp.generated.resources.balanceWithAmount
import mrtbuddy.composeapp.generated.resources.addedAccessibility
import mrtbuddy.composeapp.generated.resources.spentAccessibility
import mrtbuddy.composeapp.generated.resources.journeys
import mrtbuddy.composeapp.generated.resources.loadingTransactions
import mrtbuddy.composeapp.generated.resources.loadingTransactionsDescription
import mrtbuddy.composeapp.generated.resources.historyLoadError
import mrtbuddy.composeapp.generated.resources.noTransactionsFound
import mrtbuddy.composeapp.generated.resources.notAvailable
import mrtbuddy.composeapp.generated.resources.quickTip
import mrtbuddy.composeapp.generated.resources.recharge
import mrtbuddy.composeapp.generated.resources.recharges
import mrtbuddy.composeapp.generated.resources.spent
import mrtbuddy.composeapp.generated.resources.somethingWentWrong
import mrtbuddy.composeapp.generated.resources.transactionSummary
import mrtbuddy.composeapp.generated.resources.transactionsCount
import mrtbuddy.composeapp.generated.resources.transactionsAppearPrompt
import mrtbuddy.composeapp.generated.resources.transactionsEmptyTip
import mrtbuddy.composeapp.generated.resources.tryAgain
import mrtbuddy.composeapp.generated.resources.unnamedCard
import mrtbuddy.composeapp.generated.resources.bengali_taka
import mrtbuddy.composeapp.generated.resources.calculator_vector
import mrtbuddy.composeapp.generated.resources.spending_vector
import mrtbuddy.composeapp.generated.resources.taka_wallet
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.glassSurface
import net.adhikary.mrtbuddy.ui.components.StationRouteText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
    cardIdm: String,
    onBack: () -> Unit,
    paddingValues: PaddingValues
) {
    val viewModel: TransactionListViewModel = koinViewModel(
        key = cardIdm,
        parameters = { parametersOf(cardIdm) }
    )

    val uiState = viewModel.state.collectAsState().value
    val listState = rememberLazyListState()

    Surface(
        modifier = Modifier.fillMaxSize().then(modifier),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Top App Bar
            TopAppBar(
                title = {
                    Column {
                        val cardName = uiState.cardName?.takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.unnamedCard)
                        Text(
                            text = cardName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (uiState.balance != null) {
                            Text(
                                text = stringResource(Res.string.balanceWithAmount, translateNumber(uiState.balance)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.48f)
                ),
                windowInsets = WindowInsets.statusBars
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                when {
                    uiState.isLoading -> {
                        // Enhanced loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                            modifier = Modifier.padding(32.dp).glassSurface(RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                    text = stringResource(Res.string.loadingTransactions),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = stringResource(Res.string.loadingTransactionsDescription),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    uiState.error != null -> {
                        // Enhanced error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                            modifier = Modifier.padding(24.dp).glassSurface(RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.1f
                                    )
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                    text = stringResource(Res.string.somethingWentWrong),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = stringResource(Res.string.historyLoadError),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                    FilledTonalButton(
                                        onClick = {
                                            viewModel.refresh()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(Res.string.tryAgain), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    uiState.transactions.isEmpty() -> {
                        // Enhanced empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .padding(bottom = paddingValues.calculateBottomPadding()),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                // Animated icon container
                                AnimatedVisibility(
                                    visible = true,
                                    enter = scaleIn(animationSpec = tween(600, delayMillis = 200))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                    alpha = 0.2f
                                                )
                                            )
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            modifier = Modifier.size(72.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.noTransactionsFound),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(Res.string.transactionsAppearPrompt),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                    )
                                }

                                // Quick tips card
                                Card(
                                    modifier = Modifier.fillMaxWidth().glassSurface(RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.3f
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.quickTip),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = stringResource(Res.string.transactionsEmptyTip),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Enhanced transaction list
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp + paddingValues.calculateBottomPadding()
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Summary header
                            item {
                                TransactionSummaryCard(
                                    transactions = uiState.transactions,
                                    currentBalance = uiState.balance
                                )
                            }

                            // Transactions list
                            items(
                                items = uiState.transactions,
                                key = { transaction ->
                                    // Use a unique key: fallback to hashCode if no unique id
                                    val baseKey = "${transaction.transactionEntity.dateTime}_${transaction.transactionEntity.fromStation}_${transaction.transactionEntity.toStation}"
                                    baseKey + "_" + transaction.hashCode()
                                }
                            ) { transaction ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                                        animationSpec = tween(300)
                                    ),
                                    exit = fadeOut(animationSpec = tween(300)) + scaleOut(
                                        animationSpec = tween(300)
                                    )
                                ) {
                                    EnhancedTransactionItem(transaction)
                                }
                            }

                            // Bottom spacer
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionSummaryCard(
    transactions: List<TransactionEntityWithAmount>,
    currentBalance: Int?
) {
    val darkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val rechargeTransactions = transactions.filter { item ->
        val transaction = item.transactionEntity
        TransactionType.isRecharge(
            transaction.fixedHeader,
            transaction.fromStation,
            transaction.toStation
        )
    }
    val commuteTransactions = transactions.filterNot { item ->
        val transaction = item.transactionEntity
        TransactionType.isRecharge(
            transaction.fixedHeader,
            transaction.fromStation,
            transaction.toStation
        )
    }
    val totalSpent = commuteTransactions.mapNotNull { it.amount }.sum()
    val totalAdded = rechargeTransactions.mapNotNull { it.amount }.sum()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .glassSurface(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
//        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.transactionSummary),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(Res.string.transactionsCount, translateNumber(transactions.size)),
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top
            ) {
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DirectionsTransit,
                    label = stringResource(Res.string.journeys),
                    value = translateNumber(commuteTransactions.size),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    iconPainter = painterResource(Res.drawable.bengali_taka),
                    label = stringResource(Res.string.recharges),
                    value = translateNumber(rechargeTransactions.size),
                    color = if (darkTheme) DarkPositiveGreen else LightPositiveGreen
                )
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    iconPainter = painterResource(Res.drawable.calculator_vector),
                    label = stringResource(Res.string.amount),
                    value = "৳${translateNumber(totalAdded)}",
                    color = if (darkTheme) DarkPositiveGreen else LightPositiveGreen
                )
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    iconPainter = painterResource(Res.drawable.spending_vector),
                    label = stringResource(Res.string.spent),
                    value = "৳${translateNumber(kotlin.math.abs(totalSpent))}",
                    color = if (darkTheme) DarkNegativeRed else LightNegativeRed
                )
                if (currentBalance != null) {
                    SummaryItem(
                        modifier = Modifier.weight(1f),
                        iconPainter = painterResource(Res.drawable.taka_wallet),
                        label = stringResource(Res.string.balance),
                        value = "৳${translateNumber(currentBalance)}",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    label: String,
    value: String,
    color: Color
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EnhancedTransactionItem(trxEntity: TransactionEntityWithAmount) {
    val transaction = trxEntity.transactionEntity
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        label = "transaction_scale"
    )

    val amountText = if (trxEntity.amount != null) {
        "৳ ${translateNumber(trxEntity.amount)}"
    } else {
        stringResource(Res.string.notAvailable)
    }

    val tz = TimestampService.getDefaultTimezone()
    val dateTimeFormatted = TimestampService.formatDateTime(
        Instant.fromEpochMilliseconds(transaction.dateTime).toLocalDateTime(tz)
    )

    val isRecharge = TransactionType.isRecharge(
        transaction.fixedHeader,
        transaction.fromStation,
        transaction.toStation
    )
    val isPositive = trxEntity.amount != null && (trxEntity.amount > 0 || isRecharge)
    val amountColor = when {
        isRecharge -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
        trxEntity.amount == null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        trxEntity.amount < 0 -> if (isDarkTheme) DarkNegativeRed else LightNegativeRed
        trxEntity.amount > 0 -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .padding(vertical = 4.dp)
            .glassSurface(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Transaction type icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecharge) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecharge) {
                        Icon(
                            painter = painterResource(Res.drawable.bengali_taka),
                            contentDescription = null,
                            tint = if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DirectionsTransit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!isRecharge) {
                        StationRouteText(
                            from = StationService.translate(transaction.fromStation),
                            to = StationService.translate(transaction.toStation),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.recharge),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = dateTimeFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Amount display
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isPositive) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = stringResource(Res.string.addedAccessibility),
                            modifier = Modifier.size(16.dp),
                            tint = amountColor
                        )
                        Text(
                            text = if (isRecharge) amountText else "+$amountText",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = amountColor
                        )
                    } else if (trxEntity.amount != null) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = stringResource(Res.string.spentAccessibility),
                            modifier = Modifier.size(16.dp),
                            tint = amountColor
                        )
                        Text(
                            text = amountText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = amountColor
                        )
                    } else {
                        Text(
                            text = amountText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = amountColor
                        )
                    }
                }

                if (!isRecharge) {
                    Text(
                        text = stringResource(Res.string.journeys),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

