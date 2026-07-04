package com.jaidensiu.quickMaths.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jaidensiu.quickMaths.domain.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class ThemeRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val theme: Flow<ThemePreference> = context.settingsDataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(value = emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            preferences[THEME_KEY]
                ?.let { saved -> ThemePreference.entries.firstOrNull { it.name == saved } }
                ?: ThemePreference.LIGHT
        }

    suspend fun setTheme(theme: ThemePreference) {
        context.settingsDataStore.edit { it[THEME_KEY] = theme.name }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey(name = "theme_preference")
    }
}
