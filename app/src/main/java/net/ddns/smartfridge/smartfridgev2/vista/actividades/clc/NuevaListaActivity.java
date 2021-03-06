package net.ddns.smartfridge.smartfridgev2.vista.actividades.clc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.adaptadores.CustomArrayAdapterNuevaLista;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.ComponenteListaCompra;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.ListaCompra;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Fecha;
import net.ddns.smartfridge.smartfridgev2.persistencia.gestores.GestionFicheroLista;
import net.ddns.smartfridge.smartfridgev2.persistencia.gestores.GestionSharedP;
import net.ddns.smartfridge.smartfridgev2.persistencia.gestores.ListaCompraDB;

import java.util.ArrayList;

/**
 * Activity que muestra las opciones para añadir alimentos a una nueva lista de la compra
 */
public class NuevaListaActivity extends AppCompatActivity {
    private Intent intent;//Para trabajar con los intents para lanzar nuevos activitys
    private String alimentoNuevo;//Para recoger el dato introducido por el usuario en el dialog
    private Context context;//Para indicar el contexto del activity
    private ArrayList<String> listaAlimentosCompra;//ArrayList que lleva el nombre de los alimentos que se muestran en la lista
    private ListaCompra listaNueva;//Para trabajar con el objeto ListaCompra
    private Fecha fecha;//Para usar los métodos para obtener la fecha de hoy
    private ArrayList<ComponenteListaCompra> alimentosLeidosSP;//Para leer los aliemntos que hay en el SP almacenados
    private int elementos;//Para contar el número de elementos que hay en el SP
    private GestionSharedP gsp;//Instancia de la clase para trabajar con el SharedPreferences
    private ListaCompraDB listaCompraDB;//Para utilizar los métodos de persistencia del módulo de lista de la compra
    private ComponenteListaCompra componente;//Para crear los items que van a ir en la lista
    private int id_alimento_manual;//Para guardar el id que hay en la bbdd asociado al alimento que metemos de manera manual
    private CustomArrayAdapterNuevaLista adapter;
    private ListView listView;
    private boolean editando = false;
    private static final int REQUEST_CODE_ALIMENTOS_SUGERIDOS = 357;
    private static final int REQUEST_CODE_REVISTA = 5465;
    private ArrayList<ComponenteListaCompra> componenteListaCompras;
    private ArrayList<ComponenteListaCompra> listadoProductos;
    private ArrayList<ComponenteListaCompra> listadoProductosExternos;//ArrayList para almacenar todos los productos que vienen de la parte externa
    private static ArrayList<ListaCompra> todasLasListas = new ArrayList<ListaCompra>();//Array con todas las listas de la compra que hay en la bbdd
    private GestionFicheroLista gfl;//Para almacenar las listas en un fichero interno
    private static final int SLEEP = 1500;//Milisegundos para detener el AsyncTask

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_lista);
        gsp = new GestionSharedP();
        gfl = new GestionFicheroLista(this);
        listaCompraDB = new ListaCompraDB(this);
        listadoProductosExternos = new ArrayList<>();
        cargarAdapter();
        //Fijamos el contexto del activity
        context = this;
        //Instanciamos el arraylist
        listaAlimentosCompra = new ArrayList<String>();
        alimentosLeidosSP = new ArrayList<ComponenteListaCompra>();
        //Comprobamos si hay algún alimento almacenado en el SP para notificárselo al usuario
        elementos = gsp.productosAlmacenados();
        if(gsp.isHayElemento()){
            //Mostramos la lista indicándo que hay elementos y cuáles quiere añadir a la lista
            alimentosLeidosSP = gsp.recogerValores();
            intent = new Intent(this, SugerenciaDeAlimentoActivity.class);
            intent.putExtra(getString(R.string.ali_sug), alimentosLeidosSP);
            startActivityForResult(intent, REQUEST_CODE_ALIMENTOS_SUGERIDOS);
            gsp.borrarSP();
        }
        //Cogemos la referencia a los floating action buttons
        com.getbase.floatingactionbutton.FloatingActionButton botonManual = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.manual);
        com.getbase.floatingactionbutton.FloatingActionButton botonAlimentos = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.anadirAlimentos);
        com.getbase.floatingactionbutton.FloatingActionButton botonEditar = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.editar);
        com.getbase.floatingactionbutton.FloatingActionButton botonAceptar = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.aceptar);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        //Le asignamos el listener a cada botón
        botonManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cuando pulsemos el botón, nos da la opción de añadir alimentos de manera manual
                    //Mensaje del Alert
                    builder.setMessage(getString(R.string.agregar_lista));
                    //Título
                    builder.setTitle(getString(R.string.agregar_manual));
                    //Añadimos el layout que hemos creado
                    //builder.setView(inflater.inflate(R.layout.dialognewfood, null));
                    final EditText input = new EditText(context);
                    builder.setView(input);
                    //Añadimos los botones
                    builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Asignamos el valor introducido a la variable
                            if (!input.getText().toString().isEmpty()){
                                alimentoNuevo =  input.getText().toString();

                                //Lo añadimos a la bbdd
                                listaCompraDB.insertarAlimentoManual(alimentoNuevo);
                                //Leemos el id de ese objeto
                                id_alimento_manual = listaCompraDB.getIdAlimento(alimentoNuevo);
                                //Creamos el objeto que va a ser añadido a la vista de la lista
                                componente = new ComponenteListaCompra(id_alimento_manual, alimentoNuevo,3);
                                //Lo añadimos al adapter
                                adapter.addProducto(componente);
                            } else {
                                Toast.makeText(context, "Rellene el campo", Toast.LENGTH_SHORT).show();
                            }

                            Log.d("alimento", "alimento: " + alimentoNuevo + ", id: " + id_alimento_manual);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
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
                intent = new Intent(getApplicationContext(), CompraExternaActivity.class);
                editando = false;
                startActivityForResult(intent, REQUEST_CODE_REVISTA);
            }
        });
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSize() > 0){
                    editando = !editando;
                    if(editando && adapter.getSize() > 0){
                        adapter.mostrarCheckboxes();
                    }else if (!editando  && adapter.getSize() > 0){
                        adapter.ocultarrCheckboxes();
                        adapter.confirmarCambios();
                    }
                }
            }
        });
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cuando pulsemos el botón, se va a guardar la lista
                listadoProductos = new ArrayList<ComponenteListaCompra>();
                listadoProductos = adapter.getListaFinal();
                //listadoProductos = crearArray();
                //Creamos el nuevo objeto Lista
                fecha = new Fecha();
                listaNueva = new ListaCompra(fecha.fechaActualCompleta(), listadoProductos);
                //Guardamos los datos de la lista en la bbdd
                listaCompraDB.insertarListaCompra(listaNueva);
                Log.d("guardarLista", "Lista guardada");
                int id = listaCompraDB.getIdLista(fecha.fechaActualCompleta());
                Log.d("guardarLista", "id: " + id);
                insertarComponentesLista(listadoProductos, id);
                listaNueva.setId(id);
                //Guardamos todas las listas que se generan en un fichero interno de la app
                gfl.escribirLista(listaNueva);
                Toast.makeText(context, getString(R.string.lista_guardada) + fecha.fechaActual(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //Ejecutamos el AsyncTask para comprobar el SP mientras se está haciendo la lista
        new agregarEscasez().execute(adapter);
    }


    /**
     * Método para hacer los insert en las tablas correspondientes
     *
     * @param a       Lista de componentes a almacenar
     * @param idLista id de la lista donde están esos componentes
     */
    public void insertarComponentesLista(ArrayList<ComponenteListaCompra> a, int idLista){
        int tipo;//Para guardar el tipo de cada objeto del arrayList
        //Recorremos el ArrayList
        for (ComponenteListaCompra c : a){
            tipo = c.getTipo();
            switch (tipo){
                case 1:
                    listaCompraDB.insertComponenteInterno(c, idLista);
                    Log.d("tipo", "Entra por el 1: " + c.getNombreElemento() + " con tipo " + c.getTipo());
                    break;
                case 2:
                    listaCompraDB.insertComponenteExterno(c, idLista);
                    Log.d("tipo", "Entra por el 2: " + c.getNombreElemento() + " con tipo " + c.getTipo());
                    break;
                case 3:
                    listaCompraDB.insertComponenteManual(c, idLista);
                    Log.d("tipo", "Entra por el 3: " + c.getNombreElemento() + " con tipo " + c.getTipo());
            }
        }
    }

    /**
     * Método para cargar el adaptador
     */
    private void cargarAdapter(){
        if(componenteListaCompras != null){
            adapter = new CustomArrayAdapterNuevaLista(this, componenteListaCompras, this);
        } else {
            adapter = new CustomArrayAdapterNuevaLista(this, new ArrayList<ComponenteListaCompra>(), this);
        }
        listView = (ListView)findViewById(R.id.lvNuevaLista);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ALIMENTOS_SUGERIDOS && resultCode == RESULT_OK){
            componenteListaCompras = new ArrayList<>();
            componenteListaCompras = (ArrayList<ComponenteListaCompra>) data.getExtras().getSerializable("AlimentosSeleccionados");
            cargarAdapter();
        } else if (requestCode == REQUEST_CODE_REVISTA && resultCode == RESULT_OK){
            if (listadoProductosExternos!=null) {
                listadoProductosExternos = (ArrayList<ComponenteListaCompra>) data.getExtras().getSerializable("AlimentosSeleccionados");
                adapter.addProductosVarios(listadoProductosExternos);
                if(data.getExtras() != null){
                    listadoProductosExternos = (ArrayList<ComponenteListaCompra>) data.getExtras().getSerializable("AlimentosSeleccionados");
                    adapter.addProductosVarios(listadoProductosExternos);
                }
            }
        }
    }

    /**
     * Clase con el AsyncTask para comprobar si hay modificaciones el el SP para añadir esos elementos a la lista
     */
    public class agregarEscasez extends AsyncTask<Object, Void, Void> {
        private GestionSharedP gsp;//Para trabajar con el SP
        private CustomArrayAdapterNuevaLista adapter;//Para trabajar con el adaptador de la clase
        private ArrayList<ComponenteListaCompra> alimentosLeidosSP;//Para leer los aliemntos que hay en el SP almacenados

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gsp = new GestionSharedP();
            alimentosLeidosSP = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Object... objects) {
            //Va a estar siempre comprobando si hay modificaciones en el SharedPreferences
            while(true){
                if(gsp.isHayElemento()){
                    alimentosLeidosSP = gsp.recogerValores();
                    adapter = (CustomArrayAdapterNuevaLista) objects[0];
                    for (int i=0; i<alimentosLeidosSP.size(); i++){
                        adapter.addProducto(alimentosLeidosSP.get(i));
                    }
                    gsp.borrarSP();
                }
                try {
                    //Hacemos un sleep para que no esté continuament haciendo accesos al SP
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    //En el onDestroy cerramos la bbdd

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listaCompraDB.cerrarConexion();
    }
}

