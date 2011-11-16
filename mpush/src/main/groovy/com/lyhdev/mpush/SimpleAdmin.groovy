package com.lyhdev.mpush

import java.awt.*
import javax.swing.*
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import groovy.sql.Sql
import groovy.util.logging.*

/**
 * SimpleAdmin
 */
@Log4j
class SimpleAdmin {

	def show() {
		def label_dbtest
		def label_submit
		def label_send
		
		def frame_main
		
		def button_start
		def button_close
		def button_submit
		def button_send
		
		def field_AL_SNO
		def field_message
		def field_phone
		def field_smsurl
		def field_sysid
		def field_srcaddress
		def field_dsn
		def field_dbuser
		def field_dbpwd
		def field_dbdrv
		
		def server = null
		//def sql = null
		
		def swingbuilder = new SwingBuilder().edt {
			frame_main = frame (
				title: '簡訊服務管理',
				size: [640, 480],
				show: true,
				// defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
				windowClosing: {
					if (server != null) server.stop() 
				},
				windowClosed: {
					System.exit(0)
				}) {
				scrollPane (constraints: BL.CENTER) {
					panel () {
						tableLayout () {
							tr {
								td {
									label (text: '<html><B><BIG>簡訊服務設定</BIG></B></html>')
								}
							}
							tr {
								td {
									label (text: '伺服器位址')
								}
								td {
									field_smsurl = textField(columns: 25, text: 'http://61.20.32.60:6600')
								}
							}
							tr {
								td {
									label (text: '系統代碼')
								}
								td {
									field_sysid = textField(columns: 25, text: 'X0KYAODA')
								}
							}
							tr {
								td {
									label (text: '來源號碼')
								}
								td {
									field_srcaddress = textField(columns: 25, text: '01916800020100500000')
								}
							}
							tr {
								td {
									label (text: '<html><B><BIG>資料庫設定</BIG></B></html>')
								}
							}
							tr {
								td {
									label (text: '連線字串')
								}
								td {
									field_dsn = textField(columns: 25, text: 'jdbc:jtds:sqlserver://HAADBP01/SHCAR')
								}
							}          
							tr {
								td {
									label (text: '帳號')
								}
								td {
									field_dbuser = textField(columns: 10, text: 'SHUSER')
								}
							}	
							tr {
								td {
									label (text: '密碼')
								}
								td {
									field_dbpwd = textField(columns: 10, text: 'SH123a')
								}
							}
							tr {
								td {
									label (text: '驅動程式')
								}
								td {
									field_dbdrv = textField(columns: 20, text: 'net.sourceforge.jtds.jdbc.Driver')
								}
							}
							tr {
								td {
									button (text: '測試連線', actionPerformed: {
										try {
											def sql = Sql.newInstance(field_dsn.text, field_dbuser.text, field_dbpwd.text, field_dbdrv.text)
											log.debug sql
										
											sql.close()
										
											label_dbtest.text = "連線測試成功"
										}
										catch (e) {
											log.error e.message
										
											//label_dbtest.text = "<html><FONT color=red>${e.message}</FONT></html>"
											label_dbtest.text = "<html><FONT color=red>失敗</FONT></html>"
										}
									})
								}
								td {
									label_dbtest = label(text: '')
								}
							}
							tr {
								td {
									label (text: '<html><B><BIG>訊息設定</BIG></B></html>')
								}
							}
							tr {
								td {
									label (text: '訊息內容')
								}
								td {
									field_message = textArea (columns: 25, rows: 10, text: '親愛的用戶您好')
								}
							}
							tr {
								td {
									label (text: '<html><B><BIG>手動控制</BIG></B></html>')
								}
							}
							tr {
								td {
									label (text: 'AL_SNO')
								}
								td {
									field_AL_SNO = textField(columns: 10, text: '')
								}
							}
							tr {
								td {
									button_submit = button (text: '送出 UDP 訊息', actionPerformed: {
										log.info "手動送出 UDP 訊息 '${field_AL_SNO.text}'"
									
										button_submit.enabled = false
										label_submit.text = ''
									
										Thread.start {
											def client = new SimpleSocketClient()
											def result = client.send(field_AL_SNO.text)

											label_submit.text = "${result}"
																								
											button_submit.enabled = true
										}
									})
									button_submit.enabled = false
								}
								td {
									label_submit = label(text: '')
								}
							}
							tr {
								td {
									label (text: '行動電話號碼')
								}
								td {
									field_phone = textField(columns: 10, text: '8869')
								}
							}
							tr {
								td {
									button_send = button (text: '送出 SMS 訊息', actionPerformed: {
										log.info "手動送出 SMS 訊息 '${field_phone.text}'"
									
										button_send.enabled = false
										label_send.text = ''
									
										Thread.start {
											def sms = new SimpleSMS(
												url: field_smsurl.text,
												sysId: field_sysid.text,
												srcAddress: field_srcaddress.text,
											)
											def result = sms.submit([field_phone.text], field_message.text)

											label_send.text = "${result.ResultCode}"
																								
											button_send.enabled = true
										}
									})
								}
								td {
									label_send = label(text: '')
								}
							}
						}
					}
				}
				panel (constraints:BL.SOUTH) {
					button_start = button(text: '啟動服務', actionPerformed: {
						button_start.enabled = false
						button_close.enabled = true
						button_submit.enabled = true
						
						log.info "啟動服務"
						
						server = new SimpleSocketServer(action: {
							AL_SNO ->
							
							log.info "正在處理 AL_SNO='${AL_SNO}'"
							
							def sql = Sql.newInstance(field_dsn.text, field_dbuser.text, field_dbpwd.text, field_dbdrv.text)
							def db = new SimpleDatabase(sql: sql)
							
							def list = db.getAlertList(AL_SNO)

							//送出簡訊
							def sms = new SimpleSMS(
								url: field_smsurl.text,
								sysId: field_sysid.text,
								srcAddress: field_srcaddress.text,
							)
							def result = sms.submit([field_phone.text], field_message.text)
							
							sql.close()
						})
						server.start()
					})
					
					button_close = button (text: '停止服務', actionPerformed: {
						button_start.enabled = true
						button_close.enabled = false
						button_submit.enabled = false
						
						log.info "停止服務"
						
						if (server != null) {
							server.stop()
						}
					})
					button_close.enabled = false
					
					button (text: '關閉', actionPerformed: {
						frame_main.hide()
						frame_main.dispose()
					})
				}
			}
		}
		
		//swingbuilder.lookAndFeel('plasticXP', tabStyle:'metal')
		//swingbuilder.lookAndFeel('win2k')
		swingbuilder.lookAndFeel('nimbus')
	}
	
	static void main(String[] args) {
		new SimpleAdmin().show()
	}
}
