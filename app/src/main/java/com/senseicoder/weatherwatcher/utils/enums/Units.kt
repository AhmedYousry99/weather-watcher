package com.senseicoder.weatherwatcher.utils.enums


/**
 * Units of measurement.
 * If you do not use the units parameter,
 * standard units will be applied by default.
 * Temperature is available in Fahrenheit, Celsius and Kelvin units.
 * For temperature in Fahrenheit use units=imperial
 * For temperature in Celsius use units=metric
 * Temperature in Kelvin is used by default, no need to use units parameter in API call
*/
enum class Units {
    standard,
    metric,
    imperial
}