package com.app;

import android.app.Activity;
import android.os.Bundle;
import java.util.NotFoundException;
import java.notfound.*;
import java.notfound2.NotFoundException;

public class Application extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }


    public void aMethod(String a, int b) {
    	a = a + b;
    }

    public void amb(String a, CharSequence b) {

    }

    public void amb(String a, String b) {

    }
}
