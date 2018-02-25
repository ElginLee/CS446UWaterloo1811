package data.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import data.model.DateConverter;
import data.model.Session;

/**
 * Created by kianl on 2/23/2018.
 */
@Dao
@TypeConverters(DateConverter.class)
public interface  SessionDao {

    @Insert
    void insert(Session session);

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Query("SELECT * FROM Session")
    List<Session> getAllSession();


}