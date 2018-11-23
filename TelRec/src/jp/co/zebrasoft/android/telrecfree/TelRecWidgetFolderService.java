package jp.co.zebrasoft.android.telrecfree;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

//ホームウィジェットを制御するサービス
public class TelRecWidgetFolderService extends Service {
    private static final String FOLDER_BTNCLICK =
        "jp.co.zebrasoft.android.telrecfree.FOLDER_BTNCLICK";
    RemoteViews view;
    
    //サービス開始時に呼ばれる
    @Override
    public void onStart(Intent intent,int startId) {
        super.onStart(intent, startId);
        view=new RemoteViews(getPackageName(),R.layout.fappwidget);
        
         //ペンディングインテントの設定(4)
        Intent newintent=new Intent();
        newintent.setAction(FOLDER_BTNCLICK);
        PendingIntent pending=PendingIntent.getService(this,0,newintent,0);
        view.setOnClickPendingIntent(R.id.imageview2,pending);
        view.setImageViewResource(R.id.imageview2,R.drawable.telrec_pro_folder);
        //振るボタンがクリックされた時の処理(5)
        if (FOLDER_BTNCLICK.equals(intent.getAction())) {
            btnClicked();
        }
        
        //ホームスクリーンウィジェットの画面更新(6)
        AppWidgetManager manager=AppWidgetManager.getInstance(this);
        ComponentName widget=new ComponentName(
        		"jp.co.zebrasoft.android.telrecfree",
        		"jp.co.zebrasoft.android.telrecfree.TelRecWidgetFolder");
        manager.updateAppWidget(widget,view);
        stopService(new Intent(TelRecWidgetFolderService.this, TelRecWidgetFolderService.class));
  
    }

    //バインダーを返す
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     
    //振るボタンがクリックされた時の処理(4)
    public void btnClicked(){
    	Intent list_intent=new Intent(this,TelRecFileListView.class);
    	list_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(list_intent);
    	stopService(new Intent(TelRecWidgetFolderService.this, TelRecWidgetFolderService.class));
    }
    

}