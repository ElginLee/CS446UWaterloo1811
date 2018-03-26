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
import java.util.ArrayList;

/**
 * Created by #YEOGUOKUAN on 21/3/2018.
 */

public class GradebookDBC implements GradebookDAO {
    static String address = "http://192.168.0.18";
    public ArrayList<String[]> getRecent(String userid){
        ArrayList<String[]> gradeList = new ArrayList<String[]>();

        try {
            URL url = new URL(address + "/select_gradebook_recent.php?userid=" + userid);
            String parameters = "userid=" + userid;
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gradeList;
    }
}
