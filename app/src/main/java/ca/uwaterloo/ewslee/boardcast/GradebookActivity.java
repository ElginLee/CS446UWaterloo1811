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

public class GradebookActivity extends AppCompatActivity {

    private ListView gradebookList;
    GradebookDAO gdbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradebook);

        gdbc = new GradebookDBC();
        generateList();
    }

    private void generateList(){
        gradebookList = (ListView) findViewById(R.id.gradebook_listview);
        ArrayList<String[]> list = gdbc.getRecent(getIntent().getStringExtra("userid"));
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
                Toast.makeText(GradebookActivity.this, "Selected: " + i, Toast.LENGTH_SHORT).show();

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.pdf");
                try{
                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);

                    PdfDocument document = new PdfDocument();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100,100,1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();

                    canvas.drawText("lolwhut", 10, 10, paint);
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
        });
    }
}
