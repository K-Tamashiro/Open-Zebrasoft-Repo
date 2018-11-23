package jp.co.zebrasoft.android.telrecfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListItemAdapter extends ArrayAdapter<ListItems>{
	
	private LayoutInflater mInflater;
	
	public ListItemAdapter(Context context,	int rid,List<ListItems>list){
		
		super(context,rid,list);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

public View getView(int position,View convertView,ViewGroup parent){
	
	//データを取り出す
	ListItems items = (ListItems)getItem(position);
	
	//レイアウトファイルからViewを生成
	View view = mInflater.inflate(R.layout.menu_item, null);
	
	//画像をセット
	ImageView image;
	image = (ImageView)view.findViewById(R.id.image);
	image.setImageBitmap(items.image);
	
	//ユーザ名をセット
	TextView name;
	name = (TextView)view.findViewById(R.id.name);
	name.setText(items.name);
	
	//コメントをセット
	TextView comment;
	comment = (TextView)view.findViewById(R.id.comment);
	comment.setText(items.comment);

	//コメントをセット
	TextView vTime;
	vTime = (TextView)view.findViewById(R.id.vTime);
	vTime.setText(items.vTime);

	return view;
	}



}