package cl.duoc.android.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";

    public static final String TAREA_TABLE = "tareas";
    public static final String TAREA_ID = "_id";
    public static final String TAREA_TEXTO = "tarea";
    public static final String TAREA_PRIORIDAD = "prioridad";
    public static final String TAREA_FECHA_CREACION = "fecha_creacion";

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE tareas(\n" +
                "\t _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT\n" +
                "\t, tarea TEXT NOT NULL\n" +
                "\t, prioridad INT DEFAULT 1\n" +
                "\t, fecha_creacion DEFAULT CURRENT_TIMESTAMP\n" +
                ");";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion
							, int newVersion) {
        // código necesario para modificar la estructura en caso
        // que se hayan realizado modificaciones en el esquema
    }
}
