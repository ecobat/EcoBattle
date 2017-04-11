package com.ecosha.ecobattle;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.widget.LinearLayout.*;

public class TopActivity extends AppCompatActivity{
    int turn;
    final int turnMax = 50;
    int myRank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        String[] rank = {"１位","２位","３位","４位"};

        //画面情報をデータベースより取得
        String[] name = new String[4];
        String[] smoney = new String[4]; //国別総マネー
        String[] kmoney = new String[4]; //国別国マネー
        String[] tmoney = new String[4]; //国別民マネー
        int sumMoney = 0;
        myRank = -1;
        turn = -1;

        Cursor c = null;
        try {
            PersonOpenHelper helper = new PersonOpenHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            String sqlstr = "select co.country_id as country_id,name,smoney,kmoney,tmoney from country co inner join (select k.country_id,k.money + t.money as smoney,k.money as kmoney,t.money as tmoney from money k inner join (select country_id,money from money where kunitami_id = 2) t on k.country_id = t.country_id where kunitami_id = 1) mo on co.country_id = mo.country_id order by smoney desc,co.country_id;";
            c = db.rawQuery(sqlstr, null);

            int cnt = 0;
            if(c.moveToFirst()){
                do{
                    int rankTmp = c.getInt(c.getColumnIndex("country_id"));
                    if( rankTmp == 1){
                        myRank = cnt + 1;
                    }
                    name[cnt] = c.getString(c.getColumnIndex("name"));
                    int smoney_int = c.getInt(c.getColumnIndex("smoney"));
                    smoney[cnt] = String.valueOf(smoney_int);
                    kmoney[cnt] = String.valueOf(c.getInt(c.getColumnIndex("kmoney")));
                    tmoney[cnt] = String.valueOf(c.getInt(c.getColumnIndex("tmoney")));
                    cnt++;
                    sumMoney += smoney_int;
                }while(c.moveToNext());
            }

            //現在ターン数を取得
            sqlstr = "select num from kind where kind_id = 1";
            c = db.rawQuery(sqlstr, null);
            c.moveToFirst();
            turn = c.getInt(c.getColumnIndex("num"));

        }catch(SQLiteException e){
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        //画面レイアウト取得・画面作成
        TableLayout tl = (TableLayout) this.findViewById(R.id.tablelayout01);
        TableRow.LayoutParams lptv01 = new
                TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lptv01.weight = 1;
        for(int i=0; i<4; i++){
            TableRow tr = new TableRow(this);

            TextView tv00 = new TextView(this);
            tv00.setText(rank[i]);
            tr.addView(tv00,lptv01);

            TextView tv01 = new TextView(this);
            tv01.setText(name[i]);
            tv01.setGravity(Gravity.CENTER);
            tr.addView(tv01,lptv01);

            TextView tv02 = new TextView(this);
            tv02.setText(smoney[i]);
            tv02.setGravity(Gravity.RIGHT);
            tr.addView(tv02,lptv01);

            TextView tv03 = new TextView(this);
            tv03.setText(kmoney[i]);
            tv03.setGravity(Gravity.RIGHT);
            tr.addView(tv03,lptv01);

            TextView tv04 = new TextView(this);
            tv04.setText(tmoney[i]);
            tv04.setGravity(Gravity.RIGHT);
            tr.addView(tv04,lptv01);

            tl.addView(tr);
        }
        TableRow tr05 = new TableRow(this);
        TextView tv05 = new TextView(this);
        tv05.setText("計");
        tr05.addView(tv05,lptv01);
        tv05 = new TextView(this);
        tv05.setText("");
        tr05.addView(tv05,lptv01);
        tv05 = new TextView(this);
        tv05.setGravity(Gravity.RIGHT);
        tv05.setText(String.valueOf(sumMoney));
        tr05.addView(tv05,lptv01);
        tv05 = new TextView(this);
        tv05.setText("");
        tr05.addView(tv05,lptv01);
        tv05 = new TextView(this);
        tv05.setText("");
        tr05.addView(tv05,lptv01);
        tl.addView(tr05);

        TextView tv01 = (TextView) this.findViewById(R.id.textview01);
        String s01 = "あなたの国は" + String.valueOf(myRank) + "位です。";
        tv01.setText(s01);

        TextView tv02 = (TextView) this.findViewById(R.id.textview02);
        String s02 = "現在のターン" + String.valueOf(turn) + " / " + String.valueOf(turnMax);
        tv02.setText(s02);

        Button bu01 = (Button)findViewById(R.id.button01);
        bu01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "[ターン]" + String.valueOf(turnMax) + "ターンまで遊べます。\n"
                        + "[総マネー]国マネーと国民マネーの合計です。\n"
                        + "[国マネー]商品を生産する国のマネーです。\n"
                        + "[国民マネー]商品を購入する国民のマネーです。\n";
                new android.support.v7.app.AlertDialog.Builder(TopActivity.this)
                        .setTitle("トップ画面のの解説")
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
                if(turn <= turnMax) {
                    Intent intent = new Intent(TopActivity.this, ProductionActivity.class);
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(TopActivity.this)
                            .setTitle("ターン終了")
                            .setMessage("最大ターンを過ぎたので終了です。次回は１位を目指しましょう。")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

    }
}