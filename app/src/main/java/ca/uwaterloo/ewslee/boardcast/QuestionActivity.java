package ca.uwaterloo.ewslee.boardcast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by Harold on 21-Mar-18.
 */

public class QuestionActivity extends AppCompatActivity {
    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionlist_layout);

        Session session = new Session(1, "test", "1");
        for (int i = 1; i <= 10; i++) {
            MCQuestion mcq = new MCQuestion(session.getSessionID(), "Question " + i + " Text");
            for (int j = 1; j <= 4; j++) {
                mcq.addAnswer("Answer " + j, j == 3);
            }
            session.addQuestion(mcq);
        }
        session.addQuestion(new LongQuestion(session.getSessionID(), "Long Question Text", "123"));

        QuestionAdapter adapter = new QuestionAdapter(this, session.getQuestions());

        listView = (ListView) findViewById(R.id.listviewID);
        listView.setAdapter(adapter);
    }
}
