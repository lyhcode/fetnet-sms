package com.lyhdev.mpush

import groovy.util.logging.*

/**
 * SimpleSocketClient
 */
@Log4j
class SimpleSocketClient {

	String hostname = 'localhost'
	int port = 3003
	String encoding = 'ASCII'
	int timeout = 30000
	int bufferSize = 4096

	def send(msg) {
		def data = msg.getBytes(encoding)
		def addr = InetAddress.getByName(hostname)
		def packet = new DatagramPacket(data, data.length, addr, port)
		def socket = new DatagramSocket()
		
		socket.send(packet)
		socket.setSoTimeout(timeout)

		def buffer = (' ' * bufferSize) as byte[]

		def response = new DatagramPacket(buffer, buffer.length)
		try {
			socket.receive(response)
		}
		catch (e) {
			log.debug "client receive exception: ${e.message}"
		}
		
		//result
		def result = new String(response.data, 0, response.length)
		log.debug "client receive: ${result}"

		result
	}
}
