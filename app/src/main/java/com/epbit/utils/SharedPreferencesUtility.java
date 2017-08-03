package com.epbit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtility
{
	 static SharedPreferences mySharedPreferences;
	
	static SharedPreferencesUtility sharedPreferencesUtility;

	public SharedPreferencesUtility() {

	}

	public static SharedPreferencesUtility getInstance() {
		if (sharedPreferencesUtility == null) {
			sharedPreferencesUtility = new SharedPreferencesUtility();
		}
		return sharedPreferencesUtility;
	}
	public static void saveUsername(Context context,String Value) {
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedpreferenceeditor = mySharedPreferences.edit();
		sharedpreferenceeditor.putString("login_username", Value);
		sharedpreferenceeditor.commit();
	}

	public static String loadUsername(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getString("login_username", "0");
	}



	public static void saveProfilePic(Context context,String Value) {
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedpreferenceeditor = mySharedPreferences.edit();
		sharedpreferenceeditor.putString("profile_pic", Value);
		sharedpreferenceeditor.commit();
	}

	public static String loadProfilePic(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getString("profile_pic", "");
	}




	public static void saveRegistrationId(Context context,String Value) {
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedpreferenceeditor = mySharedPreferences.edit();
		sharedpreferenceeditor.putString("registration_id", Value);
		sharedpreferenceeditor.commit();
	}

	public static String loadRegistrationId(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getString("registration_id", "0");
	}





	public static void saveUserType(Context context,String Value) {
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedpreferenceeditor = mySharedPreferences.edit();
		sharedpreferenceeditor.putString("login_user_type", Value);
		sharedpreferenceeditor.commit();
	}

	public static String loadUserType(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getString("login_user_type", "0");
	}




	public static void savePassword(Context context,String Value)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor sharedpreferenceeditor=mySharedPreferences.edit();
		sharedpreferenceeditor.putString("login_password", Value);
	    sharedpreferenceeditor.commit();
	}
	
	
	

	public static String loadPassword(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getString("login_password", "0");
		
		
	}
	
	public static void resetSharedPreferences(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor sharedpreferenceeditor=mySharedPreferences.edit();
		sharedpreferenceeditor.putString("login_password", "0");
		sharedpreferenceeditor.putString("login_username", "0");
		sharedpreferenceeditor.putBoolean("loginflag",false);
	    sharedpreferenceeditor.commit();
	}
	public static void saveCabType(Context context,int Value)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor sharedpreferenceeditor=mySharedPreferences.edit();
		sharedpreferenceeditor.putInt("cab_type", Value);
	    sharedpreferenceeditor.commit();
	}
	public static int loadCabType(Context context)
	{
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mySharedPreferences.getInt("cab_type", 2);
	}
	public void setLoginflag(Context context, boolean loginflag) {
		mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putBoolean("loginflag", loginflag);
		editor.commit();
	}
	public boolean getLoginflag(Context context) {
		mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return mySharedPreferences.getBoolean("loginflag", false);
	}
	
	
}