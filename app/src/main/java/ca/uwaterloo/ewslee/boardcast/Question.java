package ca.uwaterloo.ewslee.boardcast;

/**
 * Created by Harold on 20-Mar-18.
 */

public abstract class Question {
    private int questionID;
    private int sessionID;
    private String questionText;

    public Question(int sessionID, String questionText) {
        this.questionID = 0;
        this.sessionID = sessionID;
        this.questionText = questionText;
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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public abstract String getQuestionDisplay();
}