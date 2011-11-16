import spock.lang.Specification
import com.lyhdev.mpush.*
import groovy.sql.Sql

class SimpleDatabaseSpec extends Specification {

	def "database read and write"() {
		given:

		def sql = Sql.newInstance("jdbc:hsqldb:mem:SHCAR", "sa", "", "org.hsqldb.jdbcDriver")

		def db = new SimpleDatabase(sql: sql)

		def INSERT_SQL = '''
			insert into PRE_AL_MSG (BSDNO, S_NO, AL_SNO, BUYERID, DELAY_C, S_MOBI, SEND_F, S_DATE, S_R_F)
			values (?, ?, ?, ?, ?, ?, ?, ?, ?)
		'''

		when:

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
		sql.execute INSERT_SQL, ['2011/11/11', '123456', '0001', '123456', '1', '886000000000', 'N', '', '']

		then:

		db.getAlertList('0001').size() > 0
		db.saveResult('0001', 'Y', '00000')

		cleanup:

		sql.execute 'drop table PRE_AL_MSG'
	}
}
