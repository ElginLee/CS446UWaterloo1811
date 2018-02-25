package data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

import data.model.Student;

/**
 * Created by kianl on 2/24/2018.
 */

public class StudentViewModel extends AndroidViewModel {

    private final LiveData<List<Student>> studentList;
    private AppDatabase appDatabase;

    public StudentViewModel(Application application ){
        super(application);

        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        studentList = appDatabase.studentDao().getStudentList(1);
    }

    public LiveData<List<Student>> getStudentList() {
        return studentList;
    }
}
