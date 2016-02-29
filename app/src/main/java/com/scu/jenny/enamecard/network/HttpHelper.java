package com.scu.jenny.enamecard.network;

import com.scu.jenny.enamecard.storage.KVStore;

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
        HttpURLConnection connection = null;
        try {
            URL u = new URL(domain + path);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            commenInitConnection(connection);
            connection.connect();
            int status = connection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
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
        }
        return null;
    }

    public static String sendPost(String path, String jsonString) throws IOException {
        URL url;
        URLConnection connection;
        DataOutputStream printout;
        DataInputStream input;
        url = new URL (domain + path);
        connection = url.openConnection();

        commenInitConnection(connection);

        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();

        printout = new DataOutputStream(connection.getOutputStream());
        printout.write(jsonString.getBytes("UTF-8"));
        printout.flush();
        printout.close();

        return parseResponse((HttpURLConnection)connection);
    }

    private static void commenInitConnection(URLConnection connection) {

        if (KVStore.getInstance().get("secret", "").length() > 0) {
            connection.setRequestProperty("secret", KVStore.getInstance().get("secret", ""));
        }

        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);
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
