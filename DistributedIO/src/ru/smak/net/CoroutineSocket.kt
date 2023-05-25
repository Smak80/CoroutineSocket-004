package ru.smak.net

import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoroutineSocket(ach: AsynchronousSocketChannel) {
    private val channel: AsynchronousSocketChannel
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var host: String = "localhost"
    private var port = 0
    var isConnected = false
        private set

    init{
        channel = ach
    }

    constructor(host: String, port: Int) : this(
        AsynchronousSocketChannel.open()
    ){
        this.host = host
        this.port = port
    }
    suspend fun connect() = scope.launch {
        suspendCoroutine {
            val address = InetSocketAddress(host, port)
            channel.connect(address, it, AsyncHandler())
        }
        isConnected = true
    }.join()

    suspend fun write(bytes: ByteArray) = scope.async{
        val buffer = ByteBuffer.allocate(bytes.size + 4)
        buffer.putInt(bytes.size)
        buffer.put(bytes)
        buffer.flip()
        suspendCoroutine {
            channel.write(buffer,it, AsyncHandler())
        }
    }.await()
    suspend fun read() = scope.async {
        var buffer = ByteBuffer.allocate(4)
        suspendCoroutine {
            channel.read(buffer, it, AsyncHandler())
        }
        buffer.flip()
        val size = buffer.int
        buffer = ByteBuffer.allocate(size)
        suspendCoroutine {
            channel.read(buffer, it, AsyncHandler())
        }
        val ba = ByteArray(size)
        buffer.flip()
        buffer.get(ba)
        ba
    }.await()

    fun close() = channel.close()
}