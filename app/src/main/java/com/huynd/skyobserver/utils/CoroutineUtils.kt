package com.huynd.skyobserver.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HuyND on 7/3/2019.
 */
class CoroutineUtils {

    init {
        throw AssertionError()
    }

    companion object {
        fun <T> startComputingThread(computingThreadBlock: suspend CoroutineScope.() -> T,
                                     mainThreadBlock: (t: T) -> Unit) {
            GlobalScope.launch {
                val result = withContext(Dispatchers.Default) {
                    computingThreadBlock()
                }
                mainThreadBlock(result)
            }
        }
    }
}
