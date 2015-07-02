package com.app;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        java.util.Date date = new java.util.Date();

				Runnable runnable = new Runnable() {
					public void run() {
						println(date);
					}
				};

    }
}
