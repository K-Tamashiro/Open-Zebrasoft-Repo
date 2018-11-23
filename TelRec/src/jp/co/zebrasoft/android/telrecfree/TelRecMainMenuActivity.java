package jp.co.zebrasoft.android.telrecfree;
///////////////////////////////////////////////
//        メインメニュー
///////////////////////////////////////////////

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
	public class TelRecMainMenuActivity extends  Activity	
				//implements onTelRecFileListDialogListener
				{
	public		Context		_context;
	final 	String 			PREFERENCES_FILE_NAME = "PreferencesFile";	
	public long fs;
	public long a;
	public long b;
	public long min;
	public long hou;
	
    private static final int MESSAGE = 1;
    private ICallbackService service;

    //	public TelRecMainMenuActivity(Context context) {
//		// TODO Auto-generated constructor stub
//		_context = context;
//	}
	//アイコン画像生成
	Bitmap defaultImage;
	
	//メニュー生成
	final List<ListItems> list = new ArrayList<ListItems>();
	ListItemAdapter adapter;

    @Override
	protected void onPause() {
		super.onPause();
//        try {
//        	if(service!=null)
//        		service.removeListener(listener);
//            unbindService(con);
//        } catch (Exception e) {
//        }
	}

	@Override
	protected void onResume() {
		super.onResume();
        Intent i = new Intent(TelRecMainMenuActivity.this, TelRecService.class);
        bindService(i, con, BIND_AUTO_CREATE);
	}
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
	
    
    private void setList(ListItems item1){
    	
    	item1.comment = "(現在サービス起動中です。)";
    	SharedPreferences _settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		if (!_settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")){
			item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro_rec);
			item1.name = "自動通話録音(録音中)";
			item1.vTime= String.format("約%02d時間%02d分録音可能です。",hou,min);
		
		}else{

			item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro_pause);
			item1.name = "自動通話録音(録音待機中)";
			item1.vTime= String.format("約%02d時間%02d分録音可能です。",hou,min);
		}
		adapter.notifyDataSetChanged();
    	
    }
    
    

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
        	if (msg.what == MESSAGE){ 
            //コールバック成功の場合ここへくる
            //Toast.makeText(TelRecMainMenuActivity.this,"callback成功！！v",Toast.LENGTH_SHORT).show();
        		setList(itemz);
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

	public ListItems itemz;	
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_LEFT_ICON);
			setContentView(R.layout.new_main);
			setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dialog_i);
	    	final String status = Environment.getExternalStorageState();
    		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    		final StatFs statFs = new StatFs(path);
		
			final ListItems item1 = new ListItems();
			//defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro);
			item1.comment = " 録音スイッチを常駐させます。";
	    	SharedPreferences _settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    	String _run = _settings.getString("SELECTED_RUN", "");
	    	
	    	//SDカードの存在確認
	    	if (status.equals(Environment.MEDIA_MOUNTED)) {
	    		a = statFs.getBlockSize();
	    		b = statFs.getFreeBlocks();
	    		fs = ((a * b)/1024);
	    		min = ((fs/100)%3600)/60;
	    		hou = (fs/100)/3600;

	    	}else{
//	            new AlertDialog.Builder(TelRecMainMenuActivity.this)
//	            		.setMessage("SDカードが無いと録音ファイルを保存出来ません。").setPositiveButton("OK", null).show();
	    		showDialog(NoSD_ID);
	    	}

	    	if (_run.equals("起動中")){
		    	if (_settings.getString("SELECTED_ITEM", "").equals("TAMAYAN")){
					item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro_pause);
					item1.name = "自動通話録音(録音待機中)";
				}else{
					item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro_rec);
					item1.name = "自動通話録音(録音中)";
				}
				//item1.vTime = "(現在サービス起動中です。)";
			}else{
				item1.name = "自動通話録音(常駐開始)";
				item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro);
				//item1.vTime = "(現在サービス停止中です。)";
			}
	    	item1.vTime= String.format("約 %02d時間%02d分録音可能です。", hou,min);
			list.add(item1);
			
			defaultImage = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_more);
			ListItems item2 = new ListItems();
			item2.image = defaultImage;
			item2.name = "常駐解除";
			item2.comment = " 自動録音TelRecを解除します。";
			list.add(item2);
			
//			defaultImage = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_info_details);
//			ListItems item3 = new ListItems();
//			item3.image = defaultImage;
//			item3.name = "xxx 強制終了 xxx";
//			item3.comment = " 再生を強制終了させます。";
//			list.add(item3);
			
			defaultImage = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_agenda);
			ListItems item3 = new ListItems();
			item3.image = defaultImage;
			item3.name = "録音ファイル一覧";
			item3.comment = " ファイルをメールに添付します。";
			item3.vTime = "(長押しで再生、リネーム、削除)";
			list.add(item3);

//			defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.menu2);
//			ListItems item5 = new ListItems();
//			item5.image = defaultImage;
//			item5.name = "テスト";
//			item5.comment = "カスタムダイアログ";
//			list.add(item5);

			
//			ListItems item6 = new ListItems();
//			item6.image = defaultImage;
//			item6.name = "インテントテスト";
//			item6.comment = "インテント実行";
//			list.add(item6);

			//ListItemAdapterを生成
			adapter = new ListItemAdapter(this,0,list);
			
			//ListViewにListItemAdapterをセット
			final ListView listView = (ListView) findViewById(R.id.list);

			TextView txt = new TextView(this);
			txt.setText("ZEBRASOFT Tamayan Trial free");
			listView.addFooterView(txt);
			//ThemeはManifestoに直接書く
			//setTheme(android.R.style.Theme_Dialog);
			listView.setAdapter(adapter);
			itemz = item1;
			
//			final Intent intent=new Intent(this,TelRecService.class);
	        final Intent i = new Intent(TelRecMainMenuActivity.this, TelRecService.class);
        	final Intent wi = new Intent(TelRecMainMenuActivity.this, TelRecWidgetService.class);
			final Intent mail_intent=new Intent(this,TelRecFileListView.class);
//			final Intent c_intent=new Intent(this,CustomDialogEx.class);
	    	//final TelRecFileListDialog dialog = new TelRecFileListDialog(this);
//	    	final TelRecPlayerDialog x_dlgThis = new TelRecPlayerDialog(this);
//            final Intent intent1 = new Intent(this,TelRecRename.class);

	    	listView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?>parent,View view,int position,long id){
						switch (position) {
						    case 0: // 1行目
						    	//SDカードの存在確認
						    	if (status.equals(Environment.MEDIA_MOUNTED)) {
						    		a = statFs.getBlockSize();
						    		b = statFs.getFreeBlocks();
						    		fs = ((a * b)/1024);
						    		min = ((fs/100)%3600)/60;
						    		hou = (fs/100)/3600;

						    	}else{
//						            new AlertDialog.Builder(TelRecMainMenuActivity.this)
//						            		.setMessage("SDカードが無いと録音ファイルを保存出来ません。").setPositiveButton("OK", null).show();
						    		showDialog(NoSD_ID);
						    		break;
						        }
						        i.putExtra("onBtn", "ON");
						        bindService(i, con, BIND_AUTO_CREATE);
								startService(i);
								break;
						    case 1: // 3行目
						    	//SDカードの存在確認
						    	if (status.equals(Environment.MEDIA_MOUNTED)) {
						    		a = statFs.getBlockSize();
						    		b = statFs.getFreeBlocks();
						    		fs = ((a * b)/1024);
						    		min = ((fs/100)%3600)/60;
						    		hou = (fs/100)/3600;
						    	}else{
//						            new AlertDialog.Builder(TelRecMainMenuActivity.this)
//						            		.setMessage("SDカードが無いと録音ファイルを保存出来ません。").setPositiveButton("OK", null).show();
						    		showDialog(NoSD_ID);
//						            break;
						        }

						        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
						        SharedPreferences.Editor editor = settings.edit();
						    	editor.putString("SELECTED_RUN", "留守");
						    	editor.putString("SELECTED_ITEM", "BYBI");
						    	editor.commit();

						    	item1.name = "自動通話録音(常駐開始)";
								item1.image = BitmapFactory.decodeResource(getResources(), R.drawable.telrec_pro);
								//item1.vTime = "(現在サービス停止中です。)";
								item1.vTime= String.format("約 %02d時間%02d分録音可能です。", hou,min);
								adapter.notifyDataSetChanged();
						        stopService(i);
						        stopService(wi);
						        break;
//						    case 2: // 4行目
//						    	//強制終了
//						        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
//						        SharedPreferences.Editor editor = settings.edit();
//						    	editor.putBoolean("MEMBER_MODE", true);
//						    	editor.putString("SELECTED_ITEM", "BIBI");
//						    	editor.putString("SELECTED_RUN", "留守");
//						    	editor.commit();
//						    	finish();
//						    	ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//						    	activityManager.restartPackage(getPackageName());
//						    	break;
						    case 2:
						    	startActivity(mail_intent);
						    break;
//						    case 4: // 2行目 
//						    	startActivity(c_intent);
//					    	break;
//						    case 5:
//				                Intent intent=new Intent();
//				                intent.setAction("jp.co.zebrasoft.android.telrecpro.VIEW");
//				                intent.putExtra("TEXT","ブロードキャストレシーバーのテストです");
//				                sendBroadcast(intent);
//						    break;
						    	
						}						
						
					}

				});
	    	
	}
	//ダイアログの管理ID
	private static final int NoSD_ID 		= 1;
	private static final int MAIL_ID 		= 3;
	private static final int HELP_ID 		= 4;
	private static final int SPEAKER_ID 	= 5;
	
	@Override
	protected Dialog onCreateDialog(int id) {
        Dialog d = super.onCreateDialog(id);
    	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        //ダイアログの表示はチョッと面倒だがshow()は使わない（メモリーリーク対策）
        switch (id) {
	    case NoSD_ID:
	    	d = (
		    new AlertDialog.Builder(TelRecMainMenuActivity.this)
		    .setTitle("Check SD Card")
		    .setMessage("SDカードが無いと録音ファイルを保存出来ません。")
		    .setPositiveButton("OK", null)
		    .create()
	    	);
	        break;

	    case MAIL_ID:
	        final EditText editText = new EditText(TelRecMainMenuActivity.this);
	        editText.setEnabled(false);
	        d =(
			        new AlertDialog.Builder(TelRecMainMenuActivity.this)
			        .setIcon(R.drawable.dialog_i)
			        .setTitle("送信メール")
			        .setMessage("送信先のメールアドレスを設定します。\nメールに添付するときのデフォルトの送信先アドレスです。" +
			        		"PRO版の機能です。")
			        .setPositiveButton("設定保存", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface fdialog, int id) {
							dismissDialog(MAIL_ID);
					      }
					  })
					  .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
								dismissDialog(MAIL_ID);
					      }
					  })
			        .setView(editText)
			        .create()
	        );
	    break;
	    case HELP_ID:
	    	d = (
    	        new AlertDialog.Builder(TelRecMainMenuActivity.this)
    	        .setTitle("TelRecPro操作説明")
    	        .setMessage("TelRecは自動的に携帯電話での通話を録音するアプリです。" +
            		"自動的に録音するには電話の着信と発信を監視する必要があるのでサービスプログラムと言う常駐する仕様になっています。" +
            		"また、録音したファイル名に日時、電話番号、電話帳の登録名を反映するために、それぞれの情報を取得します。" +
            		"\n\n■録音方法：常駐することで自動的に電話のオフフックを監視し、通話状態になると録音が開始されます。" +
            		"\n\n■録音の停止：電話をオンフック状態にすることで自動的に日時、電話番号、電話帳名をそれぞれあればファイル名にして保存します。" +
            		"\n\n■ボイスレコーダー：ボイスレコーダーとして使う場合は手動で録音開始する必要があります。" +
            		"メインメニューの最上部のアイコンの受話器が「青」の時は常駐しているのでそのまま押せば受話器が赤に変わり録音が開始されます。" +
            		"デスクトップを長押ししてウィジットを配置するとウィジットのタップで録音開始、停止が出来て便利です。" +
            		"録音を停止する場合、もう一度押すと受話器が青に戻り、録音を停止します。" +
            		"\n\n■録音停止後の動作：録音停止後、ファイルを保存するか破棄するかと言う画面を非表示にすることも可能です。" +
            		"非表示にしていた場合は録音停止後直ぐにファイルを保存して待機状態にもどります。" +
            		"\n\n■タスクバーへの常駐：タスクバーへの常駐機能を利用すると、タスクバーを下げることでいつでも録音の開始、停止が行え便利です。" +
            		"\n\n■録音ファイルリスト：リストを表示して録音ファイルをクリックするとメールへ直ぐに添付して送信できます。" +
            		"基本的に端末のスピーカーよりパソコンなどで再生した方が綺麗に聞こえます。" +
            		"リストのファイルを長押しすることで削除、再生、ファイル名の変更が出来る様になります。" +
            		"\n\n■Bluetooth、イヤホンマイクでの録音も可能です。" +
            		"可能にする為に、電話アプリが一端、全ての音声を独占してしまうので、録音開始を遅らせて独占するロジックを見届けて再度TelRec側で" +
            		"取得しなおしています。この時、一端スピーカをONにしないと復帰出来なかったので一瞬スピーカーから音声が出ますがそれは仕様です。" +
            		"\n\n■デスクトップウィジットが利用可能です。\n" +
            		"ウィジットは録音トグルボタン、リスト表示ショートカット、開始時間切替など御座います。" +
            		"\n\n■Android2.1のXperiaで実機検証はしています。特に難しい処理はしていないはずなので録音出来ない機種がある場合、電話アプリとの" +
            		"相性に問題がありそうです。録音開始時間などをずらせば上手く行くかもしれません。" +
            		"録音開始時間を遅らせないと機種によってはマイクがオフになるため、相手に送話出来ないと言う事になったりします。" +
            		"もし、そうなった場合、一端電話アプリ側のマイクボタンやスピーカーボタンを押すと概ね復帰出来ると思います。" +
            		"動作報告などこれだけ機種が増えてしまうと皆様のご協力、フィードバックが頼りです。宜しくお願いいたします。" +
            		"\n\nZEBRASOFT Tamayan" +
            		"\ntamayan@zebrasoft.co.jp")
            	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface fdialog, int id) {
			           dismissDialog(HELP_ID);
			      }
			  })
            		.create()
            		);
            break;
	    case SPEAKER_ID:
	        d =(
			        new AlertDialog.Builder(TelRecMainMenuActivity.this)
			        .setIcon(R.drawable.dialog_i)
			        .setTitle("スピーカフォン")
			        .setMessage("通話録音をする為に一瞬だけ(0.1sec)スピーカフォンのスイッチを操作します。" +
			        		"この機能でプツッと言う音が気になる場合はOFFにしてください。" +
			        		"OFFにした場合、音切れの可能性があるので注意してください。")
			        .setPositiveButton("ON", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface fdialog, int id) {
							editor.putInt("SPEAKER", 1);
					        editor.commit();
							dismissDialog(SPEAKER_ID);
					      }
					  })
					  .setNegativeButton("OFF", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
								editor.putInt("SPEAKER", 0);
						        editor.commit();
								dismissDialog(SPEAKER_ID);
					      }
					  })
					  .setNeutralButton("戻る", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
								dismissDialog(SPEAKER_ID);
					      }
					  })
			        .create()
	        );
	    break;
	    	}
	    return d;
	}
		
		//オプションメニュー
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    SubMenu subMenu = menu.addSubMenu(0,0,0,"TASK BAR").setIcon(android.R.drawable.ic_menu_view);
	    	subMenu.add(0,10,0,"録音スイッチを常駐");
	    	subMenu.add(0,20,1,"録音スイッチは解除");
	    	subMenu.add(0,51,2,"録音スイッチは解除+LED");
	    	subMenu.setGroupCheckable(0,true,true);
		    
	    	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    	final String selectedItem = settings.getString("SELECTED_GO", "常駐");
	    	final int selectedDev = settings.getInt("SELECTED_DEV", MediaRecorder.AudioSource.DEFAULT);
	    	final int selectedDev2 = settings.getInt("SELECTED_DEV2", MediaRecorder.AudioSource.DEFAULT);
	    	final int sel_fname = settings.getInt("SELECTED_FNAME", 1);
	    	final int sel_start = settings.getInt("SELECTED_START", 1);
	    	final int sel_alt = settings.getInt("SELECTED_S", 0);
	    	final int sel_time = settings.getInt("SELECTED_T", 3);
	      	final int sel_click = settings.getInt("SELECTED_C", 0);
	      	final int os_version = settings.getInt("OSVersion", 0);
	      	final int sel_time2 = settings.getInt("SELECTED_T2", 20);


	    	if (selectedItem.equals("解除")){
		    	subMenu.findItem(20).setChecked(true);
	    	}else if (selectedItem.equals("LED")){
		    	subMenu.findItem(51).setChecked(true);
	    	}else{
		    	subMenu.findItem(10).setChecked(true);
	    	}
	    	
	    	subMenu = menu.addSubMenu(0,0,0,"AUDIO SOUCE").setIcon(android.R.drawable.ic_menu_preferences);
	    	subMenu.add(1,40,0,"Aデフォルト");
	    	subMenu.add(1,50,1,"Aマイク");
	    	subMenu.add(1,60,2,"A受話＋送話両方を録音");
	    	//Xperiaで設定出来ないので選択も出来ない様にしておく
	    	subMenu.add(1,70,3,"A受話のみ録音");
	    	//subMenu.add(1,70,3,"受話のみ録音(X)").setEnabled(false);
	    	subMenu.add(1,80,4,"A送話のみ録音");
	    	subMenu.add(3,300,5,"▼▼下記は手動録音設定▼▼").setEnabled(false);
	    	subMenu.add(2,340,6,"Bデフォルト(機器依存)");
	    	subMenu.add(2,350,7,"Bマイク");
	    	subMenu.add(2,360,8,"B受話＋送話両方を録音");
	    	//Xperiaで設定出来ないので選択も出来ない様にしておく
	    	subMenu.add(2,370,9,"B受話のみ録音");
	    	//subMenu.add(1,70,3,"受話のみ録音(X)").setEnabled(false);
	    	subMenu.add(2,380,10,"B送話のみ録音");
	    	//subMenu.add(1,81,4,"カメラデバイス(X)").setEnabled(false);
	    	//subMenu.add(1,82,4,"音声認識(X)").setEnabled(false);
	    	subMenu.setGroupCheckable(1,true,true);
	    	subMenu.setGroupCheckable(2,true,true);
	    	
	    		  if (selectedDev == MediaRecorder.AudioSource.DEFAULT){
		    	subMenu.findItem(40).setChecked(true);
	    	}else if (selectedDev == MediaRecorder.AudioSource.MIC){
		    	subMenu.findItem(50).setChecked(true);
	    	}else if (selectedDev == MediaRecorder.AudioSource.VOICE_CALL){
		    	subMenu.findItem(60).setChecked(true);
	    	}else if (selectedDev == MediaRecorder.AudioSource.VOICE_DOWNLINK){
		    	subMenu.findItem(70).setChecked(true);
	    	}else if (selectedDev == MediaRecorder.AudioSource.VOICE_UPLINK){	
		    	subMenu.findItem(80).setChecked(true);
//	    	}else if (selectedDev == MediaRecorder.AudioSource.CAMCORDER){	
//		    	subMenu.findItem(81).setChecked(true);
//	    	}else if (selectedDev == MediaRecorder.AudioSource.VOICE_RECOGNITION){	
//		    	subMenu.findItem(82).setChecked(true);
	    	}else{
		    	subMenu.findItem(50).setChecked(true);
	    	}
	    		  if (selectedDev2 == MediaRecorder.AudioSource.DEFAULT){
	  		    	subMenu.findItem(340).setChecked(true);
	  	    	}else if (selectedDev2 == MediaRecorder.AudioSource.MIC){
	  		    	subMenu.findItem(350).setChecked(true);
	  	    	}else if (selectedDev2 == MediaRecorder.AudioSource.VOICE_CALL){
	  		    	subMenu.findItem(360).setChecked(true);
	  	    	}else if (selectedDev2 == MediaRecorder.AudioSource.VOICE_DOWNLINK){
	  		    	subMenu.findItem(370).setChecked(true);
	  	    	}else if (selectedDev2 == MediaRecorder.AudioSource.VOICE_UPLINK){	
	  		    	subMenu.findItem(380).setChecked(true);
//	  	    	}else if (selectedDev == MediaRecorder.AudioSource.CAMCORDER){	
//	  		    	subMenu.findItem(81).setChecked(true);
//	  	    	}else if (selectedDev == MediaRecorder.AudioSource.VOICE_RECOGNITION){	
//	  		    	subMenu.findItem(82).setChecked(true);
	  	    	}else{
	  		    	subMenu.findItem(350).setChecked(true);
	  	    	}

	    		  menu.addSubMenu(1,11,0,"SEND MAIL").setIcon(android.R.drawable.ic_menu_send);

			    subMenu = menu.addSubMenu(0,0,0,"SAVE MODE").setIcon(R.drawable.dialog_i);
		    	subMenu.add(5,31,0,"保存ダイアログ表示");
//		    	subMenu.add(5,32,1,"保存ダイアログ非表示").setEnabled(false);
		    	//選択解放した
		    	subMenu.add(5,32,1,"保存ダイアログ非表示");
		    	subMenu.add(5,33,2,"ダイアログ表示→保存").setEnabled(false);
		    	subMenu.add(5,34,3,"ダイアログ表示→破棄");
		    	//subMenu.add(5,33,2,"直ぐにメールへ添付");
		    	subMenu.setGroupCheckable(5,true,true);
		    	if (sel_alt == 0){
			    	subMenu.findItem(31).setChecked(true);
		    	}else if (sel_alt == 1){
			    	subMenu.findItem(32).setChecked(true);
		    	}else if (sel_alt == 2){
			    	subMenu.findItem(33).setChecked(true);
		    	}else if (sel_alt == 3){
			    	subMenu.findItem(34).setChecked(true);
		    	}else{
		    		subMenu.findItem(31).setChecked(true);
			    //	subMenu.findItem(33).setChecked(true);
		    	}
	  	    	//リストクリック時のアクション
	  	    	subMenu = menu.addSubMenu(0,0,0,"CLIK ACTION").setIcon(android.R.drawable.ic_menu_add);
	  	    	subMenu.add(6,101,0,"メール添付");
	  	    	subMenu.add(6,102,1,"ファイル再生");
	  	    	subMenu.setGroupCheckable(6,true,true);
	  	    	switch (sel_click){
	  	    	case 0:
	  		    	subMenu.findItem(101).setChecked(true);
	  	    		break;
	  	    	case 1:
	  		    	subMenu.findItem(102).setChecked(true);
	  	    		break;
	  	    	default:
	  		    	subMenu.findItem(101).setChecked(true);
	  	    		break;
	  	    	}
	  	    	subMenu = menu.addSubMenu(0,0,0,"FILE FORMAT").setIcon(android.R.drawable.ic_menu_save);
	    	subMenu.add(2,90,0,"yyyy-MM-dd_HH-mm-ss");
	    	subMenu.add(2,91,1,"yyyy-MM-dd_HH-mm");
	    	subMenu.add(2,92,1,"yy-MM-dd_HH-mm");
	    	subMenu.add(2,93,1,"MM-dd_HH-mm-ss");
	    	subMenu.add(2,94,1,"MM-dd_HH-mm");
	    	subMenu.add(2,95,1,"MM月dd日_HH時mm分ss秒");
	    	subMenu.add(2,96,1,"MM月dd日_HH時mm分");
	    	subMenu.setGroupCheckable(2,true,true);
	    	if (sel_fname == 0){
		    	subMenu.findItem(90).setChecked(true);
	    	}else if (sel_fname == 1){
		    	subMenu.findItem(91).setChecked(true);
	    	}else if (sel_fname == 2){
		    	subMenu.findItem(92).setChecked(true);
	    	}else if (sel_fname == 3){
		    	subMenu.findItem(93).setChecked(true);
	    	}else if (sel_fname == 4){
		    	subMenu.findItem(94).setChecked(true);
	    	}else if (sel_fname == 5){
		    	subMenu.findItem(95).setChecked(true);
	    	}else if (sel_fname == 6){
		    	subMenu.findItem(96).setChecked(true);
	    	}else {
		    	subMenu.findItem(91).setChecked(true);
	    	}
	    	


	    	subMenu = menu.addSubMenu(0,0,0,"SET STARTUP").setIcon(R.drawable.dialog_i);
	    	subMenu.add(4,21,0,"起動時に常駐").setEnabled(false);
	    	subMenu.add(4,22,1,"手動で常駐");
	    	subMenu.setGroupCheckable(4,true,true);
	    	if (sel_start == 0){
		    	subMenu.findItem(21).setChecked(true);
	    	}else{
		    	subMenu.findItem(22).setChecked(true);
	    	}

	    	subMenu = menu.addSubMenu(0,0,0,"RECORDING WAIT").setIcon(android.R.drawable.ic_menu_view);
	    	subMenu.add(5,41,0,"即録音開始");
	    	subMenu.add(5,42,1,"1 秒後に録音開始");
	    	subMenu.add(5,43,2,"2 秒後に録音開始");
	    	subMenu.add(5,44,3,"3 秒後に録音開始");
	    	subMenu.add(5,45,4,"4 秒後に録音開始");
	    	subMenu.add(5,46,5,"5 秒後に録音開始");
	    	subMenu.add(5,47,6,"6 秒後に録音開始");
	    	subMenu.add(5,48,7,"20 秒後に録音開始");
	    	subMenu.add(5,49,8,"30 秒後に録音開始");
	    	subMenu.setGroupCheckable(5,true,true);
	    	switch (sel_time){
	    	case 0:
		    	subMenu.findItem(41).setChecked(true);
	    		break;
	    	case 1:
		    	subMenu.findItem(42).setChecked(true);
	    		break;
	    	case 2:
		    	subMenu.findItem(43).setChecked(true);
	    		break;
	    	case 3:
		    	subMenu.findItem(44).setChecked(true);
	    		break;
	    	case 4:
		    	subMenu.findItem(45).setChecked(true);
	    		break;
	    	case 5:
		    	subMenu.findItem(46).setChecked(true);
	    		break;
	    	case 6:
		    	subMenu.findItem(47).setChecked(true);
	    		break;
	    	case 20:
		    	subMenu.findItem(48).setChecked(true);
	    		break;
	    	case 30:
		    	subMenu.findItem(49).setChecked(true);
	    		break;
	    	default:
		    	subMenu.findItem(45).setChecked(true);
	    		break;
	    	}
	    	//2.3対策を使って確実に起動させる
	    	subMenu = menu.addSubMenu(0,111,0,"SLIDE WAKE UP").setIcon(android.R.drawable.ic_menu_send);
	    	subMenu.add(9,112,0,"Not Android 2.3");
	    	subMenu.add(9,113,1,"Android 2.3");
	    	subMenu.add(11,1113,2,"▼▼下記は終了抑止設定▼▼").setEnabled(false);
	    	subMenu.add(10,213,3,"10分毎にチェック");
	    	subMenu.add(10,214,4,"15分毎にチェック");
	    	subMenu.add(10,215,5,"20分毎にチェック");
	    	subMenu.setGroupCheckable(9,true,true);
	    	subMenu.setGroupCheckable(10,true,true);
	    	switch (os_version){
	    	case 0:
		    	subMenu.findItem(112).setChecked(true);
	    		break;
	    	case 1:
		    	subMenu.findItem(113).setChecked(true);
	    		break;
	    	default:
		    	subMenu.findItem(112).setChecked(true);
	    		break;
	    	}
	    	switch (sel_time2){
	    	case 10:
		    	subMenu.findItem(213).setChecked(true);
	    		break;
	    	case 15:
		    	subMenu.findItem(214).setChecked(true);
	    		break;
	    	default:
		    	subMenu.findItem(215).setChecked(true);
	    		break;
	    	}
	    	
	    	subMenu = menu.addSubMenu(0,220,0,"SPEAKER PHONE").setIcon(android.R.drawable.ic_btn_speak_now);

	    	subMenu = menu.addSubMenu(0,71,0,"ABOUT TelRec").setIcon(android.R.drawable.ic_menu_help);

	    	return true;
	  }
	  
		@Override
		public boolean onMenuItemSelected(int featureId,MenuItem item){
				super.onMenuItemSelected(featureId, item);
		    	SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		        final SharedPreferences.Editor editor = settings.edit();
		        editor.putBoolean("MEMBER_MODE", true);
		        switch (item.getItemId()){
				//常駐設定メニュー
		        case 10:
					editor.putString("SELECTED_GO", "常駐");
					item.setChecked(true);
					break;
				case 20:
					editor.putString("SELECTED_GO", "解除");
					item.setChecked(true);
					break;
				case 51:
					editor.putString("SELECTED_GO", "LED");
					item.setChecked(true);
					break;
				//オーディオソース設定メニュー	
				case 40:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.DEFAULT);
					item.setChecked(true);
					break;
				case 50:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.MIC);
					item.setChecked(true);
					break;
				case 60:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.VOICE_CALL);
					item.setChecked(true);
					break;
				case 70:
					//Xperiaで設定出来ないので選択も出来ない様にしておく
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.VOICE_DOWNLINK);
					item.setChecked(true);
					//return false;
					break;
				case 80:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.VOICE_UPLINK);
					item.setChecked(true);
					break;
				case 81:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.CAMCORDER);
					item.setChecked(true);
					break;
				case 82:
					editor.putInt("SELECTED_DEV", MediaRecorder.AudioSource.VOICE_RECOGNITION);
					item.setChecked(true);
					break;
				case 340:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.DEFAULT);
					item.setChecked(true);
					break;
				case 350:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.MIC);
					item.setChecked(true);
					break;
				case 360:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.VOICE_CALL);
					item.setChecked(true);
					break;
				case 370:
					//Xperiaで設定出来ないので選択も出来ない様にしておく
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.VOICE_DOWNLINK);
					item.setChecked(true);
					//return false;
					break;
				case 380:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.VOICE_UPLINK);
					item.setChecked(true);
					break;
				case 381:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.CAMCORDER);
					item.setChecked(true);
					break;
				case 382:
					editor.putInt("SELECTED_DEV2", MediaRecorder.AudioSource.VOICE_RECOGNITION);
					item.setChecked(true);
					break;
				//ファイル名設定メニュー
				case 90:
					editor.putInt("SELECTED_FNAME", 0);
					item.setChecked(true);
					break;
				case 91:
					editor.putInt("SELECTED_FNAME", 1);
					item.setChecked(true);
					break;
				case 92:
					editor.putInt("SELECTED_FNAME", 2);
					item.setChecked(true);
					break;
				case 93:
					editor.putInt("SELECTED_FNAME", 3);
					item.setChecked(true);
					break;
				case 94:
					editor.putInt("SELECTED_FNAME", 4);
					item.setChecked(true);
					break;
				case 95:
					editor.putInt("SELECTED_FNAME", 5);
					item.setChecked(true);
					break;
				case 96:
					editor.putInt("SELECTED_FNAME", 6);
					item.setChecked(true);
					break;
				//メール送信先設定
				case 11:
					showDialog(MAIL_ID);
					break;
				case 21:
					editor.putInt("SELECTED_START", 0);
					item.setChecked(true);
					break;
				case 22:
					editor.putInt("SELECTED_START", 1);
					item.setChecked(true);
					break;
				case 31:
					editor.putInt("SELECTED_S", 0);
					item.setChecked(true);
					break;
				case 32:
					editor.putInt("SELECTED_S", 1);
					item.setChecked(true);
					break;
				case 33:
					editor.putInt("SELECTED_S", 2);
					item.setChecked(true);
					break;
				case 34:
					editor.putInt("SELECTED_S", 3);
					item.setChecked(true);
					break;
				case 41:
					editor.putInt("SELECTED_T", 0);
					item.setChecked(true);
			        break;
				case 42:
					editor.putInt("SELECTED_T", 1);
					item.setChecked(true);
			        break;
				case 43:
					editor.putInt("SELECTED_T", 2);
					item.setChecked(true);
			        break;
				case 44:
					editor.putInt("SELECTED_T", 3);
					item.setChecked(true);
			        break;
				case 45:
					editor.putInt("SELECTED_T", 4);
					item.setChecked(true);
			        break;
				case 46:
					editor.putInt("SELECTED_T", 5);
					item.setChecked(true);
			        break;
				case 47:
					editor.putInt("SELECTED_T", 6);
					item.setChecked(true);
			        break;
				case 48:
					editor.putInt("SELECTED_T", 20);
					item.setChecked(true);
			        break;
				case 49:
					editor.putInt("SELECTED_T", 30);
					item.setChecked(true);
			        break;
				case 213:
					editor.putInt("SELECTED_T2", 10);
					item.setChecked(true);
			        break;
				case 214:
					editor.putInt("SELECTED_T2", 15);
					item.setChecked(true);
			        break;
				case 215:
					editor.putInt("SELECTED_T2", 20);
					item.setChecked(true);
			        break;
				case 71:
					showDialog(HELP_ID);
			        break;
				    //リストクリック時のアクション
				case 220:
					showDialog(SPEAKER_ID);
			        break;
				case 101:
					editor.putInt("SELECTED_C", 0);
					item.setChecked(true);
			        break;
				case 102:
					editor.putInt("SELECTED_C", 1);
					item.setChecked(true);
			        break;
//				case 33:
//					editor.putInt("SELECTED_S", 2);
//					item.setChecked(true);
//					break;
				case 112:
					editor.putInt("OSVersion", 0);
					item.setChecked(true);
					break;
				case 113:
					editor.putInt("OSVersion", 1);
					item.setChecked(true);
					break;
				}
                editor.commit();
			return false;
		}

}