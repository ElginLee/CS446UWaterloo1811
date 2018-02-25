package ca.uwaterloo.ewslee.boardcast;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import data.StudentViewAdapter;
import data.StudentViewModel;
import data.model.Student;
import android.util.Log;

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
        Log.d(TAG, "1");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final StudentViewAdapter adapter = new StudentViewAdapter(this);
        Log.d(TAG, "2");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "3");
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        Log.d(TAG, "4");
        studentViewModel.getStudentList().observe(this,new Observer<List<Student>>() {
            @Override
            public void onChanged(@Nullable final List<Student> students) {
                // Update the cached copy of the words in the adapter.
                adapter.setStudent(students);
            }
        });


    }
}
