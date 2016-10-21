package com.example.matte.mypet_testlogin;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import java.io.Reader;
import java.io.InputStreamReader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class ServerComm {

    private static final String SERVER_ADDRS = "https://webdev.dibris.unige.it/~S3951060/mobile_login.php";

    public JSONObject makePostRequest(String parameters) throws IOException {
        InputStream is = null;

        JSONObject jObj = null;

        //################### SSL CERTIFICATE AVOIDER ##############
        //###### Fa male alla salute, starne alla larga ############
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        //##############################################################

        try {

            //################### SSL CERTIFICATE AVOIDER ##############
            //###### Fa male alla salute, starne alla larga ############

            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                            + session.getPeerHost());
                    return true;
                }
            };

            //##############################################################

            URL url = new URL(SERVER_ADDRS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpsURLConnection connSsl = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            //################### SSL CERTIFICATE AVOIDER ##############
            //###### Fa male alla salute, starne alla larga ############
            connSsl.setHostnameVerifier(hv);
            //##############################################################

            //Stream per i parametri
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(parameters);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("MyPet", "The response is: " + response);

            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            Log.d("MyPet", "The content is: " + contentAsString);

            try {
                jObj = new JSONObject(contentAsString);
            } catch (JSONException e) {
                return null;
            }

            // Makes sure that the InputStream is closed after the app has
            // finished using it.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return jObj;
    }

    public String readIt(InputStream stream) throws IOException {

//        Reader reader;
//        reader = new InputStreamReader(stream, "UTF-8");
//        char[] buffer = new char[len];
//        reader.read(buffer);
//        return new String(buffer);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        while(true) {
            int n = stream.read(buf);
            if( n < 0 ) break;
            baos.write(buf, 0, n);
        }

        byte data[] = baos.toByteArray();
        return new String(data);
    }
}
