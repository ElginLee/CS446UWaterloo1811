package data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import data.Dao.StudentDao;
import data.model.Student;

/**
 * Created by kianl on 2/24/2018.
 */

public class StudentRepo {
    private StudentDao studentDao;
    private LiveData<List<Student>> studentList;

    public StudentRepo(Application application, int sessionId){
        AppDatabase appDatabase =AppDatabase.getAppDatabase(application);
        studentDao = appDatabase.studentDao();
        studentList = studentDao.getStudentList(sessionId);
    }

    LiveData<List<Student>> getStudentList() {
        return studentList;
    }

    public void insert (Student student) {
        new insertAsyncTask(studentDao).execute(student);
    }

    private static class insertAsyncTask extends AsyncTask<Student, Void, Void> {

        private StudentDao mAsyncTaskDao;

        insertAsyncTask(StudentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Student... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
