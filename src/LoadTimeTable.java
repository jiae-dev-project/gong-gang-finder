package com.example.emptytimefinder;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoadTimeTable extends Activity implements
		AdapterView.OnItemSelectedListener, OnClickListener {
	ArrayList<String> itemlist, tablelist, namelist, nametablelist, schedulelist;
				//�� �̸� list,����ü�� �ð�ǥ list, �����̸� list,�ش��̸��϶��ǽð�ǥ list, ������ list 
	ArrayList<Integer> indexlist, numberlist; //���õ� index����ϴ� list, ���ý������� �ִ� index ����
	String dbName = "gteamDB.db"; // �����ͺ��̽��� �̸�;
	String tableName1 = "gteamTBL"; // �̸��� ����� table
	String tableIndex = "", str = " ", myitem; // �ð�ǥ�� ����� str, �ش�ð��������λ�� str, ���缱��item
	int dbMode = Context.MODE_PRIVATE;
	int howtime_count=0, number, i, namecount = 0, tablecount = 0; //����� ��������
	int[] num;
	int[] ischecked = new int[35];
	FrameLayout TimeTableLayout, BottomLayout; //Gridlayout, �Ʒ� textView
	TextView name, myText, WhatTime, WhoTime,MiddleText; //gridview������text,�����ؽ�Ʈ,�����Ϥ��ð�,���������,����
	Button LoadTeam_btn, CheckOK_btn, Comeback_Btn;//�ð�ǥȮ�ι�ư,gridlayout���� ���ƿ���Ȯ�ι�ư,�ð�ǥ������ ���ư���
	Button[] LoadButtons = new Button[35];
	Integer[] loadBtnIDs = { R.id.r2c1, R.id.r2c2, R.id.r2c3, R.id.r2c4,
			R.id.r2c5, R.id.r3c1, R.id.r3c2, R.id.r3c3, R.id.r3c4, R.id.r3c5,
			R.id.r4c1, R.id.r4c2, R.id.r4c3, R.id.r4c4, R.id.r4c5, R.id.r5c1,
			R.id.r5c2, R.id.r5c3, R.id.r5c4, R.id.r5c5, R.id.r6c1, R.id.r6c2,
			R.id.r6c3, R.id.r6c4, R.id.r6c5, R.id.r7c1, R.id.r7c2, R.id.r7c3,
			R.id.r7c4, R.id.r7c5, R.id.r8c1, R.id.r8c2, R.id.r8c3, R.id.r8c4,
			R.id.r8c5 };
	SQLiteDatabase sqlDB;

	// sqlDB = openOrCreateDatabase(dbName, dbMode, null);
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_timetable_layout);
		// TODO Auto-generated method stub
		BottomLayout = (FrameLayout) findViewById(R.id.bottom_layout);
		TimeTableLayout = (FrameLayout) findViewById(R.id.time_table);
		LoadTeam_btn = (Button) findViewById(R.id.load_team_button);
		CheckOK_btn = (Button) findViewById(R.id.check_timetable);
		Comeback_Btn = (Button) findViewById(R.id.come_back_button);
		name = (TextView) findViewById(R.id.timetable_name);
		myText = (TextView) findViewById(R.id.mytext);
		WhatTime = (TextView) findViewById(R.id.what_time);
		WhoTime = (TextView) findViewById(R.id.who_time);
		MiddleText = (TextView) findViewById(R.id.middle_text);
		sqlDB = openOrCreateDatabase(dbName, dbMode, null);

		itemlist = new ArrayList<String>();
		tablelist = new ArrayList<String>();
		namelist = new ArrayList<String>();
		nametablelist = new ArrayList<String>();
		schedulelist = new ArrayList<String>();
		indexlist = new ArrayList<Integer>();
		numberlist = new ArrayList<Integer>();
		
		// ������ ��ü ����
		Spinner checkspin = (Spinner) findViewById(R.id.load_spinner);
		checkspin.setOnItemSelectedListener(this);
		// ����� ��ü ����
		getMyTeam();
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, itemlist);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		checkspin.setAdapter(adapter2);
		
		for (i = 0; i < loadBtnIDs.length; i++) {
			LoadButtons[i] = (Button) findViewById(loadBtnIDs[i]);
		}
		for (i = 0; i < loadBtnIDs.length; i++) {
			final int index;
			index = i;
			LoadButtons[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					BottomLayout.setVisibility(View.VISIBLE);//�Ʒ� TextView ���̰�
					viewWeek(index); //�����Ϥ��������� ����
					getMyname(myitem); //���� ���õ� �̸��� ������
					findWhoTime(index); //���� ���õ� index�� ������ �������� ����
					MiddleText.setText("������ ���:");
					if(howtime_count==0){ //�����ϰ��(�ƹ��� ������ �ƴҰ��)
						setNumber(index); //���� �ε��� ����
						if(numberlist.contains(index)){ //�����ٿ� �� �ε����� ��ϵǾ�������
							setschedulelist(); //������ ����
							int scheduleNum = numberlist.indexOf(index); //��ȣ ������
							String scheduleStr; //������ String����
							scheduleStr = schedulelist.get(scheduleNum); //������ ������ ������
							WhoTime.setText(scheduleStr);//������ �������� TextView ����
							MiddleText.setText("���ý����� : ");
							//WhoTime.setText(schedulelist.get(0));
						}else{
						getDialog(); //�������� �����ϰڳĴ� ���̾�α׸� ���
						}
					}
					schedulelist.clear(); //�����ٸ���Ʈ Ŭ���� 
					howtime_count = 0; //����� �������� Ŭ����
				}
			});
		}
		TimeTableLayout.setVisibility(View.INVISIBLE);
		BottomLayout.setVisibility(View.INVISIBLE);
		LoadTeam_btn.setOnClickListener(this);
		CheckOK_btn.setOnClickListener(this);
		Comeback_Btn.setOnClickListener(this);
		tableColorReset();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		myitem = itemlist.get(position);
		// Toast.makeText(LoadTimeTable.this,itemlist.get(position) +
		// "�� ���� �߽��ϴ�.", 1).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	public void getMyTeam() {

		Cursor cursor;
		cursor = sqlDB.rawQuery("SELECT * FROM gteamTBL;", null);
		while (cursor.moveToNext()) {
			itemlist.add(cursor.getString(0));
		}
		cursor.close();
	}
	public void setData(){
		Cursor cursor;
		int number = getNumber();
		cursor = sqlDB.rawQuery(
				"select schedule from scheduleTable where number = '" + number
						+ "';", null);
		while (cursor.moveToNext()) {
			WhoTime.setText(cursor.getString(0));
			Log.e("tablecount", Integer.toString(tablecount));
		}
		cursor.close();
		// sqlDB.close();
	}
	public void getDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("�������")
		.setIcon(R.drawable.ic_launcher)
		.setMessage("������ ����Ͻðڽ��ϱ�?")
		.setPositiveButton("��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				getCustomDialog();
			}
		})
		.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		}).show();
	}
	public void getCustomDialog(){
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_dialog,(ViewGroup)findViewById(R.id.layout_dialog));
		
		AlertDialog.Builder aDialog = new AlertDialog.Builder(LoadTimeTable.this);
		aDialog.setTitle("������ ���");
		aDialog.setIcon(R.drawable.ic_launcher);
		aDialog.setView(layout);
		final EditText Edt_schedule = new EditText(mContext);
		aDialog.setView(Edt_schedule);
		Edt_schedule.setTextColor(Color.BLACK);
		aDialog.setPositiveButton("����ϱ�", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//setSchedule(edt_schedule.getText().toString());
				setSchedule(Edt_schedule.getText().toString());
				setData();
				dialog.cancel();
				
			}
		});
		aDialog.setNegativeButton("���", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		AlertDialog ad = aDialog.create();
		ad.show();
	}
	public void setNumber(int index){
		number = index;
	}
	public int getNumber(){
		return number;
	}
	public void setSchedule(String schedule){
		Toast.makeText(getApplicationContext(),schedule,1).show();
		int number = getNumber();
		String sql = "insert into scheduleTable values('" + number + "','"
				 + schedule + "');";
		sqlDB.execSQL(sql);
		LoadButtons[number].setBackgroundColor(Color.parseColor("#ff0000"));
	}
	public void selectSchedule(){
		Cursor cursor;
		cursor = sqlDB.rawQuery("select number from scheduleTable;", null);
		while (cursor.moveToNext()) {
			numberlist.add(Integer.parseInt(cursor.getString(0)));
		}
		cursor.close();
		for(int i=0;i<numberlist.size();i++){
			LoadButtons[numberlist.get(i)].setBackgroundColor(Color.parseColor("#ff0000"));
		}
	}
	public void setschedulelist(){
		Cursor cursor;
		cursor = sqlDB.rawQuery("select schedule from scheduleTable;", null);
		while (cursor.moveToNext()) {
			schedulelist.add(cursor.getString(0));
		}
		cursor.close();
	}
	public void getMytable(String team) {

		Cursor cursor;
		cursor = sqlDB.rawQuery(
				"select timetable from teamTimeTable where team = '" + team
						+ "';", null);
		tablecount = 0;
		while (cursor.moveToNext()) {
			tablelist.add(cursor.getString(0));
			tablecount++;
			Log.e("tablecount", Integer.toString(tablecount));
		}
		cursor.close();
		// sqlDB.close();
	}

	public void getMyname(String team) {

		Cursor cursor;
		cursor = sqlDB.rawQuery("select name from teamTimeTable where team = '"
				+ team + "';", null);
		namecount = 0;
		while (cursor.moveToNext()) {
			namelist.add(cursor.getString(0));
			namecount++;
			Log.e("namecount", Integer.toString(namecount));
		}
		cursor.close();
		// sqlDB.close();
	}

	public void findWhoTime(int index) {//���� �� �ð��� �����̴�1!!!
		for (int i = 0; i < namelist.size(); i++) {
			Cursor cursor;
			cursor = sqlDB.rawQuery(
					"select timetable from teamTimeTable where name = '"
							+ namelist.get(i) + "';", null); //�ϴ� �� �����ִ¾ֵ� �ð�ǥ ����!
			while (cursor.moveToNext()) {//��������� �ٳ���!!
				nametablelist.add(cursor.getString(0));//�� �̸��ξֵ� �ð�ǥ �� �־�!!
			}
			cursor.close();

			for (int count = 0; count < nametablelist.size(); count++) {//�ð�ǥ����ŭ�ݺ�
				String[] array = nametablelist.get(count).split(",");//�ð�ǥ�и��ؼ� array�� ����
				//�ð�ǥ�� (11,22,32,1,3,2,)�̷������� ������־ index�� ���ö�� split����
				for (int j = 0; j < array.length; j++) {//array���̸�ŭ �ݺ�
					indexlist.add(Integer.parseInt(array[j])); //array�ȿ� �ִ°� indexlist�� ����
					array[j]="";//array�� �ٽ� ���
				}
			}
			nametablelist.clear();//�� ���� �ִ� �ֵ� �ð�ǥ�� ���
			if (indexlist.contains(index)) { //�� �ð��� �� �������??
				str += namelist.get(i) + " "; // �̸��� ����??
				howtime_count++; //������»���� �Ѹ� �þ�����.
			}
			indexlist.clear(); //����� �ð�ǥ index �ʱ�ȭ
		}
		WhoTime.setText(str);//���� ����� ���
		namelist.clear(); //���� �ִ� �ֵ� �̸� �ʱ�ȭ
		str="";//���� ����� ��������ϱ� �ʵ� �ʱ�ȭ
	}

	public void tableColorReset() {
		for (i = 0; i < loadBtnIDs.length; i++) {
			LoadButtons[i].setBackgroundColor(Color.parseColor("#00FF00"));
			selectSchedule();
		}
	}

	public void viewWeek(int index) {
		if ((index % 5) == 0) {// ������
			WhatTime.setText("������ " + ((index / 5) + 1) + "����");
		} else if ((index % 5) == 1) {// ȭ����
			WhatTime.setText("ȭ���� " + (((index - 1) / 5) + 1) + "����");
		} else if ((index % 5) == 2) {// ������
			WhatTime.setText("������ " + (((index - 2) / 5) + 1) + "����");
		} else if ((index % 5) == 3) {
			WhatTime.setText("����� " + (((index - 3) / 5) + 1) + "����");
		} else {// �ݿ���
			WhatTime.setText("�ݿ��� " + (((index - 4) / 5) + 1) + "����");
		}

	}
public void setEmptytimetable(){
	for (int count = 0; count < tablecount; count++) {
		Log.e("count", Integer.toString(count));
		String[] array = tablelist.get(count).split(",");
		num = new int[1000];
		for (int j = 0; j < array.length; j++) {
			num[j] = Integer.parseInt(array[j]);
		}

		for (i = 0; i < loadBtnIDs.length; i++) {
			if ((num[i] % 2) == 0) {
				LoadButtons[num[i]].setBackgroundColor(Color
						.parseColor("#888888"));
			} else {
				LoadButtons[num[i]].setBackgroundColor(Color
						.parseColor("#808080"));
			}
		}
	}
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.load_team_button:
			TimeTableLayout.setVisibility(View.VISIBLE);
			name.setText(myitem);
			myText.setText(myitem + "�� �����ð�ǥ�Դϴ�.(�ʷϻ� : ����)");
			getMytable(myitem);
			CheckOK_btn.setText("Ȯ��");
			setEmptytimetable();
			break;
		case R.id.check_timetable:
			tableColorReset();
			TimeTableLayout.setVisibility(View.INVISIBLE);
			BottomLayout.setVisibility(View.INVISIBLE);
			break;
		case R.id.come_back_button:
			Intent mIntent_comeback = new Intent(LoadTimeTable.this,
					MakeTimeTable.class);
			startActivity(mIntent_comeback);
			break;
		}

	}
}
