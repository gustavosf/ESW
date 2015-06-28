package ts;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

class TimeClient {
	public static void main(String args[]) throws Exception {
		URL url = new URL("http://127.0.0.1:9876/ts?wsdl");
		QName qname = new QName("http://ts/",  "TimeServerImplService");
		Service ws = Service.create(url, qname);
		TimeServer ts = ws.getPort(TimeServer.class);
		System.out.println(ts.getTimeAsString());
		System.out.println(ts.getTimeAsElapsed());
	}
}
