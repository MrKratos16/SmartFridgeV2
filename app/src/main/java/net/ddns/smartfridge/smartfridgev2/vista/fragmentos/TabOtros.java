package net.ddns.smartfridge.smartfridgev2.vista.fragmentos;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.Receta;
import net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones.CustomDialogProgressBar;
import net.ddns.smartfridge.smartfridgev2.persistencia.MySQL.MySQLHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment para hacer el filtro de las recetas
 */
public class TabOtros extends Fragment {
    private EditText buscar;//TextView donde el usuario va a introducir los criterios de búsqueda
    private MySQLHelper myHelper;//Para trabajar con la bbdd
    private ArrayList<Receta> recetas;
    private ArrayList<String> tiempo;//Array para guardar los ids de la tabla del tiempo de las recetas
    private ArrayList<String> dificultad;//Array para guardar los ids de la tabla de dificultad de las recetas
    private Spinner spinnerT;//Para coger la referencia del spinner del tiempo
    private Spinner spinnerD;//Para coger la referencia del spinner de la duración
    private int procedencia=2;//Para ver por dónde se va a realizar la búsqueda
    private static ArrayList<Bitmap> imagenes = new ArrayList<>();

    /**
     * Constructor
     */
    public TabOtros() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cogemos la referencia al activity donde se sacan los datos
        TabAlimento ta = (TabAlimento)getFragmentManager().findFragmentByTag("tab1");
        //Recogemos los valores que necesitamos para los comboBox
        tiempo=ta.getTiempo();
        dificultad = ta.getDif();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_otros, container, false);
        buscar = (EditText)v.findViewById(R.id.tvRecetasD);

        spinnerT = (Spinner) v.findViewById(R.id.spnTiempo);
        spinnerD = (Spinner) v.findViewById(R.id.spnDificultad);
        //Le asignamos los valores a los spinner

        List<String> spinnerArrayTiempo =  new ArrayList<String>();
        List<String> spinnerArrayDificultad =  new ArrayList<String>();
        for (String item : tiempo) {
            spinnerArrayTiempo.add(item);
        }
        for (String item : dificultad) {
            spinnerArrayDificultad.add(item);
        }

        ArrayAdapter<String> adapterT = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArrayTiempo);
        ArrayAdapter<String> adapterD = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArrayDificultad);

        adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(adapterT);

        adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerD.setAdapter(adapterD);

        v.findViewById(R.id.ibFiltrarTabOtros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Vemos si el usuario quiere filtrar por el título de la receta o por el tiempo y duración
                switch (procedencia){
                    case 1:
                        //En caso de que filtre por el título, pasamos estos parámetros al asyncTask
                        new mostrarRecetasFiltro().execute(getString(R.string.titulo), buscar.getText().toString().toUpperCase());
                        break;
                    case 2:
                        Log.d("sentencia4", "case 2");
                        //En caso de que filtre por la duración y el tiempo, pasamos estos parámetros al asyncTask
                        new mostrarRecetasFiltro().execute(getString(R.string.spinner), spinnerT.getSelectedItem().toString(), spinnerD.getSelectedItem().toString());
                        break;
                }

            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(buscar.getText().toString().isEmpty()){
                    procedencia = 2;
                    spinnerD.setEnabled(true);
                    spinnerT.setEnabled(true);
                } else {
                    procedencia = 1;
                    spinnerD.setEnabled(false);
                    spinnerT.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinnerD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buscar.setText("");
                procedencia = 2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }

    public static ArrayList<Bitmap> getImagenes(){
        return imagenes;
    }

    /**
     * Clase interna con el AsyncTask para sacar las recetas en función del filtro aplicado
     */
    public class mostrarRecetasFiltro extends AsyncTask<String, Void, ArrayList<Receta>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenes.clear();
            //ustomDialogProgressBar.showDialogCuadrado();
        }

        @Override
        protected ArrayList<Receta> doInBackground(String... voids) {
            Log.d("sentencia3", "sentencia: " + voids[1]);
            recetas = new ArrayList<>();
            myHelper = new MySQLHelper();
            try {
                //Abrimos la conexión a la bbdd
                myHelper.abrirConexion();
                //Vemos si tenemos que hacer la consulta en función del título o de los spinner
                if (voids[0].equals("titulo")) {
                    recetas = myHelper.recogerRecetaTitulo(voids[1]);
                } else if (voids[0].equals("spinner")){
                    Log.d("sentencia4", "else if");
                    recetas = myHelper.recetaPorTiempoDificultad(voids[1], voids[2]);
                }
                Log.d("intentServiced", "doInBackground: " + recetas.size());
                for(int i = 0;i<recetas.size(); i++){
                    Log.d("intentServiced", "Receta en intentService: " + recetas.get(i).getTituloReceta());
                }
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
            if (recetas.size()>0) {
                Log.d("otros", "receta: " + recetas.get(0).getTituloReceta());
                for (Receta item : recetas) {
                    imagenes.add(item.getImagenReceta());
                    item.setImagenReceta(null);
                }
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.filtro), recetas);
                intent.putExtra(getString(R.string.id_tab) , 1);
                getActivity().setResult(getActivity().RESULT_OK, intent);
            } else {
                //Si no hay coincidencias, se mostrará un toast al usuario
                Toast.makeText(getContext(), getString(R.string.no_res), Toast.LENGTH_SHORT).show();
            }
            try {
                myHelper.cerrarConexion();

                getActivity().finish();
            } catch (SQLException e) {
                Log.d("SQL", "Error al cerrar la bbdd");
            }
        }
    }
}
