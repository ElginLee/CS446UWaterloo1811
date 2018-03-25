package ca.uwaterloo.ewslee.boardcast;

import java.util.Arrays;

/**
 * Created by Harold on 20-Mar-18.
 */

public class LongQuestion extends Question {
    private String answer;


    public LongQuestion (int sessionID, String questionText, String answer) {
        super(sessionID, questionText);
        this.answer = answer;
    }

    public String getQuestionDisplay() {
        return this.getQuestionText() + "\n\n";
    }

    public String getStudentQuestion() {
        return this.getQuestionText() + " [" + answer + "]\n\n";
    }

    public int[] calculateResults(){
        int[] result = new int[2];
        Arrays.fill(result, 0);
        return result;
    }

    public String getCorrectAnswer(){
        return answer;
    }
}
