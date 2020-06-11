package com.example.tastemap;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class APIThread extends Thread
{
    final static String APIKey = "KakaoAK 894a5114a4b85c075dd6b9d292bee563";


    private int responseCode;
    private String response;
    private String address;
    private ListData coodData;

    JSONParser parser = new JSONParser();

    int error = -1; //에러코드

    public APIThread(String address)
    {
        this.address = address;
    }

    public ListData getCoodData()
    {
        return coodData;
    }

    public void run() {
        try {
            String apiURL = "https://dapi.kakao.com/v2/local/search/address.json?query="
                    + URLEncoder.encode(address, "UTF-8");
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization",APIKey);
            Log.d("response",con.getRequestProperty("Authorization"));
            responseCode = con.getResponseCode();
            response =readURLStream(responseCode,con);
            Log.d("response",response);
            coodData = extraction(response);
            error=0;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            error=1; //인코딩 에러 발생
        } catch (ProtocolException e) {
            e.printStackTrace();
            error=2; //통신 에러 발생
        } catch (MalformedURLException e) {
            e.printStackTrace();
            error=3;//http 명시 에러
        } catch (IOException e) {
            e.printStackTrace();
            error=4;// 네트워크 에러 발생
        }catch (Exception e)
        {
            e.printStackTrace();
            error=5;
        }
    }

    public String readURLStream(int responseCode, HttpURLConnection con) {
        StringBuilder response = new StringBuilder();
        BufferedReader bufferedReader;
        String inputLine;
        try {
            if (responseCode == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }


    public ListData extraction(String response)
    {
        ListData data;
        String returnAddress;
        double latitude;
        double longitude;
        try
        {
            JSONObject jsonObject = (JSONObject) parser.parse(response);
            JSONArray documents = (JSONArray) jsonObject.get("documents");
            Log.d("documents", documents.toString());
            JSONObject address = (JSONObject) documents.get(0);
            Log.d("address", documents.toString());
            if(address.get("road_address")==null)
            {
                returnAddress = address.get("address_name").toString();
                longitude = Double.valueOf(address.get("x").toString());
                latitude = Double.valueOf(address.get("y").toString());
                data = new ListData(returnAddress, latitude, longitude);
                return data;
            }
            else
            {

            }

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
