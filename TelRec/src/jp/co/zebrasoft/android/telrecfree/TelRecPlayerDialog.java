package jp.co.zebrasoft.android.telrecfree;

//////////////////////////////////////////////
// プレイヤーダイアログ（透明Activity）
/////////////////////////////////////////////

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class TelRecPlayerDialog extends Activity{
	private 	Context 	_context;
	private 	Dialog 		_dlgThis;
	public 		String 		title;
	public 		String 		pathToFile;
	private 	MediaPlayer mp;
	private boolean 		canSeek = false;
	private 	ImageButton stopButton;
	private 	ImageButton playbackButton;
	private 	SeekBar 	seekBar;
//	private 	Thread 		seekThread = new SeekThread();
	private 	Handler 	handler = new Handler();

	public TelRecPlayerDialog(Context context) {
		_context = context;
	}
	
	public void onList() {

		mp.release();
		mp = null;

		_dlgThis.dismiss();

	}

	@Override
	public void onPause() {
		super.onPause();
//		if (_dlgThis != null && _dlgThis.isShowing())


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mp.stop();
		mp.setOnCompletionListener(null);
		mp.release();
		mp = null;
	}

	private void playback() {
		if (mp.isPlaying()) {
			mp.pause();
			playbackButton.setImageResource(R.drawable.playback);
		} else {
			mp.start();
			playbackButton.setImageResource(R.drawable.pause);

			synchronized (this) {
				notify();
			}
		}
	}

	public void stop() {
		playbackButton.setImageResource(R.drawable.playback);
		//playbackButton.setEnabled(false);
		if(mp.isPlaying()){
		canSeek = false;
		mp.stop();
		mp.prepareAsync();
		synchronized (this) {
			notify();
		
		}
		}else{
//			canSeek = false;
//			mp.stop();
//			mp.prepareAsync();
		}
	}

	
	private MediaPlayer createMediaPlayer() {
		mp = new MediaPlayer();
		try {
			mp.setDataSource(pathToFile);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mp;
	}

	public void showPlayerD() {

		String FN = pathToFile.replace("/sdcard/TelRec_", "");

		final Dialog _dlgThis = new Dialog(_context);
		_dlgThis.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		_dlgThis.setContentView(R.layout.dialog); 
		_dlgThis.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dialog_i);
		TextView text = (TextView) _dlgThis.findViewById(R.id.text);
		TextView ptitle = (TextView) _dlgThis.findViewById(R.id.ptitle);
		ImageView img = (ImageView) _dlgThis.findViewById(R.id.icon);
		TextView tv1=(TextView)_dlgThis.findViewById(R.id.alltime);
		final TextView tv2=(TextView)_dlgThis.findViewById(R.id.stime);

		
		if (FN.substring(0,1).equals("着")){
			img.setImageResource(R.drawable.recive);
			ptitle.setText("着信録音");
		}else if (FN.substring(0,1).equals("発")){
			img.setImageResource(R.drawable.send);
			ptitle.setText("発信録音");
		}else{
			img.setImageResource(R.drawable.recoder);
			ptitle.setText("手動録音");
		}
		FN = FN.replace(".3gp", "");
		FN = FN.replace("着.", "");
		FN = FN.replace("発.", "");
		FN = FN.replace("手.", "");
		FN = FN.replace(".", "\n");
		text.setText(FN);
		_dlgThis.setTitle(title);
		_dlgThis.show();
		
		
		//再生中に戻るキーを押した場合停止する
		_dlgThis.setOnKeyListener(new OnKeyListener() { 
		  @Override 
		  public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) { 
		    // キーが押されたとき呼ばれる 
			  switch(keyCode){
				  case KeyEvent.KEYCODE_BACK:
						if(mp.isPlaying()){
							//_dlgThis.dismiss();
							  Toast.makeText(_context,"再生を停止しました。",Toast.LENGTH_SHORT).show();
								
							stop();
						}
						
						  finish();
					 break;
				default:
					break;
			  }
				return false; 
		  } 
		}); 

		mp = createMediaPlayer();
		mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
			}
		});
		mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.release();
				mp = createMediaPlayer();
				return true;
			}
		});
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				playbackButton.setImageResource(R.drawable.playback);
				Toast.makeText(_context,"最後まで再生しました。",Toast.LENGTH_SHORT).show();

			}
		});
		mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				canSeek = true;
				playbackButton.setEnabled(true);
			}
		});


		stopButton = (ImageButton) _dlgThis.findViewById(R.id.stop);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
			}
		});

		playbackButton = (ImageButton) _dlgThis.findViewById(R.id.playback);
		playbackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playback();
			}
		});

		seekBar = (SeekBar) _dlgThis.findViewById(R.id.seek);
		seekBar.setMax(mp.getDuration());
		int sec = ((seekBar.getMax()/1000))%60;
		int min = (((seekBar.getMax()/1000))/60)%60;
		int hou = ((seekBar.getMax()/1000))/3600;
		tv1.setText(String.format("%02d:%02d:%02d", hou,min,sec));
		tv2.setText(String.format("%02d:%02d:%02d", 0,0,0));
		//Toast.makeText(_context,"シーク"+seekBar.getMax(),Toast.LENGTH_SHORT).show();
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser && canSeek)
					mp.seekTo(progress);
				int sec = ((progress/1000))%60;
				int min = (((progress/1000))/60)%60;
				int hou = ((progress/1000))/3600;
				tv2.setText(String.format("%02d:%02d:%02d", hou,min,sec));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		Thread seekThread = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					while (true) {
						handler.post(onSeekChanged);
						if (!mp.isPlaying()) {
							synchronized (TelRecPlayerDialog.this) {
								TelRecPlayerDialog.this.wait();
							}
						}
						Thread.sleep(500);
					}
				} catch (InterruptedException ex) {
				}
			}
			private Runnable onSeekChanged = new Runnable() {

				@Override
				public void run() {
					seekBar.setProgress(mp.getCurrentPosition());
				}
			};

		});
		seekThread.start();

	}

}