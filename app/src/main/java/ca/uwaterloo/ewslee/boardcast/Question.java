package ca.uwaterloo.ewslee.boardcast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Harold on 20-Mar-18.
 */

public abstract class Question {
    private int questionID;
    private int sessionID;
    private String questionText;
    protected List<StudentAnswer> studentResponse;

    public Question(int sessionID, String questionText) {
        this.questionID = 0;
        this.sessionID = sessionID;
        this.questionText = questionText;
        studentResponse = new ArrayList<StudentAnswer>();
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

    public void addStudentAnswer(StudentAnswer answer){
        if(studentResponse !=null) {
            removeStudentAnswer(answer);
        }
        studentResponse.add(answer);
    }

    public void removeStudentAnswer(StudentAnswer value){
        for (Iterator<StudentAnswer> iterator = studentResponse.iterator(); iterator.hasNext();) {
            StudentAnswer answer = iterator.next();
            if(answer.getStudent()==value.getStudent()) {
                iterator.remove();
            }
        }
    }

    public abstract String getQuestionDisplay();

    public abstract String getStudentQuestion();

    public abstract int[] calculateResults();

    public abstract String getCorrectAnswer();

    public abstract void insertQuestionAnswer(int sessionID);
}