package com.odessite.kos.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
    public static String getPreferredLang(Context context){
        SharedPreferences prefSetting = PreferenceManager.getDefaultSharedPreferences(context);
        return prefSetting.getString(
                context.getString(R.string.pref_lang_key),
                context.getString(R.string.pref_en_value)
        );
    }
}
