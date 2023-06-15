package com.obana.fydmod;

import com.obana.fydmod.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/* loaded from: classes.dex */
public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	private static final String SP_KEY_MAC= "mac";
	private static final String SP_KEY_REDIS_IP= "redisIp";
	private static final String SP_KEY_REDIS_PORT= "redisPort";
	private static final String SP_KEY_RELAY_PORT= "relayPort";
	
	
    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.settings);
        setSummaries();
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SP_KEY_MAC) 
        		|| key.equals(SP_KEY_REDIS_IP) 
        		|| key.equals(SP_KEY_REDIS_PORT) 
        		|| key.equals(SP_KEY_RELAY_PORT) ) {
            
			Preference pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            pref.setSummary(sharedPreferences.getString(key, ""));

        }
        //here the others preferences
    }
    
    public void setSummaries(){

        final SharedPreferences sp = getPreferenceManager().getSharedPreferences() ;


         //Pref1
         Preference stylePref = findPreference(SP_KEY_MAC);
         stylePref.setSummary(sp.getString(SP_KEY_MAC, ""));
         
         stylePref = findPreference(SP_KEY_REDIS_IP);
         stylePref.setSummary(sp.getString(SP_KEY_REDIS_IP, ""));
         
         stylePref = findPreference(SP_KEY_REDIS_PORT);
         stylePref.setSummary(sp.getString(SP_KEY_REDIS_PORT, ""));
         
         stylePref = findPreference(SP_KEY_RELAY_PORT);
         stylePref.setSummary(sp.getString(SP_KEY_RELAY_PORT, ""));

         //here the other preferences..
     }
}
