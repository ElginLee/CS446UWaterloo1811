package data.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import data.model.Student;

/**
 * Created by kianl on 2/23/2018.
 */

@Dao
public interface StudentDao {
    @Insert
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("SELECT * FROM Student where sessionId = :sessionId")
    LiveData<List<Student>> getStudentList(int sessionId);

    @Query("SELECT * FROM Student where sessionId = :sessionId")
    List<Student> getsList(int sessionId);

}

