package ca.uwaterloo.ewslee.boardcast;

/**
 * Created by Harold on 20-Mar-18.
 */

public class LongQuestion extends Question {
    private int maxMarks;

    public LongQuestion (int sessionID, String questionText, int maxMarks) {
        super(sessionID, questionText);
        this.maxMarks = maxMarks;
    }

    public String getQuestionDisplay() {
        return this.getQuestionText() + " [" + maxMarks + "]\n\n";
    }
}
