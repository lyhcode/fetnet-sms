import spock.lang.Specification
import com.lyhdev.mpush.*
import groovy.sql.Sql

class SimpleDatabaseSpec extends Specification {

	def "database read and write"() {
		given:

		def sql = Sql.newInstance("jdbc:hsqldb:mem:SHCAR", "sa", "", "org.hsqldb.jdbcDriver")
		def db = new SimpleDatabase(sql: sql)

		when:

		db.createTestTable()
		db.insertTestData('123456', '2001', 'john1111', '886912345678')

		then:

		db.getAlertList('2001').size() > 0
		db.getAlertList('2001')[0].S_MOBI == '886912345678'
		db.saveResult('123456', 'john1111', 'Y')
		
		sql.firstRow("select S_R_F as val1 from PRE_AL_MSG where AL_SNO='2001'").val1 == 'Y'

		cleanup:

		sql.execute 'drop table PRE_AL_MSG'
	}
}
