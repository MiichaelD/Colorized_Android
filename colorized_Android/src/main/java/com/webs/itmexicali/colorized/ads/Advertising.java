package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

public abstract class Advertising {

  //ADS Constants
  public final static boolean SHOW_ADS = true;

  public final static int GAMEOVERS_TO_INTERSTITIAL = 3;

  /**
   * AdMob Advertising. Your ad unit id. Replace with your actual ad unit id.
   */
  public static final String
      ADVIEW_AD_UNIT_ID = "ca-app-pub-4741238402050454/6518301004",
      INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4741238402050454/7995034204";

  public static final String APIKEY = "1409523218201390512";
  public final static int ADS_ADMOB = 0, ADS_AIRPUSH_BUNDLE = 1, ADS_AIRPUSH_STANDARD = 2;
  public final static int AD_SERVICE = ADS_AIRPUSH_STANDARD;
  public static int APPID = 236182;
  protected Activity pAct = null;


  /**
   * Create a new Ads service object and initialize it
   */
  public Advertising(Activity ctx) {
    pAct = ctx;
    initAds();
  }

  public final static Advertising instantiate(Activity activity) {
    switch (Advertising.AD_SERVICE) {
      case Advertising.ADS_AIRPUSH_BUNDLE:
        return new AirPushBundle(activity);

      case Advertising.ADS_AIRPUSH_STANDARD:
        return new AirPushStandard(activity);

      case Advertising.ADS_ADMOB:
      default:
        return new AdMob(activity);
    }
  }

  /**
   * Init ads if they are enabled, depending on the ads service using load
   * the corresponding one AirPush, AdMob, etc
   */
  public abstract void initAds();

  /**
   * Get the banner view to add it to the layout
   */
  public abstract View getBanner();

  /**
   * Destroy the banner after detaching it from the layout to create and load a new one
   */
  public abstract void destroyBanner();

  /**
   * Beging the loading of your Interstitial ad and save it to cache
   */
  public abstract void loadInterstitial();

  /**
   * Show the Interstitial saved on cache
   */
  public abstract void showInterstitial();

  public void onResume() {
  }

  public void onPause() {
  }
}
