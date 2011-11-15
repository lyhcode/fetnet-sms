package com.lyhdev.mpush

import groovy.util.logging.*
import groovy.xml.MarkupBuilder
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

/**
 * SimpleSMS call /mpushapi/smssubmit
 */
@Log4j
class SimpleSMS {
	// http://61.20.32.60:6600
	def http = new HTTPBuilder( 'http://localhost:6600' )

	def submit(sysId, srcAddress, destAddress, smsBody, drFlag) {
		submit([
			SysId: sysId,
			SrcAddress: srcAddress,
			DestAddress: destAddress,
			SmsBody: smsBody,
			DrFlag: drFlag
		])
	}

	def submit(sms) {
		def result = [:]

		http.request( POST, XML ) {
			uri.path = '/mpushapi/smssubmit'
			//uri.query = [a: 1]
			
			body = [ xml: dataToXml(sms) ]

			requestContentType = ContentType.URLENC

			response.success = { resp, xml ->
				//println resp.statusLine
				result = [
					MessageId: xml.MessageId,
					ResultCode: xml.ResultCode,
					ResultText: xml.ResultText
				]
			}

			response.failure = { resp ->
				log.error "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
			}
		}

		log.info "訊息傳送結果: ${result}"

		result
	}

	def dataToXml(sms) {
		def writer = new StringWriter()
		writer.println '<?xml version="1.0" encoding="UTF-8" ?>'

		def xml = new MarkupBuilder(writer)

		xml.doubleQuotes = true

		xml.SmsSubmitReq {
			SysId (sms.SysId)
			SrcAddress (sms.SrcAddress)
			sms.DestAddress.each {
				DestAddress (it)
			}
			SmsBody (sms.SmsBody.bytes.encodeBase64().toString())
			DrFlag (sms.DrFlag)
		}
		
		log.info "訊息已轉換為 XML 格式: ${writer.toString()}"

		writer.toString()
	}
	
	public static void main(String[] args) {
		def sms = new SimpleSMS()
		sms.submit(
			'X0KYAODA',
			'01916800020100500000',
			['886000000000'],
			'Hi. Test Message.',
			true
		)
	}
}
