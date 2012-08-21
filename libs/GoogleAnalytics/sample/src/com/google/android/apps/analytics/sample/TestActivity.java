package com.google.android.apps.analytics.sample;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

  GoogleAnalyticsTracker tracker;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tracker = GoogleAnalyticsTracker.getInstance();

    // Start the tracker in manual dispatch mode...
    tracker.startNewSession("UA-YOUR-ACCOUNT-HERE", this);

    // ...alternatively, the tracker can be started with a dispatch interval (in seconds).
    //tracker.startNewSession("UA-YOUR-ACCOUNT-HERE", 20, this);

    setContentView(R.layout.main);
    Button createEventButton = (Button)findViewById(R.id.NewEventButton);
    createEventButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        tracker.trackEvent(
            "Clicks",  // Category
            "Button",  // Action
            "clicked", // Label
            77);       // Value
      }
    });

    Button createPageButton = (Button)findViewById(R.id.NewPageButton);
    createPageButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Add a Custom Variable to this pageview, with name of "Medium" and value "MobileApp"
        tracker.setCustomVar(1, "Medium", "Mobile App");
        // Track a page view. This is probably the best way to track which parts of your application
        // are being used.
        // E.g.
        // tracker.trackPageView("/help"); to track someone looking at the help screen.
        // tracker.trackPageView("/level2"); to track someone reaching level 2 in a game.
        // tracker.trackPageView("/uploadScreen"); to track someone using an upload screen.
        tracker.trackPageView("/testApplicationHomeScreen");
      }
    });

    Button quitButton = (Button)findViewById(R.id.QuitButton);
    quitButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    Button dispatchButton = (Button)findViewById(R.id.DispatchButton);
    dispatchButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Manually start a dispatch, not needed if the tracker was started with a dispatch
        // interval.
        tracker.dispatch();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Stop the tracker when it is no longer needed.
    tracker.stopSession();
  }
}
