package com.faustgate.sonar;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

/**
 * Created by werwolf on 11/21/16.
 */
class SoundNotifier {
    private static SoundNotifier mInstance = null;
    private Context mContext = null;
    private MediaPlayer mp;
    private Uri sound;
    private Vibrator vibrator;


    private SoundNotifier(Context context) {
        mContext = context;
        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(mContext, sound);
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1, 1000, 1000}, 0);
    }

    static SoundNotifier getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SoundNotifier(context);
        }
        return mInstance;
    }

    void startSound() {
        mp.setLooping(true);
        mp.start();
    }

    void stopSound() {
        vibrator.cancel();
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
    }

}
