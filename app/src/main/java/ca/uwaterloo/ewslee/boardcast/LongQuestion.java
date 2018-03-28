package ca.uwaterloo.ewslee.boardcast;

import java.util.Arrays;

import controllers.QuestionDBC;

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
        return this.getQuestionText() + "\n\nCorrect Answer: " + answer;
    }

    public String getStudentQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append("[QL]="+this.getQuestionText());
        return sb.toString();
    }

    public int[] calculateResults(){
        int[] result = new int[2];
        Arrays.fill(result, 0);
        for(StudentAnswer ans: studentResponse){
            if(ans.getResponse().equals(answer))
            result[0]+=1;
            else
                result[1]+=1;
        }
        return result;
    }

    public String getCorrectAnswer(){
        return answer;
    }

    public void insertQuestionAnswer(int sessionID){
        QuestionDBC qDBC = new QuestionDBC();
        String[] choice = new String[4];

        qDBC.insertQuestion(getQuestionID(),sessionID,getQuestionText(),answer);
        for(StudentAnswer response: studentResponse){
            response.insertResponseDatabase(sessionID,getQuestionID());
        }
    }

    public String getPDFQuestion(){
        StringBuilder sb = new StringBuilder();
        int correctOption=0;
        sb.append(this.getQuestionText()+"~"+"(Answer)"+answer+"~");

        int[] result = calculateResults();

        sb.append(result[0]+"/"+result[1]);
        return sb.toString();
    }
    }
