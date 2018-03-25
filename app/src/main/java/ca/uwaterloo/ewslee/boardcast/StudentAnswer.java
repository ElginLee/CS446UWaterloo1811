package ca.uwaterloo.ewslee.boardcast;

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
}
