package jp.co.zebrasoft.android.telrecfree;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TelRecOutGoingCall extends BroadcastReceiver {
    //public static String ABORT_PHONE_NUMBER = "1231231234";
    public String phoneNumber=null;

    public static final String OUTGOING_CALL_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String INTENT_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";

    @Override
    public void onReceive(final Context context, Intent intent) {
    	
 
        if (intent.getAction().equals(TelRecOutGoingCall.OUTGOING_CALL_ACTION)) {

            phoneNumber = intent.getExtras().getString(TelRecOutGoingCall.INTENT_PHONE_NUMBER);
            //とりあえず番号を設定して録音しないスイッチの為にとっとこ
//            if ((phoneNumber != null) && phoneNumber.equals(TelRecOutGoingCall.ABORT_PHONE_NUMBER)) {
//                Toast.makeText(context, "NEW_OUTGOING_CALL intercepted to number 123-123-1234 - aborting call",
//                    Toast.LENGTH_LONG).show();
//                abortBroadcast();
//            }else{
                //Toast.makeText(context, phoneNumber,Toast.LENGTH_LONG).show();
                         
            	intent = new Intent(context,TelRecPutini.class);
            	intent.putExtra("tel", phoneNumber);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(intent);
                
                            
//            }
        }
    }



}
