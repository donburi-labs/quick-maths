package com.jaidensiu.quickMaths.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Single DataStore delegate for the "settings" file; creating a second
// instance against the same file throws at runtime.
internal val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
