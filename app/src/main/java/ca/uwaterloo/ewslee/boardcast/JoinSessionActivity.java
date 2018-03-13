package ca.uwaterloo.ewslee.boardcast;

import android.Manifest;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elgin on 3/2/2018.
 */



public class JoinSessionActivity extends AppCompatActivity{
    private static final String TAG = "BoardCast";

    private GoogleApiClient mGoogleApiClient;
    private String mRemoteHostEndpoint;
    private boolean mIsConnected;
    private TextView mLogs;
    private ListView lv;
    private List<String> connectionsList;
    private ArrayAdapter<String> arrayAdapter;

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();

        lv = (ListView) findViewById(R.id.sessionsAvailableListView);
        connectionsList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, connectionsList);

        lv.setAdapter(arrayAdapter);

        checkGooglePlayServices();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();
                String[] itemSplit = item.split(" ");
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                pb.setVisibility(View.VISIBLE);
                connectToHost(itemSplit[itemSplit.length-1]);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        log("onConnected: start discovering hosts to send connection requests");
                        startDiscovery();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        log("onConnectionSuspended: " + i);
                        // Try to re-connect
                        mGoogleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        log("onConnectionFailed: " + connectionResult.getErrorCode());
                    }
                })
                .addApi(Nearby.CONNECTIONS_API)
                .build();
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

    private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
            return false;
        } else {
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            return true;
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        log("onStart: connect");
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, 1);
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop: disconnect");

        if (mGoogleApiClient.isConnected()) {
            if (!mIsConnected || TextUtils.isEmpty(mRemoteHostEndpoint)) {
                Nearby.Connections.stopDiscovery(mGoogleApiClient);
                return;
            }
            sendMessage("Client disconnecting");
            Nearby.Connections.disconnectFromEndpoint(mGoogleApiClient, mRemoteHostEndpoint);
            mRemoteHostEndpoint = null;
            mIsConnected = false;

            mGoogleApiClient.disconnect();
        }
    }

    private void startDiscovery() {
        log("startDiscovery");

        Nearby.Connections.startDiscovery(mGoogleApiClient, "ca.uwaterloo.ewslee.boardcast", new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        log("onEndpointFound:" + endpointId + ":" + info.getEndpointName());
                        if(!connectionsList.contains(info.getEndpointName() + " " + endpointId)) {
                            connectionsList.add(info.getEndpointName() + " " + endpointId);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                        pb.setVisibility(View.INVISIBLE);
                        //connectToHost(endpointId);
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        // An endpoint that was previously available for connection is no longer.
                        // It may have stopped advertising, gone out of range, or lost connectivity.
                        log("onEndpointLost:" + endpointId);
                    }
                },
                new DiscoveryOptions(Strategy.P2P_STAR)
        )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            log("Discovering...");
                        } else {
                            log("Discovering failed: " + status.getStatusMessage() + "(" + status.getStatusCode() + ")");
                        }
                    }
                });
    }

    private void connectToHost(String endpointId){
        Nearby.Connections
                .requestConnection(mGoogleApiClient, null, endpointId, new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                        log("onConnectionInitiated. Token: " + connectionInfo.getAuthenticationToken());
                        // Automatically accept the connection on both sides"
                        Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {

                            @Override
                            public void onPayloadReceived(String endpointId, Payload payload) {
                                if (payload.getType() == Payload.Type.BYTES) {
                                    receiveMessage(endpointId,payload);
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
                        log("onConnectionResult:" + endpointId + ":" + resolution.getStatus());
                        if (resolution.getStatus().isSuccess()) {
                            log("Connected successfully");
                            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                            pb.setVisibility(View.INVISIBLE);
                            Nearby.Connections.stopDiscovery(mGoogleApiClient);
                            mRemoteHostEndpoint = endpointId;
                            mIsConnected = true;
                        } else {
                            if (resolution.getStatus().getStatusCode() == ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED) {
                                log("The connection was rejected by one or both sides");
                            } else {
                                log("Connection to " + endpointId + " failed. Code: " + resolution.getStatus().getStatusCode());
                            }
                            mIsConnected = false;
                        }
                    }

                    @Override
                    public void onDisconnected(String endpointId) {
                        // We've been disconnected from this endpoint. No more data can be sent or received.
                        log("onDisconnected: " + endpointId);
                    }
                })
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            // We successfully requested a connection. Now both sides
                            // must accept before the connection is established.
                        } else {
                            // Nearby Connections failed to request the connection.
                        }
                    }
                });
    }


    private void receiveMessage(String endpointId, Payload payload){
        log("onPayloadReceived: " + new String(payload.asBytes()));
        initStudentLayout();
    }

    private void sendMessage(String message) {
        log("About to send message: " + message);
        Nearby.Connections.sendPayload(mGoogleApiClient, mRemoteHostEndpoint, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
    }

    private void initLayout() {
        setContentView(R.layout.join_session);
        mLogs = (TextView) findViewById(R.id.sessionlabel);

        Button hostBtn = (Button) findViewById(R.id.checkBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGoogleApiClient.isConnected()) {
                    log("Not connected");
                    return;
                }

                sendMessage("Hello, Things!");
            }
        });
    }

    private void initStudentLayout() {
        setContentView(R.layout.student_quiz);
       mLogs = (TextView) findViewById(R.id.sessionlabel);

        Button hostBtn = (Button) findViewById(R.id.sendMessageBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGoogleApiClient.isConnected()) {
                    log("Not connected");
                    return;
                }

                sendMessage("Hello, Things!");
            }
        });
    }

    private void log(String message) {
        Log.i(TAG, message);
        mLogs = (TextView) findViewById(R.id.sessionlabel);
        mLogs.setText(message + "\n" + mLogs.getText());
    }
}
