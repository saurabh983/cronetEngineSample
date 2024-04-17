package com.saurabh.cronetenginesample

import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataSink
import org.chromium.net.UrlRequest
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class PostApiRequestUsingCronet (private val cronetEngine: CronetEngine) {

    fun makePostRequest(url: String, requestBody: ByteArray, callback: UrlRequest.Callback) {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            callback,
            Executors.newFixedThreadPool(1)
        ).apply {
            setHttpMethod("POST")
            addHeader("Content-Type", "application/json") // Set appropriate Content-Type
            setUploadDataProvider(
                ByteArrayUploadDataProvider(requestBody),
                Executors.newFixedThreadPool(1)
            )
        }
        val request = requestBuilder.build()
        request.start()
    }

    private class ByteArrayUploadDataProvider(private val data: ByteArray) :
        UploadDataProvider() {

        private var inputStream: ByteArrayInputStream = ByteArrayInputStream(data)

        override fun getLength(): Long {
            return data.size.toLong()
        }

        override fun read(uploadDataSink: UploadDataSink?, byteBuffer: ByteBuffer?) {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val readLength = inputStream.read(buffer)
            if (readLength == -1) {
                uploadDataSink?.onReadSucceeded(true)
                return
            }
            val byteBuffer = ByteBuffer.wrap(buffer, 0, readLength)
            try {
                uploadDataSink?.onRewindSucceeded()
            } catch (e: IOException) {
                uploadDataSink?.onReadError(e)
            }
        }


        override fun rewind(uploadDataSink: UploadDataSink) {
            uploadDataSink.onRewindSucceeded()
        }
    }
}
