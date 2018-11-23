package jp.co.zebrasoft.android.telrecfree;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TelRecService extends Service 
	implements 			View.OnClickListener {
	
	//コールバック変数（メニューのアイコンを変化させる）
	public static String ACTION_EXECUTE = "ACTION_EXECUTE";
    private RemoteCallbackList<ICallbackListener> listeners = new RemoteCallbackList<ICallbackListener>();

    public static final 	String 				PREFERENCES_FILE_NAME = "PreferencesFile";
	public 					String 				fileName="Start";
	public					Context				_context;
	public 					String 				phoneNM= "";
	public					String 				name = "電話帳未登録";
	public 					String	 			phoneUSER="";
	private				TelephonyManager	tm;
	private				AudioManager 		mAudioManager; 
	private 				MediaRecorder 		mr;
	private				NotificationManager nm;
	int i;
	public boolean isRepeat = true;
	public boolean nextSet = false;
	public boolean BT = false;
	public String 		S = "";
	public long 		ROOP_TIME = 1200000;
    
    //初期化
	
    @Override
    public void onStart(Intent intent,int startID){
    	super.onStart(intent, startID);

    	tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    	mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    	final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
    	//showToast(getBaseContext(),"起動したよ="+S);
        try {
    		Thread.sleep(1000);
    	} catch (InterruptedException e) {
    	}        
    	try{
        	S = intent.getStringExtra("onBtn") ;
    	} catch (NullPointerException e) {
    		
    	}
    	//showToast(getBaseContext(),"起動したよ=="+S);

   	
    	//自動録音部分
    	PhoneStateListener psListener = new PhoneStateListener() {
        public void onCallStateChanged(int state,String number) {
        	switch(state) {
	        	//着信時に通る
	        	case TelephonyManager.CALL_STATE_RINGING://鳴っている状態
		        	phoneNM=number;
		        	editor.putString("SELECTED_FN", "");
	                editor.commit();
	                //mAudioManager.setMicrophoneMute(true);
	                //mAudioManager.setMicrophoneMute(false);
		        	break;
		        //受話ボタンを押した（録音開始）
	        	case TelephonyManager.CALL_STATE_OFFHOOK://電話に出た
		        	    if (settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")) {
			        		if ( !settings.getString("SELECTED_FN", "").equals("")){ 
			        			phoneNM=settings.getString("SELECTED_FN", "");
			        			phoneUSER="発";
			        		}else{
			        			phoneUSER="着";
			        		}
//			        		editor.putBoolean("MEMBER_MODE", true);
			        		editor.putString("SELECTED_ITEM", "AUTO");
			        		editor.putString("SELECTED_FN", "");
			        		editor.putInt("SELECTED_TIME", 1);
			                editor.commit();
			            	if (settings.getString("SELECTED_GO", "").equals("無")){
			            		//録音開始のコールバック（ひっそりモードはnotificationにないので直接送信）
			            		handler.sendEmptyMessageDelayed(1, 1000);
			            	}else{
			                showNotification(TelRecService.this,R.drawable.telrec_pro_rec,"自動録音しています。",
			                																"TelRec自動録音中です。",
			                																"●現在通話自動録音中です。");
			            	}

			                try {
			            		Thread.sleep(settings.getInt("SELECTED_T", 3)*1000);
			            	} catch (InterruptedException e) {
			            	}
			            	if(mAudioManager.isBluetoothScoOn()){
				            	BT = true;
			            	}
			                mr = new MediaRecorder();
			        		prepareMediaRecorder();
			                mr.start();
//			            	if(!mAudioManager.isBluetoothScoOn()){
//				            	mAudioManager.setSpeakerphoneOn(true);
//				                mAudioManager.setSpeakerphoneOn(false);
//			            	}
			                mAudioManager.setMicrophoneMute(false);
			                //この固まりは苦肉の策マイクをミュートにする機種もある
//			            	//Bluetoothとイヤホンマイクは同時設定出来ないので検知する
			                if (mAudioManager.isWiredHeadsetOn()){
				            	mAudioManager.setSpeakerphoneOn(true);
				            	try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
								}
				                mAudioManager.setSpeakerphoneOn(false);
				            	mAudioManager.setWiredHeadsetOn(false);
				            	try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
								}
				            	mAudioManager.setWiredHeadsetOn(true);
			                	showToast(TelRecService.this,"イヤホンマイクで録音開始");
			                }else if(mAudioManager.isBluetoothScoOn()){
				            	mAudioManager.setBluetoothScoOn(false);
				            	mAudioManager.setBluetoothA2dpOn(false);
				            	try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
								}
				            	mAudioManager.setBluetoothScoOn(true);
				            	mAudioManager.startBluetoothSco();
			                	showToast(TelRecService.this,"Bluetoothで録音開始");
			                }else{
			                	//if( settings.getInt("OSVersion", 0) != 1){
			                	if (settings.getInt("SPEAKER", 1)==0){
				            		mAudioManager.setSpeakerphoneOn(true);
					            	mAudioManager.setSpeakerphoneOn(false);
//					            	showToast(TelRecService.this,"ウェイトなし");
			                	}else{
				            		mAudioManager.setSpeakerphoneOn(true);
					            	try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
									}
					            	mAudioManager.setSpeakerphoneOn(false);
//				            	showToast(TelRecService.this,"ウェイトあり");
			                	}
//					                showToast(TelRecService.this,"スピーカー");
			                	//}
			                showToast(TelRecService.this,"ここから自動録音開始です。");
			                }
			                nextSet = true;
			            	//マイクをミュートにする機種もあるのでミュートは効かない設定に
			            	//showToast(getBaseContext(),"マイクミュートOFF");
		        		}
//		        	    if(Build.VERSION.RELEASE.substring(0,3).equals("2.3")){
//			            	mAudioManager.setSpeakerphoneOn(true);
//			            	mAudioManager.setSpeakerphoneOn(false);
//		        	    }
		        	break;
		        //切った（録音終了）
	        	case TelephonyManager.CALL_STATE_IDLE://終了
	        		if (settings.getString("SELECTED_ITEM", "").equals("AUTO")){
		                isRepeat=false;
		                try {
		            		Thread.sleep(500);
		            	} catch (Exception e) {
		            	}
	
//	        			editor.putBoolean("MEMBER_MODE", true);
		        		editor.putString("SELECTED_ITEM", "TAMAYAN");
		        		editor.putInt("SELECTED_TIME", 0);
		        		editor.commit();
		                try {
		            		Thread.sleep(500);
		            	} catch (Exception e) {
		            	}
			        		
        				if (!settings.getString("SELECTED_GO", "").equals("常駐")
        						&& !settings.getString("SELECTED_GO", "").equals("")
        						&& !settings.getString("SELECTED_GO", "").equals(null)){
			            		
			            		showToast(TelRecService.this,"ひっそりと自動録音待機します。");
	
			            		//NotificationManager nm;
			            	    nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			            	    //ノティフィケーションのキャンセル(6)
			            	    nm.cancel(0);
	
			            	}else{
				        		showNotification(TelRecService.this,R.drawable.telrec_pro_pause,"自動録音待機します。",
										"TelRec自動録音待機中です。",
										"■現在通話自動録音待機中です。");
			            	}
        				try{
	    					if(mAudioManager.isBluetoothScoOn()){
	        					mAudioManager.stopBluetoothSco();
			            		mAudioManager.setBluetoothA2dpOn(true);
        					}
		            		} catch(Exception e) {
		            		
		            		}
		            		try{
		            			onStop();
		            		} catch(Exception e) {
		            	    }
	        		}else{

	        		}
	        		break;
        	}
        }};
        tm.listen(psListener, PhoneStateListener.LISTEN_CALL_STATE);

        //手動録音部分
	    if (settings.getString("SELECTED_ITEM", "").equals("TAMAYAN") && S.equals("ON")) {
	    	//長時間録音対策でこのフラグを使った
//	    	this.startForeground(0, Notification);
	    	
//	    	editor.putBoolean("MEMBER_MODE", true);
			editor.putString("SELECTED_ITEM", "GION");
			editor.putInt("SELECTED_TIME", 1);
			editor.commit();
			
	    	if (settings.getString("SELECTED_GO", "").equals("無")){
	    		showToast(TelRecService.this,"録音を開始します。");
	    		//録音開始のコールバック
	    		handler.sendEmptyMessageDelayed(1, 1000);
	    	}else{
	    	showNotification(TelRecService.this,R.drawable.telrec_pro_rec,"手動録音しています。",
	    														"TelRec手動録音中です。",
	    														"●現在手動通話録音中です。");
	    	}
	    	
			phoneNM="Voice";
			phoneUSER="手";

	        mr = new MediaRecorder();
			prepareMediaRecorder();
	        mr.start();
	        
	        nextSet = true;
	        //20分で起こす（そうしないとプロセスが落とされる）
	        //handler.sendEmptyMessageDelayed(2, 1200000);
	        ROOP_TIME = settings.getInt("SELECTED_T2", 20) * 60000;
	        handler.sendEmptyMessageDelayed(2, ROOP_TIME);
	        isRepeat=true;
	        
	    } else if (!settings.getString("SELECTED_ITEM", "").equals("TAMAYAN") && S.equals("ON")
	    		&& !settings.getString("SELECTED_ITEM", "").equals("AUTO")){
	    	
	    	isRepeat=false;

//	    	this.stopService(intent);
			
//	    	editor.putBoolean("MEMBER_MODE", true);
			editor.putString("SELECTED_ITEM", "TAMAYAN");
			editor.putString("SELECTED_RUN", "起動中");
			editor.putString("SELECTED_FN", "");
			editor.putInt("SELECTED_TIME", 0);
			editor.commit();

			if (!settings.getString("SELECTED_GO", "").equals("常駐")
					&& !settings.getString("SELECTED_GO", "").equals("")
					&& !settings.getString("SELECTED_GO", "").equals(null)){
	    		showToast(TelRecService.this,"ひっそりと自動録音待機します。");
	    	    //NotificationManager nm;
	    	    nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	    	    //ノティフィケーションのキャンセル(6)
	    	    nm.cancel(0);
	    	}else{
	    		showNotification(TelRecService.this,R.drawable.telrec_pro_pause,"自動録音待機します。",
						"TelRec自動録音待機中です。",
						"■現在通話自動録音待機中です。");
	    	}
			try{
				onStop();
			} catch(Exception e) {
		    }
				
			mr=null;

	    
	    	}
	    //ここまで手動部分//	

    }

    //アプリの停止
    public void onStop() {
    	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	       try {
	            //録音の停止(2)
	    	   mr.stop();
	    	   mr.release();
	    	   mr=null;
	        } catch (Exception e) {
//	            mr.release();
	        	mr=null;
	        }
	 	switch(settings.getInt("SELECTED_S", 0)){
	 		case 1:
	 			//weitをかけないとコールバックが間に合わない
	 	        if (!fileName.equals("Start")){
		 		 	showToast(this,"自動保存しました。");
			 	      // fileName="Start";
	 		 	}
			    break;
	 		default:
                try {
            		Thread.sleep(1000);
            	} catch (Exception e) {
            	}

	 	        if (!fileName.equals("Start")){
				        Intent intent=new Intent(this,TelRecRenameReceiver.class);
				        intent.putExtra("TEXT",fileName);
				        intent.setAction("jp.co.zebrasoft.android.telrecfree.VIEW");
				        sendBroadcast(intent); 
				 	      // fileName="Start";
	 	       }
			    break;
	 	}
    	handler.sendEmptyMessageDelayed(1, 1000);
//	    stopService(new Intent(TelRecService.this, TelRecService.class));
        Intent intent=new Intent(this,TelRecService.class);
    	intent.putExtra("onBtn", "WAKEUP");
    	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);


    }
    
    //MediaRecorderの初期化はここ
    private void prepareMediaRecorder() {
    	String name = "電話帳未登録";
    	//電話帳から登録があれば登録名を検索してファイル名に加える
		if (!phoneNM.equals("") && !phoneNM.equals("Voice")){
	        //Cursor cursor;
	        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNM)); 
	        Cursor cursor = getContentResolver().query(lookupUri,null, null, null, null);
            while (cursor.moveToNext()) {
	            // コンタクトIDを取得
	            //String contactId;
	            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	            // 電話番号が登録有無を取得
	            //String hasPhone;
	            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
	            // 電話番号が登録されている場合
	            if ("1".equals(hasPhone)) {
	                //Cursor phones;
	                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
	                    // コンタクトIDを条件に検索
	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,null, null);
	                while (phones.moveToNext()) {
	                    // 電話番号を取得
	                    //String phoneNumber = null;
	                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                    phoneNumber = phoneNumber.replace("-", "");
		                    if (phoneNumber.equals(phoneNM)){
		    		            // 名前を取得
		    		            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		    		            
//		    		          //写真（本当はトーストに表示してやろうと思ったけどこのアプリにそこまでの機能は必要ないな）
		    		            //将来的にリストに表示しても良いけどリストが履歴リストになるので考え中
//		    		            int photoID=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
//		    		            if (photoID!=0) {
//		    		            	Uri uri=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contactId));
//		    		                InputStream in=ContactsContract.Contacts.openContactPhotoInputStream((ContentResolver) phones,uri);
//		    		                phoneBMP=BitmapFactory.decodeStream(in);
//		    		            }
		    		      }
	                }
	                phones.close();
	            }
	        }
            cursor.close();
		}else if (phoneNM.equals("")){
			phoneNM="番号不明";
			name = "電話帳未登録";
		}else if (phoneUSER.equals("手")){
			name = "ボイスレコーダー";
		}else{
			name = "電話帳未登録";
		}
		
   	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		//日付のファイル名作成	
    	//String FN = phoneNM + "_" + phoneUSER + "_" + Integer.toString(phoneType) + ".3gp";
//    	String FN = phoneNM + "." + name +".3gp";
	//登録名に絵文字があれば削除する
		String NNN = "";
	int l=name.length();
	for (int i=0;i<l;i++)
	{
		Character c = name.charAt(i);
		if (Character.isDigit(c))
			//showToast(TelRecService.this,c+"=数値\n");
			NNN+=c;
		//特殊な登録をしていなければここで絵文字を削除した登録名を抽出出来る。
		if (Character.isLetter(c))
			//showToast(TelRecService.this,c+"=文字\n");
			NNN+=c;
		if (Character.isWhitespace(c))
			//showToast(TelRecService.this,(int)c+"=空白文字\n");
			NNN+=c;
		if (Character.isTitleCase(c))
			//showToast(TelRecService.this,c+"=タイトルケース文字\n");
			NNN+=c;
	}
	//数値だけを取得
	String MMM = "";
	l=phoneNM.length();
	for (int i=0;i<l;i++)
	{
		Character c = phoneNM.charAt(i);
		if (Character.isDigit(c))
			//showToast(TelRecService.this,c+"=数値\n");
			MMM+=c;
		//特殊な登録をしていなければここで絵文字を削除した登録名を抽出出来る。
//		if (Character.isLetter(c))
//			//showToast(TelRecService.this,c+"=文字\n");
//			MMM+=c;
//		if (Character.isWhitespace(c))
//			//showToast(TelRecService.this,(int)c+"=空白文字\n");
//			MMM+=c;
//		if (Character.isTitleCase(c))
//			//showToast(TelRecService.this,c+"=タイトルケース文字\n");
//			MMM+=c;
	}
	String FN = MMM + "." + NNN +".3gp";
	//ファイル名にしてるから「：」は使えない
    	SimpleDateFormat simpleDateFormat;// = new SimpleDateFormat("yyyyMMddHHmmss");
        switch (settings.getInt("SELECTED_FNAME", 1)){
		case 0:
	    	simpleDateFormat = new SimpleDateFormat(".yyyy-MM-dd_HH-mm-ss.");
			break;
		case 1:
	    	simpleDateFormat = new SimpleDateFormat(".yyyy-MM-dd_HH-mm.");
			break;
		case 2:
	    	simpleDateFormat = new SimpleDateFormat(".yy-MM-dd_HH-mm.");
			break;
		case 3:
			simpleDateFormat = new SimpleDateFormat(".MM-dd_HH-mm-ss.");
			break;
		case 4:
	    	simpleDateFormat = new SimpleDateFormat(".MM-dd_HH-mm.");
			break;
		case 5:
	    	simpleDateFormat = new SimpleDateFormat(".MM月dd日HH時mm分ss秒.");
			break;
		case 6:
	    	simpleDateFormat = new SimpleDateFormat(".MM月dd日HH時mm分.");
			break;
		default:
	    	simpleDateFormat = new SimpleDateFormat(".yyyy-MM-dd_HH-mm.");
			break;
		}
        
    	fileName = "TelRec_" + phoneUSER;
		fileName += simpleDateFormat.format(new Date(System.currentTimeMillis()));
	    fileName += FN;
	    
//	    mr.reset();
//	    mr.setAudioChannels(2);
	    if (settings.getString("SELECTED_ITEM", "").equals("AUTO") && BT == false){
		    mr.setAudioSource(settings.getInt("SELECTED_DEV", 0));
	    }else{
		    mr.setAudioSource(settings.getInt("SELECTED_DEV2", 0));
	    }
	    BT = false;
//        try {
//    		Thread.sleep(1000);
//    	} catch (InterruptedException e) {
//    	
//    	}        //3gp形式がベストなのか？THREE_GPP
	    mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    //エンコーダーもこれしかないのか？AMR_NB
	    mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    //ファイル名の作成
	    mr.setOutputFile(Environment.getExternalStorageDirectory() + "/" + settings.getString("SELECTED_FOLDER", "") + fileName);
//	    mr.setOutputFile("/sdcard/" + settings.getString("SELECTED_FOLDER", "") + fileName);

	    try {
          mr.prepare();
          
        } catch (IllegalStateException e) {
          //throw new AndroidRuntimeException(e);
        } catch (IOException e) {
          //throw new AndroidRuntimeException(e);
        }
    	//showToast(getBaseContext(),"メディアレコード初期化");

      }
    

//ノティフィケーションの表示
public void showNotification(Context context,int iconID,String ticker,String title,String message) {
    //ノティフィケーションマネージャの取得(4)
//    NotificationManager nm;
    nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    //ノティフィケーションオブジェクトの生成(5)
    Notification notification=new Notification(iconID,ticker,System.currentTimeMillis());
    //  フラグを設定しないと通知領域に表示されてしまう
	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	final String selectedItem = settings.getString("SELECTED_GO", "常駐");
	if (selectedItem.equals("常駐") || selectedItem.equals(null) || selectedItem.equals("")){
		notification.flags |=Notification.FLAG_ONGOING_EVENT;// 継続的イベント領域に表示 ※「実行中」領域
	}else{
		//フラグを追加しない
		if (selectedItem.equals("LED")){
			notification.ledARGB = 0xff0000ff;
		}else if (selectedItem.equals("青")){
			notification.ledARGB = 0xff0000ff;
		}else if (selectedItem.equals("黄")){
			notification.ledARGB = 0xffaa5500;//本来黄色はFFFF00だが見た目が緑に見えるので調整
		}else if (selectedItem.equals("赤")){
			notification.ledARGB = 0xffff0000;
		}else if (selectedItem.equals("緑")){
			notification.ledARGB = 0xff00ff00;
		}else if (selectedItem.equals("白")){
			notification.ledARGB = 0xffffffff;
		}else {
			notification.ledARGB = 0xff0000ff;
		}
		//LED設定共通部分
		notification.ledOnMS = 300;
		notification.ledOffMS = 3000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	}
	
    RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.custom_notification);
    // ImageViewのIDと設定する画像リソースID
    contentView.setImageViewResource(R.id.image,iconID);
    // TextViewのIDと設定する文字列
    contentView.setTextViewText(R.id.text,title);
    contentView.setTextViewText(R.id.sub,ticker);
    final SharedPreferences.Editor editor = settings.edit();

//    Intent newintent=new Intent(context,TelRecService.class);
    Intent newintent=new Intent(getBaseContext(),TelRecService.class);
    newintent.putExtra("onBtn", "ON");
    //newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent intent=PendingIntent.getService(this,0,newintent,0);

    if (settings.getInt("SELECTED_TIME", 0) == 1)  {
    	contentView.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "REC[ %s ]",true);
		editor.putInt("SELECTED_TIME", 0);
		editor.commit();
		//超時間録音対策でこのフラグを使った
		//this.startForeground(0, notification);
    }else{
    	//経過時間を表示したまま止めたいが出来ない（TT
        contentView.setChronometer(R.id.chronometer,SystemClock.elapsedRealtime(), "REC[ %s ]",false);
      //超時間録音対策でこのフラグを使った
        //this.stopForeground(true);
    }
    
    
//    PendingIntent intent=PendingIntent.getService(context,0,new Intent(context,TelRecService.class),0);

    try {
		Thread.sleep(500);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
	//notification.setLatestEventInfo(context,title,message,intent);
//    chronometer.start();

	
    notification.contentView = contentView;
    notification.contentIntent = intent;

//    nm.notify(iconID, notification);
	
	//ノティフィケーションのキャンセル(6)
    nm.cancel(0);
    //ノティフィケーションの表示(7)
    nm.notify(0,notification);
    //起動のコールバック
    handler.sendEmptyMessageDelayed(1, 1000);

}


	//トーストの表示
	private static void showToast(Context context,String text) {
	    Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
		}
	
	//サービス停止時に呼び出されるのでここで全部破棄
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    //ノティフィケーションマネージャの取得
	//    NotificationManager nm;
	    nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	    
	    //ノティフィケーションのキャンセル(6)
	    nm.cancel(0);
	    
	    //録音の停止
	//    onStop();
	    try {
	        //録音の停止(2)
	        mr.stop();
	        mr.release();
	        mr=null;
	    } catch (Exception e) {
	    //    mr.release();
	    	mr=null;
	    }
	    handler.sendEmptyMessageDelayed(1, 1000);
	    SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
//		editor.putBoolean("MEMBER_MODE", true);
		editor.putString("SELECTED_ITEM", "BYBI");
		editor.putString("SELECTED_RUN", "留守");
		editor.commit();
	    //トーストの表示
		if (nextSet == false){
		    showToast(this,"常駐を解除しました。");
		}
		nextSet = false;
	  }
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	//	showToast(this,"onClick(View v)");
	
	}

	public IBinder onBind(Intent intent) {
    	return stub;
    }

    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	//showToast(getBaseContext(),"Handler");

            if (msg.what == 1) {
                int numListeners = listeners.beginBroadcast();
                for (int i = 0; i < numListeners; i++) {
                    try {
                        listeners.getBroadcastItem(i).receiveMessage(getResources().getString(R.string.l_title));
                        //Toast.makeText(TelRecService.this,"callback成功！！v",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }
                //Toast.makeText(TelRecService.this,"callback成功！！v",Toast.LENGTH_SHORT).show();
                listeners.finishBroadcast();
	            }else if (msg.what == 2) {
	            	//Toast.makeText(TelRecService.this,"10秒経過",Toast.LENGTH_SHORT).show();
	            	if (isRepeat){
		            	//handler.sendEmptyMessageDelayed(2, 1200000);
		    	        handler.sendEmptyMessageDelayed(2, ROOP_TIME);
		            	isRepeat=true;
		            	Intent intent = new Intent(getBaseContext(),TelRecService.class);
		            	//Intent intent = new Intent(TelRecService.this,TelRecService.class);
		            	intent.putExtra("onBtn", "WAKEUP");
		            	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            	//getBaseContext().startService(intent);
		            	startService(intent);
	            	}
	            } else {
	                super.dispatchMessage(msg);
	            }
	        }
    };
    
    
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
        
    }

    private final ICallbackService.Stub stub = new ICallbackService.Stub() {
        public void addListener(ICallbackListener listener)
                throws RemoteException {
            listeners.register(listener);
            //showToast(getBaseContext(),"ICallbackService STUB");
            
        }

        public void removeListener(ICallbackListener listener)
                throws RemoteException {
            listeners.unregister(listener);
            //showToast(getBaseContext(),"ICallbackService");
        }


    };
}