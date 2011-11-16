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
		where AL_SNO = ?
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
	def saveResult(AL_SNO, SEND_F, S_R_F) {
		
		def S_DATE = new Date()

		sql.executeUpdate(UPDATE_SQL, [
			SEND_F,
			S_DATE,
			S_R_F,
			AL_SNO
		]) > 0
	}
}
