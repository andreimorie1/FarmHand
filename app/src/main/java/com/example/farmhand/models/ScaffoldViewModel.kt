package com.example.farmhand.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.example.farmhand.navigation.NavItems

class ScaffoldViewModel : ViewModel() {
    val navItems = listOf(
        NavItems.Home,
        NavItems.Weather,
        NavItems.PlantHealth,
        NavItems.Account
    )

    private var _bottomNavState = mutableIntStateOf(0)
    val bottomNavState: State<Int> get() = _bottomNavState

    fun updateBottomNavState(index: Int) {
        _bottomNavState.intValue = index
    }
}