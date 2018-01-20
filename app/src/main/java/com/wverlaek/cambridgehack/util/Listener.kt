package com.wverlaek.cambridgehack.util

/**
 * Created by WVerl on 20-1-2018.
 */
@FunctionalInterface
interface Listener<in T> {
    fun onComplete(result: T)
    fun onError()
}