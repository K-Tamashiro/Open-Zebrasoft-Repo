package jp.co.zebrasoft.android.telrecfree;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class TelRecPutini extends  Service	{
	public static final 	String 	PREFERENCES_FILE_NAME = "PreferencesFile";

    @Override
	public void onStart(Intent intent,int startID){
    	super.onStart(intent, startID);
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
		
		//Intent i = new Intent();
	    String fn = "";
	    try{
			fn = intent.getStringExtra("tel");
			if(!fn.equals("WAKE_UP")){
				editor.putBoolean("MEMBER_MODE", true);
				editor.putString("SELECTED_FN", fn);
			    editor.commit();
			}	    
	    } catch (NullPointerException e) {
	    }
		//Toast.makeText(this, fn,Toast.LENGTH_LONG).show();

	    stopService(new Intent(TelRecPutini.this, TelRecPutini.class));
	    //Toast.makeText(this, "自殺",Toast.LENGTH_LONG).show();
	}
    
    //サービス停止時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        
    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
