package com.example.ruolan.letgo.utils;

import android.util.Log;

/**
 * Created by ruolan on 2015/11/14.
 */
public class LogUtil {

    //版本级别
    public static final int VERSION = 1;

    //debug级别
    public static final int DEBUG = 1;

    //info级别
    public static final int INFO = 1;

    //warn级别
    public static final int WARN = 1;

    //error级别
    public static final int ERROR = 1;

    //nothing几杯
    public static final int NOTHING = 1;

    //当前的需要的版本打印级别
    public static final int LEVEL = VERSION;

    public static void v(String tag,String msg){
        if (LEVEL <= VERSION){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag,String msg){
        if (LEVEL <= DEBUG){
            Log.v(tag,msg);
        }
    }

    public static void i(String tag,String msg){
        if (LEVEL <= INFO){
            Log.v(tag,msg);
        }
    }

    public static void w(String tag,String msg){
        if (LEVEL <= WARN){
            Log.v(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if (LEVEL <= ERROR){
            Log.v(tag,msg);
        }
    }


}
