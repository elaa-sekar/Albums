package com.task.albums.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.task.albums.R
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.*

object ViewUtils {

    fun Context.showMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun Context.showMessageInSnackBar(view: View, message: String?) {
        if (!message.isNullOrEmpty()) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun AppCompatTextView.setTextColor(isSelected: Boolean, source1: Int, source2: Int) {
        setTextColor(
            ContextCompat.getColor(
                context, if (isSelected) source1 else source2
            )
        )
    }

    fun View.setBackground(isSelected: Boolean, source1: Int, source2: Int) {
        background = ContextCompat.getDrawable(this.context, if (isSelected) source1 else source2)
    }

    fun ImageView.loadImage(url: String?) {
        try {
            url?.let { imageUrl ->
                //Timber.d("Image URl $url")
                val mOptions: RequestOptions = RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)

                Glide.with(this).asBitmap().apply(mOptions)
                    .load(imageUrl)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            if (isNoImageAttached() || isNewImageResource(resource)) {
                                alpha = 0.0f
                                resource?.let { setImageBitmap(resource) }
                                animate().alpha(1.0f).duration = 700
                            }
                            return true
                        }
                    })
                    .error(R.drawable.image_album_placeholder)
                    .into(this)
            }
        } catch (e: Exception) {
            Timber.d("Glide Image Loading Exception $e")
        }
    }

    private fun ImageView.isNoImageAttached(): Boolean {
        return !(drawable != null && drawable is BitmapDrawable)
    }


    private fun ImageView.isNewImageResource(newBitmap: Bitmap?): Boolean {
        (drawable as? BitmapDrawable?)?.bitmap?.let { existingImage ->
            newBitmap?.let { newImage -> return existingImage != newImage }
        }
        return true
    }

}