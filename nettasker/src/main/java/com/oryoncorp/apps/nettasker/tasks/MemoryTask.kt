package com.oryoncorp.apps.netload.tasks

import android.util.LruCache
import com.oryoncorp.apps.netload.requests.read.DataSource
import com.oryoncorp.apps.netload.requests.read.DownloadRequest

internal class MemoryTask(private val request: DownloadRequest<*>, private val defaultCache: LruCache<String, Any>) : Runnable{
    override fun run() {
        val data = request.onLoadFromMemoryInternal(defaultCache)

        if (data != null)
            request.onDataReceivedInternal(data, DataSource.Memory)
        else
            request.onRequestError()
    }
}