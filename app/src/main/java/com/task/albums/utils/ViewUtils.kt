package com.task.albums.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.task.albums.R
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.*

object ViewUtils {

    fun Context.showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            FirebaseCrashlytics.getInstance().recordException(e)
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

    fun equals(bitmap1: Bitmap, bitmap2: Bitmap): Boolean {
        return try {
            val buffer1: ByteBuffer = ByteBuffer.allocate(bitmap1.height * bitmap1.rowBytes)
            bitmap1.copyPixelsToBuffer(buffer1)
            val buffer2: ByteBuffer = ByteBuffer.allocate(bitmap2.height * bitmap2.rowBytes)
            bitmap2.copyPixelsToBuffer(buffer2)
            Arrays.equals(buffer1.array(), buffer2.array())
        } catch (e: Exception) {
            Timber.d("Bitmap Comparison Exception $e")
            false
        }
    }

}