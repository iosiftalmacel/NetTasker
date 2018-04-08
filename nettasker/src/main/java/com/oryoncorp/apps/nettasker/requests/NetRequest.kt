package com.oryoncorp.apps.netload.requests

import android.content.Context
import android.util.Log
import java.lang.ref.WeakReference

abstract class NetRequest(context: Context) {
    internal var context : Context? = null
        get() = contextRef.get()
        private set

    private var contextRef: WeakReference<Context> = WeakReference(context)
    protected var startTime: Long = -1
    protected var readyTime: Long = -1
    protected var hadError : Boolean = false
    internal var cancelled : Boolean = false
    open var timeout : Int = 8000

    internal open fun onRequestReady(){
        readyTime = System.currentTimeMillis()
    }

    internal open fun onRequestStart(){
        startTime = System.currentTimeMillis()
        cancelled = false
    }

    internal open fun onRequestError(message : Any? = null){
        startTime = -1
        hadError = true
    }

    internal open fun hasFinished(): Boolean {
        return isTimeout() || hadError || cancelled
    }
    internal open fun isTimeout(): Boolean{
        return System.currentTimeMillis() - startTime > timeout
    }
    internal open fun onCancelRequest(){
        cancelled = true
    }
}