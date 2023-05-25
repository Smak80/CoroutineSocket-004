package ru.smak.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Client(host: String = "localhost", port: Int = 5004) {

    private val socket = CoroutineSocket(host, port)
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    fun start(){
        scope.launch {
            socket.connect()
            val data = "Привет!"
            println("Written: ${socket.write(data.encodeToByteArray())} bytes")
        }
    }
}