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
	def pathSubmit = '/mpushapi/smssubmit'

	def sysId = ''
	def srcAddress = ''
	def drFlag = true
	
	def submit(destAddress, smsBody) {
		submit([
			DestAddress: destAddress,
			SmsBody: smsBody
		])
	}

	def submit(sms) {
	 	
		def xmlResult = httpRequestXml2(url, pathSubmit, dataToXml(sms))
		
		def result = [
			MessageId: null,
			ResultCode: null,
			ResultText: null
		]

		if (xmlResult) {
			result = [
				MessageId: xmlResult.MessageId,
				ResultCode: xmlResult.ResultCode,
				ResultText: xmlResult.ResultText
			]
		}

		log.info "訊息傳送結果: ${result}"

		result
	}

	/**
	 * httpRequestXml using HTTPBuilder
	 */
	def httpRequestXml(url, path, xml) {
		def http = new HTTPBuilder(url)
	 
		def result = null

		http.request( GET, XML ) {
			uri.path = pathSubmit
			uri.query = [xml: xml]

			// POST
			//body = [xml: xml]

			requestContentType = ContentType.URLENC

			response.success = { resp, xmlresp ->
				//println resp.statusLine
				result = xmlresp
			}

			response.failure = { resp ->
				log.error "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
			}
		}

		result
	}

	def httpRequestXml2(url, path, xml) {
		def result = null
		def encodeString = "xml=" + URLEncoder.encode(xml, 'UTF-8')
		def urlobj = new URL("${url}${path}")
		def conn = (HttpURLConnection) urlobj.openConnection()
		conn.requestMethod = 'POST'
		conn.doOutput = true
		conn.setRequestProperty('Content-Type', 'application/x-www-form-urlencoded')
		def outStream = conn.outputStream
		outStream.write(encodeString.getBytes('UTF-8'))
		conn.connect()
		def responseCode = conn.responseCode
		log.info "Response Code = ${responseCode}"
		def reader = new BufferedReader(new InputStreamReader(conn.inputStream))
		def writer = new StringWriter()
		def line = null
		while ((line = reader.readLine()) != null) {
			writer.print line
		}
		try {
			result = new XmlSlurper().parseText(writer.toString())
		}
		catch (e) {
			log.error e.message
		}
		reader.close()

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
			SmsBody (sms.SmsBody.getBytes("UTF8").encodeBase64().toString())
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
