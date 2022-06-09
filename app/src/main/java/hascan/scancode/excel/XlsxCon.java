package hascan.scancode.excel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class XlsxCon extends AppCompatActivity {
    String TAG = "DBAdapter";

    public static final String Tablename = "Demarque";
    public static final String Section = "Section";
    public static final String Famille = "Famille";
    public static final String id = "Id";
    public static final String Reference = "Reference";
    public static final String Price = "Prix";
    public static final String Disc = "Disc";
    public static final String PriceDisc = "PrixDisc";

    private SQLiteDatabase db;
    private DBHelper dbHelper;


    public XlsxCon(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {


        if (null == db || !db.isOpen()) {

            try {
                db = dbHelper.getWritableDatabase();
            } catch (SQLiteException sqLiteException) {
            }
        }
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public int insert(String table, ContentValues values) {
        try {

            db = dbHelper.getWritableDatabase();
            int y = (int) db.insert(table, null, values);
            db.close();
            Log.e("Data Inserted", "Data Inserted");
            Log.e("y", y + "");
            return y;
        } catch (Exception ex) {
            Log.e("Error Insert", ex.getMessage().toString());

            return  0;
        }
    }

    public void delete() {
        db.execSQL("delete from " + Tablename);
    }

    public Cursor getAllRow(String table) {
        return db.query(table, null, null, null, null, null, null);
    }

    private class DBHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String DB_NAME = "HaDisc.db";

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String create_sql = "CREATE TABLE IF NOT EXISTS " + Tablename + "("
                    + Section + " TEXT  ," + Famille + " TEXT  ," + id + " TEXT ,"
                    + Reference + " TEXT  ," + Price + " TEXT ,"
                    + Disc + " TEXT  ," + PriceDisc + " TEXT " + ")";
            db.execSQL(create_sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Tablename);
        }

    }

}