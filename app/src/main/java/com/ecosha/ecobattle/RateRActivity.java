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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.widget.LinearLayout.OnClickListener;

public class RateRActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_r);

        int turn = -1;
        String[] cname = new String[4]; //国名
        String[] pname = new String[5]; //商品名
        int[][] rate = new int[4][5];
        int[][][] move_rate = new int[4][5][2]; //国別商品別移動前・後普及率

        //画面情報をデータベースより取得
        Cursor c = null;
        try {
            PersonOpenHelper helper = new PersonOpenHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            //ターン数を取得
            String sqlstr = "select num from kind where kind_id = 1;";
            c = db.rawQuery(sqlstr, null);
            if(c.moveToFirst()){
                do{
                    turn = c.getInt(c.getColumnIndex("num"));
                }while(c.moveToNext());
            }

            //国名を取得
            sqlstr = "select name from country order by country_id;";
            c = db.rawQuery(sqlstr, null);
            int cnt = 0;
            if(c.moveToFirst()){
                do{
                    cname[cnt] = c.getString(c.getColumnIndex("name"));
                    cnt++;
                }while(c.moveToNext());
            }

            //商品名を取得
            sqlstr = "select name from product order by product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    pname[cnt] = c.getString(c.getColumnIndex("name"));
                    cnt++;
                }while(c.moveToNext());
            }

            //普及率を取得
            sqlstr = "select rate from rate order by country_id,product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    rate[cnt / 5][cnt % 5] = c.getInt(c.getColumnIndex("rate"));
                    move_rate[cnt / 5][cnt % 5][0] = c.getInt(c.getColumnIndex("rate"));
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

        //普及率変動処理
        for(int i=0; i<4; i++){
            for(int j=0; j<5; j++){
                int value = move_rate[i][j][0] - (int)(Math.random() * 6);
                if(value < 0){
                    value = 0;
                }
                move_rate[i][j][1] = value;
            }
        }

        //普及率の動き部分作成
        //画面レイアウト取得・画面作成
        //商品・生産部分作成
        TableLayout tl = (TableLayout) this.findViewById(R.id.tablelayout01);
        TableRow.LayoutParams lptv01 = new
                TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lptv01.weight = 1;
        TableLayout tl01 = (TableLayout) this.findViewById(R.id.tablelayout01);
        TableRow tr01 = new TableRow(this);
        TextView tv01 = new TextView(this);
        tv01.setText("");
        tr01.addView(tv01,lptv01);
        for(int i=0; i<4; i++) {
            tv01 = new TextView(this);
            tv01.setText(cname[i]);
            tr01.addView(tv01, lptv01);
        }
        tl01.addView(tr01);
        for(int i=0; i<5; i++){
            tr01 = new TableRow(this);
            tv01 = new TextView(this);
            tv01.setText(pname[i]);
            tr01.addView(tv01,lptv01);
            for(int j=0; j<4; j++){
                tv01 = new TextView(this);
                String s = String.valueOf(move_rate[j][i][0]) + "⇒" + String.valueOf(move_rate[j][i][1]);
                tv01.setText(String.valueOf(s));
                tr01.addView(tv01, lptv01);
            }
            tl01.addView(tr01);
        }

        Button bu01 = (Button)findViewById(R.id.button01);
        bu01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "[普及率減少について]ターンが経過により普及率がランダムで下がります。。\n";
                new android.support.v7.app.AlertDialog.Builder(RateRActivity.this)
                        .setTitle("普及率減少の解説")
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

                PersonOpenHelper helper = new PersonOpenHelper(RateRActivity.this);
                SQLiteDatabase db = helper.getReadableDatabase();

                int turn = -1;
                //ターン数を取得
                String sqlstr = "select num from kind where kind_id = 1;";
                Cursor c = db.rawQuery(sqlstr, null);
                if(c.moveToFirst()){
                    do{
                        turn = c.getInt(c.getColumnIndex("num"));
                    }while(c.moveToNext());
                }

                ContentValues values = new ContentValues();
                values.clear();
                values.put("num", turn + 1);
                String[] s = new String[]{String.valueOf(1)};
                if (db.update("kind",values,"kind_id=?",s) == 1)  {
                } else {
                    Log.e("SQL ERROR", "ターン情報更新に失敗");
                }
                Intent intent = new Intent(RateRActivity.this, TopActivity.class);
                startActivity(intent);
            }
        });
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