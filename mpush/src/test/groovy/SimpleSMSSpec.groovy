import spock.lang.Specification
import com.lyhdev.mpush.*

class SimpleSMSSpec extends Specification {

	def "mpushapi smssubmit"() {
		given: 'create SimpleSMS'

		def sms = new SimpleSMS()
		
		when: 'call submit'

		def result = sms.submit(
			'X0KYAODA',
			'01916800020100500000',
			['886000000000'],
			'Hi. Test Message.',
			true
		)

		then: 'check result'
		
		result != null
		result.ResultCode == '00000'
	}
}
