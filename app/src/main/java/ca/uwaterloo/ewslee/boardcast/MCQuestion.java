package ca.uwaterloo.ewslee.boardcast;

import java.util.ArrayList;

/**
 * Created by Harold on 20-Mar-18.
 */

public class MCQuestion extends Question {
    private ArrayList<MCAnswer> answerList;
    private int nextAnswerID;

    public MCQuestion (int sessionID, String questionText) {
        super(sessionID, questionText);
        answerList = new ArrayList<>();
        nextAnswerID = 1;
    }

    public void setQuestionID(int questionID) {
        super.setQuestionID(questionID);
        for (int i = 0; i < answerList.size(); i++) {
            MCAnswer temp = answerList.get(i);
            temp.setQuestionID(i);
            answerList.set(i, temp);
        }
    }

    public void addAnswer(String answer, boolean isCorrect) {
        answerList.add(new MCAnswer(nextAnswerID, this.getQuestionID(), this.getSessionID(), answer, isCorrect));
        nextAnswerID++;
    }

    public void removeAnswer(int answerID) {
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getAnswerID() == answerID)
                answerList.remove(i);
            else if (answerList.get(i).getAnswerID() > answerID) {
                MCAnswer temp = answerList.get(i);
                temp.setAnswerID(temp.getAnswerID() - 1);
                answerList.set(i, temp);
            }
        }
        nextAnswerID--;
    }

    public ArrayList<MCAnswer> getAnswerList() {
        return answerList;
    }

    public String getQuestionDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getQuestionText() + "\n\n");
        for (int i = 0; i < answerList.size(); i++) {
            sb.append((i + 1) + ". " + this.answerList.get(i).getAnswerText() + " " + this.answerList.get(i).isCorrect() + "\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
