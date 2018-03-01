package ca.uwaterloo.ewslee.boardcast;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import data.StudentViewAdapter;
import data.StudentViewModel;
import data.model.Student;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kianl on 2/24/2018.
 */

public class tempActivity extends AppCompatActivity {
    private StudentViewModel studentViewModel;
    private static final String TAG = tempActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.templayout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final StudentViewAdapter adapter = new StudentViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        studentViewModel.getStudentList().observe(this,new Observer<List<Student>>() {
            @Override
            public void onChanged(@Nullable final List<Student> students) {
                // Update the cached copy of the words in the adapter.
                adapter.setStudent(students);

            }
        });

        configureStartButton();
    }

    private void configureStartButton(){
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(tempActivity.this,ResponseGraphActivity.class));
            }
        });
    }

    }
