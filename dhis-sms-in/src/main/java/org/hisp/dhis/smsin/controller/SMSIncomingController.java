package org.hisp.dhis.smsin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.smsin.beans.SMSProvider;
import org.hisp.dhis.smsin.beans.SMSServiceBean;

@Controller
public class SMSIncomingController {	
	
	protected final Log logger = LogFactory.getLog(getClass());
	
    @Autowired
    private SMSServiceBean [] smsService; 
    
    @Autowired
    private SMSProvider smsProvider; 

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
        	logger.info(smsService.length+" SMS Services configured, but no handler found for incoming SMS message:"+message);     
        	System.out.println(smsService.length+" SMS Services configured, but no handler found for incoming SMS message:"+message);  
        	smsProvider.sendSMS(sender, "Your SMS did not match a configured command: "+message);
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