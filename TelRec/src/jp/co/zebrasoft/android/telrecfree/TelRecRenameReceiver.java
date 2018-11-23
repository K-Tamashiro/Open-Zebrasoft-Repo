package jp.co.zebrasoft.android.telrecfree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TelRecRenameReceiver extends BroadcastReceiver{

    //インテントの受信
    @Override
    public void onReceive(Context context,Intent intent) {
        Bundle bundle=intent.getExtras();
        String text=bundle.getString("TEXT");
                
        intent = new Intent(context,TelRecRename.class);
        intent.putExtra("pathToFile", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        
        onDestroy();
        

    }
    
    //@Override
	private static void onDestroy() {
    	//super.onDestroy();
		// TODO Auto-generated method stub
	}
}
