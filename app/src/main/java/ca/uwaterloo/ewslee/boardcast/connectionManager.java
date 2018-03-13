package ca.uwaterloo.ewslee.boardcast;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

public class connectionManager {
    private static final String TAG = "BoardCast";

    private GoogleApiClient mGoogleApiClient;
    private String mRemoteHostEndpoint;
    private boolean mIsConnected;
    private TextView mLogs;
    private ListView lv;
    private List<String> connectionsList;
    private ArrayAdapter<String> arrayAdapter;


}
