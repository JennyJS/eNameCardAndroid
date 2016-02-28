package com.scu.jenny.enamecard.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jenny on 2/28/16.
 */
public class HttpHelper {
    private static final String domain = "http://enamecard-jennyjs.rhcloud.com";
    private static final int TIME_OUT = 5000;

    public static String sendGet(String path) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(domain + path);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(TIME_OUT);
            c.setReadTimeout(TIME_OUT);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public static String sendPost(String path, String jsonString) throws IOException {
        URL url;
        URLConnection urlConn;
        DataOutputStream printout;
        DataInputStream input;
        url = new URL (domain + path);
        urlConn = url.openConnection();
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setConnectTimeout(TIME_OUT);
        urlConn.setReadTimeout(TIME_OUT);
        urlConn.setRequestProperty("Content-Type", "application/json");
        urlConn.connect();

        printout = new DataOutputStream(urlConn.getOutputStream());
        printout.write(jsonString.getBytes("UTF-8"));
        printout.flush();
        printout.close();

        return parseResponse((HttpURLConnection)urlConn);
    }


    private static String parseResponse(HttpURLConnection connection) {
        StringBuilder sb = new StringBuilder();
        try {
            int status = connection.getResponseCode();
            switch (status) {
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    break;
                default:
                    return "{\"status\":\"failure\"}";
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return sb.toString();
        }
    }
}
