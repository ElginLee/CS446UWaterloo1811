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

public class QuestionDBC implements QuestionDAO {
    static String address = "http://192.168.0.18";

    public void insertQuestion(int questionID,
                               int sessionID,
                               String questionText,
                               String answerText1,
                               String answerText2,
                               String answerText3,
                               String answerText4,
                               int isCorrect){

        try {
            String parameters = "questionid=" + questionID +
                    "&sessionid=" + sessionID +
                    "&questiontext=" + questionText +
                    "&answertext1=" + answerText1 +
                    "&answertext2=" + answerText2 +
                    "&answertext3=" + answerText3 +
                    "&answertext4=" + answerText4 +
                    "&iscorrect=" + isCorrect;
            String data = URLEncoder.encode(parameters, "UTF-8");
            URL url = new URL(address + "/insert_question.php?" +parameters);
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void insertQuestion(int questionID,
                               int sessionID,
                               String questionText,
                               String answerText){

        try {
            String parameters = "questionid=" + questionID +
                    "&sessionid=" + sessionID +
                    "&questiontext=" + questionText +
                    "&answertext=" + answerText;
            String data = URLEncoder.encode(parameters, "UTF-8");
            URL url = new URL(address + "/insert_long_question.php?" +parameters);
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
