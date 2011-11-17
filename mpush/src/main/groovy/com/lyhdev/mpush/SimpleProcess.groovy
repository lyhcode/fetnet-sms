package com.lyhdev.mpush

import groovy.util.logging.*

/**
 * SimpleProcess
 */
@Log4j
class SimpleProcess {

	def send(db, sms, AL_SNO, message, _maxSize = 10) {
		//取得簡訊通知名單
		def list = db.getAlertList(AL_SNO)

		if (list?.size() > 0) {
			def TOTAL = list.size()
			def MAXINDEX = TOTAL-1
			def MAXSIZE = _maxSize

			def CALC_MIN = {
				a, b ->
				a < b ? a : b
			}

			//分批送出訊息
			(0..((MAXINDEX)/MAXSIZE)).each {
				round_n ->

				//計算起始結束範圍
				def index_s = (round_n*MAXSIZE)
				def index_e = (CALC_MIN(round_n*MAXSIZE+MAXSIZE-1, MAXINDEX))

				//建立收件電話號碼清單
				def addrs = []
				(index_s..index_e).each {
					index_n ->
					addrs << list[index_n].S_MOBI
				}

				//送出訊息
				def result

				result = sms.submit(addrs, message)
				log.info "第一次訊息傳送結果: ${result}"

				if (result?.ResultCode == '00000') {
					//訊息一次送出成功
				}
				else {
					//重新傳送訊息
					result = sms.submit(addrs, message)
					log.info "第二次訊息傳送結果: ${result}"
				}

				//將結果寫回資料庫
				if (result?.ResultCode == '00000') {
					//傳送成功
					(index_s..index_e).each {
						index_n ->
						def row = list[index_n]
						db.saveResult(row.S_NO, row.BUYERID, 'Y')
					}
				}
				else {
					//傳送失敗
					log.error "連續兩次訊息送結果失敗"
					(index_s..index_e).each {
						index_n ->
						def row = list[index_n]
						db.saveResult(row.S_NO, row.BUYERID, 'N')
					}
				}
			}
		}
		else {
			log.info "名單未包含任何客戶資料"
		}
	}
}
