package com.timmymike.hsiangminginterviewexam_20200629.tools

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.timmymike.hsiangminginterviewexam_20200629.BuildConfig
import com.timmymike.hsiangminginterviewexam_20200629.R
import java.time.LocalDateTime


/**Glide Circle Img setting*/
private val options by lazy {
    RequestOptions()
        .transform(MultiTransformation<Bitmap>(CenterCrop(), CircleCrop()))
        .priority(Priority.NORMAL)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
}

@BindingAdapter("app:imageUrl")
fun bindImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .apply(options)
        .placeholder(R.drawable.ic_person_outline_black_24dp)
        .into(imageView)
}


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
