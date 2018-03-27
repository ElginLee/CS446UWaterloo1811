package controllers;

/**
 * Created by #YEOGUOKUAN on 27/3/2018.
 */

public interface QuestionDAO {
    public void insertQuestion(int questionID,
                               int sessionID,
                               String questionText,
                               String answerText1,
                               String answerText2,
                               String answerText3,
                               String answerText4,
                               int isCorrect);

    public void insertQuestion(int questionID,
                               int sessionID,
                               String questionText,
                               String answerText);
}
