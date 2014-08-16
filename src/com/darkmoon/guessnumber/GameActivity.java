package com.darkmoon.guessnumber;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener {

	private static final int MSG_CHECK = 0x1;
	private static final int MSG_OVER = 0x2;
	private static final int MSG_VICTORY = 0x3;
	private static final int MSG_CLEAR = 0x4;

	private static final int STATUS_NORMAL = 0x1;
	private static final int STATUS__OVER = 0x2;
	private static final int STATUS__VICTORY = 0x3;

	private int status = STATUS_NORMAL;

	private TextView txtLeftTime;
	private TextView[] txtMyGuess = new TextView[4];

	private RadioButton[] rbIndex = new RadioButton[4];
	private Chronometer timer;

	private ListView lstGuess;

	private int index = 0;

	private int time = 10;
	private String answer = "";

	private GuessAdapter guessAdapter = new GuessAdapter();

	private ArrayList<String> myGuessList = new ArrayList<String>();

	// private long start;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CHECK: {
				int a = msg.arg1;
				int b = msg.arg2;

				StringBuilder sb = new StringBuilder();
				for (TextView txTextView : txtMyGuess) {
					sb.append(txTextView.getText().toString());
				}

				sb.append("                ").append(String.valueOf(a))
						.append("A").append(String.valueOf(b)).append("B");
				myGuessList.add(sb.toString());

				guessAdapter.notifyDataSetChanged();

				txtLeftTime.setText(String.valueOf(time));

				lstGuess.setSelection(lstGuess.getBottom());
				handler.sendEmptyMessage(MSG_CLEAR);
				break;
			}
			case MSG_OVER: {
				status = STATUS__OVER;
				Toast.makeText(GameActivity.this, "步数用完，你输了", Toast.LENGTH_LONG)
						.show();
				timer.stop();
				handler.sendEmptyMessage(MSG_CLEAR);
				break;
			}
			case MSG_VICTORY: {
				status = STATUS__VICTORY;
				Toast.makeText(GameActivity.this,
						"恭喜你获得了胜利，用时：" + timer.getText(), Toast.LENGTH_LONG)
						.show();

				timer.stop();
				handler.sendEmptyMessage(MSG_CLEAR);
				break;
			}
			case MSG_CLEAR: {
				clear();
				break;
			}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		((Button) findViewById(R.id.btnKey0)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey1)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey2)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey3)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey4)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey5)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey6)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey7)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey8)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKey9)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKeyNew)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKeyClear)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKeyCheck)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKeyLeft)).setOnClickListener(this);
		((Button) findViewById(R.id.btnKeyRight)).setOnClickListener(this);

		timer = (Chronometer) this.findViewById(R.id.chronometer1);
		txtLeftTime = (TextView) findViewById(R.id.txtLeftTime);
		txtMyGuess[0] = (TextView) findViewById(R.id.txtGuess1);
		txtMyGuess[1] = (TextView) findViewById(R.id.txtGuess2);
		txtMyGuess[2] = (TextView) findViewById(R.id.txtGuess3);
		txtMyGuess[3] = (TextView) findViewById(R.id.txtGuess4);

		rbIndex[0] = (RadioButton) findViewById(R.id.rbGuess1);
		rbIndex[1] = (RadioButton) findViewById(R.id.rbGuess2);
		rbIndex[2] = (RadioButton) findViewById(R.id.rbGuess3);
		rbIndex[3] = (RadioButton) findViewById(R.id.rbGuess4);

		lstGuess = (ListView) findViewById(R.id.lstResult);
		lstGuess.setAdapter(guessAdapter);
		init();
	}

	private void init() {
		handler.sendEmptyMessage(MSG_CLEAR);

		myGuessList.clear();
		guessAdapter.notifyDataSetChanged();

		// 生成谜底
		answer = getAnswer();
		// 状态归零
		status = STATUS_NORMAL;
		// 重置次数
		time = 10;
		txtLeftTime.setText(String.valueOf(time));
		// 启动计时
		// start = System.currentTimeMillis();

		// 将计时器清零
		timer.setBase(SystemClock.elapsedRealtime());
		// 开始计时
		timer.start();
	}

	private String getAnswer() {
		StringBuffer sb = new StringBuffer();
		String str = "0123456789";
		Random r = new Random();
		for (int i = 0; i < 4; i++) {
			int num = r.nextInt(str.length());
			sb.append(str.charAt(num));
			str = str.replace((str.charAt(num) + ""), "");
		}

		Log.e("answer", sb.toString());
		return sb.toString();
	}

	private void addNum(String num) {
		if (status == STATUS__OVER || status == STATUS__VICTORY) {
			return;
		}

		for (int i = 0; i < index; i++) {
			if (num.equals(txtMyGuess[i].getText().toString())) {
				Toast.makeText(this, "这个数字已经有了", Toast.LENGTH_LONG).show();
				return;
			}
		}

		txtMyGuess[index].setText(num);
		if (index < 3) {
			index += 1;
			rbIndex[index].setChecked(true);
		}

	}

	private void clear() {
		for (TextView textView : txtMyGuess) {
			textView.setText("");
		}

		index = 0;
		rbIndex[index].setChecked(true);
	}

	private void check() {

		int a = 0;
		int b = 0;

		for (int i = 0; i < 4; i++) {
			String guessOne = txtMyGuess[i].getText().toString();
			String an = answer.substring(i, i + 1);
			if (guessOne.equals(an)) {
				a++;
			} else if (answer.contains(guessOne)) {
				b++;
			}
		}
		Message message = new Message();
		message.what = MSG_CHECK;
		message.arg1 = a;
		message.arg2 = b;
		handler.sendMessage(message);

		time -= 1;
		if (a == 4) {
			Message msgV = new Message();
			msgV.what = MSG_VICTORY;
			handler.sendMessage(msgV);
		} else if (time == 0) {
			Message msgO = new Message();
			msgO.what = MSG_OVER;
			handler.sendMessage(msgO);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnKey0: {
			addNum("0");
			break;
		}
		case R.id.btnKey1: {
			addNum("1");
			break;
		}
		case R.id.btnKey2: {
			addNum("2");
			break;
		}
		case R.id.btnKey3: {
			addNum("3");
			break;
		}
		case R.id.btnKey4: {
			addNum("4");
			break;
		}
		case R.id.btnKey5: {
			addNum("5");
			break;
		}
		case R.id.btnKey6: {
			addNum("6");
			break;
		}
		case R.id.btnKey7: {
			addNum("7");
			break;
		}
		case R.id.btnKey8: {
			addNum("8");
			break;
		}
		case R.id.btnKey9: {
			addNum("9");
			break;
		}
		case R.id.btnKeyLeft: {
			if (index == 0) {
				return;
			}
			index -= 1;
			rbIndex[index].setChecked(true);
			break;
		}
		case R.id.btnKeyRight: {
			if (index >= 3) {
				return;
			}
			index += 1;
			rbIndex[index].setChecked(true);
			break;
		}
		case R.id.btnKeyNew: {
			init();
		}
			break;
		case R.id.btnKeyClear: {
			clear();
			break;
		}
		case R.id.btnKeyCheck: {

			if (status == STATUS__OVER || status == STATUS__VICTORY) {
				return;
			}

			for (TextView textView : txtMyGuess) {
				if (textView.getText() == null || "".equals(textView.getText())) {
					return;
				}

			}

			check();

			break;
		}
		}

	}

	private class GuessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return myGuessList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return myGuessList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater layoutInflator = LayoutInflater
						.from(GameActivity.this);

				convertView = layoutInflator.inflate(R.layout.listitem_guess,
						null);
				holder = new ViewHolder();

				holder.txtIndex = (TextView) convertView
						.findViewById(R.id.txtIndex);
				holder.txtResult = (TextView) convertView
						.findViewById(R.id.txtResult);

				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			if (myGuessList.size() > 0) {
				holder.txtIndex.setText(String.valueOf(position + 1));
				holder.txtResult.setText(myGuessList.get(position));
			}
			return convertView;
		}

		class ViewHolder {
			private TextView txtIndex, txtResult;
		}
	}
}
