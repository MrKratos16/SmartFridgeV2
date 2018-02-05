package net.ddns.smartfridge.smartfridgev2.vista.actividades.clc;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Dialogos;

public class NuevaLista extends AppCompatActivity {
    private Intent intent;//Para trabajar con los intents para lanzar nuevos activitys
    private String alimentoNuevo;//Para recoger el dato introducido por el usuario en el dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_lista);

        //Cogemos la referencia a los floating action buttons
        com.getbase.floatingactionbutton.FloatingActionButton botonManual = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.manual);
        com.getbase.floatingactionbutton.FloatingActionButton botonAlimentos = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.anadirAlimentos);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        //Le asignamos el listener a cada botón
        botonManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cuando pulsemos el botón, nos da la opción de añadir alimentos de manera manual
                    //Mensaje del Alert
                    builder.setMessage("Introduzca el alimento que quiere añadir a la lista:");
                    //Título
                    builder.setTitle("Añadir manualmente");
                    //Añadimos el layout que hemos creado
                    builder.setView(inflater.inflate(R.layout.dialognewfood, null));
                    //Añadimos los botones
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Asignamos el valor introducido a la variable
                            EditText et = (EditText)findViewById(R.id.nuevoAlimento1);

                            alimentoNuevo =  et.getText().toString();
                            Log.d("alimento", alimentoNuevo);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No hacemos nada
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        botonAlimentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cuando pulsemos el botón, se va a abrir el activity con todos los alimentos
                /*intent = new Intent(this, ListadoAlimentos.class);
                startActivity(intent);
                 */
            }
        });
        }

    }

