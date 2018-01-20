package com.wverlaek.cambridgehack.util

/**
 * Created by WVerl on 20-1-2018.
 */
interface Listener<T> {
    fun onComplete(result: T)
}