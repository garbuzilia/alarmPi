package com.example.androidapp;

import static java.time.LocalDateTime.parse;

import android.os.Build;

import androidx.annotation.NonNull;

import  java.time.LocalDateTime;
import java.util.concurrent.atomic.LongAccumulator;

public class AlarmRecording
{
    public  AlarmRecording() {

    }
    public AlarmRecording(int numberElement, String message, LocalDateTime dateTime, String music) {
        _numberElement = numberElement;
        _message = message;
        _dateTime = dateTime;
        _music = music;
    }
    public  AlarmRecording(@NonNull String string) {
        String[] strings = string.split("|||");

        _message = strings[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _dateTime = parse(strings[1]);
        }

        _music = strings[2];
    }

    public  String getString()   {
        return _message + "|||" + _dateTime + "|||" + _music + "|||";
    }

    public String getMessage()   {
        return _message;
    }
    public void setMessage(String message)    {
        _message = message;
    }

    public LocalDateTime getDateTime() {
        return _dateTime;
    }
    public void setDateTime(LocalDateTime dateTime)
    {
        _dateTime = dateTime;
    }

    public  String getMusic() {
        return _music;
    }
    public  void setMusic(String music) {
        _music = music;
    }


    private int _numberElement;
    private String _message;
    private LocalDateTime _dateTime;
    private String _music;
    private int _teg;

}
