package ts;
import java.util.Date;
import java.util.TimeZone;
import javax.jws.WebService;

@WebService(endpointInterface = "ts.TimeServer")
public class TimeServerImpl implements TimeServer {
	public String getTimeAsString() {
		return new Date().toString();
	}
	public long getTimeAsElapsed() {
		return new Date().getTime();
	}
	public String getTimeForGMT(int gmt) throws Exception {
		if (gmt > 14 || gmt < -12)
			throw new Exception("GMT should be a number between -12 and 14");
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"+Integer.toString(gmt)));
		return getTimeAsString();
	}
}
