package mahbub1.umbc.eclipse.androidwearsensordata.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mahbub on 2/3/17.
 */

public class CalendarUtils {


    final public static String dateyyyymmdd = "yyyy-MM-dd";
    final public static String dateyyyymmddhhmmss = "yyyy-MM-dd HH:mm:ss.SSSZ";

    /**
     * Checks if two date are same day.
     *
     * @param date_to_compare
     *            is the date which is present in the database and will compare
     *            to current date
     * @param dcurrent
     *            is the current date Calendar object @see java.util.Calendar
     * @return true, if two date are same day else false;
     */
    public static boolean isSameDay(Calendar date_to_compare, Calendar dcurrent)
    {
        boolean sameDay = false;
        sameDay = date_to_compare.get(Calendar.YEAR) == dcurrent.get(Calendar.YEAR)
                && date_to_compare.get(Calendar.DAY_OF_YEAR) == dcurrent.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    /**
     * Checks if CalendarUtils day is earlier than current date.
     *
     * @param d_to_compare
     *            date_to_compare is the date which is present in the database
     *            and will compare to current date
     * @param dcurrent
     *            is the current date Calendar object @see java.util.Calendar
     * @return true, if the date is earlier than current date, otherwise return
     *         false
     */
    public static boolean isEarlier(Calendar d_to_compare, Calendar dcurrent)
    {
        boolean earlier = false;
        earlier = d_to_compare.get(Calendar.YEAR) == dcurrent.get(Calendar.YEAR)
                && d_to_compare.get(Calendar.DAY_OF_YEAR) < dcurrent.get(Calendar.DAY_OF_YEAR);
        return earlier;
    }

    public static boolean isInTimeWindow(Calendar starttime, Calendar endtime)
    {
        Calendar currenttime = Calendar.getInstance();
        if (currenttime.after(starttime) && currenttime.before(endtime))
            return true;
        else
            return false;
    }

    /**
     * Date string2 calendaryyyymmddhhmmss.
     *
     * @param s
     *            the s
     * @return the calendar
     * @throws ParseException
     *             the parse exception
     */
    public static Calendar dateString2Calendaryyyymmddhhmmss(String s) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateyyyymmddhhmmss, Locale.US);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        Date d1;
        try
        {
            d1 = sdf.parse(s);
            cal.setTime(d1);
        } catch (java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cal;
    }

    /**
     * Date string2 calendaryyyymmdd.
     *
     * @param s
     *            the s
     * @return the calendar
     * @throws ParseException
     *             the parse exception
     */
    public static Calendar dateString2Calendaryyyymmdd(String s) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateyyyymmdd, Locale.US);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        Date d1;
        try
        {
            d1 = sdf.parse(s);
            cal.setTime(d1);
        } catch (java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cal;
    }

    /**
     * Cal time to date stringyyyymmddhhmmss.
     *
     * @param cal
     *            the cal
     * @return the string
     */
    public static String calTimeToDateStringyyyymmddhhmmss(Calendar cal)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateyyyymmddhhmmss, Locale.US);
        cal.setTimeZone(TimeZone.getDefault());
        Date d = cal.getTime();
        String formattedDate = sdf.format(d);
        return formattedDate;
    }

    /**
     * Cal time to date stringyyyymmdd.
     *
     * @param cal
     *            the cal
     * @return the string
     */
    public static String calTimeToDateStringyyyymmdd(Calendar cal)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateyyyymmdd, Locale.US);
        cal.setTimeZone(TimeZone.getDefault());
        Date d = cal.getTime();
        String formattedDate = sdf.format(d);
        return formattedDate;
    }

    public static String getTimeHHMM(String hours,String mins)
    {
        String timeinHHMMformat="";
        if(hours.length()!=0)
            timeinHHMMformat=hours.trim()+" hrs: "+mins.trim()+" mins";

        return timeinHHMMformat;
    }
    /**
     * Gets the hour.
     *
     * @param time the time
     * @return the hour
     */
    public static String getHour(String time) {
        String[] pieces=time.split(":");

        return(pieces[0].substring(0,pieces[0].length()-4));
    }

    /**
     * Gets the minute.
     *
     * @param time the time
     * @return the minute
     */
    public static String getMinute(String time) {
        String[] pieces=time.split(":");

        return(pieces[1].substring(0, pieces[1].length()-5));
    }

}