package cl.duoc.android.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static cl.duoc.android.persistencia.ToDoDbHelper.*;

public class MantenedorActivity extends AppCompatActivity {

    private GridView gridViewTareas;
    private CursorAdapter adapter;
    private String[] projection = {TAREA_ID, TAREA_TEXTO, TAREA_PRIORIDAD, TAREA_FECHA_CREACION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenedor);

        configurarUsuario();
        manejarEventoSeekBarPrioridad();
        configurarAdapterGridView();

        gridViewTareas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MantenedorActivity.this, "ID:"+id+"||position:"+position, Toast.LENGTH_LONG ).show();
                ToDoDbHelper toDoDbHelper = new ToDoDbHelper(MantenedorActivity.this);
                SQLiteDatabase writableDb = toDoDbHelper.getWritableDatabase();
                writableDb.delete(TAREA_TABLE, "_id = ?", new String[]{id+""});
                adapter.swapCursor(getCursor(projection));
                return true;
            }
        });
    }

    private void manejarEventoSeekBarPrioridad() {
        SeekBar sbPrioridad = (SeekBar) findViewById(R.id.sbPrioridad);
        sbPrioridad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progreso, boolean fromUser) {
                TextView tvPrioridad = (TextView) MantenedorActivity.this.findViewById(R.id.tvPrioridad);
                tvPrioridad.setText("Prioridad: "+progreso);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void configurarUsuario() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.archivoPreferencias), Context.MODE_PRIVATE);
        String usuario = sp.getString("usuario", "");
        Log.v("Usuario", "usuario: " + usuario);

        TextView tvConectadoComo = (TextView) findViewById(R.id.tvConectacoComo);
        if (!usuario.isEmpty()) {
            tvConectadoComo.setText(String.format("Conectado como %s", usuario));
        } else {
            tvConectadoComo.setText("Bienvenido usuario");
        }
    }

    private void configurarAdapterGridView() {
        int[] to = {
                R.id.tvTareaId
                , R.id.tvTarea
                , R.id.tvPrioridad
                , R.id.tvFechaCreacion
        };

        Cursor cursor = getCursor(projection);

        gridViewTareas = (GridView) findViewById(R.id.gridViewTareas);
        adapter = new SimpleCursorAdapter(this, R.layout.tarea_item, cursor, projection, to, 0);
        gridViewTareas.setAdapter(adapter);
    }

    private Cursor getCursor(String[] projection) {
        ToDoDbHelper toDoDbHelper = new ToDoDbHelper(this);
        SQLiteDatabase readableDb = toDoDbHelper.getReadableDatabase();
        Cursor cursor = readableDb.query(TAREA_TABLE, projection, null, null, null, null, TAREA_PRIORIDAD + " DESC");
        return cursor;
    }

    private String textoFromViewId(int id) {
        EditText et = (EditText) findViewById(id);
        String s = et.getText().toString();
        return s;
    }

    public void guardarTarea(View view) {
		// Recuperación valores de controles
        String tarea = textoFromViewId(R.id.etTarea);
        SeekBar sbPrioridad = (SeekBar) findViewById(R.id.sbPrioridad);
        int prioridad = sbPrioridad.getProgress();
        
        // código SQLite
        ToDoDbHelper toDoDbHelper = new ToDoDbHelper(this);
        SQLiteDatabase db = toDoDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TAREA_TEXTO, tarea);
        cv.put(TAREA_PRIORIDAD, prioridad);
        // nombre de la taba, nullhack, valores
        db.insert(TAREA_TABLE, null, cv);

        // actualiza el cursor del adapter para que se
        // vean reflejados los cambios en GridView
        adapter.swapCursor(getCursor(projection));

        // notifica la creación con un TOAST
        Toast.makeText(this, "Tarea guardada", Toast.LENGTH_LONG).show();
    }

    // Muestra la versión instalada de SQLite en el dispositivo
    public void versionSQLite(View view) {
        Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
        String sqliteVersion = "";
        while(cursor.moveToNext()){
            sqliteVersion += cursor.getString(0);
        }
        Toast.makeText(this, sqliteVersion, Toast.LENGTH_LONG).show();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private File crearDirectorioYArchivo(File file) {
        if (!file.getParentFile().mkdirs()) {
            Log.e("File", "Directory not created");
        }
        return file;
    }

    private File guardarCsvInternal(String filename) {
		// la alternativa a getFilesDir() es getCacheDir()
        File file = new File(getFilesDir(), filename);
        crearDirectorioYArchivo(file);
        return file;
    }

    private File guardarCsvExternal(String filename) {
        File file = new File(
			Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS), filename
			);
        crearDirectorioYArchivo(file);
        return file;
    }

    public void descargarCSV(View v) {
        Toast.makeText(this, "Guardando BD a archivo CSV..."
			, Toast.LENGTH_LONG).show();        
        String filename = "TODOs.csv";

        // Usuar cualquier alternativa - Internal o External
        File file = guardarCsvExternal(filename);
        //File file = guardarCsvInternal(filename);

        String texto = "Hola mundo";
        FileOutputStream fos;
        try {
            // para escribir en archivo interno
            //fos = openFileOutput(filename, Context.MODE_APPEND);

            // new FileOutputStream funciona con external
            fos = new FileOutputStream(file, true);

            fos.write(texto.getBytes());
            fos.close();
            Toast.makeText(this, "Archivo guardado en "+file.getPath()
				, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Log.e("Error", "FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error", "IOException");
            e.printStackTrace();
        }
    }
}
