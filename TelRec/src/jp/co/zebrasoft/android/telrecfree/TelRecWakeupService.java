package jp.co.zebrasoft.android.telrecfree;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

public class TelRecWakeupService extends  Service	{
	public static final 	String 	PREFERENCES_FILE_NAME = "PreferencesFile";

    @Override
	public void onStart(Intent intent,int startID){
    	super.onStart(intent, startID);
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		int os_version = settings.getInt("OSVersion", 0);
    	//2.1と2.2でこれをすると落ちるので分岐
      	if(Build.VERSION.RELEASE.substring(0,3).equals("2.3") || os_version == 1){
         	intent = new Intent(TelRecWakeupService.this,TelRecService.class);
        	intent.putExtra("onBtn", "WAKEUP");
        	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            //Toast.makeText(this, "おきろー！！",Toast.LENGTH_LONG).show();
        	intent = new Intent(TelRecWakeupService.this,TelRecWidgetService.class);
        	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
      	}
	    stopService(new Intent(TelRecWakeupService.this, TelRecWakeupService.class));
	    //Toast.makeText(this, "自殺",Toast.LENGTH_LONG).show();
	    
	}
    
    //サービス停止時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
