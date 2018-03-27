package ca.uwaterloo.ewslee.boardcast;

import controllers.StudentAnswerDBC;

/**
 * Created by kianl on 3/25/2018.
 */

public class StudentAnswer {
    private QuestionObserver student;
    private String answer;

    public StudentAnswer(QuestionObserver student, String answer){
        this.student=student;
        this.answer=answer;
    }

    public void setStudent(Student s1){
        student = s1;
    }

    public QuestionObserver getStudent(){
        return student;
    }

    public void setResponse(String response){
        answer = response;
    }

    public String getResponse(){
        return answer;
    }

    public void insertResponseDatabase(int sessionID, int questionID){
        StudentAnswerDBC sDBC = new StudentAnswerDBC();
        sDBC.insertRecord(student.getStudentID(),sessionID,questionID,answer);
    }
}
