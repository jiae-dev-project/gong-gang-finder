package com.example.emptytimefinder;

import java.util.ArrayList;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MakeTimeTable extends Activity implements
		AdapterView.OnItemSelectedListener, OnClickListener {
	String dbName = "gteamDB.db"; //데이터베이스 이름
	String tableName1 = "gteamTBL"; //데이터베이스 테이블 이름
	String tableIndex = ""; //시간표를 선택했을때의 값들을 저장할 String 값
	int dbMode = Context.MODE_PRIVATE; //데이터베이스 실행시 모드
	int[] ischecked = new int[35]; // 시간표를 눌렀는지 안눌렀는지 확인할 변수
	ArrayList<String> itemlist; //spinner에 들어갈 값들의 arraylist
	String myitem; //현재 선택한 spinner값을 저장할 String
	SQLiteDatabase sqlDB; //데이터베이스 생성시 꼭 선언해야 하는 SQliteDatabase
	EditText edt_makename; //팀에 소속된 멤버이름을 적을 EditText
	TextView name; // Layout이 떴을때 이름이 뜨는 부분
	FrameLayout TimeTableLayout; //GridLayout을 포함하고 있는 include한 레이아웃
	Button MakeName_btn, CheckOK_btn, goLoadTable; 
	//팀원이름쓰고 등록하는버튼, 확인버튼, 시간표확인하기 액티비티로 이동하는 버튼
	Button[] tableButtons = new Button[35]; //GridLayout에 해당된 버튼들
	Integer[] tableBtnIDs = { R.id.r2c1, R.id.r2c2, R.id.r2c3, R.id.r2c4,
			R.id.r2c5, R.id.r3c1, R.id.r3c2, R.id.r3c3, R.id.r3c4, R.id.r3c5,
			R.id.r4c1, R.id.r4c2, R.id.r4c3, R.id.r4c4, R.id.r4c5, R.id.r5c1,
			R.id.r5c2, R.id.r5c3, R.id.r5c4, R.id.r5c5, R.id.r6c1, R.id.r6c2,
			R.id.r6c3, R.id.r6c4, R.id.r6c5, R.id.r7c1, R.id.r7c2, R.id.r7c3,
			R.id.r7c4, R.id.r7c5, R.id.r8c1, R.id.r8c2, R.id.r8c3, R.id.r8c4,
			R.id.r8c5 };
	int i; //GridLayout 버튼들 돌릴때 사용할 i변수

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_timetable_layout);

		TimeTableLayout = (FrameLayout) findViewById(R.id.time_table);
		MakeName_btn = (Button) findViewById(R.id.make_name_button);
		CheckOK_btn = (Button) findViewById(R.id.check_timetable);
		goLoadTable = (Button) findViewById(R.id.go_loadtime);
		edt_makename = (EditText) findViewById(R.id.makename_ET);
		name = (TextView) findViewById(R.id.timetable_name);
		sqlDB = openOrCreateDatabase(dbName, dbMode, null);

		itemlist = new ArrayList<String>(); //spinner안에 있는 item들의 list
		// 스피터 객체 참조
		Spinner teamspin = (Spinner) findViewById(R.id.teamname_spinner);
		teamspin.setOnItemSelectedListener(this);
		getMyTeam();// itemlist에 Database안에 있는 팀 이름명을 가져옴
		// 어댑터 객체 생성
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, itemlist);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamspin.setAdapter(adapter);
		
		for (i = 0; i < tableBtnIDs.length; i++) //Gridlayout의 버튼들 ID 등록
			tableButtons[i] = (Button) findViewById(tableBtnIDs[i]);
		for (i = 0; i < tableBtnIDs.length; i++) {
			final int index; //반드시 final로 선언할것!
			index = i; //index를 가져와서 넣음
			tableButtons[index].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (ischecked[index] == 0) { //버튼을 선택하지 않았을 경우 들어옴
						tableButtons[index].setBackgroundColor(Color.RED); //빨간색으로 변경
						tableIndex += index; //tableIndex는 해당 시간표가 어떤지 저장함
						tableIndex += ","; //index사이를 , 으로 나눔
						ischecked[index] = 1; //버튼을 선택했다고 바꿈
					} else { //버튼을 선택했을 경우 들어옴
						if ((index % 2) == 0) { //짝수번째에는 연한 회색
							tableButtons[index].setBackgroundColor(Color
									.parseColor("#888888"));
						} else { //홀수번째에는 진한 회색
							tableButtons[index].setBackgroundColor(Color
									.parseColor("#808080"));
						}
						tableIndex = tableIndex.replaceAll(index+",","");
						ischecked[index] = 0; //버튼을 선택하지 않았다고 바꿔줌.
					}
				}
			});
		}
		tableColorReset(); //바둑판식배열로 tablecolor를 바꿔주는 메서드(진한회색연한회색)
		TimeTableLayout.setVisibility(View.INVISIBLE); //처음에는 gridlayout의 테이블이 보이지 않게함
		MakeName_btn.setOnClickListener(this);
		CheckOK_btn.setOnClickListener(this);
		goLoadTable.setOnClickListener(this);//버튼 리스너 등록
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		myitem = itemlist.get(position);
		// Toast.makeText(MakeTimeTable.this,itemlist.get(position) +
		// "을 선택 했습니다.", 1).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.make_name_button: //이름을 쓰고 등록하는 버튼
			TimeTableLayout.setVisibility(View.VISIBLE); //그리드레이아웃이 보임
			name.setText(edt_makename.getText().toString()); //그리드레이아웃 맨 위에 이름이 뜸
			Toast.makeText(getApplicationContext(), "시간표를 입력해주세요", 1).show();
			break;
		case R.id.check_timetable: //테이블을 체크하고나서 확인버튼
			Toast.makeText(getApplicationContext(), "등록되었습니다", 1).show();
			tableColorReset();//바둑판식 색상으로 초기화 (진한회색,회색)
			insertDataString(myitem, name.getText().toString(), tableIndex);
			//데이터베이스에 팀 이름, 이름, 시간표를 저장
			tableIndex = ""; //테이블 인덱스 초기화
			name.setText(""); //name 초기화
			edt_makename.setText(""); //이름을 썼던 EditText 초기화
			TimeTableLayout.setVisibility(View.INVISIBLE); //그리드레이아웃 안보임
			break;
		case R.id.go_loadtime: //시간표 확인하기 액티비티로 이동
			Intent mIntent_goLoadTable = new Intent(MakeTimeTable.this,
					LoadTimeTable.class);
			startActivity(mIntent_goLoadTable);
			break;
		}
	}

	public void getMyTeam() {

		Cursor cursor;
		cursor = sqlDB.rawQuery("SELECT * FROM gteamTBL;", null);
		while (cursor.moveToNext()) {
			itemlist.add(cursor.getString(0));
		}
		cursor.close();
	}



	public void tableColorReset() {
		for (i = 0; i < tableBtnIDs.length; i++) {
			if ((i % 2) == 0) {
				tableButtons[i].setBackgroundColor(Color.parseColor("#888888"));
			} else {
				tableButtons[i].setBackgroundColor(Color.parseColor("#808080"));
			}

		}
	}

	// Data 추가
	public void insertDataString(String teamname, String name, String table) {
		String sql = "insert into teamTimeTable values('" + teamname + "','"
				+ name + "','" + table + "');";
		sqlDB.execSQL(sql);
		Log.e("데이터추가", table);
	}

	public void insertData(String voca, String tableName) {
		String sql = "insert into " + tableName + " values('" + voca + "');";
		sqlDB.execSQL(sql);
		Log.e("데이터추가", voca);
	}

}
