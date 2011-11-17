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
	int soTimeout = 30000

	int rcvCount = 0

	def th1
	def socket = null
	
	def action = { msg -> log.debug "No action defined. data='${msg}'" }

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

				log.info "伺服器已接收訊息(${rcvCount++}): ${msg}"
				
				try {
					log.info "開始執行訊息處理程序"
					action(msg)
				}
				catch (e) {
					log.error "訊息處理程序異常終止: ${e.message}"
				}

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
				log.debug "伺服器除錯訊息: ${e.message}"
			}
		}
	}
}
