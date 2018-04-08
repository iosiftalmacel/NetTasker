package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import android.util.LruCache
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class FileDownload(context: Context, configure: (FileDownload) -> Unit) : DownloadRequest<File>(context){
    override lateinit var url: String
    lateinit var path: String
    lateinit var onComplete: (File) -> Unit
    var onError: (() -> Unit)? = null
    var fileName : String? = null

    init {
        configure(this)
        require(::url.isInitialized)
        require(::path.isInitialized)
        require(::onComplete.isInitialized)
    }

    override fun onLoadFromWeb(stream: InputStream): File? {
        try {
            fileName = fileName ?: URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url))
            val fileOutput = File(path, fileName)

            if(!fileOutput.exists()) {
                File(path).mkdirs()
                fileOutput.createNewFile()
            }

            val output = FileOutputStream(fileOutput)
            val data = ByteArray(1024)
            var count: Int

            do {
                count = stream.read(data)
                if(count != -1) output.write(data, 0, count)
            } while (count != -1)

            output.flush()
            output.close()
            return fileOutput
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    override fun onLoadFromDisk(file: File): File? {
        return if(file.exists())
            file
        else
            null
    }

    override fun onLoadFromMemory(defaultCache: LruCache<String, Any>): File? {
        return defaultCache.get(url) as File?
    }

    override fun onSaveToDisk(file: File, data: File) {

    }

    override fun onSaveToMemory(data: File, defaultCache: LruCache<String, Any>) {
        defaultCache.put(url, data)
    }

    override fun onDataReceived(data: File, source: DataSource) {
        if(source == DataSource.Web)
            loadedFromWeb = true
        else
            loadedFromCache = true

        onComplete(data)
    }

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        onError?.invoke()
    }
}

