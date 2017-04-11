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

import static android.widget.LinearLayout.OnClickListener;

public class ProductionRActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_r);

        int turn = -1;
        String[][] act = new String[4][2]; //
        int[][][] move_money = new int[4][2][2]; //国別国・民別移動前・後マネー

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

            //行動情報（商品・生産）を取得
            sqlstr = "select c.name as cname,a.name as pname from (act_product b inner join product p on b.product_id = p.product_id) a inner join country c on a.country_id = c.country_id where a.turn = (select num from kind where kind_id = 1) order by a.country_id,a.product_id;";
            c = db.rawQuery(sqlstr, null);
            int cnt = 0;
            if(c.moveToFirst()){
                do{
                    act[cnt][0] = c.getString(c.getColumnIndex("cname"));
                    act[cnt][1] = c.getString(c.getColumnIndex("pname"));
                    cnt++;
                }while(c.moveToNext());
            }

            //マネー移動情報情報を取得
            sqlstr = "select money_bef,money_aft from move_money where turn = " + turn + " and phase_id = 1 order by country_id,kunitami_id;";
            c = db.rawQuery(sqlstr, null);
            cnt = 0;
            if(c.moveToFirst()){
                do{
                    move_money[cnt / 2][cnt % 2][0] = c.getInt(c.getColumnIndex("money_bef"));
                    move_money[cnt / 2][cnt % 2][1] = c.getInt(c.getColumnIndex("money_aft"));
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
            tv00.setText(act[i][0]);
            tr.addView(tv00, lptv01);
        }
        tl.addView(tr);
        tr = new TableRow(this);
        tv00 = new TextView(this);
        tv00.setText("生産商品");
        tr.addView(tv00,lptv01);
        for(int i=0; i<4; i++) {
            tv00 = new TextView(this);
            tv00.setText(act[i][1]);
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
            tv02.setText(act[i][0]);    //国名は商品・生産から流用
            tr02.addView(tv02, lptv01);
        }
        tl02.addView(tr02);
        tr02 = new TableRow(this);
        tv02 = new TextView(this);
        tv02.setText("変動前");
        tr02.addView(tv02,lptv01);
        for(int i=0; i<4; i++) {
            tv02 = new TextView(this);
            tv02.setText(String.valueOf(move_money[i][0][0]));
            tr02.addView(tv02, lptv01);
        }
        tl02.addView(tr02);

        TableRow tr03 = new TableRow(this);
        TextView tv03 = new TextView(this);
        tr03 = new TableRow(this);
        tv03 = new TextView(this);
        tv03.setText("変動後");
        tr03.addView(tv03,lptv01);
        for(int i=0; i<4; i++) {
            tv03 = new TextView(this);
            tv03.setText(String.valueOf(move_money[i][0][1]));
            tr03.addView(tv03, lptv01);
        }
        tl02.addView(tr03);

        //国民マネーの動き部分作成
        TableLayout tl30 = (TableLayout) this.findViewById(R.id.tablelayout03);
        TableRow tr30 = new TableRow(this);
        TextView tv30 = new TextView(this);
        tv30.setText("");
        tr30.addView(tv30,lptv01);
        for(int i=0; i<4; i++) {
            tv30 = new TextView(this);
            tv30.setText(act[i][0]);    //国名は商品・生産から流用
            tr30.addView(tv30, lptv01);
        }
        tl30.addView(tr30);
        tr30 = new TableRow(this);
        tv30 = new TextView(this);
        tv30.setText("変動前");
        tr30.addView(tv30,lptv01);
        for(int i=0; i<4; i++) {
            tv30 = new TextView(this);
            tv30.setText(String.valueOf(move_money[i][1][0]));
            tr30.addView(tv30, lptv01);
        }
        tl30.addView(tr30);
        tr30 = new TableRow(this);
        tv30 = new TextView(this);
        tv30.setText("変動後");
        tr30.addView(tv30,lptv01);
        for(int i=0; i<4; i++) {
            tv30 = new TextView(this);
            tv30.setText(String.valueOf(move_money[i][1][1]));
            tr30.addView(tv30, lptv01);
        }
        tl30.addView(tr30);

        Button bu01 = (Button)findViewById(R.id.button01);
        bu01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "[生産結果について]国マネーが足りなくても生産はできます。国民への支払いはゼロで生産数の分だけ在庫が増えます。\n";
                new android.support.v7.app.AlertDialog.Builder(ProductionRActivity.this)
                        .setTitle("生産結果の解説")
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
                Intent intent = new Intent(ProductionRActivity.this, SaleActivity.class);
                startActivity(intent);
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