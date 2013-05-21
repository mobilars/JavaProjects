package org.hisp.dhis.smsin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.smsin.beans.DHISAPI;
import org.hisp.dhis.smsin.beans.SMSProvider;
import org.hisp.dhis.smsin.beans.SMSServiceBean;

@Controller
public class SMSIncomingController {	
	
	protected final Log logger = LogFactory.getLog(getClass());
	
    @Autowired
    private SMSServiceBean [] smsService; 
    
    @Autowired
    private SMSProvider smsProvider; 
    
	@Autowired
    private DHISAPI dhisapi; 

	// @RequestParam(value="id", required=false)
    @RequestMapping("/sms")
    public ModelAndView helloIndex(@RequestParam("sender") String sender, @RequestParam("message") String message) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main.jsp");
        boolean handled = false;
        for (int i = 0; i < smsService.length; i++) {
        	// Test each configured service if they want to handle this message. Give it to the first.
        	logger.info("Trying SMS Handler:"+smsService[i].getServiceName());
        	if (smsService[i].accept(sender,message)) {
        		handled = true;
        		break;
        	}
        }
        if (!handled) {
//        	logger.info(smsService.length+" SMS Services configured, but no local handler found for incoming SMS message:"+message);     
//        	System.out.println(smsService.length+" SMS Services configured, but no handler found for incoming SMS message:"+message); 
        	
        	logger.info(smsService.length+" No local handler. Forwarding:"+message);     
        	System.out.println(smsService.length+" No local handler. Forwarding:"+message); 
        	
        	try {
        		String querystring = "sender="+URLEncoder.encode(sender, "ISO-8859-1")+"&message="+URLEncoder.encode(message, "ISO-8859-1");
        		querystring = querystring.replace(' ', '+');
				dhisapi.forwardRequest(querystring);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//smsProvider.sendSMS(sender, "Your SMS did not match a configured command: "+message);
        }
        return mav;
    }
    
	// @RequestParam(value="id", required=false)
    @RequestMapping("/kannel")
    public ModelAndView kannelIndex(@RequestParam("recipient") String recipient, @RequestParam("message") String message) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("kannel.jsp");

    	try {
    		String urlstring = "http://localhost:6013/cgi-bin/sendsms?username=peter&password=ford&to=%2B{recipient}&text={message}";
    		urlstring = urlstring.replace("{recipient}",recipient);
    		urlstring = urlstring.replace("{message}",message.replace(' ', '+'));
    		
    		URL url = new URL(urlstring);
    		BufferedReader in = new BufferedReader(
    				new InputStreamReader(url.openStream()));

	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            System.out.println(inputLine);
	        in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return mav;
    }
    
    @RequestMapping("/")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main.jsp");
        mav.addObject("message", "That's wrong");
        return mav;
    }

}