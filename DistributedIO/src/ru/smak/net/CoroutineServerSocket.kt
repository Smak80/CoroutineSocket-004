package ru.smak.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import kotlin.coroutines.suspendCoroutine

class CoroutineServerSocket(port: Int) {
    private val channel = AsynchronousServerSocketChannel.open()
    private val address = InetSocketAddress(port)
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    init{
        channel.bind(address)
    }
    suspend fun accept() = scope.async{
        val sch = suspendCoroutine {
            channel.accept(it, AsyncHandler())
        }
        CoroutineSocket(sch)
    }.await()

    fun close() = channel.close()
}