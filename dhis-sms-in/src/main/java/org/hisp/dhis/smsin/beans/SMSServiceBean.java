package org.hisp.dhis.smsin.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

public class SMSServiceBean {

	private Map <String,String> maps;
	private String serviceName;
	private String dataSet;
	private String helpMessage;
	private String username;
	private String password;	
	private String dhisUrl;
	
	@Autowired
	private SMSUsers smsusers;
	
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
		String url = dhisUrl+"/api/dataValueSets";
		String username = "admin";
		String password = "district";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM"); //201203
		
		// Find the right org-unit
		String orgunit = (String) smsusers.getUsers().get(sender);
		if (orgunit == null) {
			orgunit = (String) smsusers.getUsers().get(serviceName);
		}
		if (orgunit == null) {
			orgunit = (String) smsusers.getUsers().get("default");
		}
		if (orgunit == null) {
			logger.error("Cannot find org-unit. Check your configuration. ");
			System.out.println("Cannot find org-unit. Check your configuration. ");
			return;
		}		
		
		// Generate the XML. The easy way. 
		String xml = "<dataValueSet xmlns=\"http://dhis2.org/schema/dxf/2.0\" period=\""+dateFormat.format(new Date())+"\" dataSet=\""+dataSet+"\" orgUnit=\""+orgunit+"\">\r\n";
		
		message = message.replace('\r', ' ');
		message = message.replace('\n', ' ');
		message = message.replace(':', ' ');
		message = message.replace(',', ' ').trim();
	
		if (message.indexOf(' ') == -1) {
			smsProvider.sendSMS(sender, helpMessage);
			return;
		}
		else {
			// Remove unwanted characters
			// Trim away command
			message = message.substring(message.indexOf(' ')).trim().toUpperCase();
			logger.info( "Trimmed command: " + message );
			while (message.length() > 0) {
				// Get the first code
				String code = ""+message.charAt(0);
				// Remove the code from the string
				message = message.substring(1).trim();
				// Check if this is a proper character code. If not try again. 
				String id = maps.get(code);
				if (id != null) {
					String number = "";
					for (int i = 0; i < message.length(); i++) {
						if (message.charAt(i) > 47 && message.charAt(i) < 58) {
							number += message.charAt(i);
						}
						else {
							// Not a number. Break
							break;
						}
					}
					logger.info( "Adding:" + id +", number:"+number );
					
					if (!number.equals("")) {
						xml += "<dataValue dataElement=\""+id+"\" value=\""+number+"\" />\r\n";
					}
				}
				
			}
			
		}
		
		xml +=	"</dataValueSet>";
		
		logger.info( "XML= \r\n" + xml );
		System.out.println("XML= \r\n" + xml);
		if (dhisapi.submitXML(username, password, url, xml)) {
			smsProvider.sendSMS(sender, "Thank you for submitting the report "+serviceName+" for period "+dateFormat.format(new Date()));
		}
		else {
			smsProvider.sendSMS(sender, "Form submission failed. Please try again later.");
		}
			
	}

	public Map getMaps() {
		return maps;
	}

	public void setMaps(Map maps) {
		this.maps = maps;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
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

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}


}