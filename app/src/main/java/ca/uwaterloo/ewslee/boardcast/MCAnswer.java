package ca.uwaterloo.ewslee.boardcast;

/**
 * Created by Harold on 20-Mar-18.
 */

public class MCAnswer {
    private int answerID;
    private int questionID;
    private int sessionID;
    private String answerText;
    private boolean isCorrect;

    public MCAnswer(int answerID, int questionID, int sessionID, String answerText, boolean isCorrect) {
        this.answerID = answerID;
        this.questionID = questionID;
        this.sessionID = sessionID;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
