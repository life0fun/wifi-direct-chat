Google Analytics Android SDK version 1.4.2.

Copyright 2009 Google, Inc. All rights reserved.

================================================================================
DESCRIPTION:

This SDK enables developers to add Google Analytics tracking to applications.
The tracker code is packaged as a single jar file. Add libGoogleAnalytics.jar
to your project's /libs directory (if using Eclipse right click on the jar in
the libs directory and choose "Build Path" -> "Add to Build Path"). See the
samples/SampleApplication application for an illustration of how to use page
tracking and event tracking.

The SDK requires your application to have the following permissions present
in AndroidManifest.xml:
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

An Analytics tracking 'visit' is defined as the events and page views generated
between calls to startNewSession() and stopSession() on the
GoogleAnalyticsTracker.  Every time startNewSession() is called a new visit is
started. You should call stopSession() on GoogleAnalyticsTracker when your
application is closing.

You will need an Analytics Account ID to properly initialize the
GoogleAnalyticsTracker object. We recommend you create a new website profile,
by clicking "+ Add new profile" from the main Overview page in Google Analytics
(google.com/analytics). Select "new domain" in the wizard, and choose a
descriptive but fake URL for your app. The Web Property/Account ID will take
the form "UA-0000000-1".

You must indicate to your users, either in the application itself or in your
terms of service, that you reserve the right to anonymously track and report a
user's activity inside of your app.

Referrer Tracking:

Google Analytics can track the source of the application install using referrer
tracking.

To enable referrer tracking, a Broadcast Receiver needs to be registered in
your AndroidManifest.xml:

<receiver android:name="com.google.android.apps.analytics.AnalyticsReceiver"
          android:exported="true">
  <intent-filter>
    <action android:name="com.android.vending.INSTALL_REFERRER" />
  </intent-filter>
</receiver>

To pass referrer information to your application, link to it in Market as
follows:

http://market.android.com/search?q=pname:<package>&referrer=<referral>

where <package> is the application's package name, and <referral> is a url
encoded list of Analytics Campaign information.  There is a tool you can use
to generate urls at:

http://code.google.com/mobile/analytics/docs/android/#android-market-tracking

All tracked page views/events will be now be attributed to this campaign.

(NOTE: do not start the GoogleAnalyticsTracker in your Application onCreate()
method if using referral tracking).

Implementation Details:

Pageviews, events and Ecommerce hits are stored in an SQLite database and
dispatched to the Google Analytics servers periodically, at a rate determined
by the developer or manually. A battery efficient strategy may be to
"piggy-back" a dispatch just after the application needs to perform network
activity.  Dispatching happens by pipelining HTTP requests down a single
connection (one request per pageview/event with a maximum of 30 per dispatch).

================================================================================
BUILD REQUIREMENTS:

Android SDK 1.5+

================================================================================
RUNTIME REQUIREMENTS:

Android OS 1.5+

================================================================================
DOCUMENTATION AND SAMPLES:

You can find the latest, detailed information about the SDK as well as
download examples using the SDK at:
http://code.google.com/mobile/analytics/docs/android/

================================================================================
PACKAGING LIST:

GoogleAnalytics
    libGoogleAnalytics.jar
    ReadMe.txt
    sample
        AndroidManifest.xml
        build.xml
        default.properties
        libs
            copy-libGoogleAnalytics-jar-into-here.txt
        res
            drawable
                icon.png
            layout
                main.xml
            values
                strings.xml
        src
            com
                google
                    android
                        apps
                            analytics
                                sample
                                    TestActivity.java

================================================================================
