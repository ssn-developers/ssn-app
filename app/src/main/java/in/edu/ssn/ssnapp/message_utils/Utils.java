package in.edu.ssn.ssnapp.message_utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static int getDP(float toDP, Context context) {
        if (toDP == 0) {
            return 0;
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) Math.ceil((density * toDP));
        }
    }

    public static long differenceBetweenDates(Date d1, Date d2) {
        //Comparing dates
        return Math.abs(d1.getDay() - d2.getDay());
    }

    public static boolean isYesterday(Date d) {
        return DateUtils.isToday(d.getTime() + DateUtils.DAY_IN_MILLIS);
    }

    public static String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(date.getTime());
    }

    public static boolean canDeleteMsg(String timestamp1) {
        long t1 = Long.valueOf(timestamp1);
        long t2 = new Date().getTime();
        long diff = t2 - t1;
        long diffMinutes = diff / (60 * 1000) % 60;
        return diffMinutes < 30;
    }
}
