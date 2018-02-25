package data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import data.Dao.SessionDao;
import data.Dao.StudentDao;
import data.model.Session;
import data.model.Student;

/**
 * Created by kianl on 2/23/2018.
 */

@Database(entities = {Session.class,Student.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract SessionDao sessionDao();
    public abstract StudentDao studentDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.
            //new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Populate the database in the background.
     * If you want to start with more words, just add them.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final StudentDao studentDao;
        private final SessionDao sessionDao;

        PopulateDbAsync(AppDatabase db) {
            studentDao = db.studentDao();
            sessionDao = db.sessionDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            Date c = Calendar.getInstance().getTime();
            Session session = new Session(1,"123",c);
            sessionDao.insert(session);
            session = new Session(2,"234",c);
            sessionDao.insert(session);

            Student student = new Student(1,"abc");
            studentDao.insert(student);
            student = new Student(1,"1123");
            studentDao.insert(student);
            student = new Student(1,"asd");
            studentDao.insert(student);
            return null;
        }
    }
}

