package com.ecosha.ecobattle;

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
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.widget.LinearLayout.OnClickListener;

public class SaleRActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_r);

        int turn = -1;
        String[] cname = new String[4]; //国名
        String[] pname = new String[5]; //商品名
        String[][] act = new String[4][3]; //
        int[][][] move_money = new int[4][2][2]; //国別国・民別移動前・後マネー
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

            //行動情報（販売）を取得
            sqlstr = "select c.name as cname,a.name as pname, sale_num from (act_sale b inner join product p on b.product_id = p.product_id) a inner join country c on a.country_id_you = c.country_id where a.turn = (select num from kind where kind_id = 1) order by a.country_id_my,a.product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    act[cnt][0] = c.getString(c.getColumnIndex("cname"));
                    act[cnt][1] = c.getString(c.getColumnIndex("pname"));
                    act[cnt][2] = c.getString(c.getColumnIndex("sale_num"));
                    cnt++;
                }while(c.moveToNext());
            }

            //マネー移動情報情報を取得
            sqlstr = "select money_bef,money_aft from move_money where turn = " + turn + " and phase_id = 2 order by country_id,kunitami_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    move_money[cnt / 2][cnt % 2][0] = c.getInt(c.getColumnIndex("money_bef"));
                    move_money[cnt / 2][cnt % 2][1] = c.getInt(c.getColumnIndex("money_aft"));
                    cnt++;
                }while(c.moveToNext());
            }

            //普及率を取得
            sqlstr = "select rate_bef,rate_aft from move_rate where turn = " + turn + " and phase_id = 2 order by country_id,product_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    move_rate[cnt / 5][cnt % 5][0] = c.getInt(c.getColumnIndex("rate_bef"));
                    move_rate[cnt / 5][cnt % 5][1] = c.getInt(c.getColumnIndex("rate_aft"));
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
        TableRow tr = new TableRow(this);
        TextView tv00 = new TextView(this);
        tv00.setText("");
        tr.addView(tv00,lptv01);
        for(int i=0; i<4; i++) {
            tv00 = new TextView(this);
            tv00.setText(cname[i]);
            tr.addView(tv00, lptv01);
        }
        tl.addView(tr);
        tr = new TableRow(this);
        tv00 = new TextView(this);
        tv00.setText("販売相手国");
        tr.addView(tv00,lptv01);
        for(int i=0; i<4; i++) {
            tv00 = new TextView(this);
            tv00.setText(act[i][0]);
            tr.addView(tv00, lptv01);
        }
        tl.addView(tr);
        tr = new TableRow(this);
        tv00 = new TextView(this);
        tv00.setText("販売商品");
        tr.addView(tv00,lptv01);
        for(int i=0; i<4; i++) {
            tv00 = new TextView(this);
            tv00.setText(act[i][1]);
            tr.addView(tv00, lptv01);
        }
        tl.addView(tr);
        tr = new TableRow(this);
        tv00 = new TextView(this);
        tv00.setText("販売数");
        tr.addView(tv00,lptv01);
        for(int i=0; i<4; i++) {
            tv00 = new TextView(this);
            tv00.setGravity(Gravity.RIGHT);
            tv00.setText(act[i][2]);
            tr.addView(tv00, lptv01);
        }
        tl.addView(tr);

        //国マネーの動き部分作成
        TableLayout tl02 = (TableLayout) this.findViewById(R.id.tablelayout02);
        TableRow tr02 = new TableRow(this);
        TextView tv02 = new TextView(this);
        tv02.setText("");
        tr02.addView(tv02,lptv01);
        for(int i=0; i<4; i++) {
            tv02 = new TextView(this);
            tv02.setText(cname[i]);
            tr02.addView(tv02, lptv01);
        }
        tl02.addView(tr02);
        tr02 = new TableRow(this);
        tv02 = new TextView(this);
        tv02.setText("変動前");
        tr02.addView(tv02,lptv01);
        for(int i=0; i<4; i++) {
            tv02 = new TextView(this);
            tv02.setGravity(Gravity.RIGHT);
            tv02.setText(String.valueOf(move_money[i][0][0]));
            tr02.addView(tv02, lptv01);
        }
        tl02.addView(tr02);
        tr02 = new TableRow(this);
        tv02 = new TextView(this);
        tv02.setText("変動後");
        tr02.addView(tv02,lptv01);
        for(int i=0; i<4; i++) {
            tv02 = new TextView(this);
            tv02.setGravity(Gravity.RIGHT);
            tv02.setText(String.valueOf(move_money[i][0][1]));
            tr02.addView(tv02, lptv01);
        }
        tl02.addView(tr02);

        //国マネーの動き部分作成
        TableLayout tl03 = (TableLayout) this.findViewById(R.id.tablelayout03);
        TableRow tr03 = new TableRow(this);
        TextView tv03 = new TextView(this);
        tv03.setText("");
        tr03.addView(tv03,lptv01);
        for(int i=0; i<4; i++) {
            tv03 = new TextView(this);
            tv03.setText(cname[i]);
            tr03.addView(tv03, lptv01);
        }
        tl03.addView(tr03);
        tr03 = new TableRow(this);
        tv03 = new TextView(this);
        tv03.setText("変動前");
        tr03.addView(tv03,lptv01);
        for(int i=0; i<4; i++) {
            tv03 = new TextView(this);
            tv03.setGravity(Gravity.RIGHT);
            tv03.setText(String.valueOf(move_money[i][1][0]));
            tr03.addView(tv03, lptv01);
        }
        tl03.addView(tr03);
        tr03 = new TableRow(this);
        tv03 = new TextView(this);
        tv03.setText("変動後");
        tr03.addView(tv03,lptv01);
        for(int i=0; i<4; i++) {
            tv03 = new TextView(this);
            tv03.setGravity(Gravity.RIGHT);
            tv03.setText(String.valueOf(move_money[i][1][1]));
            tr03.addView(tv03, lptv01);
        }
        tl03.addView(tr03);

        //普及率の動き部分作成
        TableLayout tl04 = (TableLayout) this.findViewById(R.id.tablelayout04);
        TableRow tr04 = new TableRow(this);
        TextView tv04 = new TextView(this);
        tv04.setText("");
        tr04.addView(tv04,lptv01);
        for(int i=0; i<4; i++) {
            tv04 = new TextView(this);
            tv04.setText(cname[i]);
            tr04.addView(tv04, lptv01);
        }
        tl04.addView(tr04);
        for(int i=0; i<5; i++){
            tr04 = new TableRow(this);
            tv04 = new TextView(this);
            tv04.setText(pname[i]);
            tr04.addView(tv04,lptv01);
            for(int j=0; j<4; j++){
                tv04 = new TextView(this);
                String s = String.valueOf(move_rate[j][i][0]) + "⇒" + String.valueOf(move_rate[j][i][1]);
                tv04.setText(String.valueOf(s));
                tr04.addView(tv04, lptv01);
            }
            tl04.addView(tr04);
        }

        Button bu01 = (Button)findViewById(R.id.button01);
        bu01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "[販売結果について]販売相手国の国民マネーが足りない場合は、その額に応じて販売数が減少します。\n"
                        + "[販売処理について]あなたの国、Ａ国、Ｂ国、Ｃ国の順で販売処理は行われます。\n";
                new android.support.v7.app.AlertDialog.Builder(SaleRActivity.this)
                        .setTitle("販売結果の解説")
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
                Intent intent = new Intent(SaleRActivity.this, RateRActivity.class);
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