package controllers;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
    static String address = "http://gknyeo.000webhostapp.com/";

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
            for(int i = 0; i < results.length - 1; i+=3){
                String temp[] = {results[i], results[i+1], results[i+2]};
                output.add(temp);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public void generateReport(ArrayList<String> output, String sessionName){
        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), sessionName + "_report.pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100,250,1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(2);
            paint.setLetterSpacing(0.5f);

            int x = 10, y = 10;
            String res = "Session Name: " + sessionName + "\n";
            canvas.drawText(res, x, y, paint);
            y += 5;
            for(int j = 0; j < output.size(); j++){
                String temp[] = output.get(j).split("~");
                y += 6;
                res = "Question " + (j+1) + ":";
                canvas.drawText(res, x, y, paint);
                y += 3;
                canvas.drawText(temp[0], x, y, paint);
                String[] temp2 = temp[1].split(";");
                for(int n = 0; n < temp2.length; n++){
                    y += 3;
                    canvas.drawText(temp2[n], x, y, paint);
                }
                y += 3;
                res = "Correct / Incorrect: " + temp[2];
                canvas.drawText(res, x, y, paint);
            }

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

        }catch(IOException e) {
            Log.i("error", e.getLocalizedMessage());
        }catch(ActivityNotFoundException e){
            Log.i("error", e.getLocalizedMessage());
        }
    }

    public void updateGradebook(int sessionID){
        try {
            URL url = new URL(address + "/updateGradebook.php?sessionid=" + sessionID);
            String parameters = "sessionid=" + sessionID;
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
