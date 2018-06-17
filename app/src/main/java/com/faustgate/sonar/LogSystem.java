package com.faustgate.sonar;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by werwolf on 11/25/16.
 */
final class LogSystem {
    private static boolean mLoggingEnabled = true;
    private static File logFile = new File(Environment.getExternalStorageDirectory() + "/UKRZaliznitsya/log.log");

    private LogSystem() {
        try {
            if (logFile.exists()) {
                logFile.delete();
            } else {
                if (!logFile.getParentFile().exists())
                    logFile.getParentFile().mkdirs();
            }
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void setDebugLogging(boolean enabled) {
        mLoggingEnabled = enabled;
    }


    public static int v(String tag, String msg) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.v(tag, msg);
            logInFile(Log.VERBOSE, tag, msg);
        }
        return result;
    }

    public static int v(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.v(tag, msg, tr);
            logInFile(Log.VERBOSE, tag, msg);
        }
        return result;
    }

    public static int d(String tag, String msg) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.d(tag, msg);
            logInFile(Log.DEBUG, tag, msg);
        }
        return result;
    }

    public static int d(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.d(tag, msg, tr);
            logInFile(Log.DEBUG, tag, msg);
        }
        return result;
    }

    public static int i(String tag, String msg) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.i(tag, msg);
            logInFile(Log.INFO, tag, msg);
        }
        return result;
    }

    public static int i(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.i(tag, msg, tr);
            logInFile(Log.INFO, tag, msg);
        }
        return result;
    }

    public static int w(String tag, String msg) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.w(tag, msg);
            logInFile(Log.WARN, tag, msg);
        }
        return result;
    }

    public static int w(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.w(tag, msg, tr);
            logInFile(Log.WARN, tag, msg);
        }
        return result;
    }

    public static int w(String tag, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.w(tag, tr);
        }
        return result;
    }

    public static int e(String tag, String msg) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.e(tag, msg);
            logInFile(Log.ERROR, tag, msg);
        }
        return result;
    }

    public static int e(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.e(tag, msg, tr);
            logInFile(Log.ERROR, tag, msg);
        }
        return result;
    }

    private static int logInFile(int priority, String tag, String msg) {
        int result = 0;
        try {
            String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));

            bw.append(MessageFormat.format("{0}\t{1} ({2})\t{3}\n", priority, timeLog, tag, msg));

            bw.close();
            result = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}