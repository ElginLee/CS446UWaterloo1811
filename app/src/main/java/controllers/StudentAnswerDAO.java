package controllers;

/**
 * Created by #YEOGUOKUAN on 26/3/2018.
 */

public interface StudentAnswerDAO {
    public void insertRecord(String studentid, int sessionid, int questionid, String answerid);
}
