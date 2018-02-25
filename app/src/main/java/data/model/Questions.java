package data.model;

/**
 * Created by kianl on 2/23/2018.
 */

public class Questions {

    public static final String TAG = Questions.class.getSimpleName();
    public static final String TABLE = "Questions";
    // Labels Table Columns names
    public static final String KEY_SessionId = "SessionId";
    public static final String KEY_Question = "Name";
    public static final String KEY_TIMESTAMP = "timestampColumn";

    private String sessionId;
    private String sessionName;
    private String timeStamp;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getTimeStamp() {
        return sessionName;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
