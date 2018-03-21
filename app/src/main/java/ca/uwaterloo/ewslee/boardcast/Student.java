package ca.uwaterloo.ewslee.boardcast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.common.api.GoogleApiClient;

import java.nio.charset.Charset;

/**
 * Created by Elgin on 20/3/2018.
 */

public class Student implements QuestionObserver{
    String deviceID;
    public void sendMessage(String message, GoogleApiClient mGoogleApiClient){
        Nearby.Connections.sendPayload(mGoogleApiClient, deviceID, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
    }
    public void setDeviceID(String deviceID){
        this.deviceID = deviceID;
    }
}
