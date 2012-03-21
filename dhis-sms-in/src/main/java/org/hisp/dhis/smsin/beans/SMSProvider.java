package org.hisp.dhis.smsin.beans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.smsin.utils.Base64;

public class SMSProvider {

	protected final Log logger = LogFactory.getLog(getClass());

	private String username;
	private String password;
	private String sender;
	
    public void sendSMS(String recipient, String message) {
        try {
            // Construct data
            String data = "";
            /*
             * Note the suggested encoding for certain parameters, notably
             * the username, password and especially the message.  ISO-8859-1
             * is essentially the character set that we use for message bodies,
             * with a few exceptions for e.g. Greek characters.  For a full list,
             * see:  http://www.bulksms.co.uk/docs/eapi/submission/character_encoding/
             */
            data += "username=" + URLEncoder.encode(username, "ISO-8859-1");
            data += "&password=" + URLEncoder.encode(password, "ISO-8859-1");
            data += "&message=" + URLEncoder.encode(message, "ISO-8859-1");
            data += "&want_report=1";
            data += "&sender="+sender;
            data += "&routing_group=2";
            data += "&msisdn="+recipient;

            // Send data
            URL url = new URL("http://www.bulksms.co.uk:5567/eapi/submission/send_sms/2/2.0");
            /*
            * If your firewall blocks access to port 5567, you can fall back to port 80:
            * URL url = new URL("http://www.bulksms.co.uk/eapi/submission/send_sms/2/2.0");
            * (See FAQ for more details.)
            */

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
                System.out.println(line);
            }
            wr.close();
            rd.close();
            
            System.out.println("Sent SMS to "+recipient);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}


}
