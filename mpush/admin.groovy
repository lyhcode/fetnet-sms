@Grab('hsqldb:hsqldb:1.8.0.10')

import java.awt.*
import javax.swing.*
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import groovy.sql.Sql

count = 0

def label_dbtest

new SwingBuilder().edt {
	frame(title: '簡訊服務管理', size:[800, 320], show: true, defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
		panel (constraints: BL.CENTER) {
			tableLayout() {
				tr {
					td {
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
										textlabel = textField(columns: 20, text:"Click the button!")
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
										textField(columns: 20, text: 'jdbc:hsqldb:mem:SHCAR')
									}
								}          
								tr {
									td {
										label (text: '帳號')
									}
									td {
										textField(columns: 10, text: 'sa')
									}
								}	
								tr {
									td {
										label (text: '密碼')
									}
									td {
										textField(columns: 10, text: '')
									}
								}
								tr {
									td {
										button (text: '測試連線', actionPerformed: {
											try {
											def sql = Sql.newInstance("jdbc:hsqldb:mem:SHCAR", "sa", "", "org.hsqldb.jdbcDriver")
												label_dbtest.text = sql
											}
											catch (e) {
												label_dbtest.text = "<html><FONT color=red>${e.message}</FONT></html>"
											}
										})
									}
									td {
										label_dbtest = label(text: '')
									}
								}
							}
						}
					}
					td {
						panel () {
							tableLayout () {
								tr {
									td {
										label (text: '<html><B><BIG>訊息設定</BIG></B></html>')
									}
								}
								tr {
									td {
										textArea (columns: 25, rows: 12, text: 'test')
									}
								}
							}
						}
					}
				}
			}
		}
		panel (constraints:BL.SOUTH) {

			button(text:'啟動服務', actionPerformed: {count++; textlabel.text = "Clicked ${count} time(s)."; println "clicked"})
				button (text: '停止服務')
		}
	}
}
