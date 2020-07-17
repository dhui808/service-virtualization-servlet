package webservicemockserver;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@WebServlet(urlPatterns = "/rest/*", loadOnStartup = 1)
public class MobileWebMockServer extends AbstractMockServer.RestMockServer {

	private static final long serialVersionUID = 3724936760788768773L;
	private static final Logger logger = LoggerFactory.getLogger(MobileWebMockServer.class);
	
	public MobileWebMockServer() {
		super("mobilewebmockserver");
	}
	
	@Override
	protected void handleException(HttpServletResponse resp, String respStr) {
		if (respStr.contains("exception")) {
	    	logger.debug("There is exception item in response string.");
	    }
	}
}