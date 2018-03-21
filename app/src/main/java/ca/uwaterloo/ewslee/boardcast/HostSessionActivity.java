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
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_session);
        mLogs = (TextView) findViewById(R.id.hostSessionLabel);
        log("Android Things project is ready!");
        configureHostButton();
        configureTestButton();
    }

    private void configureHostButton(){
        Button hostBtn = (Button) findViewById(R.id.hostBtn);
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startHosting();
                mGoogleApiClient.connect();
            }
        });
    }

    private void configureTestButton(){
        Button testBtn = (Button) findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //sendMessage("This is your host broadcasting");
                notifyObservers("This is your host broadcasting");
            }
        });
    }

    protected  void startHosting(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        log("onConnected: advertises on the network as the host");
                        startAdvertising();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        log("onConnectionSuspended: " + i);
                        mGoogleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        log("onConnectionFailed: " + connectionResult);
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
        EditText sessionInput   = (EditText)findViewById(R.id.sessionNameInput);
        String sessionName = sessionInput.getText().toString();
        Nearby.Connections
                .startAdvertising(mGoogleApiClient, sessionName, "ca.uwaterloo.ewslee.boardcast", new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                                log("onConnectionInitiated. Token: " + connectionInfo.getAuthenticationToken());
                                // Automatically accept the connection on both sides"
                                Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {
                                    @Override
                                    public void onPayloadReceived(String endpointId, Payload payload) {
                                        if (payload.getType() == Payload.Type.BYTES) {
                                            log("onPayloadReceived: " + new String(payload.asBytes()));
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
                                log("onConnectionResult");
                                if (resolution.getStatus().isSuccess()) {
                                    if (!mRemotePeerEndpoints.contains(endpointId)) {
                                        mRemotePeerEndpoints.add(endpointId);
                                    }
                                    Student newStud = new Student();
                                    newStud.setDeviceID(endpointId);
                                    registerObserver(newStud);
                                    log("Connected! (endpointId=" + endpointId + ")");
                                } else {
                                    log("Connection to " + endpointId + " failed. Code: " + resolution.getStatus().getStatusCode());
                                }
                            }

                            @Override
                            public void onDisconnected(String endpointId) {
                                // We've been disconnected from this endpoint. No more data can be sent or received.
                                Log.i(TAG, "onDisconnected: " + endpointId);
                            }
                        },
                        new AdvertisingOptions(Strategy.P2P_STAR)
                )
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(Connections.StartAdvertisingResult result) {
                        log("startAdvertising:onResult:" + result);
                        if (result.getStatus().isSuccess()) {
                            log("Advertising...");
                        }
                    }
                });
    }

    private void sendMessage(String message) {
        log("About to send message: " + message);
        for(String temp : mRemotePeerEndpoints) {
            Nearby.Connections.sendPayload(mGoogleApiClient, temp, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
        }
    }

    private void log(String message) {
        Log.i(TAG, message);
        mLogs.setText(message + "\n" + mLogs.getText());
    }

    public void registerObserver(QuestionObserver questionObserver){
        if(!observerList.contains(questionObserver)) {
            observerList.add(questionObserver);
        }
    }
    public void removeObserver(QuestionObserver questionObserver){
        if(observerList.contains(questionObserver)) {
            observerList.remove(questionObserver);
        }
    }
    public void notifyObservers(String message){
        for (QuestionObserver observer: observerList) {
            observer.sendMessage(message, mGoogleApiClient);
        }
    }
}
