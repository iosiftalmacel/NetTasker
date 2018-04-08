package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject


class JsonDownload(context: Context, configure: (JsonDownload) -> Unit) : JsonRequestBase(context){
    override lateinit var url: String
    lateinit var onCompleteObj: (JSONObject) -> Unit
    lateinit var onCompleteArray: (JSONArray) -> Unit
    var onError: (() -> Unit)? = null

    init {
        configure(this)
        require(::url.isInitialized)
        require(::onCompleteObj.isInitialized && ::onCompleteArray.isInitialized)
    }

    override fun onDataReceived(data: String, source: DataSource) {
        super.onDataReceived(data, source)
        try {
            onCompleteObj(JSONObject(data))
            return
        }catch (e: Exception){
            e.printStackTrace()
        }

        try {
            onCompleteArray(JSONArray(data))
            return
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        onError?.invoke()
    }
}

