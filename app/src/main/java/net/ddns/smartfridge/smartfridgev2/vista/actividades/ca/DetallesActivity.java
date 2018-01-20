package net.ddns.smartfridge.smartfridgev2.vista.actividades.ca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.Alimento;

import java.util.ArrayList;
import java.util.List;

public class DetallesActivity extends AppCompatActivity {
    private int unidadesWheel;
    private static int MAXUDS = 50;
    private WheelPicker wheelPicker;
    private TextView tvNombreAlimento;
    private TextView tvFechaCaducidad;
    private TextView tvDiasRestantes;
    private ImageView ivAlimento;
    private MiNeveraActivity miNeveraActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        wheelPicker = findViewById(R.id.wheelUdsDetalles);
        wheel(wheelPicker);

        miNeveraActivity = new MiNeveraActivity();
        cargarDetallesAlimento();
    }
    public void wheel(WheelPicker wheelPicker){
        //final int itemSel;//Para el item seleccionado
        //Asignamos datos al WheelPicker
        List<Integer> unidades = new ArrayList<>();
        for (int k = 1; k <= MAXUDS; k++)
            unidades.add(k);
        wheelPicker.setData(unidades);
        //Asignamos los items que van a estar visibles
        wheelPicker.setVisibleItemCount(2);
        //Le ponemos las mismas dimensiones a todos los elementos
        wheelPicker.setSameWidth(true);
        //Indicamos que al inciarse esté apuntando a un elemento, en este caso, el primero
        wheelPicker.setSelectedItemPosition(0);
        //Iniciamos la variable a 1, ya que empezará en el primer elemento, que tendrá valor 1
        unidadesWheel = 1;
        /*Para poner color de fondo
        wheelPicker.setBackgroundColor(getResources().getColor(R.color.viewfinder_laser));*/
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                int itemSel = picker.getCurrentItemPosition();
                //Las uds van a ser la posición del wheel picker + 1
                unidadesWheel = itemSel + 1;
                //Log.d("uds", "uds: " + unidadesWheel);
                //Log.d("XEXU", String.valueOf(tiempo_Caducidad));
            }
        });
    }

    private void cargarDetallesAlimento(){
        Intent intentRecyclerView = getIntent();
        Alimento alimento = (Alimento) intentRecyclerView.getSerializableExtra("Alimento");

        tvNombreAlimento = (TextView)findViewById(R.id.tvNombreAlimentoDetalles);
        tvFechaCaducidad = (TextView)findViewById(R.id.tvFechaCaducidadDetalles);
        tvDiasRestantes = (TextView)findViewById(R.id.tvDiasRestantesDetalles);
        ivAlimento = (ImageView)findViewById(R.id.ivAlimentoDetalles);

        tvNombreAlimento.setText(alimento.getNombreAlimento());
        tvFechaCaducidad.setText(alimento.getFecha_caducidad());
        tvDiasRestantes.setText(alimento.getDias_caducidad());
        ivAlimento.setImageBitmap(miNeveraActivity.getImagenDetalles());
    }
}
