package controllers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by #YEOGUOKUAN on 21/3/2018.
 */

public class UserDBC implements UserDAO{
    static String address = "http://gknyeo.000webhostapp.com/";

    public boolean login(String userid, String password){
        try {
            URL url = new URL(address + "/login.php?userid=" + userid + "&password=" + password);
            String parameters = "userid=" + userid + "&password=" + password;
            String data = URLEncoder.encode(parameters, "UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            InputStream stream = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;
            String response = "";
            while ((decodedString = in.readLine()) != null) response = decodedString;
            in.close();
            Log.w("yeps",response);
            if(response.equals("match")) return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertUser(String userid, String password, String email){
        try {
            URL url = new URL(address + "/insert_user.php?userid=" + userid +
                    "&password=" + password +
                    "&email=" + email);
            String parameters = "userid=" + userid + "&password=" + password + "&email=" + email;
            String data = URLEncoder.encode(parameters, "UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            InputStream stream = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;
            String response = "";
            while ((decodedString = in.readLine()) != null) response = decodedString;
            in.close();
            if(response.equals("fail")) return false;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
