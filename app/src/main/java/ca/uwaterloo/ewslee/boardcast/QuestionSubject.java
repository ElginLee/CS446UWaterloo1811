package ca.uwaterloo.ewslee.boardcast;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Elgin on 3/19/2018.
 */

public interface QuestionSubject {
    void registerObserver(QuestionObserver questionObserver);
    void removeObserver(String deviceID);
    void notifyObservers(String message);
}
