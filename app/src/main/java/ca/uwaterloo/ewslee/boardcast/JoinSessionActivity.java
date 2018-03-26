package ca.uwaterloo.ewslee.boardcast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jjoe64.graphview.GraphView;
import com.skyfishjy.library.RippleBackground;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

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
    private ListView lv;
    private List<String> connectionsList;
    private ArrayAdapter<String> arrayAdapter;
    private String studentID = "";
    private Utils u1 = new Utils();

    private View view1, view2;
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
        view1 = getLayoutInflater().inflate(R.layout.join_main, null);
        view2 = getLayoutInflater().inflate(R.layout.join_success, null);
        setContentView(view1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.sessionsAvailableListView);
        connectionsList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, connectionsList);

        lv.setAdapter(arrayAdapter);
        studentID = getIntent().getStringExtra("userid");
        checkGooglePlayServices();
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();
                String[] itemSplit = item.split(" ");
                //ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                //pb.setVisibility(View.VISIBLE);
                final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
                rippleBackground.startRippleAnimation();
                ImageView findImage = (ImageView)findViewById(R.id.centerImage);
                findImage.setVisibility(View.VISIBLE);
                connectToHost(itemSplit[itemSplit.length-1]);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        startDiscovery();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // Try to re-connect
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
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, 1);
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

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

        Nearby.Connections.startDiscovery(mGoogleApiClient, "ca.uwaterloo.ewslee.boardcast", new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        if(!connectionsList.contains(info.getEndpointName() + " " + endpointId)) {
                            connectionsList.add(info.getEndpointName() + " " + endpointId);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        //ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                        //pb.setVisibility(View.INVISIBLE);
                        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
                        ImageView findImage = (ImageView)findViewById(R.id.centerImage);
                        findImage.setVisibility(View.INVISIBLE);
                        rippleBackground.stopRippleAnimation();
                        //connectToHost(endpointId);
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        // An endpoint that was previously available for connection is no longer.
                        // It may have stopped advertising, gone out of range, or lost connectivity.
                    }
                },
                new DiscoveryOptions(Strategy.P2P_STAR)
        )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                        } else {
                        }
                    }
                });
    }

    private void connectToHost(String endpointId){
        Nearby.Connections
                .requestConnection(mGoogleApiClient, null, endpointId, new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                        // Automatically accept the connection on both sides"
                        Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {

                            @Override
                            public void onPayloadReceived(String endpointId, Payload payload) {
                                if (payload.getType() == Payload.Type.BYTES && endpointId !="ACK") {
                                    recieveStatus(new String(payload.asBytes()), endpointId);
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
                            //ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
                            //pb.setVisibility(View.INVISIBLE);
                            final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
                            rippleBackground.stopRippleAnimation();
                            ImageView findImage = (ImageView)findViewById(R.id.centerImage);
                            findImage.setVisibility(View.INVISIBLE);
                            rippleBackground.setVisibility(View.INVISIBLE);
                            Nearby.Connections.stopDiscovery(mGoogleApiClient);
                            mRemoteHostEndpoint = endpointId;
                            mIsConnected = true;
                            sendMessage("[N]="+studentID);
                            lv.setVisibility(View.INVISIBLE);
                            TextView mLogs = (TextView) findViewById(R.id.sessionlabel);
                            mLogs.setText("Connected");
                            mLogs.setVisibility(View.VISIBLE);
                            setContentView(view2);
                        } else {
                            if (resolution.getStatus().getStatusCode() == ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED) {
                            } else {
                            }
                            mIsConnected = false;
                        }
                    }

                    @Override
                    public void onDisconnected(String endpointId) {
                        // We've been disconnected from this endpoint. No more data can be sent or received.
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


    private void recieveStatus(String value,String deviceID){

        String[] result = u1.splitPayload(value);
        if(result[0].contains("[QM]")){
            questionType=result[0];
            initMCQuizLayout(result[1]);
        }
        else if (result[0].contains("[QL]")){
            questionType=result[0];
            initLongQuizLayout(result[1]);
        }
        else if(result[0].contains("[R]")){
            if(questionType.contains("[QM]"))
                initMCResultLayout(result[1]);
            else if (questionType.contains("[QL]"))
                initLongResultLayout(result[1]);
        }

        else if(result[0].contains("[E]")){
            initFinalResultLayout(result[1]);
        }
    }

    private void sendMessage(String message) {
        Nearby.Connections.sendPayload(mGoogleApiClient, mRemoteHostEndpoint, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
    }

    String question = "";
    String questionType = "";
    String studentAnswer = "";
    int score = 0;
    private void initMCResultLayout(String value) {
        Graph g1 = new Graph();
        final int[] results = u1.splitResults(value);
        String [] output = u1.splitString(question);
        String answer = u1.getAnswer(value);
        if(studentAnswer.equals(answer)){
            score++;
        }
        setContentView(R.layout.mc_graph);
        displayQuestion(output, answer);
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

                Graph g1 = new Graph();
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                g1.drawMCGraph(graph,results);
            }
        };
        handler.postDelayed(r, 0000);
        Button btn = (Button) findViewById(R.id.startBtn);
        btn.setVisibility(View.INVISIBLE);

    }

    private void initLongResultLayout(String value) {
        Graph g1 = new Graph();
        final int[] results = u1.splitResults(value);
        String [] output = u1.splitString(question);
        String answer = u1.getAnswer(value);
        if(studentAnswer.equals(answer)){
            score++;
        }
        setContentView(R.layout.long_graph);
        displayLongQuestion(output, answer);
        TextView c1 = (TextView) findViewById(R.id.choice1);
        c1.setText("Answer: "+answer);
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

                Graph g1 = new Graph();
                GraphView graph = (GraphView) findViewById(R.id.longgraph);
                graph.removeAllSeries();
                g1.drawLongGraph(graph,results);
            }
        };
        handler.postDelayed(r, 0000);
        Button btn = (Button) findViewById(R.id.startBtn);
        btn.setVisibility(View.INVISIBLE);

    }

    private void displayQuestion(String [] value, String answer){
        if(answer.equals("-1")){
            TextView qn = (TextView) findViewById(R.id.question);
            qn.setText(value[0]);
            TextView c1 = (TextView) findViewById(R.id.radioButton1);
            c1.setText("1. "+value[1]);
            TextView c2 = (TextView) findViewById(R.id.radioButton2);
            c2.setText("2. "+value[2]);
            TextView c3 = (TextView) findViewById(R.id.radioButton3);
            c3.setText("3. "+value[3]);
            TextView c4 = (TextView) findViewById(R.id.radioButton4);
            c4.setText("4. "+value[4]);
        }
        else {
            TextView qn = (TextView) findViewById(R.id.question);
            qn.setText(value[0]);
            TextView c1 = (TextView) findViewById(R.id.choice1);
            c1.setText("1. "+value[1]);
            TextView c2 = (TextView) findViewById(R.id.choice2);
            c2.setText("2. "+value[2]);
            TextView c3 = (TextView) findViewById(R.id.choice3);
            c3.setText("3. "+value[3]);
            TextView c4 = (TextView) findViewById(R.id.choice4);
            c4.setText("4. "+value[4]);

            switch (answer) {
                case "1":
                    c1.setTextColor(Color.RED);
                    break;
                case "2":
                    c2.setTextColor(Color.RED);
                    break;
                case "3":
                    c3.setTextColor(Color.RED);
                    break;
                case "4":
                    c4.setTextColor(Color.RED);
                    break;
                default:
                    break;
            }
        }
    }


    private void initMCQuizLayout(String value) {
        studentAnswer = "-1";
        setContentView(R.layout.student_mcquiz);
        question = value;
        String [] output = u1.splitString(question);
        displayQuestion(output,"-1");
        Button hostBtn = (Button) findViewById(R.id.startBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGoogleApiClient.isConnected()) {
                    return;
                }

                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if(selectedId != -1){
                    studentAnswer = ""+radioButton.getText().charAt(0);
                    TextView currentChoice = (TextView) findViewById(R.id.currentChoice);
                    currentChoice.setText("Current Choice : "+studentAnswer);
                    sendMessage("[R]="+studentAnswer);
                }

            }
        });
    }

    private void initLongQuizLayout(String value) {
        studentAnswer = "-1";
        setContentView(R.layout.student_longquiz);
        question = value;
        String [] output = u1.splitString(question);
        displayLongQuestion(output,"-1");
        Button hostBtn = (Button) findViewById(R.id.startBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGoogleApiClient.isConnected()) {
                    return;
                }

                EditText et = (EditText) findViewById(R.id.editText);
                studentAnswer = ""+et.getText().toString();
                TextView currentChoice = (TextView) findViewById(R.id.currentChoice);
                currentChoice.setText("Current Answer : "+studentAnswer);
                sendMessage("[R]="+studentAnswer);
            }
        });
    }

    private void displayLongQuestion(String [] value, String answer){
        if(answer.equals("-1")){
            TextView qn = (TextView) findViewById(R.id.question);
            qn.setText(value[0]);
            EditText et = (EditText) findViewById(R.id.editText);
            et.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        }
        else {
            TextView qn = (TextView) findViewById(R.id.question);
            qn.setText(value[0]);

        }
    }

    private void initFinalResultLayout(String value){
        setContentView(R.layout.student_finalscore);
        int wrong = Integer.parseInt(value)-score;
        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        mPieChart.addPieSlice(new PieModel("Correct", score, Color.parseColor("#80ff80")));
        mPieChart.addPieSlice(new PieModel("Wrong", wrong, Color.parseColor("#FE6DA8")));
        Button hostBtn = (Button) findViewById(R.id.startBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinSessionActivity.this,DrawerActivity.class);
                intent.putExtra("userid", studentID);
                startActivity(intent);
            }
            });
    }

}
