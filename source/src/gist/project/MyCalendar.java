package gist.project;

import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

/**
 *
 * @author Soladnet Software Corp
 */
public class MyCalendar {

    public String format(String str) {
        String arr[] = TextUtil.splitAndTrim(str, '@');
        String deviceDate[] = getDeviceTime();
        System.out.println(deviceDate[0] + "+++++++++++++++++++++++++" + arr[0] + "++++++++++++");
        String result;
        if (arr[0].equals(deviceDate[0])) {
            System.out.println(deviceDate[0] + "+++++++++++++++++++++++++" + arr[0] + "-----------------");
            result = "Today " + formatTime(arr[1]) + " ";
        } else {
            System.out.println(deviceDate[0] + "+++++++++++++++++++++++++" + arr[0] + "************");
            result = formatDate(arr[0]) + " " + formatTime(arr[1]) + " ";
        }
        
        return result;
    }

    public String[] getDeviceTime() {
        String date = Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-dd");
        String time = Locale.formatDate(System.currentTimeMillis(), "HH:mm");
        return new String[]{date, time};
    }

    public String formatDate(String str) {
        String[] mnt = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] d = TextUtil.splitAndTrim(str, '-');
        String date = mnt[Integer.parseInt(d[1]) - 1] + " " + d[2] + " " + d[0];
        return date;
    }

    public String formatTime(String uhr) {
        String[] t = TextUtil.splitAndTrim(uhr, ':');
        String ampm;
        int hr, min, sec = 0;

        if (Integer.parseInt(t[0]) > 12) {
            ampm = "pm";
            hr = Integer.parseInt(t[0]) - 12;
        } else {
            ampm = "am";
            hr = Integer.parseInt(t[0]);
        }
        if (t.length > 2) {
            sec = Integer.parseInt(t[2]);
        }
        min = Integer.parseInt(t[1]);

        StringBuffer hrr = new StringBuffer();
        if (hr < 10) {
            hrr.append("0").append(hr);
        } else {
            hrr.append(hr);
        }

        if (min < 10) {
            hrr.append(":0").append(min);
        } else {
            hrr.append(":").append(min);
        }
        if (t.length > 2) {
            if (sec < 10) {
                hrr.append(":0").append(sec);
            } else {
                hrr.append(":").append("00");
            }
        }

        hrr.append(ampm);

        return hrr.toString();
    }
}
