package data.model;
import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by kianl on 2/24/2018.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(long dateLong) {
        return new Date(dateLong);
    }

    @TypeConverter
    public static long fromDate(Date date) {
        return date.getTime();
    }
}