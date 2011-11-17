import spock.lang.Specification
import com.lyhdev.mpush.*
import groovy.sql.Sql

class SimpleProcessSpec extends Specification {

	def "process send"() {
		given:

		def sql = Sql.newInstance("jdbc:hsqldb:mem:SHCAR", "sa", "", "org.hsqldb.jdbcDriver")
		def db = new SimpleDatabase(sql: sql)
		def sms = new SimpleSMS()
		def proc = new SimpleProcess()

		when:

		db.createTestTable()

		(1..199).each {
			n ->
			db.insertTestData('123456', '2001', "c${n}", '886912345678+${n}')
		}

		proc.send(db, sms, '2001', 'How are you?')

		then:

		sql.firstRow("select * from PRE_AL_MSG where S_NO='123456' and BUYERID='c1'").S_DATE == db.dateString
		sql.firstRow("select * from PRE_AL_MSG where S_NO='123456' and BUYERID='c1'").S_R_F == 'Y'

		cleanup:

		sql.execute 'drop table PRE_AL_MSG'
	}
}
