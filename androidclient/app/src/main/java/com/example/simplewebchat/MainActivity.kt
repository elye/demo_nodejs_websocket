package com.example.simplewebchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplewebchat.EchoWebSocketListener.Companion.NORMAL_CLOSURE_STATUS
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : AppCompatActivity() {
    private val message : Button by lazy { findViewById(R.id.message) }
    private val output: TextView by lazy { findViewById(R.id.output) }
    private val entryText: EditText by lazy { findViewById(R.id.text_entry) }
    private val client by lazy {
        OkHttpClient()
    }
    private var ws: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message.setOnClickListener {
            ws?.apply {
                val text = entryText.text.toString()
                output("ME: $text")
                send(text)
                entryText.text.clear()
            } ?: ping("Error: Restart the App to reconnect")
        }
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    private fun start() {
        val request: Request = Request.Builder().url("ws://10.0.2.2:8082/").build()
        val listener = EchoWebSocketListener(this::output, this::ping) { ws = null }
        ws = client.newWebSocket(request, listener)
    }

    private fun stop() {
        ws?.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    override fun onDestroy() {
        super.onDestroy()
        client.dispatcher.executorService.shutdown()
    }

    private fun output(txt: String) {
        runOnUiThread {
            "${output.text}\n$txt".also { output.text = it }
        }
    }

    private fun ping(txt: String) {
        runOnUiThread {
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
        }
    }
}