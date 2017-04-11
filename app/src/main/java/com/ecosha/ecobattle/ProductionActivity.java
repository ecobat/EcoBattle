package com.ecosha.ecobattle;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.widget.LinearLayout.OnClickListener;

public class ProductionActivity extends AppCompatActivity{
    String[] pName = new String[5]; //製品名
    int[] kakaku = new int[5]; //価格
    int[] seisansu = new int[5]; //生産数
    int[] jinkenhi = new int[5]; //人件費
    int[] souko = new int[5]; //倉庫（在庫MAX）
    int[][] zaiko = new int[4][5];
    String[] cName = new String[4]; //国名
    int turn = -1;  //現在のターン数
    //int decision = -1; //以前の確定値があるか　あれば4(4ヶ国分の4)　なければ0をセット
    int[] act = new int[4]; //各国の生産商品をセット
    int[][] money = new int[4][2]; //国別国・民別マネー
    int[][] move_money  = new int[8][6]; //国・民別 0:国 1:民 2;turn 3;phase_id 4:befmoney 5:aftmoney
    int[][] move_stock  = new int[4][6]; //国別 0:国 1:turn 2;phase_id 3;product_id 4:zaiko_bef 5:zaiko_aft


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production);



        //画面情報をデータベースより取得
        Cursor c = null;
        try {
            PersonOpenHelper helper = new PersonOpenHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            //商品・生産情報を取得
            String sqlstr = "select name,kakaku,f.product_id,level,seisansu,jinkenhi,souko from factory f inner join product p on f.product_id = p.product_id where level = 1 order by f.product_id,level";
            c = db.rawQuery(sqlstr, null);
            int cnt = 0;
            if(c.moveToFirst()){
                do{
                    pName[cnt] = c.getString(c.getColumnIndex("name"));
                    kakaku[cnt] = c.getInt(c.getColumnIndex("kakaku"));
                    seisansu[cnt] = c.getInt(c.getColumnIndex("seisansu"));
                    jinkenhi[cnt] = c.getInt(c.getColumnIndex("jinkenhi"));
                    souko[cnt] = c.getInt(c.getColumnIndex("souko"));
                    cnt++;
                }while(c.moveToNext());
            }

            //在庫情報を取得
            sqlstr = "select country_id,product_id,zaiko from stock order by country_id,product_id";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    zaiko[cnt / 5][cnt % 5] = c.getInt(c.getColumnIndex("zaiko"));
                    cnt++;
                }while(c.moveToNext());
            }

            //国名を取得
            sqlstr = "select country_id,name from country order by country_id";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    cName[cnt] = c.getString(c.getColumnIndex("name"));
                    cnt++;
                }while(c.moveToNext());
            }

            //現在ターン数を取得
            sqlstr = "select num from kind where kind_id = 1";
            c = db.rawQuery(sqlstr, null);
            c.moveToFirst();
            turn = c.getInt(c.getColumnIndex("num"));

            //登録済みか取得
            //sqlstr = "select count(*) from act_product where turn = " + turn;
            //c = db.rawQuery(sqlstr, null);
            //c.moveToFirst();
            //decision = c.getInt(0);

            //マネー情報を取得
            sqlstr = "select money from money order by country_id,kunitami_id";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    money[cnt / 2][cnt % 2] = c.getInt(c.getColumnIndex("money"));
                    cnt++;
                }while(c.moveToNext());
            }

        }catch(SQLiteException e){
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        //画面レイアウト取得・画面作成
        //商品・生産部分作成
        TableLayout tl = (TableLayout) this.findViewById(R.id.tablelayout01);
        TableRow.LayoutParams lptv01 = new
                TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lptv01.weight = 1;
        for(int i=0; i<5; i++){
            TableRow tr = new TableRow(this);

            TextView tv00 = new TextView(this);
            tv00.setText(pName[i]);
            tr.addView(tv00,lptv01);

            TextView tv01 = new TextView(this);
            tv01.setText(String.valueOf(kakaku[i]));
            tv01.setGravity(Gravity.RIGHT);
            tr.addView(tv01,lptv01);

            TextView tv02 = new TextView(this);
            tv02.setText(String.valueOf(seisansu[i]));
            tv02.setGravity(Gravity.RIGHT);
            tr.addView(tv02,lptv01);

            TextView tv03 = new TextView(this);
            tv03.setText(String.valueOf(jinkenhi[i]));
            tv03.setGravity(Gravity.RIGHT);
            tr.addView(tv03,lptv01);

            TextView tv04 = new TextView(this);
            tv04.setText(String.valueOf(souko[i]));
            tv04.setGravity(Gravity.RIGHT);
            tr.addView(tv04,lptv01);

            tl.addView(tr);
        }

        //在庫部分作成
        //ヘッダ部分
        TableLayout tl20 = (TableLayout) this.findViewById(R.id.tablelayout02);
        TableRow tr20 = new TableRow(this);
        TextView tv20 = new TextView(this);
        tv20.setText("");
        tr20.addView(tv20,lptv01);
        for(int i=0; i<4; i++){
            TextView tv21 = new TextView(this);
            tv21.setText(cName[i]);
            tr20.addView(tv21,lptv01);
        }
        tl20.addView(tr20);

        //表部分
        for(int i=0; i<5; i++){
            TableRow tr21 = new TableRow(this);
            TextView tv22 = new TextView(this);
            tv22.setText(pName[i]);
            tr21.addView(tv22,lptv01);
            for(int j=0; j<4; j++) {
                tv22 = new TextView(this);
                tv22.setText(String.valueOf(zaiko[j][i]));
                tv22.setGravity(Gravity.RIGHT);
                tr21.addView(tv22,lptv01);
            }
            tl20.addView(tr21);
        }

        //商品選択のプルダウン
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner01);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Button bu01 = (Button)findViewById(R.id.button01);
        bu01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "[価格]商品の価格です。\n"
                        + "[生産数]生産数の分、在庫が増えます。\n"
                        + "[人件費]国マネーから国民マネーへ支払われます。\n"
                        + "[倉庫]倉庫を越えた分は破棄されます。\n";
                new android.support.v7.app.AlertDialog.Builder(ProductionActivity.this)
                        .setTitle("生産の解説")
                        .setMessage(s)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)   {

                            }
                        })
                        .show();
            }
        });

        Button bu02 = (Button)findViewById(R.id.button02);
        bu02.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタン1が押された場合
                //if (v.getId() == R.id.button02) {
                // Spinnerオブジェクトを取得
                Spinner spinner = (Spinner) findViewById(R.id.spinner01);

                // 選択されているアイテムのIndexを取得
                int spinnerIdx = spinner.getSelectedItemPosition();

                // 選択されているアイテムを取得
                //String item = (String)spinner.getSelectedItem();

                //各国の行動登録
                act[0] = spinnerIdx;
                for(int i=1; i<4; i++){
                    act[i] = (int) (Math.random() * 5);
                }

                //移動情報（マネー）作成
                for(int i=0; i<4; i++){
                    move_money[i*2][0] = i + 1;
                    move_money[i*2][1] = 1;
                    move_money[i*2][2] = turn;
                    move_money[i*2][3] = 1;
                    move_money[i*2][4] = money[i][0];
                    int pay = money[i][0] - jinkenhi[act[i]];
                    if( pay > 0){ //支払能力があるか
                        pay = jinkenhi[act[i]];
                    }else{
                        pay = money[i][0];
                    }
                    money[i][0] = money[i][0] - pay;
                    move_money[i*2][5] = money[i][0];

                    move_money[i*2+1][0] = i + 1;
                    move_money[i*2+1][1] = 2;
                    move_money[i*2+1][2] = turn;
                    move_money[i*2+1][3] = 1;
                    move_money[i*2+1][4] = money[i][1];
                    money[i][1] = money[i][1] + pay;
                    move_money[i*2+1][5] = money[i][1];
                }

                //移動情報（ストック）作成
                for(int i=0; i<4; i++){
                    move_stock[i][0] = i + 1;
                    move_stock[i][1] = turn;
                    move_stock[i][2] = 1;
                    move_stock[i][3] = act[i] + 1;
                    move_stock[i][4] = zaiko[i][act[i]];
                    int step = seisansu[act[i]];
                    if(zaiko[i][act[i]] + step > souko[act[i]]){ //倉庫容量を超えるか
                        step = souko[act[i]] - zaiko[i][act[i]];
                    }
                    zaiko[i][act[i]] = zaiko[i][act[i]] + step;
                    move_stock[i][5] = zaiko[i][act[i]];
                }

                PersonOpenHelper helper = new PersonOpenHelper(ProductionActivity.this);
                SQLiteDatabase db = helper.getReadableDatabase();
                ContentValues values = new ContentValues();
                String[] s = new String[]{String.valueOf(turn)};
                db.delete("act_product","turn=?",s);
                for(int i=0; i<4; i++){
                    values.clear();
                    values.put("turn", turn);
                    values.put("country_id", i + 1);
                    values.put("product_id", act[i] + 1);
                    if (db.insert("act_product", null, values) != -1)  {
                        //Log.e("SQL insert", String.valueOf(i + 1) + " " + String.valueOf(act[i] + 1));
                    } else {
                        Log.e("SQL ERROR", "生産情報登録に失敗");
                    }
                }

                s = new String[]{String.valueOf(turn),String.valueOf(1)};
                db.delete("move_money","turn=? and phase_id=?",s);
                for(int i=0; i<8; i++) {
                    values.clear();
                    values.put("country_id", move_money[i][0]);
                    values.put("kunitami_id", move_money[i][1]);
                    values.put("turn", move_money[i][2]);
                    values.put("phase_id", move_money[i][3]);
                    values.put("money_bef", move_money[i][4]);
                    values.put("money_aft", move_money[i][5]);
                    if (db.insert("move_money", null, values) != -1)  {
                    } else {
                        Log.e("SQL ERROR", "マネー移動情報登録に失敗");
                    }

                    values.clear();
                    values.put("money", move_money[i][5]);
//Log.e("debug", String.valueOf(move_money[i][0]) + "/" + String.valueOf(move_money[i][1]));
                    s = new String[]{String.valueOf(move_money[i][0]),String.valueOf(move_money[i][1])};
                    if (db.update("money",values,"country_id=? and kunitami_id=?",s) == 1)  {
                    } else {
                        Log.e("SQL ERROR", "マネー情報登録に失敗");
                    }
                }

                s = new String[]{String.valueOf(turn),String.valueOf(1)};
                db.delete("move_stock","turn=? and phase_id=?",s);
                for(int i=0; i<4; i++) {
                    values.clear();
                    values.put("country_id", move_stock[i][0]);
                    values.put("turn", move_stock[i][1]);
                    values.put("phase_id", move_stock[i][2]);
                    values.put("product_id", move_stock[i][3]);
                    values.put("zaiko_bef", move_stock[i][4]);
                    values.put("zaiko_aft", move_stock[i][5]);
                    if (db.insert("move_stock", null, values) != -1)  {
                    } else {
                        Log.e("SQL ERROR", "在庫移動情報登録に失敗");
                    }

                    values.clear();
                    values.put("zaiko", move_stock[i][5]);
                    s = new String[]{String.valueOf(move_stock[i][0]),String.valueOf(move_stock[i][3])};
                    if (db.update("stock",values,"country_id=? and product_id=?",s) == 1)  {
                    } else {
                        Log.e("SQL ERROR", "在庫情報登録に失敗");
                    }
                }

                //AlertDialog dialog = builder.create();
                //dialog.show();

                Intent intent = new Intent(ProductionActivity.this, ProductionRActivity.class);
                startActivity(intent);
                //}
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("アプリケーションの終了")
                    .setMessage("アプリケーションを終了してよろしいですか？")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)   {
                            moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            return true;
        }
        return false;
    }
}