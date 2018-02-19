package net.ddns.smartfridge.smartfridgev2.vista.actividades.sr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.ddns.smartfridge.smartfridgev2.R;

public class SugerirRecetaModuloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerir_receta);
        com.getbase.floatingactionbutton.FloatingActionButton botonFiltros = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.filtros);
        com.getbase.floatingactionbutton.FloatingActionButton botonAleatorio = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.aleatorio);
        com.getbase.floatingactionbutton.FloatingActionButton botonMiNevera = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.miNevera);

        Intent intent = getIntent();
    }

    public void abrirSeleccionar(View v){
        //Abrirmos el intent
        Intent i = new Intent(this, SeleccionarRecetaActivity.class);
        startActivity(i);
    }
}
