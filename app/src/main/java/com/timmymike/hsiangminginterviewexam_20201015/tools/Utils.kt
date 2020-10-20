package com.timmymike.hsiangminginterviewexam_20201015.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.timmymike.hsiangminginterviewexam_20201015.BuildConfig
import com.timmymike.hsiangminginterviewexam_20201015.R
import java.time.LocalDateTime


/**
 * GetRectangle BackGroundDrawable
 * */
fun getRectangleBg(context: Context, tldp: Int, trdp: Int, bldp: Int, brdp: Int, bgColorID: Int, strokeColorID: Int, strokeWidth: Int): GradientDrawable {
    val tl = getPixelFromDpByDevice(context, tldp)
    val tr = getPixelFromDpByDevice(context, trdp)
    val bl = getPixelFromDpByDevice(context, bldp)
    val br = getPixelFromDpByDevice(context, brdp)
    return createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), getPixelFromDpByDevice(context, strokeWidth), strokeColorID, GradientDrawable.RECTANGLE)
}

fun createShapeDrawable(context: Context, colorID: Int, radii: FloatArray?, strokeWidth: Int, strokeColorID: Int, gradientDrawableShape: Int): GradientDrawable {
    val gradientDrawable = GradientDrawable()
    gradientDrawable.setColor(context.getColor(colorID))
    if (radii != null) {
        gradientDrawable.cornerRadii = radii
    }
    if (strokeWidth != 0) {
        gradientDrawable.setStroke(strokeWidth, context.resources.getColor(strokeColorID))
    }

    if (gradientDrawableShape != 0) {
        gradientDrawable.shape = gradientDrawableShape
    }
    return gradientDrawable
}

fun getPixelFromDpByDevice(context: Context, dpSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val realSpSize = ((dpSize * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, realSpSize.toFloat(), context.resources.displayMetrics).toInt()
}

/**Glide Circle Img setting*/
private val options by lazy {
    RequestOptions()
        .transform(MultiTransformation<Bitmap>(CenterCrop(), CircleCrop()))
        .priority(Priority.NORMAL)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
}

@BindingAdapter("app:imageUrl")
fun bindImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url ?: R.drawable.ic_person_outline_black_24dp)
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
