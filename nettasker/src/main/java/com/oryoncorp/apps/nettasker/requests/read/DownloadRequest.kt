package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import android.util.LruCache
import com.oryoncorp.apps.netload.requests.NetRequest
import java.io.File
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.net.URLEncoder

enum class RequestFrom{ Web, Cache, CacheAndWeb }
enum class DataSource{ Web, Memory, Disk }

abstract class DownloadRequest<T>(context: Context) : NetRequest(context){
    open lateinit var url: String

    var saveInMemory: Boolean = false
    var saveOnDisk: Boolean = false
    var from: RequestFrom = RequestFrom.Web

    protected var loadedFromCache : Boolean = false
    protected var loadedFromWeb : Boolean = false

    protected abstract fun onLoadFromWeb(stream: InputStream): T?
    protected abstract fun onLoadFromDisk(file: File) : T?
    protected abstract fun onLoadFromMemory(defaultCache: LruCache<String, Any>) : T?
    protected abstract fun onSaveToDisk(file: File, data: T)
    protected abstract fun onSaveToMemory(data: T, defaultCache: LruCache<String, Any>)
    protected abstract fun onDataReceived(data: T, source: DataSource)

    internal fun shouldGetFromCache() : Boolean = from == RequestFrom.Cache || from == RequestFrom.CacheAndWeb
    internal fun shouldGetFromWeb() : Boolean = from == RequestFrom.Web || from == RequestFrom.CacheAndWeb


    override fun hasFinished(): Boolean {
        if(from == RequestFrom.Web && loadedFromWeb)
            return true
        if(from == RequestFrom.Cache && (loadedFromCache || loadedFromWeb))
            return true
        else if(from == RequestFrom.CacheAndWeb && loadedFromWeb)
            return true

        return super.hasFinished()
    }

    internal open fun hasMemoryCache(defaultCache: LruCache<String, Any>) : Boolean{
        return defaultCache[url] != null
    }

    internal open fun hasDiskCache(context: WeakReference<Context>) : Boolean{
        return context.get() != null && File(context.get()!!.cacheDir, computeFileName()).exists()
    }

    internal open fun computeFileName() : String?{
        return try {
            URLEncoder.encode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            null
        }
    }


    internal fun onLoadFromWebInternal(stream: InputStream): T?
            = onLoadFromWeb(stream)

    internal fun onLoadFromDiskInternal(file: File) : T?
            = onLoadFromDisk(file)

    internal fun onLoadFromMemoryInternal(defaultCache: LruCache<String, Any>)
            = onLoadFromMemory(defaultCache)

    internal fun onSaveToDiskInternal(file: File, data: Any)
            = onSaveToDisk(file, cast(data))

    internal fun onSaveToMemoryInternal(defaultCache: LruCache<String, Any>, data: Any)
            = onSaveToMemory(cast(data), defaultCache)

    internal fun onDataReceivedInternal(data: Any, source: DataSource){
        if(!cancelled && context != null) onDataReceived(cast(data), source)
    }

    @Suppress("UNCHECKED_CAST")
    private fun cast(obj: Any?): T {
        return (obj as? T)!!
    }
}