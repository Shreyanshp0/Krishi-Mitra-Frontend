package com.example.krishimitra.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.krishimitra.data.network.api.UserData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile_prefs")

@Singleton
class UserProfileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_STATE_KEY = stringPreferencesKey("user_state")
        private val USER_DISTRICT_KEY = stringPreferencesKey("user_district")
    }

    /**
     * Flow that emits the user profile whenever it changes.
     * Returns null if no user profile is saved.
     */
    val userProfile: Flow<UserData?> = context.userProfileDataStore.data.map { preferences ->
        val id = preferences[USER_ID_KEY]
        val name = preferences[USER_NAME_KEY]
        val email = preferences[USER_EMAIL_KEY]
        val phone = preferences[USER_PHONE_KEY]
        val state = preferences[USER_STATE_KEY]
        val district = preferences[USER_DISTRICT_KEY]

        // Only return user data if we have at least an ID
        if (!id.isNullOrEmpty()) {
            UserData(
                id = id,
                name = name ?: "",
                email = email ?: "",
                phone = phone ?: "",
                state = state ?: "",
                district = district ?: ""
            )
        } else {
            null
        }
    }

    /**
     * Save user profile to DataStore.
     * This is called after successfully fetching user profile from API.
     *
     * @param user The UserData to save
     */
    suspend fun saveUserProfile(user: UserData) {
        context.userProfileDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_PHONE_KEY] = user.phone
            preferences[USER_STATE_KEY] = user.state
            preferences[USER_DISTRICT_KEY] = user.district
        }
    }

    /**
     * Clear the user profile from DataStore.
     * This is called during logout.
     */
    suspend fun clearUserProfile() {
        context.userProfileDataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_PHONE_KEY)
            preferences.remove(USER_STATE_KEY)
            preferences.remove(USER_DISTRICT_KEY)
        }
    }
}


