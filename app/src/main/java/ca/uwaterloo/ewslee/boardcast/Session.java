package ca.uwaterloo.ewslee.boardcast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Harold on 20-Mar-18.
 */

public class Session implements Serializable {
    private int sessionID;
    private String name;
    private String creatorID;
    private ArrayList<Question> questionList;
    private int nextQuestionID;

    public Session() {
        this.questionList = new ArrayList<>();
    }

    public Session(int sessionID, String name, String creatorID) {
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

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void addQuestion(Question question) {
        question.setQuestionID(nextQuestionID);
        questionList.add(question);
        nextQuestionID++;
    }

    public void setQuestionList(ArrayList<Question> questionList){
        this.questionList = questionList;
    }

    public ArrayList<Question> getQuestionList(){
        return questionList;
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

    public int getQuestionSize(){
        return questionList.size();
    }

    public String[] getQuestions() {
        String[] questionArray = new String[questionList.size()];

        for (int i = 0; i < questionList.size(); i++)
            questionArray[i] = questionList.get(i).getQuestionDisplay();

        return questionArray;
    }

    public Question getQuestion(int id){
        for(Question qn : questionList){
            if(qn.getQuestionID()==(id)){
                return qn;
            }
        }
        return null;
    }


}
