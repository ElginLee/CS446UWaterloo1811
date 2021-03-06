package ca.uwaterloo.ewslee.boardcast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.jjoe64.graphview.GraphView;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import controllers.GradebookDBC;
import controllers.QuestionDBC;
import controllers.SessionDBC;

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

    Session session = new Session();
    private Utils u1 = new Utils();
    private String sessionName;
    private String id = "";
    boolean saveResponse =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_main);
        Intent intent = getIntent();
        session = (Session)intent.getSerializableExtra("Session");
        id = getIntent().getStringExtra("userid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureHostButton();
        //qn from harold
        //mockQn();
    }

    private void configureHostButton(){
        Button hostBtn = (Button) findViewById(R.id.hostBtn);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if(id.equals("anonymous")){
            checkBox.setVisibility(View.INVISIBLE);
        }
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText sessionInput   = (EditText)findViewById(R.id.sessionNameInput);
                sessionName = sessionInput.getText().toString();
                session.setName(sessionName);
                session.setCreatorID(id);
                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

                if(!id.equals("anonymous")){
                    saveResponse = checkBox.isChecked();
                }
                startHosting();
                mGoogleApiClient.connect();
                setContentView(R.layout.waiting_screen);
                configureQuestionButton();

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
                                            Context context = getApplicationContext();
                                            CharSequence text = endpointId;
                                            int duration = Toast.LENGTH_SHORT;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                            receiveStatus(new String(payload.asBytes()), endpointId);
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
                                if(state)
                                initWaitingScreen();
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

    private void initWaitingScreen(){
        ListView joinedStudent = (ListView)findViewById(R.id.StudentConnectionList);
        ArrayList<String> studentNameList = new ArrayList<String>();
        for (QuestionObserver observer: observerList) {
            studentNameList.add(observer.getStudentID()+"::"+observer.getDeviceID());
        }
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, studentNameList);
        // Set The Adapter
        joinedStudent.setAdapter(arrayAdapter);
        TextView size = (TextView)findViewById(R.id.totalSize);
        String count = joinedStudent.getAdapter().getCount() + " students joined.";
        size.setText(count);
    }

    int [] results = new int[4];
    boolean state = true;
    private void receiveStatus(String value, String deviceID){
         String[] result = u1.splitPayload(value);
        if(value.contains("[N]") && state ){

            for (QuestionObserver observer: observerList) {
                if(observer.getDeviceID().equals(deviceID))
                    observer.setStudentID(result[1]);
            }
            initWaitingScreen();

            configureQuestionButton();
        }

        if(value.contains("[R]")){
            QuestionObserver student= new Student();
            for (QuestionObserver observer: observerList) {
                if(observer.getDeviceID().equals(deviceID))
                    student = observer;
            }

            Question currentQuestion = session.getQuestion(currentQn);
            StudentAnswer answer = new StudentAnswer(student,result[1]);
            currentQuestion.addStudentAnswer(answer);
            results = currentQuestion.calculateResults();
        }
    }


    int currentQn=0;

    private void mockQn(){
        MCQuestion mcq = new MCQuestion(session.getSessionID(), "What state is Waterloo in Canada?");
        mcq.addAnswer("Ontario", true);
        mcq.addAnswer("Quebec", false);
        mcq.addAnswer("Nova Scotia", false);
        mcq.addAnswer("Alberta", false);
        session.addQuestion(mcq);
        session.addQuestion(new LongQuestion(session.getSessionID(), "Name the seventh planet from the sun.", "Uranus"));
    }

    private void configureQuestionButton(){

        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setText("Next");
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                results = new int[4];
                state = false;
                Question currentQuestion = session.getQuestion(currentQn);
                String text = currentQuestion.getStudentQuestion();
                notifyObservers(text);
                if(text.contains("[QL]")){
                    initLongResponseScreen(text);
                }
                else if(text.contains("[QM]")){
                    initMCResponseScreen(text);
                }


            }
        });
    }

    private void initMCResponseScreen(String text){

        Question currentQuestion = session.getQuestion(currentQn);
        String[] value = u1.splitPayload(text);
        value = u1.splitString(value[1]);
        String answer = currentQuestion.getCorrectAnswer();

            setContentView(R.layout.mc_graph);
            displayQuestion(value,answer);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    Graph g1 = new Graph();
                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    graph.removeAllSeries();
                    handler.postDelayed(this, 1000);

                    g1.drawMCGraph(graph,results);
                }
            };
            handler.postDelayed(r, 0000);
        configureResultButton(handler,r);
    }

    private void initLongResponseScreen(String text){
        Question currentQuestion = session.getQuestion(currentQn);
        String[] value = u1.splitPayload(text);
        value = u1.splitString(value[1]);
        String answer = currentQuestion.getCorrectAnswer();
            setContentView(R.layout.long_graph);
            displayLongQuestion(value,answer);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    Graph g1 = new Graph();
                    GraphView graph = (GraphView) findViewById(R.id.longgraph);
                    graph.removeAllSeries();
                    handler.postDelayed(this, 1000);

                    g1.drawLongGraph(graph,results);
                }
            };
            handler.postDelayed(r, 0000);

        configureResultButton(handler,r);
    }

    private void displayQuestion(String [] value, String answer){
        TextView qn = (TextView)findViewById(R.id.question);
        qn.setText(value[0]);
        TextView c1 = (TextView) findViewById(R.id.choice1);
        c1.setText("1. "+value[1]);
        TextView c2 = (TextView) findViewById(R.id.choice2);
        c2.setText("2. "+value[2]);
        TextView c3 = (TextView) findViewById(R.id.choice3);
        c3.setText("3. "+value[3]);
        TextView c4 = (TextView) findViewById(R.id.choice4);
        c4.setText("4. "+value[4]);

        switch(answer){
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

    private void displayLongQuestion(String [] value, String answer){
        TextView qn = (TextView)findViewById(R.id.question);
        qn.setText(value[0]);
        TextView c1 = (TextView) findViewById(R.id.choice1);
        c1.setText("Answer: "+answer);
    }

    private void configureResultButton(final Handler handler, final Runnable r){
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setText("Show answer");
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Question currentQuestion = session.getQuestion(currentQn);
                String text = currentQuestion.getCorrectAnswer();
                results = currentQuestion.calculateResults();
                notifyObservers("[R]="+text+u1.resultsToString(results));
                handler.removeCallbacks(r);
                if(currentQn !=session.getQuestionSize()-1) {
                    configureQuestionButton();
                    currentQn++;
                }
                else{
                    configureEndButton();
                }
            }
        });
    }

    private void configureEndButton(){
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setText("Save & Finish");
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                notifyObservers("[E]="+session.getQuestionSize());
                if(saveResponse){
                    insertIntoDatabase();

                }
                Question currentQuestion;
                ArrayList <String> pdfData = new ArrayList<String>();
                for(int i =0; i< session.getQuestionSize(); i++){
                    currentQuestion = session.getQuestion(i);
                    pdfData.add(currentQuestion.getPDFQuestion());
                }
                GradebookDBC gDBC = new GradebookDBC();
                gDBC.generateReport(pdfData,session.getName());
                //pass data to pdf
                Intent intent = new Intent(HostSessionActivity.this,DrawerActivity.class);
                intent.putExtra("userid", id);
                startActivity(intent);
            }
        });
    }

    public void insertIntoDatabase(){
        SessionDBC sDBC = new SessionDBC();
        int sessionID = sDBC.insertSession(session.getName(),session.getCreatorID());
        Question currentQuestion;
       for(int i =0; i< session.getQuestionSize(); i++){
         currentQuestion = session.getQuestion(i);
          currentQuestion.insertQuestionAnswer(sessionID);
        }
        GradebookDBC gDBC = new GradebookDBC();
        gDBC.updateGradebook(sessionID);
    }
}
