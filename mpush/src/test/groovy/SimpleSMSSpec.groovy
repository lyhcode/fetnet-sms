import spock.lang.Specification
import com.lyhdev.mpush.*

class SimpleSMSSpec extends Specification {

	def "mpushapi smssubmit"() {
		given: 'create SimpleSMS'

		def sms = new SimpleSMS(
			sysId: 'X0KYAODA',
			srcAddress: '01916800020100500000',
			drFlag: true
		)
		
		when: 'call submit'

		def result = sms.submit(
			['886000000000'],
			'Hi. Test Message.'
		)

		then: 'check result'
		
		result != null
		result.ResultCode == '00000'
	}
}
