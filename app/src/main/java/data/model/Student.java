package data.model;

/**
 * Created by kianl on 2/23/2018.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = { "sessionId", "studentId" },foreignKeys = @ForeignKey(entity = Session.class, parentColumns = "sessionId" ,childColumns = "sessionId",onDelete = CASCADE))
public class Student {
    @NonNull
    private int sessionId;
    @NonNull
    private String studentId;

    public Student(int sessionId, String studentId){
        this.sessionId = sessionId;
        this.studentId = studentId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getStudentId() {
        return studentId;
    }

    }

