package com.oryoncorp.apps.netload

import android.content.Context
import android.os.Handler
import android.util.LruCache
import com.oryoncorp.apps.netload.requests.NetRequest
import com.oryoncorp.apps.netload.requests.read.DownloadRequest
import com.oryoncorp.apps.netload.requests.write.UploadRequest
import com.oryoncorp.apps.netload.tasks.DiskGetAsyncTask
import com.oryoncorp.apps.netload.tasks.MemoryTask
import com.oryoncorp.apps.netload.tasks.WebGetAsyncTask
import com.oryoncorp.apps.netload.tasks.WebPostAsyncTask
import java.lang.ref.WeakReference
import java.util.*

object NetTasker {
    //public vars
    var maxActiveRequests: Int = 3

    //private vars
    private lateinit var contextRef: WeakReference<Context>

    private lateinit var activeRequests: MutableList<NetRequest>
    private lateinit var waitingRequests: MutableList<NetRequest>
    private lateinit var memoryCache: LruCache<String, Any>
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var inited: Boolean = false

    fun request(request: NetRequest) {
        request.context?.let {
            initialise(it)
            addRequest(request)
            request.onRequestReady()
        }
    }

    fun cancelRequest(request: NetRequest){
        request.onCancelRequest()
        activeRequests.remove(request)
        waitingRequests.remove(request)
    }

    fun cancelRequests(context: Context){
        for (index in 0 until activeRequests.lastIndex) {
            if(activeRequests[index].context == context){
                activeRequests[index].onCancelRequest()
                activeRequests.removeAt(index)
            }
        }
        for (index in 0 until waitingRequests.lastIndex) {
            if(waitingRequests[index].context == context){
                waitingRequests[index].onCancelRequest()
                waitingRequests.removeAt(index)
            }
        }
    }

    fun cancelAllRequests(){
        for (index in 0 until activeRequests.lastIndex) {
            activeRequests[index].onCancelRequest()
            activeRequests.removeAt(index)
        }
        for (index in 0 until waitingRequests.lastIndex) {
            waitingRequests[index].onCancelRequest()
            waitingRequests.removeAt(index)
        }
    }

    private fun initialise(context: Context) {
        if (inited) return
        inited = true

        contextRef = WeakReference(context)
        activeRequests = ArrayList()
        waitingRequests = ArrayList()

        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = LruCache(cacheSize)

        handler = Handler()

        runnable = Runnable {
            val updateRate = if (activeRequests.size > 0) 50 else 150

            for (index in activeRequests.lastIndex downTo 0) {
                if(activeRequests[index].isTimeout())
                    activeRequests[index].onRequestError()

                if(activeRequests[index].hasFinished())
                    activeRequests.removeAt(index)
            }

            if (activeRequests.size < maxActiveRequests && contextRef.get() != null) {
                val delta = Math.min(maxActiveRequests - activeRequests.size, waitingRequests.size)

                for (index in 0 until delta) {
                    val request = getNextRequest()
                    activeRequests.add(request)
                    request.onRequestStart()

                    when(request){
                        is UploadRequest -> onUploadRequestStart(request)
                        is DownloadRequest<*> -> onDownloadRequestStart(request)
                    }
                }
            }
            updateRunnable(updateRate)
        }
        handler.post(runnable)
    }

    private fun updateRunnable(delay: Int) = handler.postDelayed(runnable, delay.toLong())

    private fun addRequest(netRequest: NetRequest) {
        waitingRequests.add(netRequest)
        updateRunnable(0)
    }

    private fun getNextRequest(): NetRequest {
        val request = waitingRequests.last()
        waitingRequests.removeAt(waitingRequests.lastIndex)
        return request
    }

    private fun onDownloadRequestStart(request: DownloadRequest<*>) {
        var hasCache = false
        if (request.shouldGetFromCache()) {
            if (request.hasMemoryCache(memoryCache)) {
                MemoryTask(request, memoryCache).run()
                hasCache = true
            } else if (request.hasDiskCache(contextRef)) {
                DiskGetAsyncTask(contextRef, request).execute()
                hasCache = true
            }
        }

        if (request.shouldGetFromWeb() || !hasCache)
            WebGetAsyncTask(request, memoryCache).execute()
    }

    private fun onUploadRequestStart(request: UploadRequest) {
         WebPostAsyncTask(request).execute()
    }
}

