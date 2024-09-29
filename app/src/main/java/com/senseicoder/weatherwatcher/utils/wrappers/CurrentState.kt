package com.senseicoder.weatherwatcher.utils.wrappers

sealed class CurrentState<T>{
    data class Success<T>(val data: T) : CurrentState<T>()
    data class Failure<T>(val msg: String) : CurrentState<T>()
    class Loading<T> : CurrentState<T>()
}