package com.google.ar.core.examples.kotlin.helloar

import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class UDPmanager(ip: String, port: Int) {
    var udpSocket: DatagramSocket
    var serverAddr: InetAddress
    var port: Int

    init {
        udpSocket = DatagramSocket(port)
        serverAddr = InetAddress.getByName(ip)
        this.port = port
    }
    fun close() {
        udpSocket.close()
    }

    suspend fun send(data: ByteArray): Boolean {
        try {
            val packet = DatagramPacket(data, data.size, serverAddr, port)
            udpSocket.send(packet)
        } catch (e: SocketException) {
            Log.e("Udp:", "Socket Error:", e)
            return false
        } catch (e: IOException) {
            Log.e("Udp Send:", "IO Error:", e)
            return false
        }
        return true
    }

    suspend fun send6dof(data: DoubleArray) {
        val ba = ByteBuffer.allocate(8 * 6).order(ByteOrder.LITTLE_ENDIAN)      // 6 double
        for (i in 0..5) {
            ba.putDouble(i*8, data[i])
        }
        send(ba.array())
    }
}