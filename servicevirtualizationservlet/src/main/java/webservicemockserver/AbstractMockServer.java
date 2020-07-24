package webservicemockserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import servicevirtualizationutils.MockData;

public abstract class AbstractMockServer extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMockServer.class);
	
	private static final long serialVersionUID = 5495230795670132443L;
	protected MockData mockData;
	@Value("${servicevirtualizationdata_home}")
	private String mockDataHome;
	@Value("${configpath}")
	private String configpath;
	private String mockServerName;
	
	protected AbstractMockServer(String mockServerName) {

		this.mockServerName = mockServerName;
	}
	
	public void init() {
		System.out.println("mockDataHome="+mockDataHome);
		this.mockData = MockData.getMockData(mockServerName, mockDataHome);
	}
	
	protected void doOptions(HttpServletRequest req,
            HttpServletResponse resp)
     throws ServletException,
            IOException {
		
		populateResponseHeader(req, resp);
	}

	protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
		
		doPost(req, resp);
		
//		createMockData();
//		
//		String pathInfo = req.getPathInfo();
//		
//		// Handles retrieveEntryPageURL request
//		if ("/retrieveEntryPageURL".equals(pathInfo)) {
//			handleRetrieveEntryPageURL(resp);
//			
//			return;
//		}
//		
//		// Handle other requests - by default, it is the selecting flow and scenario.
//		boolean isSetFlow = addFlowScenarioCookies(req, resp);
//		if (!isSetFlow) {
//			//normal rest GET request
//			String respFilePath = findResponseFilePath(req, resp, "_GET");
//
//			populateResponse(req, resp, respFilePath);
//		}
	}
	
	// Handle retrieveEntryPageURL, selecting flow/scenario
	// Previously handled by doGet in Servlet-based service
	private void doConfig(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
		
		String retrieveEntryPageURL = req.getParameter("retrieveEntryPageURL");

		// Handles retrieveEntryPageURL request
		if (null != retrieveEntryPageURL) {
			handleRetrieveEntryPageURL(resp);
			
			return;
		}
		
		// Handle other requests - by default, it is the selecting flow and scenario.
		boolean isSetFlow = addFlowScenarioCookies(req, resp);
	}
	
	private void handleRetrieveEntryPageURL(HttpServletResponse resp) throws IOException {
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter out = resp.getWriter();

		Map<String,String> payload = new HashMap<>();
		payload.put("entryPageUrl",mockData.getEntryPageUrl());

		String json = new ObjectMapper().writeValueAsString(payload);
		
		out.print(json);
		out.flush();
	}

	protected void doPut(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
		
		String respFilePath = findResponseFilePath(req, resp, "_PUT");

		populateResponse(req, resp, respFilePath);

	}
	
	protected void doPost(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
		
		String config = req.getPathInfo();
		
		//Handles configuration POST request
		if (configpath.equals(config)) {
			
			doConfig(req, resp);
			
			return;
		}
		
		String respFilePath = findResponseFilePath(req, resp, "");
logger.debug("respFilePath=" + respFilePath);
		populateResponse(req, resp, respFilePath);
	}
	
	protected Map<String, String> getFlowScenarioMap(HttpServletRequest req) {
		
		Cookie[] cookies = req.getCookies();
		
		Map<String, String> flowScenarioMap = new HashMap<String, String>(2);
		
		if (null == cookies) return flowScenarioMap;
		
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if (name.equals("flow") || name.equals("scenario")) {
				String value = cookie.getValue();
				flowScenarioMap.put(name, value);
			}
		}
		
		return flowScenarioMap;
	}

	protected String populateResponse(HttpServletRequest req,
            HttpServletResponse resp, String responseFilePath)
            throws ServletException, IOException {
		
		populateResponseHeader(req, resp);
		resp.setContentType(getContentType());
	    PrintWriter out = resp.getWriter();
	    //InputStream is = getClass().getResourceAsStream(responseFilePath);
	    InputStream is = new FileInputStream(responseFilePath);
	    Scanner s = new Scanner(is).useDelimiter("\\A");
	    String respStr = s.hasNext() ? s.next() : "";
	    out.print(respStr);
	    handleException(resp, respStr);
	    return respStr;
	}
	
	private void populateResponseHeader(HttpServletRequest req,
            HttpServletResponse resp) {
		
		resp.setHeader("Allow","GET, PUT, POST, HEAD, TRACE, OPTIONS");
		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	private boolean addFlowScenarioCookies(HttpServletRequest req,
            HttpServletResponse resp) 
            throws ServletException, IOException {

		String flow = req.getParameter("flow");
		String scenario = req.getParameter("scenario");
		
		logger.debug(mockServerName + " flow:" + flow + " Scenario:" + scenario);
		logger.debug("entryPageUrl:" + mockData.getEntryPageUrl());
		
		if (null == flow) return false;
		
		eraseCookie(req, resp);
		
		Cookie flowCookie = new Cookie("flow", flow);
		Cookie scenarioCookie = new Cookie("scenario", scenario);
		
		flowCookie.setPath("/");
		scenarioCookie.setPath("/");
		
		resp.addCookie(flowCookie);
		resp.addCookie(scenarioCookie);
		
		return true;
	}
	
	private void eraseCookie(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if(cookies!=null) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0);
				cookies[i].setPath("/");
				resp.addCookie(cookies[i]);
			}
		}
	}
	
	protected String adjustResponseFile(String responseFile, HttpServletRequest req,
			HttpServletResponse resp, String method) {
		//By default, the responseFile in the alternateResponseFileMap will alternate with the 2nd version;
		List<String> alternateResponseFiles = mockData.getAlternateResponseFiles();
		String filePathNoSuffix = responseFile.substring(0, responseFile.indexOf(".json"));
		String responseFileName = filePathNoSuffix.substring(filePathNoSuffix.lastIndexOf("/") + 1);

		if(alternateResponseFiles.contains(req.getPathInfo()+method)) {
			boolean sendAlternate = changeCountCookie(responseFileName, req, resp);
			if (sendAlternate) {
				responseFile = filePathNoSuffix + "2.json";
			}
		}
		
		return responseFile;
	}
	
	private boolean changeCountCookie(String responseFileName, HttpServletRequest req, HttpServletResponse resp) {
		boolean sendAlternate = false;
		boolean foundCookie = false;
		Cookie[] cookies = req.getCookies();
		
		if (null == cookies) cookies = new Cookie[0];
		
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if (name.equals(responseFileName)) {
				foundCookie = true;
				String count = cookie.getValue();
				
				if ("0".equals(count)) {
					cookie.setValue("1");
					sendAlternate = true;
				} else {
					cookie.setValue("0");
				}
				
				resp.addCookie(cookie);
				break;
			}
		}
		
		if (!foundCookie) {
			resp.addCookie(new Cookie(responseFileName, "0"));
		}
		
		return sendAlternate;
	}

	protected void handleException(HttpServletResponse resp, String respStr) {
		//By default, do nothing
	}
	
	protected abstract String getContentType();
	protected abstract String findResponseFilePath(HttpServletRequest req,
			HttpServletResponse resp, String method) throws JsonProcessingException, IOException;
	
	public static abstract class SoapMockServer extends AbstractMockServer {

		private static final long serialVersionUID = -6997974777818769295L;

		public SoapMockServer(String mockServerName) {
			super(mockServerName);
		}
		
		@Override
		protected String getContentType() {
			return "application/xml";
		}

		@Override
		protected String findResponseFilePath(HttpServletRequest req,
				HttpServletResponse resp, String method) throws JsonProcessingException, IOException {
			
			Map<String, String> flowScenarioMap = getFlowScenarioMap(req);
			String flow = flowScenarioMap.get("flow");
			String scenario = flowScenarioMap.get("scenario");
			Enumeration<String> soapActions = req.getHeaders("SOAPAction");
			String soapAction = soapActions.nextElement();
			logger.debug("SOAPAction path:" + soapAction);
			
			String path = soapAction.replace("\"", "");
			String responseFile = mockData.findFilePath(path, flow, scenario);
			
			return responseFile;
		}
	}
	
	public static abstract class RestMockServer extends AbstractMockServer {

		private static final long serialVersionUID = 3091888435538524506L;

		public RestMockServer(String mockServerName) {
			super(mockServerName);
		}
		
		@Override
		protected String getContentType() {
			return "application/json";
		}
		
		//PUT and GET request have the _PUT and _GET suffix being added to pathInfo for matching.
		//The purpose of this approach is to support the scenarios where PUT/GET/POST request have the same 
		//path in the RESTful services. For suffix POST requests, "" suffix is added.
		@Override
		protected String findResponseFilePath(HttpServletRequest req,
				HttpServletResponse resp, String method) throws JsonProcessingException, IOException {
			
			Map<String, String> flowScenarioMap = getFlowScenarioMap(req);
			String flow = flowScenarioMap.get("flow");
			String scenario = flowScenarioMap.get("scenario");
			String responseFile = mockData.findFilePath(req.getPathInfo()+method, flow, scenario);
			
			responseFile = adjustResponseFile(responseFile, req, resp, method);
			logger.debug("response file after adjustment;" + responseFile);
			return responseFile;
		}
	}
}