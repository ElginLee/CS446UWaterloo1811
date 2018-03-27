package ca.uwaterloo.ewslee.boardcast;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Elgin on 3/19/2018.
 */

public interface QuestionObserver {
    void sendMessage(String message, GoogleApiClient mGoogleApiClient);
    void setDeviceID(String deviceID);
    String getDeviceID();
    void setStudentID(String studentID);
    String getStudentID();
}
