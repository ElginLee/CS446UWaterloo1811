package ca.uwaterloo.ewslee.boardcast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elgin on 3/2/2018.
 */

public class HostSessionActivity extends AppCompatActivity implements QuestionSubject{

    private static final String TAG = "BoardCast";

    private GoogleApiClient mGoogleApiClient;
    private List<String> mRemotePeerEndpoints = new ArrayList<>();
    private List<Student> studentNameList = new ArrayList<>();
    private List<QuestionObserver> observerList = new ArrayList<>();
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    private TextView mLogs;
    private String sessionName;
    private List<String>  sessionStudent = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_session);
        configureHostButton();
    }

    private void configureHostButton(){
        Button hostBtn = (Button) findViewById(R.id.hostBtn);
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText sessionInput   = (EditText)findViewById(R.id.sessionNameInput);
                sessionName = sessionInput.getText().toString();
                startHosting();
                mGoogleApiClient.connect();
                setContentView(R.layout.waiting_screen);
                configureStartQuizButton();

            }
        });
    }

    protected  void startHosting(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        startAdvertising();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, 1);
        }
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != 1) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Nearby.Connections.stopAdvertising(mGoogleApiClient);

            if (!mRemotePeerEndpoints.isEmpty()) {
                Nearby.Connections.sendPayload(mGoogleApiClient, mRemotePeerEndpoints, Payload.fromBytes("Shutting down host".getBytes(Charset.forName("UTF-8"))));
                Nearby.Connections.stopAllEndpoints(mGoogleApiClient);
                mRemotePeerEndpoints.clear();
            }

            mGoogleApiClient.disconnect();
        }
    }

    private void startAdvertising() {

        Nearby.Connections
                .startAdvertising(mGoogleApiClient, sessionName, "ca.uwaterloo.ewslee.boardcast", new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                                // Automatically accept the connection on both sides"
                                Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {
                                    @Override
                                    public void onPayloadReceived(String endpointId, Payload payload) {
                                        if (payload.getType() == Payload.Type.BYTES) {
                                            recieveStatus(new String(payload.asBytes()), endpointId);
                                            Nearby.Connections.sendPayload(mGoogleApiClient, endpointId, Payload.fromBytes("ACK".getBytes(Charset.forName("UTF-8"))));
                                        }
                                    }

                                    @Override
                                    public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                                        // Provides updates about the progress of both incoming and outgoing payloads
                                    }
                                });
                            }

                            @Override
                            public void onConnectionResult(String endpointId, ConnectionResolution resolution) {
                                if (resolution.getStatus().isSuccess()) {
                                    if (!mRemotePeerEndpoints.contains(endpointId)) {
                                        mRemotePeerEndpoints.add(endpointId);
                                    }
                                    Student newStud = new Student();
                                    newStud.setDeviceID(endpointId);
                                    registerObserver(newStud);
                                }
                            }

                            @Override
                            public void onDisconnected(String endpointId) {
                                // We've been disconnected from this endpoint. No more data can be sent or received.
                                mRemotePeerEndpoints.remove(endpointId);
                                removeObserver(endpointId);
                                updateWaitingScreen();
                            }
                        },
                        new AdvertisingOptions(Strategy.P2P_STAR)
                )
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(Connections.StartAdvertisingResult result) {
                        if (result.getStatus().isSuccess()) {
                        }
                    }
                });
    }

    private void sendMessage(String message) {
        for (String temp : mRemotePeerEndpoints) {
            Nearby.Connections.sendPayload(mGoogleApiClient, temp, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
        }
    }

    public void registerObserver(QuestionObserver questionObserver){
        if(!observerList.contains(questionObserver)) {
            observerList.add(questionObserver);
        }
    }

    public void removeObserver(String deviceID){
        for (QuestionObserver observer: observerList) {
             if(observer.getDeviceID().equals(deviceID))
                 observerList.remove(observer);
        }
    }

    public void notifyObservers(String message){
        for (QuestionObserver observer: observerList) {
            observer.sendMessage(message, mGoogleApiClient);
        }
    }

    private void updateWaitingScreen(){
        ListView joinedStudent = (ListView)findViewById(R.id.StudentConnectionList);
        ArrayList<String> studentNameList = new ArrayList<String>();
        for (QuestionObserver observer: observerList) {
            studentNameList.add(observer.getStudentID());
        }
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, studentNameList);
        // Set The Adapter
        joinedStudent.setAdapter(arrayAdapter);
        TextView size = (TextView)findViewById(R.id.totalSize);
        String count = joinedStudent.getAdapter().getCount() + " students joined.";
        size.setText(count);
    }

    private void configureStartQuizButton(){
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                notifyObservers("[S]");
            }
        });
    }

    private void recieveStatus(String value, String deviceID){
        String[] result = splitPayload(value);
        if(value.contains("[N]")){

            for (QuestionObserver observer: observerList) {
                if(observer.getDeviceID().equals(deviceID))
                    observer.setStudentID(result[1]);
            }
            updateWaitingScreen();
        }
    }

    private String[] splitPayload(String message){
        String[] result = message.split("=");
        return result;
    }
}
