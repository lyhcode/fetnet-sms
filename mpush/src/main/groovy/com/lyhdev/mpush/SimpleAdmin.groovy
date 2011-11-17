package com.lyhdev.mpush

import java.awt.*
import javax.swing.*
import groovy.swing.SwingBuilder
import groovy.sql.Sql
import groovy.util.logging.*

/**
 * SimpleAdmin
 */
@Log4j
class SimpleAdmin {

	def CONFIG_FILENAME = 'config.ini'

	def show() {
		def label_dbtest
		def label_submit
		def label_send
		
		def frame_main
		
		def button_start
		def button_close
		def button_submit
		def button_send
	
		def field_portudp
		def field_timeout
		def field_AL_SNO
		def field_message
		def field_phone
		def field_smsurl
		def field_smspathsubmit
		def field_sysid
		def field_srcaddress
		def field_dsn
		def field_dbuser
		def field_dbpwd
		def field_dbdrv
		
		def server = null
		//def sql = null
	
		// 預設字型設定（中文最佳化）
		def font1 = new Font('Dialog', Font.PLAIN, 12)
		def font2 = new Font('Dialog', Font.PLAIN, 13)
		
		// Mac OS X 系統選單設定
		System.setProperty("apple.laf.useScreenMenuBar", "true")
		//System.setProperty("com.apple.mrj.application.apple.menu.about.name", '簡訊服務管理')
		System.setProperty('com.apple.mrj.application.apple.menu.about.name', 'SimpleAdmin')
		System.setProperty('apple.awt.application.name', 'SimpleAdmin')

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		def swingbuilder = new SwingBuilder().edt {
			frame_main = frame (
				title: '簡訊服務管理',
				locationRelativeTo: null,
				size: [640, 480],
				show: true,
				pack: true,
				defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
				windowClosing: {
					if (server != null) server.stop()
					//System.exit(0)
				},
				windowClosed: {
					//if (server != null) server.stop()
					System.exit(0)
				}) {
				
				lookAndFeel('system')

				//選單列
				menuBar() {
					menu (text: '功能表', mnemonic: 'F') {
						menuItem (text: '儲存設定', mnemonic: 'S', actionPerformed: {
							def conff = new File(CONFIG_FILENAME)
							def confs = new ConfigSlurper()
							def conf
							if (conff.exists()) {
								conf = confs.parse(conff.toURL())
							}
							else {
								conf = confs.parse('')
							}
							conf.server.port.udp = field_portudp.text
							conf.server.timeout = field_timeout.text
							conf.mpush.url = field_smsurl.text
							conf.mpush.path = field_smspathsubmit.text
							conf.mpush.sysId = field_sysid.text
							conf.mpush.srcAddress = field_srcaddress.text
							conf.database.dsn = field_dsn.text
							conf.database.user = field_dbuser.text
							conf.database.password = field_dbpwd.text
							conf.database.driver = field_dbdrv.text
							conf.message.content = field_message.text
							conff.withWriter('UTF-8', {
								w->
								conf.writeTo(w)
							})
						})
						menuItem (text: '關閉', mnemonic: 'X', actionPerformed: {
							dispose()
						})
					}
				}
				tabbedPane (constraints: BorderLayout.CENTER, tabLayoutPolicy: JTabbedPane.SCROLL_TAB_LAYOUT) {
					panel (name: '伺服器設定') {
						tableLayout () {
							tr {
								td {
									label (text: '<html><font color=blue>伺服器設定</font></html>')
								}
							}
							tr {
								td {
									label (font: font1, text: '本地網路位址')
								}
								td {
									label(text: InetAddress.localHost.hostAddress)
								}
							}
							tr {
								td {
									label (font: font1, text: '連接埠（UDP）')
								}
								td {
									field_portudp = textField(columns: 10, text: '3003')
								}
							}
							tr {
								td {
									label (font: font1, text: '連線逾時（秒）')
								}
								td {
									field_timeout = textField(columns: 10, text: '30')
								}
							}
						}
					}
					panel (name: '簡訊服務設定') {
						tableLayout () {
							tr {
								td {
									label (text: '<html><font color=blue>簡訊服務設定</font></html>')
								}
							}
							tr {
								td {
									label (font: font1, text: '伺服器位址')
								}
								td {
									field_smsurl = textField(columns: 25, text: 'http://localhost:6600')
								}
							}
							tr {
								td {
									label (font: font1, text: '服務路徑')
								}
								td {
									field_smspathsubmit = textField(columns: 25, text: '/mpushapi/smssubmit')
								}
							}
							tr {
								td {
									label (font: font1, text: '系統代碼')
								}
								td {
									field_sysid = textField(columns: 25, text: '')
								}
							}
							tr {
								td {
									label (font: font1, text: '來源號碼')
								}
								td {
									field_srcaddress = textField(columns: 25, text: '')
								}
							}
						}
					}
					panel (name: '資料庫設定') {
						tableLayout () {
							tr {
								td {
									label (text: '<html><font color=blue>資料庫設定</font></html>')
								}
							}
							tr {
								td {
									label (font: font1, text: '連線字串')
								}
								td {
									field_dsn = textField(columns: 25, text: 'jdbc:hsqldb:mem:SHCAR')
								}
							}          
							tr {
								td {
									label (font: font1, text: '帳號')
								}
								td {
									field_dbuser = textField(columns: 10, text: 'sa')
								}
							}	
							tr {
								td {
									label (font: font1, text: '密碼')
								}
								td {
									field_dbpwd = passwordField(columns: 10, text: '')
								}
							}
							tr {
								td {
									label (font: font1, text: '驅動程式')
								}
								td {
									field_dbdrv = textField(columns: 20, text: 'org.hsqldb.jdbcDriver')
								}
							}
							tr {
								td {
									button (font: font2, text: '測試連線', actionPerformed: {
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
						}
					}
					panel (name: '訊息設定') {
						tableLayout () {
							tr {
								td {
									label (text: '<html><font color=blue>訊息設定</font></html>')
								}
							}
							tr {
								td {
									field_message = textArea (columns: 30, rows: 5, text: '')
								}
							}
						}
					}
					panel (name: '手動控制') {
						tableLayout () {
							tr {
								td {
									label (text: '<html><font color=blue>手動控制</font></html>')
								}
							}
							tr {
								td {
									label (font: font1, text: 'AL_SNO')
								}
								td {
									field_AL_SNO = textField(columns: 10, text: '')
								}
							}
							tr {
								td {
									button_submit = button (font: font2, text: '送出 UDP 訊息', actionPerformed: {
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
									label (font: font1, text: '行動電話號碼')
								}
								td {
									field_phone = textField(columns: 10, text: '8869')
								}
							}
							tr {
								td {
									button_send = button (font: font2, text: '送出 SMS 訊息', actionPerformed: {
										log.info "手動送出 SMS 訊息 '${field_phone.text}'"
									
										button_send.enabled = false
										label_send.text = ''
									
										Thread.start {
											def sms = new SimpleSMS(
												url: field_smsurl.text,
												pathSubmit: field_smspathsubmit.text,
												sysId: field_sysid.text,
												srcAddress: field_srcaddress.text
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
				panel (constraints: BorderLayout.SOUTH) {
					button_start = button(font: font2, text: '啟動服務', actionPerformed: {
						button_start.enabled = false
						button_close.enabled = true
						button_submit.enabled = true
						
						log.info "啟動服務"
						
						server = new SimpleSocketServer(port: new Integer(field_portudp.text), soTimeout: new Integer(field_timeout.text) * 1000, action: {
							AL_SNO ->
							
							log.info "正在處理 AL_SNO='${AL_SNO}'"
							
							def sql = Sql.newInstance(field_dsn.text, field_dbuser.text, field_dbpwd.text, field_dbdrv.text)
							def db = new SimpleDatabase(sql: sql)
							
							def list = db.getAlertList(AL_SNO)

							//送出簡訊
							def sms = new SimpleSMS(
								url: field_smsurl.text,
								pathSubmit: field_smspathsubmit.text,
								sysId: field_sysid.text,
								srcAddress: field_srcaddress.text
							)
							def result = sms.submit([field_phone.text], field_message.text)
							
							sql.close()
						})
						server.start()
					})
					
					button_close = button (font: font2, text: '停止服務', actionPerformed: {
						button_start.enabled = true
						button_close.enabled = false
						button_submit.enabled = false
						
						log.info "停止服務"
						
						if (server != null) {
							server.stop()
						}
					})
					button_close.enabled = false
					
					button (font: font2, text: '關閉', actionPerformed: {
						frame_main.hide()
						frame_main.dispose()
					})
				}
			}
		}
		
		def conff = new File(CONFIG_FILENAME)
		if (conff.exists()) {
			def conf = new ConfigSlurper().parse(conff.getText('UTF-8'))
			field_portudp.text = conf.server.port.udp 
			field_timeout.text = conf.server.timeout 
			field_smsurl.text = conf.mpush.url 
			field_smspathsubmit.text = conf.mpush.path 
			field_sysid.text = conf.mpush.sysId 
			field_srcaddress.text = conf.mpush.srcAddress 
			field_dsn.text = conf.database.dsn 
			field_dbuser.text = conf.database.user 
			field_dbpwd.text = conf.database.password 
			field_dbdrv.text = conf.database.driver 
			field_message.text = conf.message.content 
		}
		//swingbuilder.lookAndFeel('plasticXP', tabStyle:'metal')
		//swingbuilder.lookAndFeel('win2k')
		//swingbuilder.lookAndFeel('nimbus')
	}
	
	static void main(String[] args) {
		new SimpleAdmin().show()
	}
}
