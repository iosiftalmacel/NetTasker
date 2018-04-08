package com.oryoncorp.apps.netload.requests.read

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject


class ModelDownload<T>(context: Context, tClass: Class<T>, configure: (ModelDownload<T>) -> Unit) : JsonRequestBase(context){
    override lateinit var url: String
    lateinit var onComplete: (T) -> Unit
    var model : Class<T> = tClass
    var onError: (() -> Unit)? = null

    init {
        configure(this)
        require(::url.isInitialized)
        require(::onComplete.isInitialized)
    }

    override fun onDataReceived(data: String, source: DataSource) {
        super.onDataReceived(data, source)

        try {
            val obj = JSONObject(data)
            val converted = jsonToClass(model, obj.toString())
            onComplete(converted)
            return
        }catch (e: Exception){
            e.printStackTrace()
        }

        try {
            val objs = JSONArray(data)
            val converted = jsonToClass(model, objs.toString())
            onComplete(converted)
            return
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onRequestError(message: Any?) {
        super.onRequestError(message)
        onError?.invoke()
    }

    private fun <T> jsonToClass(tClass: Class<T>, json: String): T {
        val parser = JsonParser()
        val mJson = parser.parse(json)
        return Gson().fromJson(mJson, tClass)
    }

}

