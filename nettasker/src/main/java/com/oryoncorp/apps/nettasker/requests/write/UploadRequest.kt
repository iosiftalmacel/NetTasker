package com.oryoncorp.apps.netload.requests.write

import android.content.Context
import com.oryoncorp.apps.netload.requests.NetRequest
import java.io.DataOutputStream

enum class UploadType{ POST, PUT }

abstract class UploadRequest(context: Context) : NetRequest(context){
    open lateinit var url: String
    var type = UploadType.POST
    var properties : Array<Pair<String, String>>? = null

    protected abstract fun onStartUpload(stream: DataOutputStream)
    protected abstract fun onUploadFinished()

    internal fun onUploadFinishedInternal(){
        if(!cancelled && context != null) onUploadFinished()
    }

    internal fun onStartUploadInternal(stream: DataOutputStream)
            = onStartUpload(stream)

}