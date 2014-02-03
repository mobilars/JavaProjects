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
import java.net.MalformedURLException;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.smsin.utils.Base64;

public class DHISAPI {

	protected final Log logger = LogFactory.getLog(getClass());

	private String username;
	private String password;
	private String dhisUrl;

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

	public boolean submitXML(String username, String password, String argUrl,
			String requestXml) {
		try {
			String authStr = username + ":" + password;
			String authEncoded = Base64.encodeBytes(authStr.getBytes());

			URL url = new URL(argUrl);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Authorization", "Basic " + authEncoded);

			// specify that we will send output and accept input
			con.setDoInput(true);
			con.setDoOutput(true);

			con.setConnectTimeout(20000); // long timeout, but not infinite
			con.setReadTimeout(20000);

			con.setUseCaches(false);
			con.setDefaultUseCaches(false);

			// tell the web server what we are sending
			con.setRequestProperty("Content-Type", "application/xml");

			OutputStreamWriter writer = new OutputStreamWriter(
					con.getOutputStream());
			writer.write(requestXml);
			writer.flush();
			writer.close();

			// reading the response
			InputStreamReader reader = new InputStreamReader(
					con.getInputStream());

			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[2048];
			int num;

			while (-1 != (num = reader.read(cbuf))) {
				buf.append(cbuf, 0, num);
			}

			String result = buf.toString();

			if (result.indexOf("SMS success") != -1) {
				logger.info("SMS Success response from server (may not mean it worked)");
			} else {
				logger.info("SMS negative response from server (may not mean it didn't work)");
			}
			// logger.debug("Response from server after POST:\n" + result);

			return true;

		} catch (Throwable t) {
			logger.info("Exception while uploading to DHIS: " + t);
			return false;
		}
	}

	public boolean forwardRequest(String queryString) {

		// testIt();

		if (dhisUrl.startsWith("https")) {
			return forwardHTTPSRequest(dhisUrl, username, password, queryString);
		} else {
			return forwardRequest(dhisUrl, username, password, queryString);
		}
	}

	public boolean forwardHTTPSRequest(String urlString, String u, String p,
			String queryString) {

		String https_url = urlString + '?' + queryString;
		logger.info("HTTPS url:" + https_url);
		
		URL url;
		try {

			url = new URL(https_url);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			
			String authStr = u + ":" + p;
			String authEncoded = Base64.encodeBytes(authStr.getBytes());
			con.setRequestProperty("Authorization", "Basic " + authEncoded);

			con.setRequestMethod("GET");
			
			con.setConnectTimeout(20000); // long timeout, but not infinite
			con.setReadTimeout(20000);

			con.setUseCaches(false);
			con.setDefaultUseCaches(false);

			// tell the web server what we are sending
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
						
			//logger.info("Response Code : " + con.getResponseCode());
			//logger.info("Cipher Suite : " + con.getCipherSuite());
			//logger.info("\n");
			
			//con.setDoInput(true);
			//con.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(
					con.getOutputStream());
			// writer.write( "?"+queryString );
			writer.write("");
			writer.flush();
			writer.close();

			// reading the response
			InputStreamReader reader = new InputStreamReader(
					con.getInputStream());

			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[2048];
			int num;

			while (-1 != (num = reader.read(cbuf))) {
				buf.append(cbuf, 0, num);
			}

			String result = buf.toString();

			if (result.indexOf("SMS success") != -1) {
				logger.info("SMS Success response from server (may not mean it worked) "+con.getResponseCode());
			} else {
				logger.info("SMS negative response from server (may not mean it didn't work) "+con.getResponseCode());
			}
			logger.debug("Response from server after POST:\n" + result);
			logger.debug("Server url:" + urlString);

			return true;

			// dump all the content
			// print_content(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean forwardRequest(String urlString, String u, String p,
			String queryString) {
		try {

			String authStr = u + ":" + p;
			String authEncoded = Base64.encodeBytes(authStr.getBytes());

			URL url = new URL(urlString + "?" + queryString);
			// URL url = new URL( dhisUrl );
			URLConnection con = url.openConnection();
			con.setRequestProperty("Authorization", "Basic " + authEncoded);

			// specify that we will send output and accept input
			con.setDoInput(true);
			con.setDoOutput(true);

			con.setConnectTimeout(20000); // long timeout, but not infinite
			con.setReadTimeout(20000);

			con.setUseCaches(false);
			con.setDefaultUseCaches(false);

			// tell the web server what we are sending
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			OutputStreamWriter writer = new OutputStreamWriter(
					con.getOutputStream());
			// writer.write( "?"+queryString );
			writer.write("");
			writer.flush();
			writer.close();

			// reading the response
			InputStreamReader reader = new InputStreamReader(
					con.getInputStream());

			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[2048];
			int num;

			while (-1 != (num = reader.read(cbuf))) {
				buf.append(cbuf, 0, num);
			}

			String result = buf.toString();

			if (result.indexOf("SMS success") != -1) {
				logger.info("SMS Success response from server (may not mean it worked)");
			} else {
				logger.info("SMS negative response from server (may not mean it didn't work)");
			}
			logger.debug("Response from server after POST:\n" + result);
			logger.debug("Server url:" + urlString);

			return true;

		} catch (Throwable t) {
			logger.error("Exception while uploading to DHIS: " + t);
			return false;
		}
	}

	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				// ip address of the service URL(like.23.28.244.244)
//				if (hostname.equals("88.80.189.129"))
//					return true;
//				return false;
				return true;
			}

		});
	}

	private void print_https_cert(HttpsURLConnection con) {

		if (con != null) {

			try {

				logger.info("Response Code : " + con.getResponseCode());
				logger.info("Cipher Suite : " + con.getCipherSuite());
				logger.info("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					logger.info("Cert Type : " + cert.getType());
					logger.info("Cert Hash Code : " + cert.hashCode());
					logger.info("Cert Public Key Algorithm : "
							+ cert.getPublicKey().getAlgorithm());
					logger.info("Cert Public Key Format : "
							+ cert.getPublicKey().getFormat());
					logger.info("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void print_content(HttpsURLConnection con) {
		if (con != null) {

			try {

				logger.info("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null) {
					System.out.println(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
