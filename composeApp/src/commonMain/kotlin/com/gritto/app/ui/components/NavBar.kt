package com.gritto.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class MainNavDestination {
    Home,
    Chat,
    Profile,
}

@Composable
fun GrittoNavBar(
    selectedDestination: MainNavDestination,
    onDestinationSelected: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        NavBarDefaults.items.forEach { item ->
            NavigationBarItem(
                selected = item.destination == selectedDestination,
                onClick = { onDestinationSelected(item.destination) },
                icon = {
                    Icon(
                        imageVector = if (item.destination == selectedDestination) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(),
            )
        }
    }
}

@Immutable
private data class NavBarItem(
    val destination: MainNavDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
)

private object NavBarDefaults {
    val items = listOf(
        NavBarItem(
            destination = MainNavDestination.Home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home",
        ),
        NavBarItem(
            destination = MainNavDestination.Chat,
            selectedIcon = Icons.Filled.Chat,
            unselectedIcon = Icons.Outlined.Chat,
            label = "Chat",
        ),
        NavBarItem(
            destination = MainNavDestination.Profile,
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            label = "Profile",
        ),
    )
}

@Preview
@Composable
private fun GrittoNavBarPreview() {
    GrittoNavBar(
        selectedDestination = MainNavDestination.Home,
        onDestinationSelected = {},
    )
}
