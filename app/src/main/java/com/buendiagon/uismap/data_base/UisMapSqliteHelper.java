package com.buendiagon.uismap.data_base;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.buendiagon.uismap.entities.Edge;
import com.buendiagon.uismap.entities.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UisMapSqliteHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "";
    private static final String DB_NAME = "uisMap.db";
    private SQLiteDatabase myDataBase;
    private Context context;
    public static final String TB_NODES = "tb_nodes";
    public static final String TB_EDGES = "tb_edges";


    public UisMapSqliteHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        DB_PATH = this.context.getDatabasePath(DB_NAME).toString();
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.e("message", "" + e);
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Node> getNodes(Activity activity) {
        SQLiteOpenHelper sqLiteOpenHelper = new UisMapSqliteHelper(activity);
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();

        List<Node> list = new ArrayList<>();

        String query = "SELECT * FROM " + TB_NODES;
        Log.e("message", db.getPath());
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                Node node = new Node(cursor.getInt(0), cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3));
                list.add(node);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public List<Edge> getEdges(Activity activity) {
        SQLiteOpenHelper sqLiteOpenHelper = new UisMapSqliteHelper(activity);
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();

        List<Edge> list = new ArrayList<>();

        String query = "SELECT * FROM " + TB_EDGES;
        Log.e("message", db.getPath());
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                Edge edge = new Edge(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getFloat(3));
                list.add(edge);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
