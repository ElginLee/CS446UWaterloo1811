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
 * Created by #YEOGUOKUAN on 26/3/2018.
 */

public class StudentAnswerDBC implements StudentAnswerDAO {
    static String address = "http://gknyeo.000webhostapp.com/";
    public void insertRecord(String studentid, int sessionid, int questionid, String answerid){
        try {
            URL url = new URL(address + "/insert_studentanswer.php?studentid=" + studentid + "&sessionid=" + sessionid + "&questionid=" + questionid + "&answerid=" + answerid);
            String parameters = "studentid=" + studentid + "&sessionid=" + sessionid + "&questionid=" + questionid + "&answerid=" + answerid;
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
