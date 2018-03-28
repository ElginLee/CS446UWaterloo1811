package controllers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by #YEOGUOKUAN on 27/3/2018.
 */

public class SessionDBC implements SessionDAO {
    static String address = "http://gknyeo.000webhostapp.com/";

    public int insertSession(String sessionName, String creatorID){
        try {
            String parameters = "sessionname=" + sessionName +
                    "&creatorid=" + creatorID;
            String data = URLEncoder.encode(parameters, "UTF-8");
            URL url = new URL(address + "/insert_session.php?" +parameters);
            Log.d("data", url.toString());
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
            return Integer.parseInt(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
