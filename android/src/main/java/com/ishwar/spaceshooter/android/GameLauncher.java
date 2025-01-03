package com.ishwar.spaceshooter.android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ishwar.spaceshooter.R;
import com.ishwar.spaceshooter.SpaceGame;

/** Launches the Android application. */
public class GameLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new SpaceGame(), configuration);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        RelativeLayout root = new RelativeLayout(this);
        root.setAlpha(0.0f);
        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(Color.BLACK);

        RelativeLayout.LayoutParams lp = createText(root, 28, Color.WHITE, (int) (dm.widthPixels * 0.05), Typeface.DEFAULT_BOLD, getString(R.string.app_name));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        lp = createText(root, 29, Color.RED, (int) (dm.widthPixels * 0.02), Typeface.DEFAULT, String.format("By %s", getString(R.string.developer_name)));
        lp.addRule(RelativeLayout.ALIGN_END, 28);
        lp.addRule(RelativeLayout.BELOW, 28);

        root.post(() -> {
            root.animate().alpha(1.0f).setDuration(1900).setInterpolator(new LinearInterpolator()).start();
            root.postDelayed(() -> {
                setContentView(graphics.getView(), createLayoutParams());
            }, 2000);
        });

        setContentView(root);


    }

    RelativeLayout.LayoutParams createText(RelativeLayout root, int id, int color, int size, Typeface tf, String str) {
        TextView textView = new TextView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);
        textView.setId(id);
        textView.setGravity(Gravity.CENTER);
        textView.setText(str);
        textView.setTypeface(tf);
        textView.setTextSize(size);
        textView.setTextColor(color);
        root.addView(textView);
        return lp;
    }
}
