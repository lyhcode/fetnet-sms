package com.lyhdev.mpush

import groovy.util.logging.*

/**
 * SimpleDatabase
 */
@Log4j
class SimpleDatabase {

	def sql

	/*
		則以下條件的資料才發送:
		1.BSDNO='20111116'(當天日期)
		2.AL_SNO='2001'(UPD收到的拍賣編號)
		3.SEND_F='Y'
		4.S_MOBI(要有資料)
		5.S_DATE(沒有資料才傳,有資料表示已傳送給遠傳)
	*/
	def SELECT_SQL = '''
		select * from PRE_AL_MSG
		where AL_SNO = ?
		and BSDNO = ?
		and SEND_F = 'Y'
		and (S_MOBI is not null and S_MOBI <> '')
		and (S_DATE is null or S_DATE = '')
	'''

	def UPDATE_SQL = '''
		update PRE_AL_MSG
		set S_DATE=?, S_R_F=?
		where AL_SNO = ?
		and BSDNO = ?
		and SEND_F = 'Y'
		and (S_MOBI is not null and S_MOBI <> '')
		and (S_DATE is null or S_DATE = '')
	'''

	/**
	 * 透過 AL_SNO 取得需要提醒的編號
	 */
	def getAlertList(AL_SNO) {
		sql.rows(SELECT_SQL, [
			AL_SNO,
			new Date().format('yyyyMMdd')
		])
	}

	/**
	 * 將簡訊傳送結果寫回資料表
	 */
	def saveResult(AL_SNO, S_R_F) {
		
		def S_DATE = new Date().format('yyyyMMdd') //日期格式：19991231

		sql.executeUpdate(UPDATE_SQL, [
			S_DATE,
			S_R_F,
			AL_SNO,
			new Date().format('yyyyMMdd')
		]) > 0
	}
}
