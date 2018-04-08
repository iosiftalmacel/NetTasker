package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

open class BitmapRequestBase(context: Context) : DownloadRequest<Bitmap>(context){
    override fun onLoadFromWeb(stream: InputStream): Bitmap? {
        return BitmapFactory.decodeStream(stream)
    }

    override fun onLoadFromDisk(file: File): Bitmap? {
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun onLoadFromMemory(defaultCache: LruCache<String, Any>): Bitmap? {
        return defaultCache.get(url) as Bitmap?
    }

    override fun onSaveToDisk(file: File, data: Bitmap) {
        try {
            val out = FileOutputStream(file)
            data.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e : Exception ) {
            e.printStackTrace()
        }
    }

    override fun onSaveToMemory(data: Bitmap, defaultCache: LruCache<String, Any>) {
        defaultCache.put(url, data)
    }

    override fun onDataReceived(data: Bitmap, source: DataSource) {
        if(source == DataSource.Web)
            loadedFromWeb = true
        else
            loadedFromCache = true
    }

}

