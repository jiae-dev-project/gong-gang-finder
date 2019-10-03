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
				//팀 이름 list,팀전체의 시간표 list, 팀원이름 list,해당이름일때의시간표 list, 스케줄 list 
	ArrayList<Integer> indexlist, numberlist; //선택된 index기억하는 list, 팀플스케줄이 있는 index 저장
	String dbName = "gteamDB.db"; // 데이터베이스의 이름;
	String tableName1 = "gteamTBL"; // 이름이 저장된 table
	String tableIndex = "", str = " ", myitem; // 시간표가 저장될 str, 해당시간에수업인사람 str, 현재선택item
	int dbMode = Context.MODE_PRIVATE;
	int howtime_count=0, number, i, namecount = 0, tablecount = 0; //몇명이 수업인지
	int[] num;
	int[] ischecked = new int[35];
	FrameLayout TimeTableLayout, BottomLayout; //Gridlayout, 아래 textView
	TextView name, myText, WhatTime, WhoTime,MiddleText; //gridview맨위에text,현재텍스트,ㅇ요일ㅇ시간,누가듣는지,설명
	Button LoadTeam_btn, CheckOK_btn, Comeback_Btn;//시간표확인버튼,gridlayout에서 돌아오는확인버튼,시간표만들기로 돌아가기
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
		
		// 스피터 객체 참조
		Spinner checkspin = (Spinner) findViewById(R.id.load_spinner);
		checkspin.setOnItemSelectedListener(this);
		// 어댑터 객체 생성
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
					BottomLayout.setVisibility(View.VISIBLE);//아래 TextView 보이게
					viewWeek(index); //ㅇ요일ㅇ교시인지 설정
					getMyname(myitem); //현재 선택된 이름을 가져옴
					findWhoTime(index); //현재 선택된 index에 수업이 누구인지 설정
					MiddleText.setText("수업인 사람:");
					if(howtime_count==0){ //공강일경우(아무도 수업이 아닐경우)
						setNumber(index); //현재 인덱스 설정
						if(numberlist.contains(index)){ //스케줄에 이 인덱스가 등록되어있을때
							setschedulelist(); //스케줄 설정
							int scheduleNum = numberlist.indexOf(index); //번호 가져옴
							String scheduleStr; //스케줄 String생성
							scheduleStr = schedulelist.get(scheduleNum); //스케줄 내용을 가져옴
							WhoTime.setText(scheduleStr);//스케줄 내용으로 TextView 설정
							MiddleText.setText("팀플스케줄 : ");
							//WhoTime.setText(schedulelist.get(0));
						}else{
						getDialog(); //스케줄을 생성하겠냐는 다이얼로그를 띄움
						}
					}
					schedulelist.clear(); //스케줄리스트 클리어 
					howtime_count = 0; //몇명이 수업인지 클리어
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
		// "을 선택 했습니다.", 1).show();
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
		builder.setTitle("일정등록")
		.setIcon(R.drawable.ic_launcher)
		.setMessage("일정을 등록하시겠습니까?")
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				getCustomDialog();
			}
		})
		.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			
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
		aDialog.setTitle("스케줄 등록");
		aDialog.setIcon(R.drawable.ic_launcher);
		aDialog.setView(layout);
		final EditText Edt_schedule = new EditText(mContext);
		aDialog.setView(Edt_schedule);
		Edt_schedule.setTextColor(Color.BLACK);
		aDialog.setPositiveButton("등록하기", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//setSchedule(edt_schedule.getText().toString());
				setSchedule(Edt_schedule.getText().toString());
				setData();
				dialog.cancel();
				
			}
		});
		aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			
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

	public void findWhoTime(int index) {//누가 이 시간에 수업이니1!!!
		for (int i = 0; i < namelist.size(); i++) {
			Cursor cursor;
			cursor = sqlDB.rawQuery(
					"select timetable from teamTimeTable where name = '"
							+ namelist.get(i) + "';", null); //일단 이 팀에있는애들 시간표 내놔!
			while (cursor.moveToNext()) {//다음사람도 다내놔!!
				nametablelist.add(cursor.getString(0));//이 이름인애들 시간표 다 넣어!!
			}
			cursor.close();

			for (int count = 0; count < nametablelist.size(); count++) {//시간표수만큼반복
				String[] array = nametablelist.get(count).split(",");//시간표분리해서 array에 넣음
				//시간표가 (11,22,32,1,3,2,)이런식으로 저장되있어서 index만 떼올라고 split해줌
				for (int j = 0; j < array.length; j++) {//array길이만큼 반복
					indexlist.add(Integer.parseInt(array[j])); //array안에 있는걸 indexlist에 넣음
					array[j]="";//array는 다시 비움
				}
			}
			nametablelist.clear();//이 팀에 있는 애들 시간표도 비움
			if (indexlist.contains(index)) { //이 시간에 너 수업듣니??
				str += namelist.get(i) + " "; // 이름이 뭐니??
				howtime_count++; //수업듣는사람이 한명 늘었구나.
			}
			indexlist.clear(); //저장된 시간표 index 초기화
		}
		WhoTime.setText(str);//누가 듣는지 출력
		namelist.clear(); //팀에 있는 애들 이름 초기화
		str="";//누가 듣는지 출렸햇으니까 너도 초기화
	}

	public void tableColorReset() {
		for (i = 0; i < loadBtnIDs.length; i++) {
			LoadButtons[i].setBackgroundColor(Color.parseColor("#00FF00"));
			selectSchedule();
		}
	}

	public void viewWeek(int index) {
		if ((index % 5) == 0) {// 월요일
			WhatTime.setText("월요일 " + ((index / 5) + 1) + "교시");
		} else if ((index % 5) == 1) {// 화요일
			WhatTime.setText("화요일 " + (((index - 1) / 5) + 1) + "교시");
		} else if ((index % 5) == 2) {// 수요일
			WhatTime.setText("수요일 " + (((index - 2) / 5) + 1) + "교시");
		} else if ((index % 5) == 3) {
			WhatTime.setText("목요일 " + (((index - 3) / 5) + 1) + "교시");
		} else {// 금요일
			WhatTime.setText("금요일 " + (((index - 4) / 5) + 1) + "교시");
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
			myText.setText(myitem + "팀 공강시간표입니다.(초록색 : 공강)");
			getMytable(myitem);
			CheckOK_btn.setText("확인");
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
