package jp.co.zebrasoft.android.telrecfree;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.widget.RemoteViews;

//ホームウィジェットを制御するサービス
public class TelRecWidgetService extends Service {
    private static final String REC_BTNCLICK =
        "jp.co.zebrasoft.android.telrecfree.REC_BTNCLICK";
    final 	String 			PREFERENCES_FILE_NAME = "PreferencesFile";
    private static final int MESSAGE = 1;
    RemoteViews view;
    
    private ICallbackService service;
    
    //サービス開始時に呼ばれる
    @Override
    public void onStart(Intent intent,int startId) {
        super.onStart(intent, startId);
        view=new RemoteViews(getPackageName(),R.layout.appwidget);
        
         //ペンディングインテントの設定(4)
        Intent newintent=new Intent();
        newintent.setAction(REC_BTNCLICK);
        PendingIntent pending=PendingIntent.getService(this,0,newintent,0);
        view.setOnClickPendingIntent(R.id.imageview1,pending);
        
        //ボタンがクリックされた時の処理(5)
        try{
	        if (REC_BTNCLICK.equals(intent.getAction())) {
	            btnClicked(view);
	        }
        } catch (NullPointerException e) {
        	
        }
        
        //ホームスクリーンウィジェットの画面更新(6)
       
    	int ic = 0;
    	SharedPreferences _settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
   		if (_settings.getString("SELECTED_ITEM", "").equals("BYBI")){
				ic=0;
		}else if (!_settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")){
				ic=2;
		}else{
				ic=1;
		}
		int[] ids={
        R.drawable.w_vrec,R.drawable.w_srec,R.drawable.w_rec};
		view.setImageViewResource(R.id.imageview1,ids[ic]);
		
        AppWidgetManager manager=AppWidgetManager.getInstance(this);
        ComponentName widget=new ComponentName(
        		"jp.co.zebrasoft.android.telrecfree",
        		"jp.co.zebrasoft.android.telrecfree.TelRecWidget");
        manager.updateAppWidget(widget,view);
        handler.sendEmptyMessageDelayed(1, 1800000);
  
    }

    //バインダーを返す
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    //振るボタンがクリックされた時の処理(4)
    public void btnClicked(RemoteViews view){
   	Intent i = new Intent(TelRecWidgetService.this, TelRecService.class);
    i.putExtra("onBtn", "ON");
        bindService(i, con, BIND_AUTO_CREATE);
		startService(i);
    	int ic = 0;
    	SharedPreferences _settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
   		if (_settings.getString("SELECTED_ITEM", "").equals("BYBI")){
				ic=0;
		    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",false);
		}else if (!_settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")){
				ic=2;
		    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",true);
		}else{
			ic=1;
	    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",false);
		}
		int[] ids={
        R.drawable.w_vrec,R.drawable.w_srec,R.drawable.w_rec};
		view.setImageViewResource(R.id.imageview1,ids[ic]);
        
    }
    
    private Handler handler = new Handler() {
        @Override
       public void dispatchMessage(Message msg) {
       	if (msg.what == MESSAGE){ 
           //コールバック成功の場合ここへくる
           //Toast.makeText(TelRecWidgetService.this,"callback成功！！v",Toast.LENGTH_SHORT).show();
          	int ic = 0;
           	SharedPreferences _settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
       		if (_settings.getString("SELECTED_ITEM", "").equals("BYBI")){
    	    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",false);
       				ic=0;		
       		}else if (!_settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")){
       				ic=2;
    		    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",true);
       		}else{
       			ic=1;
		    	view.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "%s",false);
      		}
       		int[] ids={
       		        R.drawable.w_vrec,R.drawable.w_srec,R.drawable.w_rec};
       		view.setImageViewResource(R.id.imageview1,ids[ic]);

            //ホームスクリーンウィジェットの画面更新(6)
            AppWidgetManager manager=AppWidgetManager.getInstance(TelRecWidgetService.this);
            ComponentName widget=new ComponentName(
            		"jp.co.zebrasoft.android.telrecfree",
            		"jp.co.zebrasoft.android.telrecfree.TelRecWidget");
            manager.updateAppWidget(widget,view);

       	
       	}else{ 
               super.dispatchMessage(msg);
           }
       	

       }
   };

   private ICallbackListener listener = new ICallbackListener.Stub() {
       public void receiveMessage(String message) throws RemoteException {
           handler.sendMessage(handler.obtainMessage(MESSAGE, message));
       }
   };

   private ServiceConnection con = new ServiceConnection() {
       public void onServiceConnected(ComponentName componentName, IBinder binder) {
           service = ICallbackService.Stub.asInterface(binder);
           try {
               service.addListener(listener);
           } catch (RemoteException e) {
           }
       }
       public void onServiceDisconnected(ComponentName componentName) {
       }
   };

}