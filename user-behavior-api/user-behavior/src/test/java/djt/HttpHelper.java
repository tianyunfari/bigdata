package djt;

import org.apache.commons.httpclient.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpHelper {
	
	public static HttpClient httpClient=new HttpClient();
	public static final String loc="file:D:/zukgit/usercenter/be-ugs-api/src/main/resources/spring/sping-main.xml";
	public static final String parivateKey="538b520311686319";
	public static String uri;
	public static String domain;
	public static String clientId;
	public static String lpsust;

	public static String env = "local";

	static {
		if (env.equals("local")) {
			uri = "http://localhost:8080/usercenter/";
			domain="localhost";
		}

		if (env.equals("dev")) {
			uri = "http://10.100.13.144:8080/usercenter/";
			domain="10.100.13.124:8080";
		}

		if (env.equals("local") || env.equals("dev")) {
			clientId="-1672019870_a23_480_v2000326_10000_i867695020019393_1478073108062_60";
			lpsust="ZAgAAAAAAAGE9MTAwNzc1NTk3MzgmYj0yJmM9NCZkPTE0NzMwJmU9QzgyQkY3QzU1OTFDNzUxNEFGQUY3MTYyMTJBQTNFQzMxJmg9MTQ3ODEzODg0NzEyNyZpPTQzMjAwJm89ODY3Njk1MDIwMDE5MzkzJnA9aW1laSZxPTAmdXNlcm5hbWU9MTg3MDEyNDgxOTcmaWw9Y24tFW6dxvpbTbEX956BNmse";
		}

		if (env.equals("test")) {
			uri = "http://test.uc.zui.lenovomm.com/usercenter/";
			domain="test.uc.zui.lenovomm.com";
			clientId="769172223_a23_480_v2000326_10000_i867695020019393_1471585270440_221";
			lpsust="ZAgAAAAAAAGE9MTAwMjQ3MTY5MjImYj0yJmM9NCZkPTE0NzMwJmU9NDk3RjVEOTNFNUQ0MDY3Qzg1QzVGREUzOEZDMDdFQjYxJmg9MTQ3MTU4NTI3NzIyNSZpPTQzMjAwJm89ODY3Njk1MDIwMDE5MzkzJnA9aW1laSZxPTAmdXNlcm5hbWU9MTgxMDEwMjE0NDUmaWw9Y26ODJki27QKq7tsKQ4GFjh2";
		}
	}
	
	public static HttpClient getHttpClient() {
		Cookie clientIdCookie = new Cookie();
		clientIdCookie.setDomain(domain);
		clientIdCookie.setPath("/");
		clientIdCookie.setName("clientid");
		clientIdCookie.setValue(clientId);

		Cookie tokenCookie = new Cookie();
		tokenCookie.setDomain(domain);
		tokenCookie.setPath("/");
		tokenCookie.setName("lpsust");
		tokenCookie.setValue(lpsust);

		HttpState httpState = new HttpState();
		httpState.addCookie(clientIdCookie);
		httpState.addCookie(tokenCookie);

		httpClient.setState(httpState);
		//httpClient.getHostConfiguration().setProxy("127.0.0.1", 8888);

		return httpClient;
	}

	public static String getHttpRequestResult(HttpMethod method) throws IOException {
		StringBuilder result = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader br = null;

		try {
			int status = method.getStatusCode();
			inputStream = method.getResponseBodyAsStream();
			if (inputStream == null) {
				System.out.println("inputStream is close");
			}

			if (status == HttpStatus.SC_NOT_MODIFIED) {
				System.out.println("hit cache,result=" + inputStream);
			}

			if (status != HttpStatus.SC_OK) {
				System.out.println("Not 200 status code");
			}

			br = new BufferedReader(new InputStreamReader(inputStream));
			String str = null;

			while ((str = br.readLine()) != null) {
				result.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			method.releaseConnection();

			if (inputStream != null) {
				inputStream.close();
			}

			if (br != null) {
				br.close();
			}
		}

		return result.toString();
	}

	public static void setHttpClient(HttpClient httpClient) {
		HttpHelper.httpClient = httpClient;
	}
	
}
