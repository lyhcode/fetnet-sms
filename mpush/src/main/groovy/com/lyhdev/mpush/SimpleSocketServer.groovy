package com.lyhdev.mpush

import groovy.util.logging.*

/**
 * SimpleSocketServer 接收目前[拍賣編號]
 */
@Log4j
class SimpleSocketServer {

	// 暫定 UDP PORT=3003
	int port = 3003
	int bufferSize = 4096
	int soTimeout = 3000

	int rcvCount = 0

	def th1
	def socket = null

	def start() {
		if (socket != null && !socket.isClosed()) {
			socket.close()
		}

		socket = new DatagramSocket(port)
		socket.soTimeout = soTimeout

		th1 = Thread.start {			
			listen()
		}
	}
	
	def stop() {
		if (socket != null) {
			socket.close()
		}
	}

	def listen() {
		def buffer = (' ' * bufferSize) as byte[]
		
		while (!socket.isClosed()) {
			def incoming = new DatagramPacket(buffer, buffer.length)

			try {
				socket.receive(incoming)

				def msg = new String(incoming.data, 0, incoming.length)

				log.debug "server receive(${rcvCount++}): ${msg}"

				def outgoing = new DatagramPacket(
					msg.bytes,
					msg.size(),
					incoming.address,
					incoming.port
				)

				// Echo
				socket.send(outgoing)
			}
			catch (e) {
				log.debug "server receive exception: ${e.message}"
			}
		}
	}

	public static void main(String[] args) {
		def server = new SimpleSocketServer();
		server.listen();
	}
}
