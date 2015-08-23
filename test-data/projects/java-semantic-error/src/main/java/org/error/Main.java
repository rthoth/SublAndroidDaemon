package org.error;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity implements Some
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        doSomethingg();
    }


    public void dooSomething() {
    	return "";
    }

    public void load() {
    	new Create();
    }
}
