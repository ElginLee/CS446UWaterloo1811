package ca.uwaterloo.ewslee.boardcast;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import controllers.GradebookDAO;
import controllers.GradebookDBC;
import controllers.QuestionDAO;
import controllers.QuestionDBC;

public class GradebookActivity extends AppCompatActivity {

    private ListView gradebookList;
    GradebookDAO gdbc;
    String loginUser = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradebook);

        loginUser = getIntent().getStringExtra("userid");
        gdbc = new GradebookDBC();
        generateList();
    }

    private void generateList(){
        gradebookList = (ListView) findViewById(R.id.gradebook_listview);
        ArrayList<String[]> list = gdbc.getRecent(loginUser);
        ArrayList<String> grades = new ArrayList<String>();
        grades.add("All Grades");
        for(int i = 0; i < list.size(); i++){
            grades.add(list.get(i)[0] + "(" + list.get(i)[1] + ")");
        }
        String[] gradesList = new String[grades.size()];
        for(int i = 0; i < grades.size(); i++) gradesList[i] = grades.get(i);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, gradesList);
        gradebookList.setAdapter(adapter);

        gradebookList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(GradebookActivity.this, "Selected: " + i, Toast.LENGTH_SHORT).show();
                if(i == 0){
                    ArrayList<String[]> gl = gdbc.getRecent(loginUser);
                    try{
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), loginUser + "_allgrades.pdf");
                        file.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(file);

                        PdfDocument document = new PdfDocument();
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100,150,1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        paint.setTextSize(2);
                        paint.setLetterSpacing(0.5f);

                        int x = 10, y = 10;
                        String res = "Gradebook for " + loginUser + "\n";
                        //canvas.drawText(res, x, y, paint);
                        for(int j = 0; j < gl.size(); j++){
                            y += 10;
                            res = gl.get(j)[0];
                            res += ": ";
                            res += gl.get(j)[1];
                            res += "\n";
                            canvas.drawText(res, x, y, paint);
                        }

                        canvas.drawText(res, 10, 10, paint);
                        document.finishPage(page);
                        document.writeTo(fOut);
                        document.close();

                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(Uri.fromFile(file),"application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        startActivity(intent);
                    }catch(IOException e) {
                        Log.i("error", e.getLocalizedMessage() + getFilesDir().getAbsolutePath());
                    }catch(ActivityNotFoundException e){
                        Log.i("error", e.getLocalizedMessage());
                    }
                }
                else{
                }
            }
        });
    }
}
