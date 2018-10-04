package me.buscar_ra.conectar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.buscar_ra.links.SedEducacao;

public class Conectar {
	
	private String url;
	private String post;
	private final String USER_AGENT = "Felipe157/5.0";
	
	public Conectar(SedEducacao site, String post) {

		this.url = site.url;
		this.post = post;

	}

	
	public String resposta() {
		try {
			return sendPost();
		} catch (Exception e) {
		  return "Falha";
		}
	}
	
	private String sendPost() throws Exception {

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(post);
		wr.flush();
		wr.close();
		

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response.toString();
	}
}
