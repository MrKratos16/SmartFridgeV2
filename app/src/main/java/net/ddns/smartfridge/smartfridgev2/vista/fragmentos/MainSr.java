package net.ddns.smartfridge.smartfridgev2.vista.fragmentos;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.adaptadores.CustomRecyclerViewAdapterRecetas;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.Receta;
import net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones.CustomDialogProgressBar;
import net.ddns.smartfridge.smartfridgev2.persistencia.MySQL.MySQLHelper;
import net.ddns.smartfridge.smartfridgev2.vista.actividades.sr.DetallesRecetaActivity;
import net.ddns.smartfridge.smartfridgev2.vista.actividades.sr.FiltroRecetaActivity;
import net.ddns.smartfridge.smartfridgev2.vista.actividades.sr.MiNeveraFiltroActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Fragment para la parte de sugerir receta
 */
public class MainSr extends Fragment {
    private ParallaxRecyclerView recyclerView;
    private CustomRecyclerViewAdapterRecetas adapter;
    private ArrayList<Receta> recetas;
    private static final int REQUEST_FILTRO = 506;
    private static final int REQUEST_FILTRO2 = 78;
    private CustomDialogProgressBar customDialogProgressBar;
    private MySQLHelper myHelper;//Para trabajar con la bbdd
    private Receta recetaDado;//Para sacar la menu_receta aleatoria
    private Intent intent;//Para abrir los detalles

    /**
     * Constructor
     */
    public MainSr() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_sr, container, false);
        customDialogProgressBar = new CustomDialogProgressBar(getActivity());
        recyclerView = (ParallaxRecyclerView) view.findViewById(R.id.rvRecetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new mostrarRecetas(this).execute();
        com.getbase.floatingactionbutton.FloatingActionButton botonFiltro = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.filtros);
        com.getbase.floatingactionbutton.FloatingActionButton botonAleatorio = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.aleatorio);
        com.getbase.floatingactionbutton.FloatingActionButton botonNevera = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.miNevera);
        botonFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FiltroRecetaActivity.class);
                startActivityForResult(i, REQUEST_FILTRO);
            }
        });
        botonAleatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Buscará una menu_receta aleatoria
                recetaDado = recetaAleatoria(recetas);
                intent = new Intent(getActivity(), DetallesRecetaActivity.class);
                intent.putExtra(getString(R.string.id), recetaDado.getIdReceta());
                intent.putExtra(getString(R.string.nombre), recetaDado.getTituloReceta());
                intent.putExtra(getString(R.string.desc), recetaDado.getDescripcion());
                intent.putExtra(getString(R.string.tipo), recetaDado.getTipoReceta());
                intent.putExtra(getString(R.string.duracion), recetaDado.getTiempo());
                intent.putExtra(getString(R.string.dificultad), recetaDado.getDificultad());
                intent.putExtra(getString(R.string.imagen), recetaDado.getImagenReceta());
                startActivity(intent);
            }
        });
        botonNevera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MiNeveraFiltroActivity.class);
                startActivityForResult(i, REQUEST_FILTRO2);
            }
        });
        customDialogProgressBar =  new CustomDialogProgressBar(getActivity());
        return view;
    }

    /**
     * Clase para crear el adapter
     *
     * @param recetas listado de las recetas a mostrar
     */
    public void crearAdapter(ArrayList<Receta> recetas){
        this.recetas = recetas;
        adapter = new CustomRecyclerViewAdapterRecetas(recetas, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Clase con el AsyncTask para recoger todas las recetas de la bbdd y mostrarlas
     */
    public class mostrarRecetas extends AsyncTask<Void, Void, ArrayList<Receta>>{
        private MainSr mainSr;

        /**
         * Constructor
         *
         * @param mainSr Fragment
         */
        public mostrarRecetas(MainSr mainSr){
            this.mainSr = mainSr;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Receta> doInBackground(Void... voids) {
            myHelper = new MySQLHelper();
            try {
                //Abrimos la conexión a la bbdd
                myHelper.abrirConexion();
                //Recogemos las recetas una a una
                recetas = myHelper.recogerRecetas();
            } catch (SQLException e) {
                Log.d("SQL", "Error al conectarse a la bbdd: " + e.getErrorCode());
            } catch (ClassNotFoundException e) {
                Log.d("SQL", "Error al establecer la conexión: " + e.getMessage());
            }

            return recetas;
        }

        @Override
        protected void onPostExecute(ArrayList<Receta> recetas) {
            super.onPostExecute(recetas);
            try {
                myHelper.cerrarConexion();
                mainSr.crearAdapter(recetas);
                Log.d("AAAAAAAA", "tiempo en AsyncTask: " + recetas.get(0).getTiempo());
            } catch (SQLException e) {
                Log.d("SQL", "Error al cerrar la bbdd");
            } catch (NullPointerException ex) {
                Toast.makeText(getContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_FILTRO){
            Log.d("mainSR", "mainSR request: "+ requestCode);
            Log.d("mainSR", "mainSR result: "+ resultCode);
            if(resultCode == Activity.RESULT_OK){
                Log.d("mainSR", "mainSR: Result Ok");
                recetas = new ArrayList<>();
                recetas = (ArrayList<Receta>)data.getSerializableExtra(getString(R.string.filtro));
                Log.d("mainSR", "mainSR: "+ recetas.size());
                ArrayList<Bitmap> imgs = new ArrayList<>();
                imgs.clear();



                /*ArrayList<byte[]> bitmaps = (ArrayList<byte[]>)data.getSerializableExtra(getString(R.string.filtro_i));*/
                    /*byte[] bytes = bitmaps.get(i);*/
                    if((int)data.getExtras().get(getString(R.string.id_tab)) > 0){
                        //Bitmap bmp = TabOtros.getImagenes().get(i);
                        imgs = TabOtros.getImagenes();
                    } else if((int)data.getExtras().get(getString(R.string.id_tab)) < 0){
                        //Bitmap bmp = TabAlimento.getImagenes().get(i);
                        imgs = TabAlimento.getImagenes();
                    } else if((int)data.getExtras().get(getString(R.string.id_tab)) == 0){
                        //Bitmap bmp = TabTipo.getImagenes().get(i);
                        imgs = TabTipo.getImagenes();

                    }
                for (int i = 0; i < imgs.size(); i++){
                    recetas.get(i).setImagenReceta(imgs.get(i));
                }
                adapter.filtrarArray(recetas);
            }
        } else if (requestCode==REQUEST_FILTRO2){
            if(resultCode == Activity.RESULT_OK){
                Log.d("mainSR", "mainSR: Result Ok");
                recetas = new ArrayList<>();
                recetas = (ArrayList<Receta>)data.getSerializableExtra(getString(R.string.filtro));
                Log.d("mainSR", "mainSR: "+ recetas.size());

                ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>)data.getSerializableExtra(getString(R.string.filtro_i));
                for (int i = 0; i < bitmaps.size(); i++){
                    recetas.get(i).setImagenReceta(bitmaps.get(i));
                }
                adapter.filtrarArray(recetas);
            }
        }
    }

    /**
     * Método para coger una menu_receta aleatoria de la bbdd
     *
     * @param array Lista con todas las rectas
     * @return receta seleccionada aleatoriamente
     */
    public Receta recetaAleatoria(ArrayList<Receta> array){
        //Miramos el número de elementos que tiene el array para sacar el número aleatorio
        int longitud = array.size();
        int posicion;//Para determinar la posición del array que ocupa el número aleatorio
        Random r = new Random();
        posicion = r.nextInt(longitud);
        Log.d("random", "numero: " + posicion);
        //Seleccionamos la menu_receta que ha salido
        recetaDado = array.get(posicion);
        Log.d("random", "menu_receta: " + recetaDado.getTituloReceta());
        return recetaDado;
    }
}
