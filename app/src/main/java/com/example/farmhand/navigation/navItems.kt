package com.example.farmhand.navigation

import androidx.annotation.DrawableRes
import com.example.farmhand.R


sealed class NavItems(
    val route: String,
    val title: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val hasBadge: Boolean,
    val badgeNum: Int
) {
    object Home : NavItems(
        route = "home",
        title = "Home",
        selectedIcon = R.drawable.home_fill,
        unselectedIcon = R.drawable.home_outline,
        hasBadge = false,
        badgeNum = 0
    )
    object Weather : NavItems(
        route = "weather",
        title = "Weather",
        selectedIcon = R.drawable.cloudy_snowy_fill,
        unselectedIcon = R.drawable.cloudy_snowy_outline,
        hasBadge = false,
        badgeNum = 0
    )
    object PlantHealth : NavItems(
        route = "plant_health",
        title = "Plant health",
        selectedIcon = R.drawable.psychiatry_fill,
        unselectedIcon = R.drawable.psychiatry_outline,
        hasBadge = false,
        badgeNum = 0
    )
    object Account : NavItems(
        route = "account",
        title = "Account",
        selectedIcon = R.drawable.account_circle_fill,
        unselectedIcon = R.drawable.account_circle_outline,
        hasBadge = false,
        badgeNum = 0
    )

}