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

	/*
	def UPDATE_SQL = '''
		update PRE_AL_MSG
		set S_DATE=?, S_R_F=?
		where AL_SNO = ?
		and BSDNO = ?
		and SEND_F = 'Y'
		and (S_MOBI is not null and S_MOBI <> '')
		and (S_DATE is null or S_DATE = '')
	'''
	*/

	// 個別更新記錄
	def UPDATE_SQL = '''
		update PRE_AL_MSG
		set S_DATE=?, S_R_F=?
		where BSDNO = ?
		and S_NO = ?
		and BUYERID = ?
	'''

	/**
	 * 建立測試用的資料表（危險！請小心呼叫）
	 */
	def createTestTable() {
		/*
			[BSDNO]:拍賣日期
			[S_NO]:拍賣編號
			[AL_SNO]:提醒拍賣編號
			[BUYERID]:參拍會員編號
			[DELAY_C]:提前台數(紀錄用)
			[S_MOBI]:會員手機電話

			[SEND_F]:已發送,請寫'Y'
			[S_DATE]:請寫入發送時間
			[S_R_F]:請寫如遠傳傳回的接收完成與否(此系統傳給遠傳的結果,非遠傳簡訊傳送結果)
		*/
		sql.execute '''
			create table PRE_AL_MSG (
				BSDNO varchar(255),
				S_NO varchar(255),
				AL_SNO varchar(255),
				BUYERID varchar(255),
				DELAY_C varchar(255),
				S_MOBI varchar(255),
				SEND_F varchar(255),
				S_DATE varchar(255),
				S_R_F varchar(255)
			)
		'''
	}

	/**
	 * 建立測試資料（危險！請小心使用）
	 */
	def insertTestData(S_NO, AL_SNO, BUYERID, S_MOBI) {
		def INSERT_SQL = '''
			insert into PRE_AL_MSG (BSDNO, S_NO, AL_SNO, BUYERID, DELAY_C, S_MOBI, SEND_F, S_DATE, S_R_F)
			values (?, ?, ?, ?, ?, ?, ?, ?, ?)
		'''
		sql.execute INSERT_SQL, [getDateString(), S_NO, AL_SNO, BUYERID, '1', S_MOBI, 'Y', null, null]
	}

	/**
	 * 取得今天日期字串（BSDNO）
	 */
	def getDateString() {
		new Date().format('yyyyMMdd')
	}

	/**
	 * 透過 AL_SNO 取得需要提醒的編號
	 */
	def getAlertList(AL_SNO) {
		sql.rows(SELECT_SQL, [
			AL_SNO,
			getDateString()
		])
	}

	/**
	 * 將簡訊傳送結果寫回資料表
	 */
	def saveResult(S_NO, BUYERID, S_R_F) {
		sql.executeUpdate(UPDATE_SQL, [
			new Date().format('yyyy-MM-dd HH:mm:ss'),
			S_R_F,
			getDateString(),
			S_NO,
			BUYERID
		]) > 0
	}
}
