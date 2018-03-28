package ca.uwaterloo.ewslee.boardcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Harold on 26-Mar-18.
 */

public class QuestionCreationActivity extends AppCompatActivity {
    String id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addquestion_layout);

        final Session session = new Session();
        final ArrayList<String> questionDisplayList = new ArrayList<>();
        id = getIntent().getStringExtra("userid");
        final EditText questionTextTextBox = (EditText) findViewById(R.id.questionTextTextBoxID);
        final EditText answerTextTextBox1 = (EditText) findViewById(R.id.answerTextTextBox1ID);
        final EditText answerTextTextBox2 = (EditText) findViewById(R.id.answerTextTextBox2ID);
        final EditText answerTextTextBox3 = (EditText) findViewById(R.id.answerTextTextBox3ID);
        final EditText answerTextTextBox4 = (EditText) findViewById(R.id.answerTextTextBox4ID);

        final Spinner questionTypeSpinner = (Spinner) findViewById(R.id.questionTypeSpinnerID);
        ArrayAdapter<CharSequence> questionTypeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.question_type_array, android.R.layout.simple_spinner_dropdown_item);
        questionTypeSpinner.setAdapter(questionTypeSpinnerAdapter);
        questionTypeSpinner.setSelection(0);
        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch(pos) {
                    case 0:
                        answerTextTextBox1.setHint("Answer 1");
                        answerTextTextBox2.setVisibility(View.VISIBLE);
                        answerTextTextBox3.setVisibility(View.VISIBLE);
                        answerTextTextBox4.setVisibility(View.VISIBLE);
                        break;
                    default:
                        answerTextTextBox1.setHint("Answer");
                        answerTextTextBox2.setVisibility(View.INVISIBLE);
                        answerTextTextBox3.setVisibility(View.INVISIBLE);
                        answerTextTextBox4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Spinner correctAnswerSpinner = (Spinner) findViewById(R.id.correctAnswerSpinnerID);
        ArrayAdapter<CharSequence> correctAnswerSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.correct_answer_array, android.R.layout.simple_spinner_dropdown_item);
        correctAnswerSpinner.setAdapter(correctAnswerSpinnerAdapter);
        correctAnswerSpinner.setSelection(0);

        final ListView listView = (ListView) findViewById(R.id.pastQuestionsListViewID);

        Button clearButton = (Button) findViewById(R.id.clearButtonID);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionTextTextBox.setText("");
                answerTextTextBox1.setText("");
                answerTextTextBox2.setText("");
                answerTextTextBox3.setText("");
                answerTextTextBox4.setText("");
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButtonID);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, questionDisplayList);
        listView.setAdapter(adapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (questionTypeSpinner.getSelectedItemPosition()) {
                    case 0:
                        MCQuestion mcq = new MCQuestion(0, questionTextTextBox.getText().toString());
                        mcq.addAnswer(answerTextTextBox1.getText().toString(), correctAnswerSpinner.getSelectedItemPosition() == 0);
                        mcq.addAnswer(answerTextTextBox2.getText().toString(), correctAnswerSpinner.getSelectedItemPosition() == 1);
                        mcq.addAnswer(answerTextTextBox3.getText().toString(), correctAnswerSpinner.getSelectedItemPosition() == 2);
                        mcq.addAnswer(answerTextTextBox4.getText().toString(), correctAnswerSpinner.getSelectedItemPosition() == 3);
                        session.addQuestion(mcq);
                        questionDisplayList.add(0, mcq.getQuestionDisplay());
                        break;
                    case 1:
                        LongQuestion longq = new LongQuestion(0, questionTextTextBox.getText().toString(), answerTextTextBox1.getText().toString());
                        session.addQuestion(longq);
                        questionDisplayList.add(0, longq.getQuestionDisplay());
                        break;
                    default:
                }
                questionTextTextBox.setText("");
                answerTextTextBox1.setText("");
                answerTextTextBox2.setText("");
                answerTextTextBox3.setText("");
                answerTextTextBox4.setText("");

                adapter.notifyDataSetChanged();
            }
        });

        Button doneButton = (Button) findViewById(R.id.doneButtonID);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionCreationActivity.this, DrawerActivity.class);
                intent.putExtra("userid", id);
                intent.putExtra("Session",session);
                startActivity(intent);
            }
        });
    }
}
