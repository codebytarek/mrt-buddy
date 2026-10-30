package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.luminance
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.recentJourneys
import mrtbuddy.composeapp.generated.resources.recharge
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.glassSurface
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransactionHistoryList(transactions: List<TransactionWithAmount>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(24.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.recentJourneys),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
            
            val validTransactions = transactions

            items(
                items = validTransactions,
                key = { item ->
                    "${item.transaction.timestamp}_${item.transaction.fromStation}_${item.transaction.toStation}_${item.transaction.balance}_${item.transaction.fixedHeader}"
                }
            ) { transactionWithAmount ->
                TransactionItem(
                    type = TransactionType.fromHeader(transactionWithAmount.transaction.fixedHeader),
                    date = transactionWithAmount.transaction.timestamp,
                    fromStation = transactionWithAmount.transaction.fromStation,
                    toStation = transactionWithAmount.transaction.toStation,
                    balance = "৳ ${transactionWithAmount.transaction.balance}",
                    amount = transactionWithAmount.amount?.let { "৳ ${translateNumber(it)}" }
                        ?: "N/A",
                    amountValue = transactionWithAmount.amount
                )

                if (transactionWithAmount != validTransactions.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    type: TransactionType,
    date: LocalDateTime,
    fromStation: String,
    toStation: String,
    balance: String,
    amount: String,
    amountValue: Int?
) {
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val isRecharge = type == TransactionType.BalanceUpdate ||
        fromStation.equals("Recharge", ignoreCase = true) ||
        toStation.equals("Recharge", ignoreCase = true) ||
        (fromStation.isBlank() && toStation.isBlank())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!isRecharge) {
                StationRouteText(
                    from = StationService.translate(fromStation),
                    to = StationService.translate(toStation),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = stringResource(Res.string.recharge),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = TimestampService.formatDateTime(date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            val amountColor = when {
                isRecharge -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
                amountValue == null -> MaterialTheme.colorScheme.onSurface
                amountValue < 0 -> if (isDarkTheme) DarkNegativeRed else LightNegativeRed
                else -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
            }

            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
