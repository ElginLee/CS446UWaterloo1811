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

    public int getID(String userid, int pos){
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
            return Integer.parseInt(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

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

            String results[] = response.split(";");
            for(int i = 0; i < results.length - 1; i+=2){
                String temp[] = {results[i], results[i+1]};
                Log.d("yeppie",temp[0]+temp[1]);
                gradeList.add(temp);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gradeList;
    }

    public ArrayList<String[]> getQuizResult(String userid, int pos){
        ArrayList<String[]> output = new ArrayList<String[]>();

        try {
            URL url = new URL(address + "/select_gradebook_specific.php?userid=" + userid + "&pos=" + pos);
            String parameters = "userid=" + userid + "&pos=" + pos;
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

            String results[] = response.split(";");
            for(int i = 0; i < results.length - 6; i+=6){
                String temp[] = {results[i], results[i+1], results[i+2], results[i+3], results[i+4], results[i+5]};
                output.add(temp);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}
