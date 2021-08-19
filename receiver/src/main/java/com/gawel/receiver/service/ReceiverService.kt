package com.gawel.receiver.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.io.*
import java.net.*

import android.net.wifi.WifiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "ReceiverService"

class ReceiverService() : Service() {

    private lateinit var serverSocket: ServerSocket
    private lateinit var connectionSocket: Socket

    private var isClientConnected = false

    fun getIpAddress(): String? {
        NetworkInterface.getNetworkInterfaces().iterator().forEach { networkInterface ->
            networkInterface.inetAddresses.iterator().forEach { inetAddress ->
                val addrs: String
                if (!inetAddress.isLoopbackAddress) {
                    addrs = inetAddress.hostAddress
                    val isIPv4: Boolean = addrs.indexOf(':') < 0
                    Log.d(TAG, "getIpAddress: found: $addrs")
                    if (isIPv4)
                        return addrs
                }
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun getBroadcastAddress(): InetAddress? {
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val dhcp = wifi.dhcpInfo
        // handle null somehow
        val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        return InetAddress.getByAddress(quads)
    }

    override fun onCreate() {
        val broadCastJob = Job()
        CoroutineScope(IO + broadCastJob).launch {
            Log.d(TAG, "onCreate: run broadCastThread")

            val ipAddress = getIpAddress()
            if (ipAddress != null) {
                val byName = InetAddress.getByName("255.255.255.255")
                val broadcastAddress = getBroadcastAddress()
                val packet = DatagramPacket(
                    ipAddress.encodeToByteArray(),
                    ipAddress.length,
                    byName,
                    3999
                )
                val broadcastSocket = DatagramSocket()
                broadcastSocket.broadcast = true

                broadcastSocket.send(packet)
                broadcastSocket.close()
            }
        }

        val socketJob = Job()
        CoroutineScope(IO + socketJob).launch {
            Log.d(TAG, "onCreate: run thread called.")
            serverSocket = ServerSocket(4000)
            serverSocket.soTimeout = 0
            connectionSocket = serverSocket.accept()
            onClientConnected()

            val inputStream =
                DataInputStream(BufferedInputStream(connectionSocket.getInputStream()))
            readData(inputStream)
        }

    }

    private fun onClientConnected() {
        Log.d(TAG, "onClientConnected: called.")
        isClientConnected = true
    }

    private fun onClientDisconnected() {
        Log.d(TAG, "onClientDisconnected: called.")
        isClientConnected = false
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    private fun readData(inputStream: DataInputStream) {
        while (isClientConnected) {
            try {
//                val isPhoto = (inputStream.readByte() == photoByte)

                val name = inputStream.readUTF()
                val albumName = inputStream.readUTF()

                val length = inputStream.readLong()
                if (length < 0) {
                    Log.e(TAG, "readData: no length")
                    break
                }
                var actualLength = 0

                val photoUploader = PhotoUploader()
                val newPhotoFile =
                    photoUploader.getNewPhotoFile(applicationContext, name, albumName)
                val dataOutputStream = DataOutputStream(newPhotoFile)

                while (actualLength < length) {
                    val data = ByteArray(1024)
                    val count = inputStream.read(data)
                    if (count == -1)
                        break
                    actualLength += count
                    dataOutputStream.write(data, 0, count)
                }
                dataOutputStream.flush()
                dataOutputStream.close()

                photoUploader.clear()
            } catch (throwable: Throwable) {
                if (throwable is EOFException)
                    Log.w(TAG, "readData: no data")
                Log.e(TAG, "readData: ", throwable)
            }

        }
        inputStream.close()
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}