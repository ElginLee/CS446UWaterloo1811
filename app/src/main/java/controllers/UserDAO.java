package controllers;

/**
 * Created by #YEOGUOKUAN on 21/3/2018.
 */

public interface UserDAO {
    public boolean login(String userid, String password);
    public boolean insertUser(String userid, String password, String email);
}
