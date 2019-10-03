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
	String dbName = "gteamDB.db"; //�����ͺ��̽� �̸�
	String tableName1 = "gteamTBL"; //�����ͺ��̽� ���̺� �̸�
	String tableIndex = ""; //�ð�ǥ�� ������������ ������ ������ String ��
	int dbMode = Context.MODE_PRIVATE; //�����ͺ��̽� ����� ���
	int[] ischecked = new int[35]; // �ð�ǥ�� �������� �ȴ������� Ȯ���� ����
	ArrayList<String> itemlist; //spinner�� �� ������ arraylist
	String myitem; //���� ������ spinner���� ������ String
	SQLiteDatabase sqlDB; //�����ͺ��̽� ������ �� �����ؾ� �ϴ� SQliteDatabase
	EditText edt_makename; //���� �Ҽӵ� ����̸��� ���� EditText
	TextView name; // Layout�� ������ �̸��� �ߴ� �κ�
	FrameLayout TimeTableLayout; //GridLayout�� �����ϰ� �ִ� include�� ���̾ƿ�
	Button MakeName_btn, CheckOK_btn, goLoadTable; 
	//�����̸����� ����ϴ¹�ư, Ȯ�ι�ư, �ð�ǥȮ���ϱ� ��Ƽ��Ƽ�� �̵��ϴ� ��ư
	Button[] tableButtons = new Button[35]; //GridLayout�� �ش�� ��ư��
	Integer[] tableBtnIDs = { R.id.r2c1, R.id.r2c2, R.id.r2c3, R.id.r2c4,
			R.id.r2c5, R.id.r3c1, R.id.r3c2, R.id.r3c3, R.id.r3c4, R.id.r3c5,
			R.id.r4c1, R.id.r4c2, R.id.r4c3, R.id.r4c4, R.id.r4c5, R.id.r5c1,
			R.id.r5c2, R.id.r5c3, R.id.r5c4, R.id.r5c5, R.id.r6c1, R.id.r6c2,
			R.id.r6c3, R.id.r6c4, R.id.r6c5, R.id.r7c1, R.id.r7c2, R.id.r7c3,
			R.id.r7c4, R.id.r7c5, R.id.r8c1, R.id.r8c2, R.id.r8c3, R.id.r8c4,
			R.id.r8c5 };
	int i; //GridLayout ��ư�� ������ ����� i����

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

		itemlist = new ArrayList<String>(); //spinner�ȿ� �ִ� item���� list
		// ������ ��ü ����
		Spinner teamspin = (Spinner) findViewById(R.id.teamname_spinner);
		teamspin.setOnItemSelectedListener(this);
		getMyTeam();// itemlist�� Database�ȿ� �ִ� �� �̸����� ������
		// ����� ��ü ����
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, itemlist);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamspin.setAdapter(adapter);
		
		for (i = 0; i < tableBtnIDs.length; i++) //Gridlayout�� ��ư�� ID ���
			tableButtons[i] = (Button) findViewById(tableBtnIDs[i]);
		for (i = 0; i < tableBtnIDs.length; i++) {
			final int index; //�ݵ�� final�� �����Ұ�!
			index = i; //index�� �����ͼ� ����
			tableButtons[index].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (ischecked[index] == 0) { //��ư�� �������� �ʾ��� ��� ����
						tableButtons[index].setBackgroundColor(Color.RED); //���������� ����
						tableIndex += index; //tableIndex�� �ش� �ð�ǥ�� ��� ������
						tableIndex += ","; //index���̸� , ���� ����
						ischecked[index] = 1; //��ư�� �����ߴٰ� �ٲ�
					} else { //��ư�� �������� ��� ����
						if ((index % 2) == 0) { //¦����°���� ���� ȸ��
							tableButtons[index].setBackgroundColor(Color
									.parseColor("#888888"));
						} else { //Ȧ����°���� ���� ȸ��
							tableButtons[index].setBackgroundColor(Color
									.parseColor("#808080"));
						}
						tableIndex = tableIndex.replaceAll(index+",","");
						ischecked[index] = 0; //��ư�� �������� �ʾҴٰ� �ٲ���.
					}
				}
			});
		}
		tableColorReset(); //�ٵ��ǽĹ迭�� tablecolor�� �ٲ��ִ� �޼���(����ȸ������ȸ��)
		TimeTableLayout.setVisibility(View.INVISIBLE); //ó������ gridlayout�� ���̺��� ������ �ʰ���
		MakeName_btn.setOnClickListener(this);
		CheckOK_btn.setOnClickListener(this);
		goLoadTable.setOnClickListener(this);//��ư ������ ���
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		myitem = itemlist.get(position);
		// Toast.makeText(MakeTimeTable.this,itemlist.get(position) +
		// "�� ���� �߽��ϴ�.", 1).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.make_name_button: //�̸��� ���� ����ϴ� ��ư
			TimeTableLayout.setVisibility(View.VISIBLE); //�׸��巹�̾ƿ��� ����
			name.setText(edt_makename.getText().toString()); //�׸��巹�̾ƿ� �� ���� �̸��� ��
			Toast.makeText(getApplicationContext(), "�ð�ǥ�� �Է����ּ���", 1).show();
			break;
		case R.id.check_timetable: //���̺��� üũ�ϰ��� Ȯ�ι�ư
			Toast.makeText(getApplicationContext(), "��ϵǾ����ϴ�", 1).show();
			tableColorReset();//�ٵ��ǽ� �������� �ʱ�ȭ (����ȸ��,ȸ��)
			insertDataString(myitem, name.getText().toString(), tableIndex);
			//�����ͺ��̽��� �� �̸�, �̸�, �ð�ǥ�� ����
			tableIndex = ""; //���̺� �ε��� �ʱ�ȭ
			name.setText(""); //name �ʱ�ȭ
			edt_makename.setText(""); //�̸��� ��� EditText �ʱ�ȭ
			TimeTableLayout.setVisibility(View.INVISIBLE); //�׸��巹�̾ƿ� �Ⱥ���
			break;
		case R.id.go_loadtime: //�ð�ǥ Ȯ���ϱ� ��Ƽ��Ƽ�� �̵�
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

	// Data �߰�
	public void insertDataString(String teamname, String name, String table) {
		String sql = "insert into teamTimeTable values('" + teamname + "','"
				+ name + "','" + table + "');";
		sqlDB.execSQL(sql);
		Log.e("�������߰�", table);
	}

	public void insertData(String voca, String tableName) {
		String sql = "insert into " + tableName + " values('" + voca + "');";
		sqlDB.execSQL(sql);
		Log.e("�������߰�", voca);
	}

}
