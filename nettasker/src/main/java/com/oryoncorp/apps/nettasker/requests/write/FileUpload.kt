package com.oryoncorp.apps.netload.requests.write

import android.content.Context
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream

class FileUpload(context: Context, configure: (FileUpload) -> Unit) : UploadRequest(context){
    override lateinit var url : String
    lateinit var filepath : String
    lateinit var onFinish: (success: Boolean) -> Unit

    init {
        configure(this)
        require(::url.isInitialized)
        require(::filepath.isInitialized)
        require(::onFinish.isInitialized)
    }

    private var lineEnd = "\r\n"
    private var twoHyphens = "--"
    private var boundary = "*****"

    private var bytesRead: Int = 0
    private var bytesAvailable:Int = 0
    private var bufferSize:Int = 0
    private var buffer: ByteArray? = null
    private var maxBufferSize = 1 * 1024 * 1024
    private var selectedFile = File(filepath)

    override fun onStartUpload(stream: DataOutputStream) {
        stream.writeBytes(twoHyphens + boundary + lineEnd)
        stream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"$filepath\"$lineEnd")
        stream.writeBytes(lineEnd)

        val fileInputStream = FileInputStream(selectedFile)

        bytesAvailable = fileInputStream.available()
        bufferSize = Math.min(bytesAvailable,maxBufferSize)
        buffer = ByteArray(bufferSize)

        bytesRead = fileInputStream.read(buffer,0,bufferSize);

        while (bytesRead > 0){
            stream.write(buffer,0,bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable,maxBufferSize)
            bytesRead = fileInputStream.read(buffer,0,bufferSize)
        }

        stream.writeBytes(lineEnd)
        stream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        fileInputStream.close()
    }

    override fun onUploadFinished() = onFinish(true)

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        onFinish(false)
    }
}

