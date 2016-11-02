package org.godotengine.godot;

import com.adcolony.sdk.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import java.util.Locale;

public class GodotAdColony extends Godot.SingletonBase
{
	private Activity activity = null; // The main activity of the game
	private int instance_id = 0;

	private static final String TAG = "godot";

	private AdColonyInterstitial ad;
	private AdColonyInterstitialListener listener;
	private AdColonyAdOptions ad_options;
	private AdColonyAppOptions app_options;

	private String app_id;
	private String zone_id;

	/* Init
	 * ********************************************************************** */

	/**
	 * Prepare for work with AdColony
	 * @param boolean app_id AdColony APP ID
	 * @param boolean zone_id AdColony ZONE ID
	 * @param int instance_id The instance id from Godot (get_instance_ID())
	 *							for callbacks
	 */
	public void init(final String app_id, final String zone_id, final int instance_id)
	{
		this.app_id = app_id;
		this.zone_id = zone_id;
		this.instance_id = instance_id;
		this.app_options = new AdColonyAppOptions()
								.setUserID(getAdColonyDeviceId());

		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				AdColony.configure(activity, app_options, app_id, zone_id);

				/** Optional user metadata sent with the ad options in each request */
        		AdColonyUserMetadata metadata = new AdColonyUserMetadata();
                	//.setUserAge( 26 )
                	//.setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                	//.setUserGender( AdColonyUserMetadata.USER_MALE )

        		/** Ad specific options to be sent with request */
        		ad_options = new AdColonyAdOptions().setUserMetadata(metadata);

				/**
		         * Set up listener for interstitial ad callbacks. You only need to implement the callbacks
		         * that you care about. The only required callback is onRequestFilled, as this is the only
		         * way to get an ad object.
		         */

		        listener = new AdColonyInterstitialListener()
		        {
		            /** Ad passed back in request filled callback, ad can now be shown */
		            @Override
		            public void onRequestFilled(AdColonyInterstitial ad)
		            {
		                GodotAdColony.this.ad = ad;
						GodotLib.calldeferred(instance_id, "_on_adcolony_request_filled", new Object[]{ });
		                Log.d(TAG, "onRequestFilled");
		            }

		            /** Ad request was not filled */
		            @Override
		            public void onRequestNotFilled(AdColonyZone zone)
		            {
						GodotLib.calldeferred(instance_id, "_on_adcolony_request_not_filled", new Object[]{ });
		                Log.d(TAG, "onRequestNotFilled");
		            }

		            /** Ad opened, reset UI to reflect state change */
		            @Override
		            public void onOpened(AdColonyInterstitial ad)
		            {
						GodotLib.calldeferred(instance_id, "_on_adcolony_opened", new Object[]{ });
		                Log.d(TAG, "onOpened");
		            }

		            /** Request a new ad if ad is expiring */
		            @Override
		            public void onExpiring(AdColonyInterstitial ad)
		            {
						GodotLib.calldeferred(instance_id, "_on_adcolony_expiring", new Object[]{ });
		                AdColony.requestInterstitial(zone_id, this, ad_options);
		                Log.d(TAG, "onExpiring");
		            }
		        };

			}
		});

		Log.d("godot", "AdColony: init");
	}


	/**
	 * Load Ad
	 */
	public void loadAd()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (ad == null || ad.isExpired()) {
		            AdColony.requestInterstitial(zone_id, listener, ad_options);
					Log.d("godot", "AdColony: Load Ad");
		        }
			}
		});
	}

	/**
	 * Show Ad
	 */
	public void showAd()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				ad.show();
				Log.d("godot", "AdColony: Show Ad");
			}
		});
	}

	/* Utils
	 * ********************************************************************** */

	/**
	 * Generate MD5 for the deviceID
	 * @param String s The string to generate de MD5
	 * @return String The MD5 generated
	 */
	private String md5(final String s)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2) h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch(NoSuchAlgorithmException e) {
			//Logger.logStackTrace(TAG,e);
		}
		return "";
	}

	/**
	 * Get the Device ID for AdColony
	 * @return String Device ID
	 */
	private String getAdColonyDeviceId()
	{
		String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase(Locale.US);
		return deviceId;
	}

	/* Definitions
	 * ********************************************************************** */

	/**
	 * Initilization Singleton
	 * @param Activity The main activity
	 */
 	static public Godot.SingletonBase initialize(Activity activity)
 	{
 		return new GodotAdColony(activity);
 	}

	/**
	 * Constructor
	 * @param Activity Main activity
	 */
	public GodotAdColony(Activity p_activity) {
		registerClass("AdColony", new String[] {
			"init",
			"loadAd", "showAd"
		});
		activity = p_activity;
	}
}
