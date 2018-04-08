package com.oryoncorp.apps.netload.requests.read

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView
import java.lang.ref.WeakReference

class ImageDownload(image: ImageView, configure: (ImageDownload) -> Unit) : BitmapRequestBase(image.context){
    private var target : WeakReference<ImageView> = WeakReference(image)
    override lateinit var url: String

    var placeholderRes : Int? = null
    var errorRes : Int? = null
    var fadeDuration : Int = 0

    var placeholderDrawable : Drawable? = null
    var errorDrawable : Drawable? = null

    init {
        configure(this)
        require(::url.isInitialized)
    }

    override fun onRequestReady() {
        super.onRequestReady()
        errorRes?.let {
            target.get()?.setImageResource(it)
            errorDrawable = target.get()?.drawable
        }
        placeholderRes?.let {
            target.get()?.setImageResource(it)
            placeholderDrawable = target.get()?.drawable
        }

        if(placeholderDrawable != null)
            target.get()?.setImageDrawable(placeholderDrawable)
        else
            target.get()?.setImageDrawable(null)
    }

    override fun onDataReceived(data: Bitmap, source: DataSource) {
        super.onDataReceived(data, source)
        target.get()?.let {
            if(fadeDuration != 0){
                val bitmapDrawable = BitmapDrawable(it.context.resources, data)
                val transitionDrawable = TransitionDrawable(arrayOf(placeholderDrawable, bitmapDrawable))
                transitionDrawable.startTransition(fadeDuration)
                it.setImageDrawable(transitionDrawable)
            }else{
                it.setImageBitmap(data)
            }
        }
    }

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        errorDrawable?.let { target.get()?.setImageDrawable(it) }
    }


}