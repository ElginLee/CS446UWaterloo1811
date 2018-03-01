package ca.uwaterloo.ewslee.boardcast;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import data.StudentViewModel;
import data.model.Student;

/**
 * Created by kianl on 2/23/2018.
 */

public class WaitingScreenActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        StudentViewModel studentViewModel;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_screen);
        ListView joinedStudent = (ListView)findViewById(R.id.StudentConnectionList);
        ArrayList<String> studentNameList = new ArrayList<String>();
        List<Student> studentList;
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        studentList = studentViewModel.getStudentList().getValue();
        for(int i = 0; i < studentList.size(); i++) {
                studentNameList.add(studentList.get(i).getStudentId());
        }
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, studentNameList);
        // Set The Adapter
        joinedStudent.setAdapter(arrayAdapter);
        TextView size = (TextView)findViewById(R.id.totalSize);
        String count = joinedStudent.getAdapter().getCount() + " students joined.";
        size.setText(count);
        configureStartButton();
    }

    private void configureStartButton(){
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(WaitingScreenActivity.this,ResponseGraphActivity.class));
            }
        });
    }

}
