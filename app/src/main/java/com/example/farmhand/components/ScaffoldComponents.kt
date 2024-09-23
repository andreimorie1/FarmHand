package com.example.farmhand.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.farmhand.models.ScaffoldViewModel
import com.example.farmhand.navigation.NavGraph
import com.example.farmhand.ui.theme.Typography

@Composable
fun NavIcon(
    @DrawableRes id: Int,
    contentDescription: String,
    modifier: Modifier
) {
    Icon(painter = painterResource(id = id), contentDescription = contentDescription)
}

@Composable
fun MainAppScaffold(
    navController: NavHostController, // Passed down from NavGraph
    content: @Composable (NavHostController) -> Unit // Expecting a NavHostController for inner screens
) {
    val scaffoldModel = ScaffoldViewModel()
    val bottomNavState by scaffoldModel.bottomNavState
    val innerNavController = rememberNavController() // Inner NavController for bottom nav screens

    Scaffold(
        bottomBar = {
            NavigationBar {
                scaffoldModel.navItems.forEachIndexed { index, navItemState ->
                    NavigationBarItem(
                        selected = bottomNavState == index,
                        onClick = {
                            scaffoldModel.updateBottomNavState(index)
                            innerNavController.navigate(navItemState.route) {
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            NavIcon(
                                id = if (bottomNavState == index) navItemState.selectedIcon else navItemState.unselectedIcon,
                                contentDescription = navItemState.title,
                                modifier = Modifier.size(if (bottomNavState == index) 35.dp else 24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = navItemState.title,
                                style = Typography.labelSmall,
                                fontWeight = if (bottomNavState == index) FontWeight.Bold else null,
                                fontSize = if (bottomNavState == index) Typography.labelSmall.fontSize.times(1.18f) else Typography.labelSmall.fontSize
                            )
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            content(innerNavController) // Pass the inner NavController to the content
        }
    }
}

