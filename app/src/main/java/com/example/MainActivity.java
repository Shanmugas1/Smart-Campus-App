package com.example;

import android.content.Context;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import com.example.ui.AppEntry;


public class MainActivity extends ComponentActivity {

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        AppEntry.launch(this);
    }
}
