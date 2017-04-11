package com.ecosha.ecobattle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタンの取得
        Button button1 = (Button)findViewById(R.id.button01);
        Button button2 = (Button)findViewById(R.id.button02);

        // リスナーの登録
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // ボタン1が押された場合
        if (v.getId() == R.id.button01) {
            deleteDatabase("moneywars.db");

            Intent intent = new Intent(this, TopActivity.class);
            intent.putExtra("data", 1);
            startActivity(intent);
            // ボタン2が押された場合
        } else if (v.getId() == R.id.button02) {
            Intent intent = new Intent(this, TopActivity.class);
            intent.putExtra("data", 2);
            startActivity(intent);
        }
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
