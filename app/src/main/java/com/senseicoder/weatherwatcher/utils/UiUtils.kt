package com.senseicoder.weatherwatcher.utils

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.senseicoder.weatherwatcher.R

object UiUtils {
    fun getNavHostFragment(fragmentActivity: FragmentActivity): NavHostFragment {
        return fragmentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    fun getNavController(fragmentActivity: FragmentActivity): NavController {
        val navHostFragment =
            fragmentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}