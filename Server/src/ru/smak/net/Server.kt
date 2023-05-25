package ru.smak.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Server(port: Int) {
    private val sSocket = CoroutineServerSocket(port)
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    init{
        scope.launch {
            val s = sSocket.accept()
            val d = s.read()
            println(d.decodeToString())
            s.close()
            sSocket.close()
        }
    }
}