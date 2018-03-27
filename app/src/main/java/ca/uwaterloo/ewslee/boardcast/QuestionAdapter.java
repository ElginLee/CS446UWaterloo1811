package ca.uwaterloo.ewslee.boardcast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ca.uwaterloo.ewslee.boardcast.R;

/**
 * Created by Harold on 21-Mar-18.
 */

public class QuestionAdapter extends ArrayAdapter {
    private final Activity context;
    private String[] questionArray;

    public QuestionAdapter(Activity context, String[] questionArray) {
        super(context, R.layout.question_layout, questionArray);

        this.context = context;
        this.questionArray = questionArray;
    }

    public void setQuestionArray(String[] questionArray) {
        this.questionArray = questionArray;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.question_layout, null, true);

        TextView questionText = (TextView) rowView.findViewById(R.id.questionTextID);
        questionText.setText(questionArray[position]);

/*        final EditText textBox1 = (EditText) rowView.findViewById(R.id.answerTextTextBox1ID);
        final EditText textBox2 = (EditText) rowView.findViewById(R.id.answerTextTextBox2ID);
        final EditText textBox3 = (EditText) rowView.findViewById(R.id.answerTextTextBox3ID);
        final EditText textBox4 = (EditText) rowView.findViewById(R.id.answerTextTextBox4ID);

        Spinner questionTypeSpinner = (Spinner) rowView.findViewById(R.id.questionTypeSpinnerID);
        ArrayAdapter<CharSequence> questionTypeSpinnerAdapter = ArrayAdapter.createFromResource(context, R.array.question_type_array, android.R.layout.simple_spinner_dropdown_item);
        questionTypeSpinner.setAdapter(questionTypeSpinnerAdapter);
        questionTypeSpinner.setSelection(0);
        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch(pos) {
                    case 0:
                        textBox1.setVisibility(View.VISIBLE);
                        textBox2.setVisibility(View.VISIBLE);
                        textBox3.setVisibility(View.VISIBLE);
                        textBox4.setVisibility(View.VISIBLE);
                        break;
                    default:
                        textBox1.setVisibility(View.INVISIBLE);
                        textBox2.setVisibility(View.INVISIBLE);
                        textBox3.setVisibility(View.INVISIBLE);
                        textBox4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        return rowView;
    }
}