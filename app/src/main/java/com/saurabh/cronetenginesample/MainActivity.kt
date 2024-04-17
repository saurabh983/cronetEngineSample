package com.saurabh.cronetenginesample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.saurabh.cronetenginesample.ui.theme.CronetEngineSampleTheme
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    var engine : CronetEngine ?= null

    private val mutableLis = mutableStateListOf<ResponseModel>()

    val callback = RequestCallback{ success, response, exception->
        run {
            if (success) {
                val objectList = Gson().fromJson(response, Array<ResponseModel>::class.java).asList()

                mutableLis.addAll(objectList)
                Log.e("response", response)
            } else {
                exception?.printStackTrace()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        engine = CronetEngineInstance.getCronetInstance(this)

        val request = engine?.newUrlRequestBuilder("https://jsonplaceholder.typicode.com/posts",
            callback,
            Executors.newFixedThreadPool(1))

        //to add headers
//        request.headers.forEach {
//            cronetRequestBuilder.addHeader(it.key, it.value)
//        }

        request?.build()?.start()

        setContent {
            CronetEngineSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(content = {
                        items(mutableLis){
                            Row (Modifier.padding(16.dp)){
                                Text(text = it.title!!, color = Color.Black)
                            }
                        }
                    })
                }
            }
        }
    }
}