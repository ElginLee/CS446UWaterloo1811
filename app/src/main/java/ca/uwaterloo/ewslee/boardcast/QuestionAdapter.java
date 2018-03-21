package ca.uwaterloo.ewslee.boardcast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ca.uwaterloo.ewslee.boardcast.R;

/**
 * Created by Harold on 21-Mar-18.
 */

public class QuestionAdapter extends ArrayAdapter {
    private final Activity context;
    private final String[] questionArray;

    public QuestionAdapter(Activity context, String[] questionArray) {
        super(context, R.layout.question_layout, questionArray);

        this.context = context;
        this.questionArray = questionArray;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.question_layout, null, true);

        TextView questionDisplayField = (TextView) rowView.findViewById(R.id.questionDisplayTextViewID);

        questionDisplayField.setText(questionArray[position]);

        return rowView;
    }
}
