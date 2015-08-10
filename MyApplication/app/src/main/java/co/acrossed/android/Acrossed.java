package co.acrossed.android;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

/**
 * Created by CHRIS on 8/2/2015.
 */
public class Acrossed extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        ParseObject.registerSubclass(Task.class);
        ParseObject.registerSubclass(Category.class);

        Parse.enableLocalDatastore(this);

        ParseCrashReporting.enable(this);


        Parse.initialize(this, "Ce6lFJPBDZyQ6qzswNZr74jEYD5YeZUwMscUza4X", "7eD006vBtJSXizBJ6OE3Osmwg98YCWdgPsYUSydB");



    }

    public static void makeToastLong( String toast )
    {
        Toast.makeText(sContext, toast, Toast.LENGTH_LONG).show();
    }
    public static void makeToastShort( String toast )
    {
        Toast.makeText( sContext, toast, Toast.LENGTH_LONG ).show();
    }
}
