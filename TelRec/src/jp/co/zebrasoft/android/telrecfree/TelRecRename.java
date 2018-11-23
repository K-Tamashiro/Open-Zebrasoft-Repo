package jp.co.zebrasoft.android.telrecfree;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TelRecRename extends  Activity{
	public static final 	String 			PREFERENCES_FILE_NAME = "PreferencesFile";
	//ダイアログの管理ID
	private static final int NoSD_ID 		= 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		showDialog(NoSD_ID);

	}
	@Override
	protected Dialog onCreateDialog(int id) {
		Intent 				intent = getIntent();
		final String 		fname = intent.getStringExtra("pathToFile");
		final EditText 		editText = new EditText(TelRecRename.this);
		final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		editText.setText(fname);
		editText.setEnabled(false);
		final Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
		final Timer mTimer = new Timer(true);


		Dialog d = super.onCreateDialog(id);

		switch (id) {
	    case NoSD_ID:
	    	d = (
			        new AlertDialog.Builder(TelRecRename.this)
			        .setTitle("録音ファイルの保存")
			        .setMessage("Name Change\nPRO版はここでファイル名を変更できます。")
			        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface fdialog, int id) {

							Toast.makeText(TelRecRename.this,"録音ファイルを保存しました。",Toast.LENGTH_SHORT).show();
							removeDialog(NoSD_ID);
							mTimer.cancel();
							finish();
					      }
					  })
					  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
								File d = new File("/sdcard/"+fname);
								File e = new File("/sdcard/"+editText.getText());
								d.renameTo(e);
								File file= new File("/sdcard/"+editText.getText());
							    file.delete();
							    //fdialog.cancel();
								Toast.makeText(TelRecRename.this,"録音ファイルは破棄しました。",Toast.LENGTH_SHORT).show();
								removeDialog(NoSD_ID);
								mTimer.cancel();
							    finish();
					      }
					  })
			        .setView(editText)
			        .create()
			        );
	        break;
        }
		//ダイアログを表示するが自動で消す処理（保存するか破棄するかの分岐）
		switch(settings.getInt("SELECTED_S", 0)){
			case 2:
				mTimer.schedule( new TimerTask(){
			        @Override
			        public void run() {
			            // mHandlerを通じてUI Threadへ処理をキューイング
			            mHandler.post( new Runnable() {
			                public void run() {
		 
								Toast.makeText(TelRecRename.this,"録音ファイルを保存しました。",Toast.LENGTH_SHORT).show();
								removeDialog(NoSD_ID);
			                	mTimer.cancel();
			                	finish();
			                }
			            });
			        }
			    }, 10000, 10000);		
				break;
			case 3:
				mTimer.schedule( new TimerTask(){
			        @Override
			        public void run() {
			            // mHandlerを通じてUI Threadへ処理をキューイング
			            mHandler.post( new Runnable() {
			                public void run() {
								File d = new File("/sdcard/"+fname);
								File e = new File("/sdcard/"+editText.getText());
								d.renameTo(e);
								File file= new File("/sdcard/"+editText.getText());
							    file.delete();
							    //fdialog.cancel();
								Toast.makeText(TelRecRename.this,"録音ファイルは破棄しました。",Toast.LENGTH_SHORT).show();
								removeDialog(NoSD_ID);
								mTimer.cancel();
							    finish();
			                }
			            });
			        }
			    }, 10000, 10000);		
				break;
			default:
				break;
		}
		return d;
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
