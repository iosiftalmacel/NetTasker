package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import android.graphics.Bitmap

class BitmapDownload(context: Context, configure: (BitmapDownload) -> Unit) : BitmapRequestBase(context){
    override lateinit var url: String
    lateinit var onComplete: (Bitmap) -> Unit
    var onError: (() -> Unit)? = null

    init {
        configure(this)
        require(::url.isInitialized)
        require(::onComplete.isInitialized)
    }

    override fun onDataReceived(data: Bitmap, source: DataSource) {
        super.onDataReceived(data, source)
        onComplete(data)
    }

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        onError?.invoke()
    }
}

