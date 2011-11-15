import spock.lang.Specification
import com.lyhdev.mpush.*

class SimpleSockerServerSpec extends Specification {

	def "server listen"() {
		given:

		def server = new SimpleSocketServer()
		def client = new SimpleSocketClient()

		server.start()
		
		when:

		def result = client.send('message')

		then:

		result == 'message'

		cleanup:

		server.stop()
	}

	def "easy load test to server"() {
		given:

		def server = new SimpleSocketServer()
		def client = new SimpleSocketClient()

		server.start()
		
		when:

		def result = []
		
		(1..1000).each {
			def msg = client.send('message')
			if (msg == 'message') {
				result << msg
			}
			Thread.sleep 10
		}

		then:

		result.size() == 1000

		cleanup:

		server.stop()	
	}
}
