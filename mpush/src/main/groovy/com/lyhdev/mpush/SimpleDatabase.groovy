package com.lyhdev.mpush

import groovy.util.logging.*

/**
 * SimpleDatabase
 */
@Log4j
class SimpleDatabase {

	def sql

	def SELECT_SQL = '''
		select * from PRE_AL_MSG
		where AL_SNO = ?
	'''

	def UPDATE_SQL = '''
		update PRE_AL_MSG
		set SEND_F=?, S_DATE=?, S_R_F=?
		where BSDNO=? and S_NO=? and BUYERID=?
	'''

	/**
	 * 透過 AL_SNO 取得需要提醒的編號
	 */
	def getAlertList(AL_SNO) {
		
		sql.rows(SELECT_SQL, [AL_SNO])
	}

	/**
	 * 將簡訊傳送結果寫回資料表
	 */
	def saveResult(alert, SEND_F, S_R_F) {
		
		def S_DATE = new Date()

		sql.execute(UPDATE_SQL, [
			SEND_F,
			S_DATE,
			S_R_F,
			alert.BSDNO,
			alert.S_NO,
			alert.BUYERID
		])
	}
}
