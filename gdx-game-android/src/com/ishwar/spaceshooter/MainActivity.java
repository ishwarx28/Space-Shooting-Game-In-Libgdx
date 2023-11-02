package com.ishwar.spaceshooter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import com.ishwar.spaceshooter.SpaceGame;

public class MainActivity extends AndroidApplication {
	
	private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUncaughtExceptionHandler();
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        
        initialize(new SpaceGame(this), cfg);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	private void setupUncaughtExceptionHandler(){
        this.defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread p1, Throwable p2)
			{
				StringWriter sw = new StringWriter();
				p2.printStackTrace(new PrintWriter(sw));

				String title = "Unhandled exception occurred";

				StringBuilder errorText = new StringBuilder(title).append(" on ").append(new Date().toString()).append("\n\n");
				errorText.append("Device Info:\n");
				errorText.append("Model: ").append(Build.MODEL).append("\n");
				errorText.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
				errorText.append("SDK Version: ").append(Build.VERSION.SDK_INT).append("\n\n");
				errorText.append("Stack Trace:\n");
				errorText.append(sw.toString());

				composeGmail(MainActivity.this, "mrdev.288@gmail.com", title, errorText.toString());

				defaultUncaughtExceptionHandler.uncaughtException(p1, p2);

				// prevent app from going to freeze state
				endApplication();
			}
		};

        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

	public static void endApplication(){
        android.os.Process.killProcess(android.os.Process.myPid());
        Runtime.getRuntime().exit(1);
    }

    public static void composeGmail(Context context, String sendTo, String subject, String message){
        Intent feedback = new Intent(Intent.ACTION_SENDTO);
        feedback.setData(Uri.parse("mailto:"));
        feedback.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
        feedback.putExtra(Intent.EXTRA_SUBJECT, subject);
        feedback.putExtra(Intent.EXTRA_TEXT, message);
        feedback.setPackage("com.google.android.gm");
        try{
            context.startActivity(Intent.createChooser(feedback, "Send Crash Report To SparkChat").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }catch(Exception e){
            feedback = new Intent(Intent.ACTION_SEND);
            feedback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            feedback.putExtra(Intent.EXTRA_TEXT, e.getMessage());
            feedback.setType("text/plain");
            context.startActivity(feedback);
        }
    }
}
