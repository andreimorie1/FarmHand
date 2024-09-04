package com.example.farmhand.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.farmhand.models.ScaffoldViewModel
import com.example.farmhand.navigation.NavGraph
import com.example.farmhand.ui.theme.Typography

@Composable
fun NavIcon(
    @DrawableRes id: Int,
    contentDescription: String
) {
    Icon(painter = painterResource(id = id), contentDescription = contentDescription)
}

@Preview
@Composable
fun MainAppScaffold() {
    val scaffoldModel = ScaffoldViewModel()
    val bottomNavState by scaffoldModel.bottomNavState
    val navController = rememberNavController() // Initialize NavController

    Scaffold(
        bottomBar = {
            NavigationBar {
                scaffoldModel.navItems.forEachIndexed { index, navItemState ->
                    NavigationBarItem(
                        selected = bottomNavState == index,
                        onClick = {
                            scaffoldModel.updateBottomNavState(index)
                            navController.navigate(navItemState.route) {
                                popUpTo(navController.graph.startDestinationId){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (navItemState.hasBadge) Badge {}
                                if (navItemState.badgeNum != 0) Badge {
                                    Text(text = navItemState.badgeNum.toString())
                                }
                            }) {
                                NavIcon(
                                    id = if (bottomNavState == index) navItemState.selectedIcon else navItemState.unselectedIcon,
                                    contentDescription = navItemState.title
                                )
                            }
                        },
                        label = {
                            Text(text = navItemState.title, style = Typography.labelSmall)
                        },
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
            NavGraph(navController)
        }
    }
}


