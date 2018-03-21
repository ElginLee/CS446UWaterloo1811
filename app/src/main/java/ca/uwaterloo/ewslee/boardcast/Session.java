package ca.uwaterloo.ewslee.boardcast;

import java.util.ArrayList;

/**
 * Created by Harold on 20-Mar-18.
 */

public class Session {
    private int sessionID;
    private String name;
    private int creatorID;
    private ArrayList<Question> questionList;
    private int nextQuestionID;

    public Session(int sessionID, String name, int creatorID) {
        this.sessionID = sessionID;
        this.name = name;
        this.creatorID = creatorID;
        this.questionList = new ArrayList<>();
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void addQuestion(Question question) {
        question.setQuestionID(nextQuestionID);
        questionList.add(question);
        nextQuestionID++;
    }

    public void removeQuestion(int questionID) {
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getQuestionID() == questionID)
                questionList.remove(i);
            else if (questionList.get(i).getQuestionID() > questionID) {
                Question temp = questionList.get(i);
                temp.setQuestionID(temp.getQuestionID() - 1);
                questionList.set(i, temp);
            }
        }
        nextQuestionID--;
    }

    public String[] getQuestions() {
        String[] questionArray = new String[questionList.size()];

        for (int i = 0; i < questionList.size(); i++)
            questionArray[i] = questionList.get(i).getQuestionDisplay();

        return questionArray;
    }
}
