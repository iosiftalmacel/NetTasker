package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import android.util.LruCache
import java.io.*


open class JsonRequestBase(context: Context) : DownloadRequest<String>(context){
    override fun onLoadFromWeb(stream: InputStream): String? {
        val reader = BufferedReader(InputStreamReader(stream))

        var sResponse: String?
        var s = StringBuilder()
        do {
            sResponse = reader.readLine()
            s = s.append(sResponse)
        }
        while (sResponse != null)

        return s.toString()
    }

    override fun onLoadFromDisk(file: File): String? {
        val reader = BufferedReader(FileReader(file))
        var sResponse: String?
        var s = StringBuilder()

        do {
            sResponse = reader.readLine()
            s = s.append(sResponse)
        }
        while (sResponse != null)

        return s.toString()
    }

    override fun onLoadFromMemory(defaultCache: LruCache<String, Any>): String? {
        return defaultCache.get(url) as String?
    }

    override fun onSaveToDisk(file: File, data: String) {
        try {
            val writer = FileWriter(file)
            writer.append(data)
            writer.flush()
            writer.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onSaveToMemory(data: String, defaultCache: LruCache<String, Any>) {
        defaultCache.put(url, data)
    }

    override fun onDataReceived(data: String, source: DataSource) {
        if(source == DataSource.Web)
            loadedFromWeb = true
        else
            loadedFromCache = true

    }

}

