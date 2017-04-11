package com.ecosha.ecobattle;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

import static android.widget.LinearLayout.OnClickListener;

public class SaleActivity extends AppCompatActivity{
    String[] cName = new String[4]; //国名
    int[][] money = new int[4][2]; //国・民別マネー
    int[][] zaiko = new int[4][5];
    String[] pName = new String[5]; //商品名
    int[][] rate = new int[4][5];
    int[][] act = new int[4][2]; //各国の販売国・販売商品をセット
    int[][] move_money  = new int[8][6]; //国・民別 0:国 1:民 2;turn 3;phase_id 4:befmoney 5:aftmoney
    int[][][] move_stock  = new int[4][5][6]; //国別 0:国 1:turn 2;phase_id 3;product_id 4:zaiko_bef 5:zaiko_aft
    int[][][] move_rate  = new int[4][5][6]; //国別 0:国 1:turn 2;phase_id 3;product_id 4:rate_bef 5:rate_aft
    int turn = -1;  //現在のターン数
    int[] kakaku = new int[5];     //商品ごとの価格
    int[][] saleNumSum = new int[4][2];    //国毎の販売商品の販売数と販売総額
    int[][] moneyAft = new int[4][2]; //販売後の各国・国民毎のマネー

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);



        //画面情報をデータベースより取得
        Cursor c = null;
        try {
            PersonOpenHelper helper = new PersonOpenHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            //現在ターン数を取得
            String sqlstr = "select num from kind where kind_id = 1";
            c = db.rawQuery(sqlstr, null);
            c.moveToFirst();
            turn = c.getInt(c.getColumnIndex("num"));

            //国・国民別マネー情報を取得
            sqlstr = "select money,name from money m inner join country c on m.country_id = c.country_id order by m.country_id,kunitami_id ";
            c = db.rawQuery(sqlstr, null);
            int cnt = 0;
            if(c.moveToFirst()){
                do{
                    cName[cnt / 2] = c.getString(c.getColumnIndex("name"));
                    money[cnt / 2][cnt % 2] = c.getInt(c.getColumnIndex("money"));
                    cnt++;
                }while(c.moveToNext());
            }

            //在庫情報を取得
            sqlstr = "select country_id,s.product_id,zaiko,name as pname from stock s inner join product p on s.product_id = p.product_id order by country_id,s.product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    zaiko[cnt / 5][cnt % 5] = c.getInt(c.getColumnIndex("zaiko"));
                    if(cnt < 5) {
                        pName[cnt] = c.getString(c.getColumnIndex("pname"));
                    }
                    cnt++;
                }while(c.moveToNext());
            }

            //商品・生産情報を取得
            sqlstr = "select kakaku,product_id,level,seisansu,jinkenhi,souko from factory where level = 1 order by product_id,level";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    kakaku[cnt] = c.getInt(c.getColumnIndex("kakaku"));
                    cnt++;
                }while(c.moveToNext());
            }

            //普及率情報を取得
            sqlstr = "select country_id,r.product_id,rate,name as pname from rate r inner join product p on r.product_id = p.product_id order by country_id,r.product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    rate[cnt / 5][cnt % 5] = c.getInt(c.getColumnIndex("rate"));
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
        TableRow tr00 = new TableRow(this);
        TextView tv00 = new TextView(this);
        tv00.setText("");
        tr00.addView(tv00,lptv01);
        for(int i=0; i<4; i++){
            tv00 = new TextView(this);
            tv00.setText(cName[i]);
            tr00.addView(tv00,lptv01);
        }
        tl.addView(tr00);
        tr00 = new TableRow(this);
        tv00 = new TextView(this);
        tv00.setText("国民マネー");
        tr00.addView(tv00,lptv01);
        for(int i=0; i<4; i++){
            tv00 = new TextView(this);
            tv00.setGravity(Gravity.RIGHT);
            tv00.setText(String.valueOf(money[i][1]));
            tr00.addView(tv00,lptv01);
        }
        tl.addView(tr00);

        TableLayout tl20 = (TableLayout) this.findViewById(R.id.tablelayout02);
        TableRow tr20 = new TableRow(this);
        TextView tv20 = new TextView(this);
        tv20.setText("");
        tr20.addView(tv20,lptv01);
        for(int i=0; i<4; i++){
            tv20 = new TextView(this);
            tv20.setText(cName[i]);
            tr20.addView(tv20,lptv01);
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
                tv22.setGravity(Gravity.RIGHT);
                tv22.setText(String.valueOf(zaiko[j][i]));
                tr21.addView(tv22,lptv01);
            }
            tl20.addView(tr21);
        }

        TableLayout tl30 = (TableLayout) this.findViewById(R.id.tablelayout03);
        TableRow tr30 = new TableRow(this);
        TextView tv30 = new TextView(this);
        tv30.setText("");
        tr30.addView(tv30,lptv01);
        for(int i=0; i<4; i++){
            tv30 = new TextView(this);
            tv30.setText(cName[i]);
            tr30.addView(tv30,lptv01);
        }
        tl30.addView(tr30);

        //表部分
        for(int i=0; i<5; i++){
            TableRow tr21 = new TableRow(this);
            TextView tv22 = new TextView(this);
            tv22.setText(pName[i]);
            tr21.addView(tv22,lptv01);
            for(int j=0; j<4; j++) {
                tv22 = new TextView(this);
                tv22.setGravity(Gravity.RIGHT);
                tv22.setText(String.valueOf(rate[j][i]));
                tr21.addView(tv22,lptv01);
            }
            tl30.addView(tr21);
        }

        //国選択のプルダウン
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner01);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //商品選択のプルダウン
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pName);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner02);
        // アダプターを設定します
        spinner2.setAdapter(adapter2);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                String s = "[販売について]普及率が低いほど、販売数は多くなります。\n"
                        + "乗用車の普及率は冷蔵庫、洗濯機、テレビより大きくなりません。\n"
                        + "ダイヤの普及率は乗用車より大きくなりません。\n";
                new android.support.v7.app.AlertDialog.Builder(SaleActivity.this)
                        .setTitle("販売の解説")
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
                // Spinnerオブジェクトを取得
                Spinner spinner = (Spinner) findViewById(R.id.spinner01);
                Spinner spinner2 = (Spinner) findViewById(R.id.spinner02);

                // 選択されているアイテムのIndexを取得
                int spinnerIdx = spinner.getSelectedItemPosition();
                int spinnerIdx2 = spinner2.getSelectedItemPosition();

                // 選択されているアイテムを取得
                //String item = (String)spinner.getSelectedItem();

                //各国の行動登録
                act[0][0] = spinnerIdx;
                act[0][1] = spinnerIdx2;
                for(int i=1; i<4; i++){
                    act[i][0] = (int) (Math.random() * 4);
                    act[i][1] = (int) (Math.random() * 5);
                }

                //販売数決定
                saleNumSum = reSaleNum(act, rate, zaiko, money, kakaku);

                //マネー変動後作成
                moneyAft = money_aft(act, money, saleNumSum);

                //移動情報（マネー）作成
                for(int i=0; i<4; i++){
                    move_money[i*2][0] = i + 1;
                    move_money[i*2][1] = 1;
                    move_money[i*2][2] = turn;
                    move_money[i*2][3] = 2;
                    move_money[i*2][4] = money[i][0];
                    move_money[i*2][5] = moneyAft[i][0];

                    move_money[i*2+1][0] = i + 1;
                    move_money[i*2+1][1] = 2;
                    move_money[i*2+1][2] = turn;
                    move_money[i*2+1][3] = 2;
                    move_money[i*2+1][4] = money[i][1];
                    move_money[i*2+1][5] = moneyAft[i][1];
                }

                //移動情報（ストック）作成
                for(int i=0; i<4; i++){
                    for(int j=0; j<5; j++){
                        move_stock[i][j][0] = i + 1;
                        move_stock[i][j][1] = turn;
                        move_stock[i][j][2] = 2;
                        move_stock[i][j][3] = j + 1;
                        move_stock[i][j][4] = zaiko[i][j];
                        move_stock[i][j][5] = zaiko[i][j];
                    }
                }
                for(int i=0; i<4; i++){
                    move_stock[i][act[i][1]][5] -= saleNumSum[i][0];
                }

                //移動情報（rate）作成
                for(int i=0; i<4; i++){
                    for(int j=0; j<5; j++){
                        move_rate[i][j][0] = i + 1;
                        move_rate[i][j][1] = turn;
                        move_rate[i][j][2] = 2;
                        move_rate[i][j][3] = j + 1;
                        move_rate[i][j][4] = rate[i][j];
                        move_rate[i][j][5] = rate[i][j];
                    }
                }
                for(int i=0; i<4; i++){
                    move_rate[act[i][0]][act[i][1]][5] += saleNumSum[i][0];
                }

                PersonOpenHelper helper = new PersonOpenHelper(SaleActivity.this);
                SQLiteDatabase db = helper.getReadableDatabase();
                ContentValues values = new ContentValues();
                String[] s = new String[]{String.valueOf(turn)};
                db.delete("act_sale","turn=?",s);
                for(int i=0; i<4; i++){
                    values.clear();
                    values.put("turn", turn);
                    values.put("country_id_my", i + 1);
                    values.put("country_id_you", act[i][0] + 1);
                    values.put("product_id", act[i][1] + 1);
                    values.put("sale_num", saleNumSum[i][0]);
                    if (db.insert("act_sale", null, values) != -1)  {
                    } else {
                        Log.e("SQL ERROR", "生産情報登録に失敗");
                    }
                }

                s = new String[]{String.valueOf(turn),String.valueOf(2)};
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

                s = new String[]{String.valueOf(turn),String.valueOf(2)};
                db.delete("move_stock","turn=? and phase_id=?",s);
                for(int i=0; i<4; i++) {
                    for (int j = 0; j < 5; j++) {
                        values.clear();
                        values.put("country_id", move_stock[i][j][0]);
                        values.put("turn", move_stock[i][j][1]);
                        values.put("phase_id", move_stock[i][j][2]);
                        values.put("product_id", move_stock[i][j][3]);
                        values.put("zaiko_bef", move_stock[i][j][4]);
                        values.put("zaiko_aft", move_stock[i][j][5]);
                        if (db.insert("move_stock", null, values) != -1) {
                        } else {
                            Log.e("SQL ERROR", "在庫移動情報登録に失敗");
                        }
                    }
                }
                for(int i=0; i<4; i++) {
                    values.clear();
                    values.put("zaiko", move_stock[i][act[i][1]][5]);
                    s = new String[]{String.valueOf(i + 1),String.valueOf(act[i][1] + 1)};
                    if (db.update("stock",values,"country_id=? and product_id=?",s) == 1)  {
                    } else {
                        Log.e("SQL ERROR", "在庫情報登録に失敗");
                    }
                }

                s = new String[]{String.valueOf(turn),String.valueOf(2)};
                db.delete("move_rate","turn=? and phase_id=?",s);
                for(int i=0; i<4; i++) {
                    for (int j = 0; j < 5; j++) {
                        values.clear();
                        values.put("country_id", move_rate[i][j][0]);
                        values.put("turn", move_rate[i][j][1]);
                        values.put("phase_id", move_rate[i][j][2]);
                        values.put("product_id", move_rate[i][j][3]);
                        values.put("rate_bef", move_rate[i][j][4]);
                        values.put("rate_aft", move_rate[i][j][5]);
                        if (db.insert("move_rate", null, values) != -1) {
                        } else {
                            Log.e("SQL ERROR", "普及率移動情報登録に失敗");
                        }
                    }
                }
                for(int i=0; i<4; i++) {
                    values.clear();
                    values.put("rate", move_rate[act[i][0]][act[i][1]][5]);
                    s = new String[]{String.valueOf(act[i][0] + 1),String.valueOf(act[i][1] + 1)};
                    if (db.update("rate",values,"country_id=? and product_id=?",s) == 1)  {
                    } else {
                        Log.e("SQL ERROR", "普及率情報登録に失敗");
                    }
                }

                Intent intent = new Intent(SaleActivity.this, SaleRActivity.class);
                startActivity(intent);
            }
        });
    }

    private int[][] reSaleNum(int[][] act, int[][] rate, int[][] zaiko, int[][] money, int[] kakaku){
        int[][] re = new int[4][2];
//        int[][] hikiate = Arrays.copyOf(money, money.length);
        int[][] money_hikiate = new int[money.length][];
        int[][] rate_hikiate = new int[rate.length][];
        for(int i=0; i<money.length; i++){
            money_hikiate[i] = Arrays.copyOf(money[i], money[i].length);
        }
        for(int i=0; i<rate.length; i++){
            rate_hikiate[i] = Arrays.copyOf(rate[i], rate[i].length);
        }

        for(int i=0; i<4; i++){
            int limitA = 100; //乗用車用の値
            int limitB = 100; //ダイヤ用の値
            int limitTmp = 100;
            for(int j=0; j<3; j++){
                if(rate_hikiate[act[i][0]][j] < limitTmp){
                    limitTmp = rate_hikiate[act[i][0]][j];
                }
            }
            limitA = limitTmp;
            limitB = limitTmp;
            if(rate_hikiate[act[i][0]][3] < limitTmp ){ //
                limitB = rate_hikiate[act[i][0]][3];
            }

            int max = 100;
            if(act[i][1]==3){
                max = limitA;
            }else if(act[i][1]==4){
                max = limitB;
            }

            int buyNum = (max - rate_hikiate[act[i][0]][act[i][1]]) / 2;
            if(buyNum > zaiko[i][act[i][1]]){
                buyNum = zaiko[i][act[i][1]];
            }
            int buySum = buyNum * kakaku[act[i][1]];
            String s = "i" + String.valueOf(i) + " buyNum " + String.valueOf(buyNum) + " buySum " + String.valueOf(buySum);
            Log.e("judge",s);
            if(buySum > money_hikiate[act[i][0]][1]){  //対象国民に支払能力がなかったら
                buyNum = money_hikiate[act[i][0]][1] / kakaku[act[i][1]];
                buySum = buyNum * kakaku[act[i][1]];
            }
            s = "money_hikiate[act[i][0]][1] " + String.valueOf(money_hikiate[act[i][0]][1]) + " buyNum " + String.valueOf(buyNum) + " buySum " + String.valueOf(buySum);
            Log.e("judge",s);
            s = "buyNum " + String.valueOf(buyNum);
            Log.e("judge",s);
            money_hikiate[act[i][0]][1] -= buySum;
            rate_hikiate[act[i][0]][act[i][1]] += buyNum;
            re[i][0] = buyNum;
            re[i][1] = buySum;
        }
        return re;
    }

    private int[][] money_aft(int[][] act, int[][] money, int[][] saleNumSum){
        //int[][] moneyRe = Arrays.copyOf(money, money.length);
        int[][] moneyRe = new int[money.length][];
        for(int i=0; i<money.length; i++){
            moneyRe[i] = Arrays.copyOf(money[i], money[i].length);
        }

        for(int i=0; i<4; i++){
            moneyRe[i][0] += saleNumSum[i][1];
            moneyRe[act[i][0]][1] -= saleNumSum[i][1];
        }
        return moneyRe;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle("アプリケーションの終了")
                    .setMessage("アプリケーションを終了してよろしいですか？")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)   {
                            // TODO 自動生成されたメソッド・スタブ
                            //RateRActivity.this.finish();
                            moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自動生成されたメソッド・スタブ

                        }
                    })
                    .show();

            return true;
        }
        return false;
    }
}