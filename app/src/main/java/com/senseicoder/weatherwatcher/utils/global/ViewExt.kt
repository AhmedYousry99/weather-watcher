
package com.senseicoder.weatherwatcher.utils.global

/**
 * Extension functions and Binding Adapters.
 */

import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.weatherwatcher.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int = 4000) {
    Snackbar.make(this, snackbarText, timeLength).run {
        view.setBackgroundColor(this.context.getColor(R.color.secondary))
        show()
    }
}

fun LocalDateTime.toDateTime(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return  formatter.format(this)
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */


fun Double.toMilesPerHour(): Double {
    val conversionFactor = 2.23694
    return this * conversionFactor
}

fun Double.toKelvin(): Double {
    return this + 273.15
}

fun Double.toFahrenheit(): Double {
    return (this * 9 / 5) + 32
}

fun Double.toTwoDecimalPlaces(locale: Locale = Locale.US): String {
    val numberFormat = NumberFormat.getInstance(locale)
    val parsedNumber = numberFormat.parse(this.toString())?.toDouble() ?: throw NumberFormatException("Cannot parse: $this")
    return String.format(locale, "%.2f", parsedNumber)
}

/*fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}*/


fun String.toDrawable() : Int {
    when (this) {
        "01d" -> return R.drawable.sun
        "01n" -> return R.drawable.moon
        "02d" -> return R.drawable.sun_clouds
        "02n" -> return R.drawable.moon_clouds
        "03d" -> return R.drawable.clouds
        "03n" -> return R.drawable.clouds
        "04d" -> return R.drawable.broken
        "04n" -> return R.drawable.broken
        "09d" -> return R.drawable.rainy
        "09n" -> return R.drawable.rainy
        "10d" -> return R.drawable.sun_clouds_rain
        "10n" -> return R.drawable.moon_clouds_rain
        "11d" -> return R.drawable.storm
        "11n" -> return R.drawable.storm
        "13d" -> return R.drawable.clouds_snow
        "13n" -> return R.drawable.clouds_snow
        "50d" -> return R.drawable.mist
        "50n" -> return R.drawable.mist
        else -> return R.drawable.clouds
    }
}

fun Long.toDateTime(pattern: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.time.format.DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
            .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
    } else {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val netDate = Date(this * 1000)
        sdf.format(netDate)
    }
}

fun String.toDrawable2X() : Int {
    when (this) {
        "01d" -> return R.drawable.sun_2x
        "01n" -> return R.drawable.moon_2x
        "02d" -> return R.drawable.sun_clouds_2x
        "02n" -> return R.drawable.moon_clouds_2x
        "03d" -> return R.drawable.clouds_2x
        "03n" -> return R.drawable.clouds_2x
        "04d" -> return R.drawable.broken_2x
        "04n" -> return R.drawable.broken_2x
        "09d" -> return R.drawable.rainy_2x
        "09n" -> return R.drawable.rainy_2x
        "10d" -> return R.drawable.sun_clouds_rain_2x
        "10n" -> return R.drawable.moon_clouds_rain_2x
        "11d" -> return R.drawable.storm_2x
        "11n" -> return R.drawable.storm_2x
        "13d" -> return R.drawable.clouds_snow_2x
        "13n" -> return R.drawable.clouds_snow_2x
        "50d" -> return R.drawable.mist_2x
        "50n" -> return R.drawable.mist_2x
        else -> return R.drawable.cloud_placeholder
    }
}

/*fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.primary),
        ContextCompat.getColor(requireActivity(), R.color.on_primary),
        ContextCompat.getColor(requireActivity(), R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}*/

/*fun Activity.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(this@setupRefreshLayout, R.color.primary),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.on_primary),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}*/
