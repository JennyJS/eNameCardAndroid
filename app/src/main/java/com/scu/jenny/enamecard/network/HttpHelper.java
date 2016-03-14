package com.scu.jenny.enamecard.network;

import com.scu.jenny.enamecard.storage.KVStore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jenny on 2/28/16.
 */
public class HttpHelper {
    private static final String domain = "http://enamecard-jennyjs.rhcloud.com";
    private static final int TIME_OUT = 5000;

    public static String sendGet(String path) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(domain + path);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            addSecretHeader(connection);
            connection.connect();
            return parseResponse(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String sendPost(String path, String jsonString) throws IOException {
        return commendSend("POST", path, jsonString);
    }

    public static String sendPut(String path, String jsonString) throws IOException{
        return commendSend("PUT", path, jsonString);
    }

    public static String sendDelete(String path, String jsonString) throws IOException {
        return commendSend("DELETE", path, jsonString);
    }

    public static String commendSend(String method, String path, String jsonString) throws IOException {
        URL url = new URL (domain + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        addSecretHeader(connection);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        osw.write(jsonString);
        osw.flush();
        osw.close();
        return parseResponse(connection);
    }



    private static void addSecretHeader(URLConnection connection) {
        if (KVStore.getInstance().get("secret", "").length() > 0) {
            connection.setRequestProperty("secret", KVStore.getInstance().get("secret", ""));
        }
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
