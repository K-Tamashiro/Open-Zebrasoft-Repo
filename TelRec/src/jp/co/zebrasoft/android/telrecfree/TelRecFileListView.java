package jp.co.zebrasoft.android.telrecfree;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TelRecFileListView extends  Activity{

	public		Context		_context;
	private	File		_fileCurrent;	//現在表示しているフォルダ
	public		File[]		_aFileList;		//現在表示しているフォルダのファイル一覧
	public		String[]	_astrFileName;	//現在表示しているフォルダのメニュー用ファイル名
	public		Dialog		_dlgThis;
	public		boolean	_sort=true;
	final 	String 			PREFERENCES_FILE_NAME = "PreferencesFile";
	int cnt=0;
	ListItems item;
	ListView listView1;
	Dialog d;

	
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_LEFT_ICON);
			setContentView(R.layout.new_main);
			setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dialog_i);
			CreateFileList("/sdcard/");
			if (cnt < 1){
				Toast.makeText(TelRecFileListView.this,"録音ファイルはありません。",Toast.LENGTH_SHORT).show();
				finish();
			}
			}
	//ダイアログの管理ID
	private static final int MAIN_ID 	= 1;
	private static final int RENAME_ID 	= 2;
	private static final int DELETE_ID 	= 3;

	@Override
	protected Dialog onCreateDialog(int id) {
        d = super.onCreateDialog(id);
		String delFile = item.name.replace(".","\n");
		item.name = item.name.replace("\n", ".");
    	final TelRecPlayerDialog dialog = new TelRecPlayerDialog(TelRecFileListView.this);
        final EditText editText = new EditText(TelRecFileListView.this);
        editText.setText(item.name);
        editText.setEnabled(false);

		//ダイアログの表示はチョッと面倒だがshow()は使わない（メモリーリーク対策）
        switch (id) {
        //メインダイアログ
        default:break;
        case MAIN_ID:
    		CharSequence[] items = {"録音ファイル名の変更","録音ファイルの削除", "録音ファイルの再生"};
        	d=(
					new AlertDialog.Builder(TelRecFileListView.this)
					.setIcon(R.drawable.dialog_i)
					.setTitle("選択して下さい。")
					.setItems(items, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface d, int mitem) {
					    	removeDialog(MAIN_ID);
							switch (mitem) {
							case 0://ファイル名変更
								showDialog(RENAME_ID);
						        break;
							case 1://ファイルの削除
								showDialog(DELETE_ID);
						        break;
							case 2://再生
								//Player呼び出し
								File file= new File("/sdcard/TelRec_"+item.tmp+editText.getText()+".3gp");
								dialog.pathToFile=file.getAbsolutePath();
								dialog.title="録音ファイル再生";
								dialog.showPlayerD();
						        break;
						        }
					    }
					}).create()
			);
        	break;
        //リネーム
	    case RENAME_ID:
	    	d = (
			        new AlertDialog.Builder(TelRecFileListView.this)
			        .setIcon(R.drawable.dialog_i)
			        .setTitle("ファイル名の変更")
			        .setMessage("Name Change\nPRO版はここでファイル名を変更できます。")
			        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface fdialog, int id) {
							dismissDialog(RENAME_ID);
					      }
					  })
					  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
								dismissDialog(RENAME_ID);
					      }
					  })
			        .setView(editText)
			        .create()
			);
	        break;
	    //ファイル削除
	    case DELETE_ID:
	    	d = (
			        new AlertDialog.Builder(TelRecFileListView.this)
			        .setIcon(R.drawable.dialog_i)
			        .setTitle("ファイル削除")
			        .setMessage("録音ファイルを削除します。\n\n" + delFile )
			        .setPositiveButton("削除", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface fdialog, int id) {
							File file= new File("/sdcard/TelRec_"+item.tmp+item.name+".3gp");
							file.delete();
							Toast.makeText(TelRecFileListView.this,"ファイルを削除しました。",Toast.LENGTH_SHORT).show();
							CreateFileList(Environment.getExternalStorageDirectory() + "/");
							removeDialog(DELETE_ID);
					      }
					  })
					  .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface fdialog, int id) {
					    	  removeDialog(DELETE_ID);
					      }
					  })
			        //.setView(editText)
			        .create()
	    );
		break;
        }
 	    return d;
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
		
	}
	
	@SuppressWarnings("unused")
	public	boolean CreateFileList(String strPath)	{
		File[]	aFiles;
		_aFileList = null;
		_astrFileName = null;
	
		_fileCurrent = new File(strPath);
		if(_fileCurrent == null)
			return	false;
	
		aFiles = _fileCurrent.listFiles();
		if(aFiles == null || aFiles.length == 0){
			_aFileList = new File[0];
			_astrFileName = new String[0];
		return	true;
		}


	int			i;
	int			nCount;
	String[]	astrName;
	if(_sort == true){
		Arrays.sort(aFiles);
		Collections.reverse(Arrays.asList(aFiles));
		
	}else{
		Arrays.sort(aFiles);
	}
	astrName = new String[aFiles.length];

	//nCount = 0;
	//メニュー生成
	List<ListItems> list = new ArrayList<ListItems>();
	

	Bitmap Recive = BitmapFactory.decodeResource(getResources(), R.drawable.recive);//受信
	Bitmap Send = BitmapFactory.decodeResource(getResources(), R.drawable.send);//発信
	Bitmap Voice = BitmapFactory.decodeResource(getResources(), R.drawable.recoder);//手動
	
	for(i = 0; i < aFiles.length; i++)
	{
		try{
			if(aFiles[i].isFile() && aFiles[i].isHidden() == false && aFiles[i].getName().substring(0,6).equals("TelRec"))
			{
		        Cursor cursor;
		            //アイコン画像生成
				//Bitmap defaultImage;
				//defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.mail);
				//ステータスアイコン生成
	
				ListItems item = new ListItems();
				String fn = aFiles[i].getName().replace("TelRec_", "");
	//			item.comment=dname;
				if (fn.indexOf("発")!= -1){
					item.image = Send;
					item.tmp = "発.";
					fn = fn.replace("発.", "");
				}else if (fn.indexOf("着")!= -1){
					item.image = Recive;
					item.tmp = "着.";
					fn = fn.replace("着.", "");
				}else if (fn.indexOf("手")!= -1){
					item.image = Voice;
					item.tmp = "手.";
					fn = fn.replace("手.", "");
				}else{
					item.image = Voice;
					item.tmp = "手._";
					fn = fn.replace("手.", "");
				}
				item.name = fn.replace(".3gp", "");
				item.name = item.name.replace(".", "\n");
				int size = (int) aFiles[i].length();
				double size_kb = ((double) aFiles[i].length()/1024);
				item.type = String.format("FileSize = %8.2f KB (%,3d Byte)\n", size_kb,size);
				int sec = (((int) aFiles[i].length()/1600))%60;
				int min = ((((int) aFiles[i].length()/1600))/60)%60;
				int hou = (((int) aFiles[i].length()/1600))/3600;
	//			item.vTime = String.format("Time = ( %02d:%02d:%02d )", hou,min,sec);
				item.type += String.format("Time = ( %02d:%02d:%02d )", hou,min,sec);
				list.add(item);
				cnt ++;
				
			}
		}catch (StringIndexOutOfBoundsException e){
			
		}
			//nCount++;
	}
				//ソートするならここでソート

		
		//ListItemAdapterを生成
			FileListItemAdapter adapter;
			adapter = new FileListItemAdapter(this,0,list);
			
			//ListViewにListItemAdapterをセット
			ListView listView = (ListView) findViewById(R.id.list);
			
			setTheme(android.R.style.Theme_Dialog);
			listView.setAdapter(adapter);
	    	//選択時メール添付
			listView.setOnItemClickListener(
					new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?>parent,View view,int position,long id){
							//ListViewにキャスト
							ListView listView =  (ListView)parent;
							//リストアイテムを取得
							item = (ListItems)listView.getItemAtPosition(position);
							item.name = item.name.replace("\n", ".");
							File file= new File("/sdcard/TelRec_"+item.tmp+item.name + ".3gp");
					    	final TelRecPlayerDialog dialog = new TelRecPlayerDialog(TelRecFileListView.this);
					        final EditText editText = new EditText(TelRecFileListView.this);
					        editText.setText(item.name);
							SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);

							if (settings.getInt("SELECTED_C", 0) == 0){
								Toast.makeText(TelRecFileListView.this,"PRO版はこのままメールに添付できます。\n長押しでファイル再生メニューが出ます。",Toast.LENGTH_SHORT).show();	
							} else {
								//Player呼び出し
								file= new File("/sdcard/TelRec_"+item.tmp+editText.getText()+".3gp");
								dialog.pathToFile=file.getAbsolutePath();
								dialog.title="録音ファイル再生";
								dialog.showPlayerD();
							}
						}
			});
			//長押し時再生
			listView.setOnItemLongClickListener(
					new AdapterView.OnItemLongClickListener(){
						public boolean onItemLongClick(AdapterView<?>parent,View view,int position,long id) {
							
							//ListViewにキャスト
							//ListView listView =  (ListView)parent;
							listView1 =  (ListView)parent;
							//リストアイテムを取得
							//final ListItems item = (ListItems)listView.getItemAtPosition(position);
							item = (ListItems)listView1.getItemAtPosition(position);

							showDialog(MAIN_ID);

						return true;					

					}
				});
	return false;
	}
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    menu.addSubMenu(0,0,0,"SORT").setIcon(android.R.drawable.ic_menu_sort_alphabetically);
//	    menu.addSubMenu(0,1,0,"SORT DOWN").setIcon(android.R.drawable.ic_menu_sort_by_size);
    	    	return true;
	  }
	  
		@Override
		public boolean onMenuItemSelected(int featureId,MenuItem item){
				super.onMenuItemSelected(featureId, item);
		        switch (item.getItemId()){
				case 0:
					if (_sort ==true){
						_sort = false;
					}else {
						_sort=true;
					}
					
					CreateFileList("/sdcard/");
					break;
//				case 1:
//					_sort=false;
//					CreateFileList("/sdcard/");
//					break;
		        }
				return false;
		}
}