package ca.uwaterloo.ewslee.boardcast;

import java.util.ArrayList;
import java.util.Arrays;

import controllers.QuestionDBC;
import controllers.StudentAnswerDBC;

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
            sb.append((i + 1) + ". " + this.answerList.get(i).getAnswerText() + " " + this.answerList.get(i).isCorrect() + ((i == answerList.size() - 1) ? "" : "\n"));
        }
        return sb.toString();
    }

    public String getStudentQuestion(){
        StringBuilder sb = new StringBuilder();
        sb.append("[QM]="+this.getQuestionText());
        for (int i = 0; i < answerList.size(); i++) {
            sb.append("/" + this.answerList.get(i).getAnswerText());
        }
        return sb.toString();
    }

    public int[] calculateResults(){
        int[] result = new int[answerList.size()];
        Arrays.fill(result, 0);
        for(StudentAnswer ans: studentResponse){
           result[Integer.parseInt(ans.getResponse())-1] += 1;
        }
        return result;
    }

    public String getCorrectAnswer(){
        for(MCAnswer answer: answerList){
            if(answer.isCorrect()){
                return (Integer.toString(answer.getAnswerID()));
            }
        }
        return null;
    }

    public void insertQuestionAnswer(int sessionID){
        QuestionDBC qDBC = new QuestionDBC();

        int answer =0;
        String[] choice = new String[4];
        for (int i = 0; i < answerList.size(); i++) {
            choice[i]= this.answerList.get(i).getAnswerText();
            if (this.answerList.get(i).isCorrect()){
                answer = i+1;
            }
        }
        qDBC.insertQuestion(getQuestionID(),sessionID,getQuestionText(),choice[0],choice[1],choice[2],choice[3],answer);
        for(StudentAnswer response: studentResponse){
            response.insertResponseDatabase(sessionID,getQuestionID());
        }
    }

    public String getPDFQuestion(){
        StringBuilder sb = new StringBuilder();
        int correctOption=0;
        sb.append(this.getQuestionText()+"~");
        for (int i = 0; i < answerList.size(); i++) {
            sb.append("("+(i+1)+") "+this.answerList.get(i).getAnswerText());
            if( this.answerList.get(i).isCorrect()){
                sb.append(" (Correct);");
                correctOption=i;
            }
            else{
                sb.append(";");
            }
        }

        int[] result = calculateResults();
        int wrong = 0;
        int correct = 0;
        for(int x = 0; x < result.length; x ++){
            if(x==correctOption){
                correct = result[x];
            }
            else
                wrong += result[x];
        }
        sb.append("~"+correct+"/"+wrong);
        return sb.toString();
    }
}
