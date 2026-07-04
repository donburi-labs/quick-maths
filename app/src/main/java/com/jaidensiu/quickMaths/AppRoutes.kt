package com.jaidensiu.quickMaths

import kotlinx.serialization.Serializable

sealed class AppRoute {
    @Serializable
    data object Start : AppRoute()

    @Serializable
    data object Game : AppRoute()
}
