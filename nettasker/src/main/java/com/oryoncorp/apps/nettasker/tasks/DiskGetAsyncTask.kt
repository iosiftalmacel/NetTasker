package com.oryoncorp.apps.netload.tasks

import android.content.Context
import android.os.AsyncTask
import com.oryoncorp.apps.netload.requests.read.DataSource
import com.oryoncorp.apps.netload.requests.read.DownloadRequest
import java.io.File
import java.lang.ref.WeakReference

internal class DiskGetAsyncTask(private val contextRef: WeakReference<Context>, private val netRequest: DownloadRequest<*>) : AsyncTask<String, Void, Any>() {
    var initialUrl = netRequest.url

    override fun doInBackground(vararg params: String): Any? {
        val context : Context = contextRef.get() ?: return null
        val fileName = netRequest.computeFileName()

        if(fileName != null){
            val file = File(context.cacheDir, fileName)
            return netRequest.onLoadFromDiskInternal(file)
        }
        return null
    }

    override fun onPostExecute(data: Any?) {
        if(isCancelled || netRequest.url != initialUrl || netRequest.cancelled)
            return

        if (data == null)
            netRequest.onRequestError()
        else
            netRequest.onDataReceivedInternal(data, DataSource.Disk)
    }
}