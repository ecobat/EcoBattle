package com.ecosha.ecobattle;




import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersonOpenHelper extends SQLiteOpenHelper {
    final static private int DB_VERSION = 1;

    public PersonOpenHelper(Context context) {
        super(context, "moneywars.db", null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table country(" +
                        "   country_id integer not null," +
                        "   name text," +
                        "   primary key(country_id)"+
                        ");"
        );
        db.execSQL(
                "create table kunitami(" +
                        "   kunitami_id integer not null," +
                        "   name text," +
                        "   primary key(kunitami_id)"+
                        ");"
        );
        db.execSQL(
                "create table money("+
                        "   country_id integer not null,"+
                        "   kunitami_id integer not null,"+
                        "   money integer,"+
                        "   primary key(country_id ,kunitami_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(kunitami_id) references kunitami(kunitami_id)"+
                        ");"
        );
        db.execSQL(
                "create table product(" +
                        "   product_id integer not null," +
                        "   name text," +
                        "   primary key(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table factory("+
                        "   product_id integer not null,"+
                        "   level integer not null,"+
                        "   seisansu integer,"+
                        "   jinkenhi integer,"+
                        "   kakaku integer,"+
                        "   souko integer,"+
                        "   primary key(product_id,level),"+
                        "   foreign key(product_id) references product(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table stock("+
                        "   country_id integer not null,"+
                        "   product_id integer not null,"+
                        "   zaiko integer,"+
                        "   primary key(country_id,product_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(product_id) references product(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table rate("+
                        "   country_id integer not null,"+
                        "   product_id integer not null,"+
                        "   rate integer,"+
                        "   primary key(country_id,product_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(product_id) references product(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table kind(" +
                        "   kind_id integer not null," +
                        "   name text," +
                        "   num integer not null," +
                        "   primary key(kind_id)"+
                        ");"
        );
        db.execSQL(
                "create table act_product("+
                        "   turn integer not null,"+
                        "   country_id integer not null,"+
                        "   product_id integer not null,"+
                        "   primary key(turn,country_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(product_id) references product(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table act_sale("+
                        "   turn integer not null,"+
                        "   country_id_my integer not null,"+
                        "   country_id_you integer not null,"+
                        "   product_id integer not null,"+
                        "   sale_num integer,"+
                        "   primary key(turn,country_id_my),"+
                        "   foreign key(country_id_my) references country(country_id),"+
                        "   foreign key(country_id_you) references country(country_id)"+
                        "   foreign key(product_id) references product(product_id)"+
                        ");"
        );
        db.execSQL(
                "create table phase("+
                        "   phase_id integer not null,"+
                        "   name text," +
                        "   primary key(phase_id)"+
                        ");"
        );
        db.execSQL(
                "create table move_money("+
                        "   country_id integer not null,"+
                        "   kunitami_id integer not null,"+
                        "   turn integer not null,"+
                        "   phase_id integer not null,"+
                        "   money_bef integer not null,"+
                        "   money_aft integer not null,"+
                        "   primary key(country_id,kunitami_id,turn,phase_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(kunitami_id) references kunitami(kunitami_id),"+
                        "   foreign key(phase_id) references phase(phase_id)"+
                        ");"
        );
        db.execSQL(
                "create table move_stock("+
                        "   country_id integer not null,"+
                        "   turn integer not null,"+
                        "   phase_id integer not null,"+
                        "   product_id integer not null,"+
                        "   zaiko_bef integer,"+
                        "   zaiko_aft integer,"+
                        "   primary key(country_id,turn,phase_id,product_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(product_id) references product(product_id),"+
                        "   foreign key(phase_id) references phase(phase_id)"+
                        ");"
        );
        db.execSQL(
                "create table move_rate("+
                        "   country_id integer not null,"+
                        "   turn integer not null,"+
                        "   phase_id integer not null,"+
                        "   product_id integer not null,"+
                        "   rate_bef integer,"+
                        "   rate_aft integer,"+
                        "   primary key(country_id,turn,phase_id,product_id),"+
                        "   foreign key(country_id) references country(country_id),"+
                        "   foreign key(product_id) references product(product_id),"+
                        "   foreign key(phase_id) references phase(phase_id)"+
                        ");"
        );

        db.beginTransaction();
        try{
            db.execSQL("insert into country(country_id,name) values (1, 'あなたの国');");
            db.execSQL("insert into country(country_id,name) values (2, 'Ａ国');");
            db.execSQL("insert into country(country_id,name) values (3, 'Ｂ国');");
            db.execSQL("insert into country(country_id,name) values (4, 'Ｃ国');");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into kunitami(kunitami_id,name) values (1, '国');");
            db.execSQL("insert into kunitami(kunitami_id,name) values (2, '民');");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into money(country_id,kunitami_id,money) values (1, 1, 50000);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (1, 2, 50000);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (2, 1, 300000);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (2, 2, 0);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (3, 1, 0);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (3, 2, 300000);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (4, 1, 150000);");
            db.execSQL("insert into money(country_id,kunitami_id,money) values (4, 2, 150000);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into product(product_id,name) values (1, '冷蔵庫');");
            db.execSQL("insert into product(product_id,name) values (2, '洗濯機');");
            db.execSQL("insert into product(product_id,name) values (3, 'テレビ');");
            db.execSQL("insert into product(product_id,name) values (4, '乗用車');");
            db.execSQL("insert into product(product_id,name) values (5, 'ダイヤ');");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into factory(product_id,level,seisansu,jinkenhi,kakaku,souko) values (1,1,20,180,20,200);");
            db.execSQL("insert into factory(product_id,level,seisansu,jinkenhi,kakaku,souko) values (2,1,20,180,20,200);");
            db.execSQL("insert into factory(product_id,level,seisansu,jinkenhi,kakaku,souko) values (3,1,20,180,20,200);");
            db.execSQL("insert into factory(product_id,level,seisansu,jinkenhi,kakaku,souko) values (4,1,10,1800,200,200);");
            db.execSQL("insert into factory(product_id,level,seisansu,jinkenhi,kakaku,souko) values (5,1,5,9000,2000,200);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (1,1,50);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (1,2,40);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (1,3,30);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (1,4,20);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (1,5,10);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (2,1,50);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (2,2,40);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (2,3,30);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (2,4,20);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (2,5,10);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (3,1,50);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (3,2,40);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (3,3,30);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (3,4,20);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (3,5,10);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (4,1,50);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (4,2,40);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (4,3,30);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (4,4,20);");
            db.execSQL("insert into stock(country_id,product_id,zaiko) values (4,5,10);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into rate(country_id,product_id,rate) values (1,1,8);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (1,2,6);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (1,3,4);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (1,4,2);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (1,5,0);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (2,1,8);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (2,2,6);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (2,3,4);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (2,4,2);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (2,5,0);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (3,1,8);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (3,2,6);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (3,3,4);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (3,4,2);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (3,5,0);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (4,1,8);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (4,2,6);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (4,3,4);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (4,4,2);");
            db.execSQL("insert into rate(country_id,product_id,rate) values (4,5,0);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into kind(kind_id,name,num) values (1,'turn',1);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try{
            db.execSQL("insert into phase(phase_id ,name) values (1,'販売');");
            db.execSQL("insert into phase(phase_id ,name) values (2,'生産');");
            db.execSQL("insert into phase(phase_id ,name) values (3,'経年劣化');");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースの変更が生じた場合は、ここに処理を記述する。
    }
}
