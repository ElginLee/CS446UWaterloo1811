package controllers;

import java.util.ArrayList;

/**
 * Created by #YEOGUOKUAN on 21/3/2018.
 */

public interface GradebookDAO {
    public ArrayList<String[]> getRecent(String userid);
    public int getID(String userid, int pos);
}
