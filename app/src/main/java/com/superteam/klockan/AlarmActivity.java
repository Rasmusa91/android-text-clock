package com.superteam.klockan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity
{
    private TimeHandler m_TimeHandler;
    private MediaPlayer m_MediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initialize()
    {
        m_TimeHandler = new TimeHandler(new Callback() {
            @Override
            public void onCallback(Object p_Parameter) {
                onTimeHandlerUpdate();
            }
        });

        initializeButtons();
        initializeAnimation();
        initializeSound();
        initializeNotification();
    }

    private void initializeAnimation()
    {
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        shake.setRepeatCount(Animation.INFINITE);
        shake.setRepeatMode(Animation.RESTART);
        ImageView image = (ImageView) findViewById(R.id.alarmImage);
        image.startAnimation(shake);
    }

    private void initializeButtons()
    {
        findViewById(R.id.backToTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_MediaPlayer.stop();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.backToAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_MediaPlayer.stop();
                Intent intent = new Intent(getApplicationContext(), EditAlarmActivity.class);
                intent.putExtra("editObjectID", getIntent().getExtras().getInt("id"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeSound()
    {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if(alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        m_MediaPlayer = MediaPlayer.create(getApplicationContext(), alert);
        m_MediaPlayer.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initializeNotification()
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification n  = new Notification.Builder(this)
                .setContentTitle("Alarm")
                .setContentText(Preferences.getAllAlarms(getApplicationContext()).get(getIntent().getExtras().getInt("id")).getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void onTimeHandlerUpdate()
    {
        ((Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
        ((TextView) findViewById(R.id.headerTime)).setText(Utilities.getDefaultTimeObject(getApplicationContext()).toString());
    }
}
