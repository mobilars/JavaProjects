package org.hisp.dhis.smsin.beans;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

public class SMSServiceBean {

	private String serviceName;
	private String username;
	private String password;	
	private String dhisUrl;
	
//	@Autowired
//	private SMSUsers smsusers;
	
	@Autowired
    private SMSProvider smsProvider; 
	
	@Autowired
    private DHISAPI dhisapi; 
	
	protected final Log logger = LogFactory.getLog(getClass());
	
    public SMSServiceBean() {
    }
    
    public boolean accept(String sender, String message) {
    	if (message.toUpperCase().startsWith(serviceName.toUpperCase())) {
    		// Handle this message
    		logger.info(serviceName+" handling this SMS: "+message);
    		System.out.println(serviceName+" handling this SMS: "+message+" (to system.out.println)");   		
    		handleMessage(sender, message);
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
	

	public void handleMessage(String sender, String message)
	{
    	try {
    		String querystring = "sender="+URLEncoder.encode(sender, "ISO-8859-1")+"&message="+URLEncoder.encode(message, "ISO-8859-1");
    		querystring = querystring.replace(' ', '+');
			dhisapi.forwardRequest(dhisUrl, username, password, querystring);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDhisUrl() {
		return dhisUrl;
	}

	public void setDhisUrl(String dhisUrl) {
		this.dhisUrl = dhisUrl;
	}

}