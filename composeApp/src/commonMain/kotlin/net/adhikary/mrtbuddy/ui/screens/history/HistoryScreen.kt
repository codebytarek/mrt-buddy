package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cancel
import mrtbuddy.composeapp.generated.resources.delete
import mrtbuddy.composeapp.generated.resources.deleteCard
import mrtbuddy.composeapp.generated.resources.deleteCardConfirm
import mrtbuddy.composeapp.generated.resources.deleteCardHistoryWarning
import mrtbuddy.composeapp.generated.resources.loadingCards
import mrtbuddy.composeapp.generated.resources.loadingCardsDescription
import mrtbuddy.composeapp.generated.resources.noCardsFound
import mrtbuddy.composeapp.generated.resources.payments
import mrtbuddy.composeapp.generated.resources.scanCardPrompt
import mrtbuddy.composeapp.generated.resources.somethingWentWrong
import mrtbuddy.composeapp.generated.resources.tryAgain
import mrtbuddy.composeapp.generated.resources.yourCards
import mrtbuddy.composeapp.generated.resources.cardsCount
import mrtbuddy.composeapp.generated.resources.lowBalanceCount
import mrtbuddy.composeapp.generated.resources.lowBalanceWarning
import mrtbuddy.composeapp.generated.resources.historyLoadError
import mrtbuddy.composeapp.generated.resources.renameCardError
import mrtbuddy.composeapp.generated.resources.deleteCardError
import mrtbuddy.composeapp.generated.resources.cardRenamed
import mrtbuddy.composeapp.generated.resources.cardDeleted
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.glassSurface
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onCardSelected: (String) -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel(),
) {
    val uiState = viewModel.state.collectAsState().value
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val loadErrorText = stringResource(Res.string.historyLoadError)
    val renameErrorText = stringResource(Res.string.renameCardError)
    val deleteErrorText = stringResource(Res.string.deleteCardError)
    val renameSuccessText = stringResource(Res.string.cardRenamed)
    val deleteSuccessText = stringResource(Res.string.cardDeleted)

    LaunchedEffect(viewModel) {
        viewModel.onAction(HistoryScreenAction.OnInit)
    }

    LaunchedEffect(viewModel, loadErrorText, renameErrorText, deleteErrorText, renameSuccessText, deleteSuccessText) {
        viewModel.events.collect { event ->
            when (event) {
                is HistoryScreenEvent.Error -> {
                    val message = when (event.error) {
                        HistoryScreenViewModel.LOAD_ERROR -> loadErrorText
                        HistoryScreenViewModel.RENAME_ERROR -> renameErrorText
                        HistoryScreenViewModel.DELETE_ERROR -> deleteErrorText
                        else -> event.error
                    }
                    snackbarHostState.showSnackbar(message)
                }
                HistoryScreenEvent.RenameSuccess -> snackbarHostState.showSnackbar(renameSuccessText)
                HistoryScreenEvent.DeleteSuccess -> snackbarHostState.showSnackbar(deleteSuccessText)
                is HistoryScreenEvent.ShowRenameDialog -> Unit
            }
        }
    }

    val displayedError = when (uiState.error) {
        HistoryScreenViewModel.LOAD_ERROR -> loadErrorText
        else -> uiState.error.orEmpty()
    }

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cardToRename by remember { mutableStateOf<Pair<String, String?>>("" to null) }
    var cardToDelete by remember { mutableStateOf("") }

    // Enhanced Rename Dialog
    if (showRenameDialog) {
        RenameDialog(
            currentName = cardToRename.second,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                cardToRename.first.let { cardIdm ->
                    viewModel.onAction(HistoryScreenAction.RenameCard(cardIdm, newName))
                }
                showRenameDialog = false
            }
        )
    }

    // Enhanced Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier.glassSurface(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            tonalElevation = 0.dp,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(Res.string.deleteCard),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.deleteCardConfirm),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(Res.string.deleteCardHistoryWarning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAction(HistoryScreenAction.DeleteCard(cardToDelete))
                        showDeleteDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.delete), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize().then(modifier),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Box(Modifier.fillMaxSize()) {
          AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
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
                                containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                                    text = stringResource(Res.string.loadingCards),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(Res.string.loadingCardsDescription),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                uiState.error != null && uiState.cards.isEmpty() -> {
                    // Enhanced error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(24.dp).glassSurface(RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                                    text = displayedError,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                FilledTonalButton(
                                    onClick = { viewModel.onAction(HistoryScreenAction.OnInit) }
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

                uiState.cards.isEmpty() -> {
                    // Enhanced empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.payments),
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
                                    text = stringResource(Res.string.noCardsFound),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(Res.string.scanCardPrompt),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                )
                            }
                        }
                    }
                }

                else -> {
                    // Enhanced cards list with better animations
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header with cards count
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .glassSurface(RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(Res.string.yourCards),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(Res.string.cardsCount, translateNumber(uiState.cards.size)),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    // Status indicators
                                    val lowBalanceCount = uiState.cards.count { it.isLowBalance }
                                    if (lowBalanceCount > 0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = stringResource(Res.string.lowBalanceWarning),
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = stringResource(Res.string.lowBalanceCount, translateNumber(lowBalanceCount)),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Cards list with animations
                        items(
                            items = uiState.cards,
                            key = { it.card.idm }
                        ) { cardWithBalance ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
                            ) {
                                CardItem(
                                    card = cardWithBalance.card,
                                    balance = cardWithBalance.balance,
                                    onCardSelected = { onCardSelected(cardWithBalance.card.idm) },
                                    onRenameClick = {
                                        cardToRename = cardWithBalance.card.idm to cardWithBalance.card.name
                                        showRenameDialog = true
                                    },
                                    onDeleteClick = {
                                        cardToDelete = cardWithBalance.card.idm
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }

                        // Bottom spacer for better scrolling
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
          }
          SnackbarHost(
              hostState = snackbarHostState,
              modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
          )
        }
    }
}
