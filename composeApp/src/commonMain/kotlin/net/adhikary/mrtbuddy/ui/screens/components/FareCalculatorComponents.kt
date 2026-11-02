package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import net.adhikary.mrtbuddy.ui.theme.glassSurface
import net.adhikary.mrtbuddy.ui.theme.frostedGlassSurface
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import net.adhikary.mrtbuddy.data.model.Station
import net.adhikary.mrtbuddy.ui.components.StaticNfcIcon
import net.adhikary.mrtbuddy.ui.components.StationRouteText
import androidx.compose.ui.Alignment
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.localizedNfcErrorMessage
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.taka_wallet
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.layout.Column
import mrtbuddy.composeapp.generated.resources.fromStationLabel
import mrtbuddy.composeapp.generated.resources.journeyInformationDescription
import mrtbuddy.composeapp.generated.resources.journeyInformationLabel
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.selectDestination
import mrtbuddy.composeapp.generated.resources.selectOrigin
import mrtbuddy.composeapp.generated.resources.selectRouteDescription
import mrtbuddy.composeapp.generated.resources.selectRouteText
import mrtbuddy.composeapp.generated.resources.singleTicket
import mrtbuddy.composeapp.generated.resources.tapToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.toStationLabel
import mrtbuddy.composeapp.generated.resources.chooseStationAccessibility
import mrtbuddy.composeapp.generated.resources.fareInvoice
import mrtbuddy.composeapp.generated.resources.summary
import mrtbuddy.composeapp.generated.resources.invoice
import mrtbuddy.composeapp.generated.resources.baseFare
import mrtbuddy.composeapp.generated.resources.passDiscount
import mrtbuddy.composeapp.generated.resources.total
import mrtbuddy.composeapp.generated.resources.currentBalance
import mrtbuddy.composeapp.generated.resources.sufficient
import mrtbuddy.composeapp.generated.resources.insufficient
import mrtbuddy.composeapp.generated.resources.insufficientBalanceMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    // Use BoxWithConstraints so we can adapt layout to the available width
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 420.dp

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .glassSurface(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding( if (isCompact) 12.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp)) {

                // Header (ModernHeader, horizontal layout, transparent, no padding)
                // Swap animation state for header
                val swapAnimCount = remember { mutableStateOf(0) }
                val swapRotation by animateFloatAsState(
                    targetValue = (swapAnimCount.value % 2) * 180f,
                    animationSpec = tween(durationMillis = 420)
                )
                val swapPulse = remember { mutableStateOf(false) }
                val swapScale by animateFloatAsState(
                    targetValue = if (swapPulse.value) 1.06f else 1f,
                    animationSpec = spring()
                )
                LaunchedEffect(swapAnimCount.value) {
                    if (swapAnimCount.value > 0) {
                        swapPulse.value = true
                        delay(180)
                        swapPulse.value = false
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 14.dp),
                    ) {
                        // Circular icon avatar on the left
                        Surface(
                            modifier = Modifier
                                .size(if (isCompact) 44.dp else 52.dp)
                                .glassSurface(RoundedCornerShape(15.dp)),
                            shape = RoundedCornerShape(15.dp),
                            color = Color.Transparent,
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Route,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(if (isCompact) 22.dp else 28.dp)
                                )
                            }
                        }

                        // Title + description
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.selectRouteText),
                                style = if (isCompact) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.selectRouteDescription),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }

                        // Swap button (moved to header)
                        Box(
                            modifier = Modifier.size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .size(44.dp)
                                    .scale(swapScale)
                                    .glassSurface(CircleShape)
                                    .clickable(enabled = uiState.fromStation != null && uiState.toStation != null) {
                                        swapAnimCount.value = swapAnimCount.value + 1
                                        viewModel.onAction(FareCalculatorAction.SwapStations)
                                    },
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = stringResource(Res.string.selectRouteText),
                                        modifier = Modifier.rotate(swapRotation),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // rescan action if available
                        if (getPlatform().name != "android") {
                            TextButton(onClick = { RescanManager.requestRescan() }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(Res.string.rescan), tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

                // Station chips row — show From/To stacked vertically only
                val combinedAnchor = remember { mutableStateOf<String?>(null) }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // Left column: From and To stacked vertically
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val fromExpanded = combinedAnchor.value == "from"
                        StationChip(
                            label = stringResource(Res.string.fromStationLabel),
                            value = uiState.fromStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectOrigin),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            leading = Icons.Default.LocationOn,
                            expanded = fromExpanded,
                            stations = viewModel.stations.drop(1),
                            selectedStationId = uiState.fromStation?.id,
                            onClick = { combinedAnchor.value = if (fromExpanded) null else "from" },
                            onDismissRequest = { combinedAnchor.value = null },
                            onStationSelected = { station ->
                                viewModel.onAction(FareCalculatorAction.UpdateFromStation(station))
                                combinedAnchor.value = null
                            }
                        )

                        val toExpanded = combinedAnchor.value == "to"
                        StationChip(
                            label = stringResource(Res.string.toStationLabel),
                            value = uiState.toStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectDestination),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            leading = Icons.Default.LocationOn,
                            expanded = toExpanded,
                            stations = viewModel.stations.drop(1),
                            selectedStationId = uiState.toStation?.id,
                            onClick = { combinedAnchor.value = if (toExpanded) null else "to" },
                            onDismissRequest = { combinedAnchor.value = null },
                            onStationSelected = { station ->
                                viewModel.onAction(FareCalculatorAction.UpdateToStation(station))
                                combinedAnchor.value = null
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun StationChip(
    label: String,
    value: String,
    color: Color,
    leading: ImageVector,
    expanded: Boolean,
    stations: List<Station>,
    selectedStationId: Int?,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onStationSelected: (Station) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    BoxWithConstraints(modifier = modifier) {
        var anchorHeightPx by remember { mutableStateOf(0) }
        val arrowRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = 180),
            label = "station_dropdown_arrow"
        )

        Column(
            modifier = Modifier.onGloballyPositioned { anchorHeightPx = it.size.height },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlassSurface(RoundedCornerShape(18.dp))
                    .sizeIn(minWidth = 140.dp, minHeight = 58.dp)
                    .semantics { contentDescription = "$label: $value" }
                    .clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = color.copy(alpha = 0.18f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = leading, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Surface(
                        modifier = Modifier.size(34.dp).glassSurface(CircleShape),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(Res.string.chooseStationAccessibility, label),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(27.dp).rotate(arrowRotation)
                            )
                        }
                    }
                }
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, anchorHeightPx),
                onDismissRequest = onDismissRequest,
                properties = PopupProperties(focusable = true)
            ) {
                val menuShape = RoundedCornerShape(20.dp)
                Card(
                    modifier = Modifier
                        .width(maxWidth)
                        .frostedGlassSurface(menuShape),
                    shape = menuShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
                        if (stations.isEmpty()) {
                            Text(
                                text = value,
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                                contentPadding = PaddingValues(vertical = 5.dp)
                            ) {
                                items(stations, key = { it.id }) { station ->
                                    val selected = selectedStationId == station.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                                else Color.Transparent
                                            )
                                            .clickable { onStationSelected(station) }
                                            .heightIn(min = 48.dp)
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = StationService.translate(station.name),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (selected) {
                                            Text(
                                                text = "✓",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val isCompact = maxWidth < 420.dp
        val vmState = viewModel.state.value

        Card(
            modifier = Modifier.fillMaxWidth().animateContentSize().glassSurface(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            when (val cs = uiState.cardState) {
                is CardState.Balance -> {
                    val fare = vmState.discountedFare
                    val original = vmState.calculatedFare
                    val discount = (original - fare).coerceAtLeast(0)

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(if (isCompact) 14.dp else 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(Res.string.fareInvoice), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                if (uiState.fromStation != null || uiState.toStation != null) {
                                    StationRouteText(
                                        from = uiState.fromStation?.let { StationService.translate(it.name) } ?: "-",
                                        to = uiState.toStation?.let { StationService.translate(it.name) } ?: "-",
                                        arrowSize = 14.dp,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = stringResource(if (isCompact) Res.string.summary else Res.string.invoice), style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Line items
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(Res.string.baseFare), style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "৳ ${translateNumber(original)}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(Res.string.passDiscount), style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "-৳ ${translateNumber(discount)}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary))
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(Res.string.total), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "৳ ${translateNumber(fare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                            }
                        }

                        // Card balance summary
                        val balance = cs.amount
                        val enough = balance >= fare

                        // Professional balance panel: icon + balance + status chip
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(painter = painterResource(Res.drawable.taka_wallet), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = stringResource(Res.string.currentBalance), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(text = "৳ ${translateNumber(balance)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                            }

                            // Status chip
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = if (enough) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                                modifier = Modifier.padding(start = 8.dp).glassSurface(RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = stringResource(if (enough) Res.string.sufficient else Res.string.insufficient),
                                    color = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // If not enough, show a short professional message and a Top up CTA
                        if (!enough) {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = stringResource(Res.string.insufficientBalanceMessage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // --- end merged Travel Info section ---
                     }
                 }

                is CardState.Reading -> {
                    // Keep a static NFC mark visible during the brief card-reading state.
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        StaticNfcIcon(size = 40.dp)
                        Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is CardState.Error -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = localizedNfcErrorMessage(cs.message), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                else -> {
                    // Single-ticket preview
                    Column(modifier = Modifier.fillMaxWidth().padding(if (isCompact) 14.dp else 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Rounded.Sell, contentDescription = stringResource(Res.string.singleTicket), modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        Text(text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(vmState.calculatedFare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

                    }
                }
            }
        }
    }
}

