AdColony
========
This is the AdColony module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Interstitial

How to use
----------
Drop the "adcolony" directory inside the "modules" directory on the Godot source.

Recompile android export template (For documentation: http://docs.godotengine.org/en/latest/reference/compiling_for_android.html#compiling-export-templates).


In Example project goto Export > Target > Android:

	Options:
		Custom Package:
			- place your apk from build
		Permissions on:
			- Access Network State
            - Access Fine Location (Recommended)
			- Internet


Configuring your game
---------------------

To enable the module on Android, add the path to the module to the "modules" property on the [android] section of your engine.cfg file. It should look like this:

	[android]
	modules="org/godotengine/godot/GodotAdColony"

If you have more separate by comma.

API Reference
-------------

The following methods are available:
```python

# Init AdColony
# @param string app_id AdColony APP ID
# @param string zone_id AdColony ZONE ID
# @param bool reward_confirmation_dialog Confirmation dialog before ads
# @param bool reward_result_dialog Result dialog after ads
# @param int instance_id The instance id from Godot (get_instance_ID())
init(app_id, zone_id, reward_confirmation_dialog, reward_result_dialog, instance_id)

# Callback on Ad reward (after view a rewarded ad)
_on_adcolony_reward

# Callback for Ad Request Filled (ready for show)
_on_adcolony_request_filled()

# Callback for Ad Request Not Filled (some network error for example)
_on_adcolony_request_not_filled()

# Callback for Ad Opened (on ad show)
_on_adcolony_opened()

# Callback for Ad Expiring
on_adcolony_expiring()

# Ads Methods
# --------------

# Request a new Ad
loadAd()

# Show the Ad
showAd()
```

License
-------------
MIT license
