package com.gritto.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gritto.app.ui.MainDestination

@Composable
fun GrittoBottomBar(
    selected: MainDestination,
    onSelected: (MainDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem(
                destination = MainDestination.Home,
                isSelected = selected == MainDestination.Home,
                onClick = { onSelected(MainDestination.Home) }
            )
            BottomBarItem(
                destination = MainDestination.Goals,
                isSelected = selected == MainDestination.Goals,
                onClick = { onSelected(MainDestination.Goals) }
            )
            ReflectionBottomItem(
                isSelected = selected == MainDestination.Reflection,
                onClick = { onSelected(MainDestination.Reflection) }
            )
            BottomBarItem(
                destination = MainDestination.Schedules,
                isSelected = selected == MainDestination.Schedules,
                onClick = { onSelected(MainDestination.Schedules) }
            )
            BottomBarItem(
                destination = MainDestination.Profile,
                isSelected = selected == MainDestination.Profile,
                onClick = { onSelected(MainDestination.Profile) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: MainDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavigationIcon(
            imageVector = destination.icon,
            isSelected = isSelected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ReflectionBottomItem(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = MainDestination.Reflection.icon,
                contentDescription = MainDestination.Reflection.label,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = MainDestination.Reflection.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NavigationIcon(
    imageVector: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
