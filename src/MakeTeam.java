package com.example.emptytimefinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MakeTeam extends Activity implements OnClickListener {
	String dbName = "gteamDB.db"; // name of Database;
	String tableName = "gteamTBL"; // name of Table;
	int dbMode = Context.MODE_PRIVATE;

	SQLiteDatabase sqlDB;
	Button maketeam_button, deleteteam_button, goteamname_button;
	EditText edtTeamName, edt_team;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_team_layout);

		edtTeamName = (EditText) findViewById(R.id.maketeamname_ET);
		maketeam_button = (Button) findViewById(R.id.make_team_button);
		maketeam_button.setOnClickListener(this);
		deleteteam_button = (Button) findViewById(R.id.delete_team_button);
		deleteteam_button.setOnClickListener(this);
		goteamname_button = (Button) findViewById(R.id.go_teamname_button);
		goteamname_button.setOnClickListener(this);
		edt_team = (EditText) findViewById(R.id.edit_team);

		sqlDB = openOrCreateDatabase(dbName, dbMode, null);
		seeMyTeam();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.make_team_button:
			Log.e("�������", "��ưŬ��");
			insertData(edtTeamName.getText().toString());
			Toast.makeText(getApplicationContext(), "�Էµ�", Toast.LENGTH_SHORT)
					.show();
			edtTeamName.setText("");
			seeMyTeam();

			break;
		case R.id.delete_team_button:
			removeData(edtTeamName.getText().toString());
			Toast.makeText(getApplicationContext(), "������", Toast.LENGTH_SHORT)
					.show();
			edtTeamName.setText("");
			seeMyTeam();
			break;
		case R.id.go_teamname_button:
			Intent mIntent_maketeambtn = new Intent(MakeTeam.this,
					MakeTimeTable.class);
			startActivity(mIntent_maketeambtn);
			finish();
			break;
		}
	}

	// Database ���� �� ����
	public void createDatabase(String dbName, int dbMode) {
		sqlDB = openOrCreateDatabase(dbName, dbMode, null);
	}

	// Table ����
	public void createTable() {
		String sql = "create table " + tableName + "(gteamName char(20))";
		sqlDB.execSQL(sql);
	}

	// Table ����
	public void removeTable() {
		String sql = "drop table " + tableName;
		sqlDB.execSQL(sql);
	}

	// Data �߰�
	public void insertData(String voca) {

		String sql = "insert into " + tableName + " values('" + voca + "');";
		sqlDB.execSQL(sql);
		Log.e("�������߰�", voca);
	}

	// Data ������Ʈ
	public void updateData(int index, String voca) {
		String sql = "update " + tableName + " set voca = '" + voca + "';";
		sqlDB.execSQL(sql);
	}

	// Data ����
	public void removeData(String voca) {
		String sql = "delete from " + tableName + " where gteamName = '" + voca
				+ "';";
		sqlDB.execSQL(sql);
		Log.e("������ ����", voca);
	}

	// Data �б�(��������)
	public void selectData(int index) {
		String sql = "select * from " + tableName + " where gteamName = "
				+ index + ";";
		Cursor result = sqlDB.rawQuery(sql, null);

		// result(Cursor ��ü)�� ��� ������ false ����
		if (result.moveToFirst()) {
			int id = result.getInt(0);
			String voca = result.getString(1);
		}
		result.close();
	}

	// ��� Data �б�
	public void selectAll() {
		String sql = "select * from " + tableName + ";";
		Cursor results = sqlDB.rawQuery(sql, null);

		results.moveToFirst();
		while (!results.isAfterLast()) {
			int id = results.getInt(0);
			String voca = results.getString(1);
			results.moveToNext();
		}
		results.close();
	}

	public void seeMyTeam() {
		Cursor cursor;
		cursor = sqlDB.rawQuery("SELECT * FROM gteamTBL;", null);

		String strNames = "�� �̸�" + "\r\n" + "--------" + "\r\n";

		while (cursor.moveToNext()) {
			strNames += cursor.getString(0) + "\r\n";
		}

		edt_team.setText(strNames);

		cursor.close();
	}
}
