package com.gritto.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ErrorDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmLabel: String = "OK",
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    GrittoDialogContainer(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        iconTint = MaterialTheme.colorScheme.error,
        title = title,
        body = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        primaryAction = DialogAction(label = confirmLabel, onClick = onConfirm),
        onDismissRequest = onConfirm,
    )
}

@Composable
fun WarningDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmLabel: String = "Understood",
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    GrittoDialogContainer(
        modifier = modifier,
        icon = Icons.Outlined.WarningAmber,
        iconTint = MaterialTheme.colorScheme.tertiary,
        title = title,
        body = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        primaryAction = DialogAction(label = confirmLabel, onClick = onConfirm),
        onDismissRequest = onConfirm,
    )
}

@Composable
fun ChoiceDialog(
    visible: Boolean,
    title: String,
    description: String,
    primaryLabel: String,
    secondaryLabel: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onDismissRequest: () -> Unit = onSecondary,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    GrittoDialogContainer(
        modifier = modifier,
        icon = Icons.Outlined.CheckCircle,
        iconTint = MaterialTheme.colorScheme.primary,
        title = title,
        body = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        primaryAction = DialogAction(label = primaryLabel, onClick = onPrimary),
        secondaryAction = DialogAction(label = secondaryLabel, onClick = onSecondary),
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun GrittoDialogContainer(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    body: @Composable () -> Unit,
    primaryAction: DialogAction,
    secondaryAction: DialogAction? = null,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            color = AlertDialogDefaults.containerColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        imageVector = icon,
                        tint = iconTint,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    body()
                }
                DialogActionsRow(
                    primaryAction = primaryAction,
                    secondaryAction = secondaryAction,
                )
            }
        }
    }
}

@Composable
private fun DialogActionsRow(
    primaryAction: DialogAction,
    secondaryAction: DialogAction?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (secondaryAction != null) {
            TextButton(
                onClick = secondaryAction.onClick,
            ) {
                Text(text = secondaryAction.label)
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Button(onClick = primaryAction.onClick) {
            Text(text = primaryAction.label)
        }
    }
}

@Immutable
private data class DialogAction(
    val label: String,
    val onClick: () -> Unit,
)

//@Preview
//@Composable
//private fun ErrorDialogPreview() {
//    ErrorDialog(
//        visible = true,
//        title = "Sync failed",
//        message = "We could not connect to the server. Try again in a few moments.",
//        onConfirm = {},
//    )
//}

//@Preview
//@Composable
//private fun WarningDialogPreview() {
//    WarningDialog(
//        visible = true,
//        title = "Unsaved changes",
//        message = "You have unsaved changes. If you leave now those updates will be lost.",
//        onConfirm = {},
//    )
//}

@Preview
@Composable
private fun ChoiceDialogPreview() {
    ChoiceDialog(
        visible = true,
        title = "Reprioritise goals",
        description = "Switching goal priority will change the order in your roadmap. Continue?",
        primaryLabel = "Apply",
        secondaryLabel = "Cancel",
        onPrimary = {},
        onSecondary = {},
    )
}
