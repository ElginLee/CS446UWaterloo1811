package data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

/**
 * Created by kianl on 2/23/2018.
 */

@Entity

public class Session {
    @PrimaryKey(autoGenerate = true)
    public int sessionId;
    private String sessionName;
    @TypeConverters(DateConverter.class)
    public Date timeStamp;


    public Session(int sessionId, String sessionName, Date timeStamp) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.timeStamp = timeStamp;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }


}
