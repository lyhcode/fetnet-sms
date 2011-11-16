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
	def url = 'http://localhost:6600'

	def sysId = 'X0KYAODA'
	def srcAddress = '01916800020100500000'
	def drFlag = true
	
	def http
	
	def submit(destAddress, smsBody) {
		submit([
			DestAddress: destAddress,
			SmsBody: smsBody
		])
	}

	def submit(sms) {
		// http://61.20.32.60:6600
	 	http = new HTTPBuilder( url )
	 
		def result = [:]

		http.request( GET, XML ) {
			uri.path = '/mpushapi/smssubmit'
			uri.query = [xml: dataToXml(sms)]
			//body = [xml: dataToXml(sms)]

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
		writer.print '<?xml version="1.0" encoding="UTF-8"?>'
		writer.print "\n"

		def xml = new MarkupBuilder(writer)

		xml.doubleQuotes = true

		xml.SmsSubmitReq {
			SysId (sysId)
			SrcAddress (srcAddress)
			sms.DestAddress.each {
				DestAddress (it)
			}
			SmsBody (sms.SmsBody.getBytes("BIG5").encodeBase64().toString())
			DrFlag (drFlag)
		}
		
		//writer.print "\n"
		
		//def result = writer.toString().replace("\n", '')
		def result = writer.toString()
		
		log.info "訊息已轉換為 XML 格式: ${result}"

		result
		//'<?xml version="1.0" encoding="UTF-8" ?><SmsSubmitReq><SysId>X0KYAODA</SysId><SrcAddress>01916800020100500000</SrcAddress><DestAddress>886937397377</DestAddress><SmsBody>SGkuIFRlc3QgTWVzc2FnZS4=</SmsBody></SmsSubmitReq>'
	}
}
