package org.mushare.rate.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dklap on 1/3/2017.
 */


public class HttpDataHandler {

    public static int sendGet(String url, String params, RequestHeader[] headers, Appendable result) {
        int responseCode = 0;
        BufferedReader in = null;
        try {
            String urlName = url + "?" + params;
            URL realUrl = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            if (headers != null) {
                for (RequestHeader header : headers) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            responseCode = conn.getResponseCode();
            if (responseCode < 400)
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return responseCode;
    }

    public static int sendGet(String url, String params, Appendable result) {
        return sendGet(url, params, null, result);
    }

    public static int sendPost(String url, String params, RequestHeader[] headers, Appendable result) {
        int responseCode = 0;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            if (headers != null) {
                for (RequestHeader header : headers) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params);
            out.flush();
            responseCode = conn.getResponseCode();
            if (responseCode < 400)
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return responseCode;
    }

}