package cl.duoc.android.persistencia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 
         Checkea si las credenciales habían sido guardadas
         y toma las acciones pertinentes si son correctas 
        */        
        sp = getSharedPreferences( 
				getString(R.string.archivoPreferencias)
				, Context.MODE_PRIVATE 
			);
        // recupera el usuario, si el valor no existe 
        // devuelve el valor por defecto ""
        String usuario  = sp.getString(
							getString(R.string.usuario)
							, ""
						);
        String password = sp.getString( 
							getString(R.string.password)
							, "" 
						);
        if(checkearCredenciales(usuario, password)){
            irActividad(MantenedorActivity.class);
        }
        // Verificar actualización al iniciar
        UpdateChecker.checkForUpdate(this);
    }

    private void irActividad(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private String getTexto(View v) {
        EditText et = (EditText) v;
        return et.getText().toString();
    }

    public static boolean checkearCredenciales(String usuario, String password) {
        // esto debería consultar con un WS
        return usuario.equalsIgnoreCase("marchelo") && password.equals("1234");
    }

    public void ingresar(View v) {
        String usuario = getTexto(findViewById(R.id.etEmail));
        String password = getTexto(findViewById(R.id.etPassword));
        // verifica usuario y contraseña        
        if( checkearCredenciales(usuario, password) ) {
            // Veo si el usuario quiere guardar sus datos de ingreso
            CheckBox cbRecordarPassword = 
				(CheckBox) findViewById(R.id.cbRecordarPassword);			            
            if( cbRecordarPassword.isChecked() ){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("usuario", usuario);
                editor.putString("password", password);
                editor.commit();
            }
            // voy a la otra pantalla
            irActividad(MantenedorActivity.class);
        } else {
            Toast.makeText(this, "Usuario y/o contraseña incorrectos!!!"
				, Toast.LENGTH_LONG).show();
        }
    }
}
