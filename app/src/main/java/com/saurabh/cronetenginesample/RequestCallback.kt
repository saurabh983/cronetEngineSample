package com.saurabh.cronetenginesample

import android.util.Log
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels

open class RequestCallback(val onResponse: (Boolean, String, Exception?)-> Unit): UrlRequest.Callback() {

    private var responseHeaders : Map<String, List<String>>? = null

    private val mBytesReceived = ByteArrayOutputStream()
    private val mReceiveChannel = Channels.newChannel(mBytesReceived)

    private var mException: IOException? = null
    private var mResponse : String? = null

    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        val httpStatusCode = info?.httpStatusCode
        if (httpStatusCode == 200) {
            // The request was fulfilled. Start reading the response.
            request?.read(ByteBuffer.allocateDirect(32 * 1024))
        } else if (httpStatusCode == 503) {
            // The service is unavailable. You should still check if the request
            // contains some data.
            request?.read(ByteBuffer.allocateDirect(32 * 1024))
        }
        responseHeaders = info?.allHeaders
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        byteBuffer!!.flip()

        try {
            mReceiveChannel.write(byteBuffer)
        } catch (e: IOException) {
            throw e
        }

        byteBuffer.clear()

        request!!.read(byteBuffer)
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        mResponse = String(mBytesReceived.toByteArray())
//        mResponseCondition.open();
//        Log.e("Response", mResponse!!)
        onResponse(true, mResponse!!, null)
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        val e = IOException("Cronet Exception Occurred", error)
        mException = e
        onResponse(false, "", mException)
//        mResponseCondition.open()
    }
}