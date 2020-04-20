package com.philng.MeetupTrivia;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class TriviaDatabaseInterface
{
    public static JSONObject getTriviaQuestions(
            int amount,
            String difficulty,
            String category
    ) throws Exception
    {
        HttpClient client = createAcceptSelfSignedCertificateClient();

        String url = String.format("https://opentdb.com/api.php?amount=%d", amount);
        if( difficulty != null && !difficulty.equals("any"))
            url = url + "&difficulty=" + difficulty;

        if( category != null && !category.equals("any"))
            url = url + "&category=" + category;


        HttpGet get = new HttpGet( url );

        HttpResponse response = client.execute(get);

        //verify the valid error code first
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200)
        {
            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
        }

        //Now pull back the response object
        HttpEntity httpEntity = response.getEntity();
        String apiOutput = EntityUtils.toString(httpEntity);

        JSONObject jsonObject = new JSONObject(apiOutput);

        return jsonObject;

    }

    private static HttpClient createAcceptSelfSignedCertificateClient()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
    {

        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                System.out.println("getAcceptedIssuers =============");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
                System.out.println("checkClientTrusted =============");
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
                System.out.println("checkServerTrusted =============");
            }
        } }, new SecureRandom());

        SSLSocketFactory sf = new SSLSocketFactory(sslContext);
        Scheme httpsScheme = new Scheme("https", 443, sf);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        // apache HttpClient version >4.2 should use BasicClientConnectionManager
        HttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(sslContext)
                .build();

        return httpClient;
    }
}
