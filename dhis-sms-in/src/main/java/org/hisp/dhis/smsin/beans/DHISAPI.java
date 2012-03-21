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

public class DHISAPI {

	protected final Log logger = LogFactory.getLog(getClass());
	
	public boolean submitXML(String username, String password, String argUrl, String requestXml)
	{
		try
		{
			String authStr = username + ":" + password;
	        String authEncoded = Base64.encodeBytes(authStr.getBytes());

		    URL url = new URL( argUrl );
		    URLConnection con = url.openConnection();
	        con.setRequestProperty("Authorization", "Basic " + authEncoded);
		    
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setConnectTimeout( 20000 );  // long timeout, but not infinite
		    con.setReadTimeout( 20000 );

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "application/xml" );

		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    writer.write( requestXml );
		    writer.flush();
		    writer.close();

		    // reading the response
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    String result = buf.toString();
		    System.out.println( "\nResponse from server after POST:\n" + result );
		    
		    return true;
		    
		}
		catch( Throwable t )
		{
			System.out.println("Exception while uploading to DHIS: "+t);
		    return false;
		}
	}


}
