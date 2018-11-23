package jp.co.zebrasoft.android.telrecfree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TelRecWakeUp extends BroadcastReceiver {

    public static final String USER_PRESENT = "android.intent.action.USER_PRESENT";

    @Override
    public void onReceive(final Context context, Intent intent) {
    	
 
        if (intent.getAction().equals(TelRecWakeUp.USER_PRESENT)) {
         	intent = new Intent(context,TelRecWakeupService.class);
            context.startService(intent);
//        	//2.1と2.2でこれをすると落ちるので分岐
//          	if(Build.VERSION.RELEASE.substring(0,3).equals("2.3")){
//             	intent = new Intent(context,TelRecService.class);
//            	intent.putExtra("onBtn", "WAKEUP");
//            	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startService(intent);
//                           
//            	intent = new Intent(context,TelRecWidgetService.class);
//            	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startService(intent);
//          	}

                            
        }
    }



}
