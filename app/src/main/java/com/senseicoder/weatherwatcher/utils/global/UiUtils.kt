package com.senseicoder.weatherwatcher.utils.global

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils{
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(
            InputMethodManager::class.java
        )
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

object LanguageUtils{
   /* fun checkAndChangLocality()
    {
        val languageCode = if(sharedViewModel.settingsLanguage.value == Constants.ENGLISH_SELECTION_VALUE) "en" else "ar"
        val locale = resources.configuration.locales[0]

        if(locale.language != languageCode)
        {

            val newLocale = Locale(languageCode)
            Locale.setDefault(newLocale)

            val config = resources.configuration

            config.setLocale(newLocale)
            config.setLayoutDirection(newLocale)

            resources.updateConfiguration(config,resources.displayMetrics)

            recreate()

        }
    }*/
}