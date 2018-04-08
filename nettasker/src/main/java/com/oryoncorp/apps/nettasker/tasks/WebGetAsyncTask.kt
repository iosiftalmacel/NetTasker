package com.oryoncorp.apps.netload.tasks

import android.os.AsyncTask
import android.util.Log
import android.util.LruCache
import com.oryoncorp.apps.netload.requests.read.DataSource
import com.oryoncorp.apps.netload.requests.read.DownloadRequest
import java.io.BufferedInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class WebGetAsyncTask(private val netRequest: DownloadRequest<*>, private val memoryCache: LruCache<String, Any>) : AsyncTask<String, Void, Any>() {
    private var initialUrl = netRequest.url

    override fun doInBackground(vararg params: String): Any? {
        var urlConnection: HttpURLConnection? = null
        try {
            val uri = URL(netRequest.url)
            urlConnection = uri.openConnection() as HttpURLConnection

            val statusCode = urlConnection.responseCode
            if (statusCode != HttpsURLConnection.HTTP_OK) {
                return null
            }
            val inputStream = BufferedInputStream(urlConnection.inputStream)

            if (netRequest.context != null) {

                netRequest.onLoadFromWebInternal(inputStream)?.let {
                    if(netRequest.saveOnDisk)
                    {
                        val file = File(netRequest.context!!.cacheDir, netRequest.computeFileName())
                        netRequest.onSaveToDiskInternal(file, it)
                    }
                    if (netRequest.saveInMemory){
                        netRequest.onSaveToMemoryInternal(memoryCache, it)
                    }
                    return it
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (urlConnection != null)
                urlConnection.disconnect()
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect()
        }
        return null
    }

    override fun onPostExecute(data: Any?) {
        if(isCancelled || netRequest.url != initialUrl || netRequest.cancelled)
            return

        if (data == null)
            netRequest.onRequestError()
        else
            netRequest.onDataReceivedInternal(data, DataSource.Web)

    }
}