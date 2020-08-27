package com.buendiagon.uismap.data_base;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.buendiagon.uismap.clases.Graph;
import com.buendiagon.uismap.clases.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

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

    public void createDataBase() {
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

    public HashMap<Integer, Node> getNodes(Activity activity) {
        SQLiteOpenHelper sqLiteOpenHelper = new UisMapSqliteHelper(activity);
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();

        HashMap<Integer, Node> list = new HashMap<>();

        String query = "SELECT * FROM " + TB_NODES;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                Node node = new Node(cursor.getInt(0), cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3));
                list.put(node.getId(), node);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public Graph getGraph(Activity activity) {
        SQLiteOpenHelper sqLiteOpenHelper = new UisMapSqliteHelper(activity);
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        HashMap<Integer, Node> nodes = this.getNodes(activity);
        Graph graph = new Graph();

        String query = "SELECT * FROM " + TB_EDGES;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                int fromId = cursor.getInt(1);
                int toId = cursor.getInt(2);
                float weight = cursor.getFloat(3);
                Node fromNode = nodes.get(fromId);
                Node toNode = nodes.get(toId);
                if(nodes.containsKey(fromId) && nodes.containsKey(toId)){
                    assert fromNode != null;
                    fromNode.addDestination(toNode, weight);
                    assert toNode != null;
                    toNode.addDestination(fromNode, weight);
                }
            }while (cursor.moveToNext());
        }
        for(Node node : nodes.values()) {
            graph.addNode(node);
        }
        cursor.close();
        return graph;
    }
}
