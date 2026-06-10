package com.example

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object Splash : Screen()
    @Serializable data object Dashboard : Screen()
    @Serializable data object ChatBot : Screen()
    @Serializable data object Setting : Screen()
    @Serializable data object Update : Screen()
    @Serializable data object About : Screen()
}
