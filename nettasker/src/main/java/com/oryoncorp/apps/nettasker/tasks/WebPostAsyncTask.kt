package com.oryoncorp.apps.netload.tasks

import android.os.AsyncTask
import android.util.Log
import com.oryoncorp.apps.netload.requests.write.UploadRequest
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL


internal class WebPostAsyncTask(private val netRequest: UploadRequest) : AsyncTask<String, Void, Any?>() {
    override fun doInBackground(vararg params: String): Any? {
        var urlConnection: HttpURLConnection? = null
        try {
            val uri = URL(netRequest.url)
            urlConnection = uri.openConnection() as HttpURLConnection
            urlConnection.useCaches = false
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.requestMethod = netRequest.type.toString()

            netRequest.properties?.let{
                it.forEach {
                    urlConnection.setRequestProperty(it.first, it.second)
                }
            }

            val dataOutputStream = DataOutputStream(urlConnection.outputStream)
            netRequest.onStartUploadInternal(dataOutputStream)


            val r = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val total = StringBuilder()
            var line: String? = null

            do {
                line = r.readLine()
                total.append(line)
            }
            while (line != null)

            r.close()
            println(total.toString())


            return urlConnection.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
            if (urlConnection != null)
                urlConnection.disconnect()
            return e.message
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect()
        }
    }

    override fun onPostExecute(data: Any?) {
        if(data is Int && data == HTTP_OK)
            netRequest.onUploadFinishedInternal()
        else
            netRequest.onRequestError(data)
    }
}