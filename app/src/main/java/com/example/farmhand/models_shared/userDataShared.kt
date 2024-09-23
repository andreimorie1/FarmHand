package com.example.farmhand.models_shared

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserSharedModel @Inject constructor() : ViewModel() {
    var userId: Int? = null
}