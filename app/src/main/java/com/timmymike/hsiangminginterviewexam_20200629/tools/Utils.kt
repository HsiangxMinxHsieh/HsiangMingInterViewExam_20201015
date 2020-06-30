package com.timmymike.hsiangminginterviewexam_20200629.tools

import android.os.Build
import android.util.Log
import com.timmymike.hsiangminginterviewexam_20200629.BuildConfig
import java.time.LocalDateTime

/**
 *  log all data in collection
 *  @author Hsiang Ming     *
 **/
fun Collection<Any>.logiAllData(TAG: String = "printData") {
    for (data in this) {
        logi(TAG, data)
    }
}

fun logi(tag: String, log: Any) {

    if (BuildConfig.DEBUG_MODE) Log.i(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "TIME"
        }
    }
}


fun loge(tag: String, log: Any) {
    if (BuildConfig.DEBUG_MODE) Log.e(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "TIME"
        }
    }
}
