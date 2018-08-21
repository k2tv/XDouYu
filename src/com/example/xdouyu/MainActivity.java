package com.example.xdouyu;

import java.security.MessageDigest;
import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnClickListener{
	
	private String roomid,url;
	private EditText text;
	private Button bn,hq;
	private TextView room_name;
	private TextView online,nickname;
	private VideoView video;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bn = (Button) findViewById(R.id.bn);
		bn.setText("未直播");
		bn.setEnabled(false);
		hq = (Button) findViewById(R.id.hq);
		text = (EditText) findViewById(R.id.text);
		hq.setOnClickListener(this);
		bn.setOnClickListener(this);
		video=(VideoView)findViewById(R.id.video);

		room_name = (TextView) findViewById(R.id.room_name);
		nickname = (TextView) findViewById(R.id.nickname);
		online = (TextView) findViewById(R.id.online);
	}
	
	public void connect() {
		
		roomid = text.getText().toString();
		String path = "https://m.douyu.com/html5/live?roomId="+roomid;
		Log.e("intel", "path:" + path);
		HttpUtils http = new HttpUtils(1000);// 超时
		http.configCurrentHttpCacheExpiry(500); // 设置缓存
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
				//Log.e("intel","err:"+arg0+"---"+arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// 加载数据 转化为JSON格式
				Connect content = (Connect) JSONObject.parseObject(arg0.result,Connect.class);
				DataAPI data = content.getData();
				if (content.getError().equals("0")) {
					room_name.setText("房间:"+data.getRoom_name());
					nickname.setText("主播:"+data.getNickname());
					
					if(data.getShow_status().equals("1")){
						bn.setEnabled(true);
						bn.setText("弹窗直播");	

						online.setText("在线人数:"+data.getOnline());
						url = data.getHls_url();
						
						video.setVideoPath(url);  
						 
			            //让VideiView获取焦点  
			            video.requestFocus();  
			            video.start();
						
					}else{
						bn.setText("未直播");
						bn.setEnabled(false);
					
					}
						
				} else{
					Toast.makeText(getBaseContext(), content.getError(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.hq:
			if(text.getText().toString().equals("")){
				Toast.makeText(getBaseContext(), "请输入房间号", Toast.LENGTH_SHORT).show();
			}else{
				connect();
			}
			
			break;
		case R.id.bn:
			
			video.stopPlayback();
			Uri uri = Uri.parse(url);
			//Toast.makeText(getBaseContext(), "成功", Toast.LENGTH_SHORT).show()		
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//Log.v("URI:::::::::", uri.toString());
			intent.setDataAndType(uri, "video/mp4");
			startActivity(intent);
			 
            
			break;
		default:
			break;
		}
		
	}

}