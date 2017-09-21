package com.example.zhy.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志输出控制
 * @Author zhouy
 * @Date 2017-04-07
 */

public class LogUtils {

    private static boolean isDebug = true;

    private static volatile LogUtils INSTANCE;

    private static String logPath; //log存放路径

    private static String logName;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static Date date = new Date();

    private int mPid;

    private LogUtils(){
        mPid = android.os.Process.myPid();
    }

    public static LogUtils getInstance(){
        if(null == INSTANCE){
            synchronized (LogUtils.class){
                if(null == INSTANCE){
                    INSTANCE = new LogUtils();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            logPath = context.getExternalFilesDir(null).getPath()+"/logs";
            Log.e("LOG External", logPath);
        }else {
            //存入data/data里，非root手机看不到
//            logPath = context.getFilesDir().getPath();
            logPath = Environment.getDataDirectory().getPath()+"/logs";
            Log.e("LOG", logPath);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        logName = logPath + "/log_" + format.format(new Date()) + ".log";
    }


    public static void setDebugMode(boolean debug){
        isDebug = debug;
    }

    /**
     * 快捷的日志删除
     * @param object
     * @param message
     */
    public static void log(Object object, String message){
        if(isDebug){
            Log.d(object.getClass().getSimpleName(), message);
        }
    }


    public static void v(String tag, String message){
        if(isDebug){
            Log.v(tag, message);
        }else {
            writeToFile('v',tag, message);
        }
    }

    public static void d(String tag, String message){
        if(isDebug){
            Log.d(tag, message);
        }else {
            writeToFile('d',tag, message);
        }
    }

    public static void i(String tag, String message){
        if(isDebug){
            Log.i(tag, message);
        }else {
            writeToFile('i',tag, message);
        }
    }

    public static void w(String tag, String message){
        if(isDebug){
            Log.w(tag, message);
        }else {
            writeToFile('w',tag, message);
        }
    }

    public static void e(String tag, String message){
        if(isDebug){
            Log.e(tag, message);
        }else {
            writeToFile('e',tag, message);
        }
    }

    /**
     * 输出log日志至文件中
     * @param type
     * @param tag
     * @param message
     */
    private static void writeToFile(char type, String tag, String message){
        if(null == logPath){
            LogUtils.e(tag, "logPath == null, 未初始化LogUtil");
            return;
        }
        String log = dateFormat.format(date) +"  "+type+" "+tag+" "+ message;
        File logFile = new File(logPath);
        if(!logFile.exists()){
            logFile.mkdirs();  //创建父目录
        }
        try{
            FileWriter fileWriter = new FileWriter(logName, true);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));;
            printWriter.print(log);
            printWriter.println();
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
