package com.senseicoder.weatherwatcher.features.Main

import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.ActivityMainBinding
import com.senseicoder.weatherwatcher.utils.dialogs.InitialSettingsDialogFragment
import com.senseicoder.weatherwatcher.utils.global.Constants
import com.senseicoder.weatherwatcher.utils.global.showSnackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var settings: SharedPreferences
    private lateinit var initialSettingsDialog: InitialSettingsDialogFragment
    val registerMapActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            val editor = settings.edit()
            val name = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.NAME}")
            val latitude = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.LATITUDE}")
            val longitude = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.LONGITUDE}")
            Log.d(TAG, "registerMapActivity: putting latitude and longitude: $latitude , $longitude ; name: $name")
            editor.putString(Constants.LONGITUDE, longitude)
                .putString(Constants.LATITUDE, latitude)
                .putString(Constants.NAME, name)
            editor.commit()
        }else{
            binding.root.showSnackbar(
                getString(R.string.you_must_pick_location),
                5000
            )
            initialSettingsDialog.binding.apply {
                radioGroup.clearCheck()
                radioButtonGPS.isChecked = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

            enterTransition = Slide(Gravity.START)
            // Set an exit transition
            exitTransition = Slide(Gravity.END)
        }
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.body.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Get the existing LayoutParams
            binding.navigationView.layoutParams.also{
                (it as ViewGroup.MarginLayoutParams).setMargins(0, systemBars.top, 0, systemBars.bottom)
            }
            binding.navigationView.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        settings = getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, MODE_PRIVATE)
        handleFirstTimeLaunch()
        /*InitialSettingsDialogFragment {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }.show(supportFragmentManager, null)*/

        Log.d(TAG, "onCreate: lang")
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp: ${supportFragmentManager.backStackEntryCount}")
        return (navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    private fun handleFirstTimeLaunch(){
        settings.apply {
            if(getBoolean(Constants.SharedPrefs.IS_FIRST_TIME, true)){
                Log.d(TAG, "handleFirstTimeLaunch: ")
                val editor = settings.edit()
                initialSettingsDialog = InitialSettingsDialogFragment{
                    location, notifications ->
                    editor.putString(Constants.SharedPrefs.Settings.Notifications.NOTIFICAITONS, notifications)
                    editor.putString(Constants.SharedPrefs.Settings.Location.LOCATION, location)
                    editor.putBoolean(Constants.SharedPrefs.IS_FIRST_TIME, false)
                    editor.commit()
                    initialSetup()
                    binding.apply {
                        drawerLayout.visibility = View.VISIBLE
                    }
                }.also {
                    it.show(supportFragmentManager, null)
                }
            }else{
                initialSetup()
                binding.apply {
                    drawerLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initialSetup(){
        Log.d(TAG, "initialSetup: settings up the navigator")
        setSupportActionBar(binding.toolbar)

        navController = findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(setOf(
            R.id.homeFragment,
            R.id.favoritesFragment,
            R.id.settingsFragment,
            R.id.alertsFragment,
        ))
            .setOpenableLayout(binding.drawerLayout)
            .build()
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.homeFragment)
        navController.graph = navGraph
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
        //navController.navigate(R.id.action_blankFragment_to_homeFragment)
        //doesn't work...
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(binding.drawerLayout.isDrawerOpen(binding.navigationView))
                    binding.drawerLayout.closeDrawer(binding.navigationView)
                else {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        navController.addOnDestinationChangedListener { navController1: NavController, navDestination: NavDestination, _: Bundle? ->
            val title = getString(when(navDestination.id){
                R.id.homeFragment -> R.string.home_fragment
                R.id.favoritesFragment -> R.string.favorites_fragment
                R.id.settingsFragment -> R.string.settings_fragment
                R.id.alertsFragment -> R.string.alerts_fragment
                else -> {
                    R.string.unknown
                }
            })
            binding.toolbarTitle.text = title
            binding.toolbar.title = null
        }
    }



    companion object{
        private const val TAG = "MainActivity"
    }
}