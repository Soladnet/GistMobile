
package gist.project;

import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

/**
 *
 * @author Soladnet Software Corp
 */
public class MyCalendar {
    
    public String format(String str){
        String arr[] = TextUtil.splitAndTrim(str, '@');
        String deviceDate = Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-dd");
        System.out.println(deviceDate+"+++++++++++++++++++++++++ "+arr[0]);
        if(arr[0].equals(formatDeviceDate(deviceDate))){
            return "Today "+arr[1]+" ";
        }else{
            return arr[0]+" "+arr[1]+" ";
        }
        
    }
    public String[] getDeviceTime(){
        String date = formatDeviceDate(Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-dd"));
        String time = formatDeviceTime(Locale.formatDate(System.currentTimeMillis(),"HH mm"));
        return new String[]{date,time};
    }
    private String formatDeviceDate(String str){
        String[] mnt = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String[] d = TextUtil.splitAndTrim(str, '-');
        String date = mnt[Integer.parseInt(d[1])-1]+" "+d[2]+" "+d[0];
        return date;
    }
    public String formatDate(String str){
        String[] mnt = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String[] d = TextUtil.splitAndTrim(str, '-');
        String date = mnt[Integer.parseInt(d[1])-1]+" "+d[2]+" "+d[0];
        return date;
    }
    public String formatFigure(String str){
        String[] d = TextUtil.splitAndTrim(str, '-');
        String date = d[1]+" "+d[2]+" "+d[0];
        return date;
    }
    private String formatDeviceTime(String uhr){
        String[] t = TextUtil.splitAndTrim(uhr, ' ');
        String ampm;
        int hr,min;
        
        if(Integer.parseInt(t[0])>12){
            ampm = "pm";
            hr = Integer.parseInt(t[0])-12;
        }else{
            ampm = "am";
            hr = Integer.parseInt(t[0]);
        }
        
        //sec = Integer.parseInt(t[2]);
        min = Integer.parseInt(t[1]);
        
        StringBuffer hrr = new StringBuffer();
        if(hr<10){
            hrr.append("0").append(hr);
        }else{
            hrr.append(hr);
        }
        
        if(min<10){
            hrr.append(":0").append(min);
        }else{
            hrr.append(":").append(min);
        }
        
//        if(sec<10){
//            hrr.append(":0").append(sec);
//        }else{
//            hrr.append(":").append(sec);
//        }
        hrr.append(ampm);
        
        return hrr.toString();
    }
    public String formatTime(String uhr){
        String[] t = TextUtil.splitAndTrim(uhr, ':');
        String ampm;
        int hr,min,sec=0;
        
        if(Integer.parseInt(t[0])>12){
            ampm = "pm";
            hr = Integer.parseInt(t[0])-12;
        }else{
            ampm = "am";
            hr = Integer.parseInt(t[0]);
        }
        if(t.length>2){
            sec = Integer.parseInt(t[2]);
        }
        min = Integer.parseInt(t[1]);
        
        StringBuffer hrr = new StringBuffer();
        if(hr<10){
            hrr.append("0").append(hr);
        }else{
            hrr.append(hr);
        }
        
        if(min<10){
            hrr.append(":0").append(min);
        }else{
            hrr.append(":").append(min);
        }
        if(t.length>2){
            if(sec<10){
                hrr.append(":0").append(sec);
            }else{
                hrr.append(":").append(sec);
            }
        }
        
        hrr.append(ampm);
        
        return hrr.toString();
    }
}
