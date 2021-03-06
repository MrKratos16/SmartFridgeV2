package net.ddns.smartfridge.smartfridgev2.vista.actividades.ca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.Alimento;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.Alimento_Codigo;
import net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones.CustomDatePicker;
import net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones.CustomOnDragListener;
import net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones.CustomOnLongClickListener;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Dialogos;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Fecha;
import net.ddns.smartfridge.smartfridgev2.persistencia.gestores.AlimentoDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity para elegir la caducidad de los alimentos
 */
public class CaducidadAlimento extends AppCompatActivity {
    /**
     * Constante con el número máximo de unidades del WheelPicker
     */
    public static final int MAXUDS = 50;//Número máximo de uds del WheelPicker
    private int unidadesWheel;//Unidades del Wheel Picker
    private Alimento_Codigo ac;//Para almacenar el objeto que recojamos el ConfirmadorAlimentoActivity
    private Alimento a;//Para almacenar el objeto que recojamos el ConfirmadorAlimentoActivity
    private int tiempo_Caducidad;//Para almacenar los días de caducidad
    private AlimentoDB adb;//Instancia del Gestor de BD de Alimentos
    private CustomDatePicker customDatePicker;
    private String fecha_inicial;//Para asignar la fecha actual
    private String fecha_final;//Para asignar la fecha de caducidad a través del calendario
    private int controlDragAndDrop = 0;//Para determinar si la selección de la caducidad se ha hecho por un medio u otro
    private Alimento al;//Para construir el objeto de tipo alimento que se almacenará en la bbdd
    private Intent intent;
    private ImageView dd;
    private String nombreAlimento;//Para almacenar el nomber del alimento
    private Bitmap imagenAlimento;//Para almacenar la imagen del alimento
    private static boolean manual=false;//Será true cuando venga de inserción manual
    private String cod_barras;//Para el código de barras del alimento

    /**
     * Booleano para ver la pila de actividades
     */
    public static Boolean isInFront = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("manual", "manual inicion onCreate Caducidad Alimento:" + manual);
        super.onCreate(savedInstanceState);
        intent = getIntent();
        try{
            cod_barras = intent.getStringExtra("CODIGO_BARRAS");
            Log.d("cod", "codigo 6: " + cod_barras);
        } catch (NullPointerException e){
            //No hacemos nada
        }
        customDatePicker = new CustomDatePicker(this, this);

        setContentView(R.layout.activity_caducidad_alimento);
        ponerImagenes();
        dd = (ImageView)findViewById(R.id.ivDropZone);
        comprobarPadre();
        cargarDragAndDrop();
        WheelPicker wheelPicker = (WheelPicker) findViewById(R.id.wheelUdsDetalles);
        wheel(wheelPicker);
        adb = new AlimentoDB(this);
        mostrarTutorial();
    }
    /**
     * Método para comprobar de qué Activity proceden los datos del intent
     *
     */
    private void comprobarPadre(){
        //Comprobamos si venimos de insertar manualmente o no
        if(intent.getExtras().get(getString(R.string.clasePadre)).equals(getString(R.string.insertador))){
            nombreAlimento = String.valueOf(intent.getExtras().get(getString(R.string.nombreAlimento)));
            imagenAlimento = (Bitmap) intent.getExtras().get(getString(R.string.fotoB));
            dd.setImageBitmap(imagenAlimento);
            if (cod_barras!=null){
                manual = true;
            }
        }else if (intent.getExtras().get(getString(R.string.clasePadre)).equals(getString(R.string.confirmador))){
            manual = false;
            //Si venimos de confirmar alimento debemos ver si venimos desde el scaner o desde el Cloud Vision
            //Intentamos coger el objeto
            ac = ConfirmadorAlimentoActivity.getAlimento();
            Log.d("NULL_MIS_HUEVOS", "comprobarPadre: " + ac);
            if (ac == null){
                //Si es null, venimos del cloud vision

                File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
                Uri uri = Uri.fromFile(file);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    bitmap = crupAndScale(bitmap, 300);
                    imagenAlimento = bitmap;
                    //Llamamos al metodo cargarImagen y le pasamos la uri
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("jjjjj", "onActivityResult: " + "hoaalala2");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("jjjjj", "onActivityResult: " + "hoaalala3");
                }

                dd.setImageBitmap(imagenAlimento);
                nombreAlimento = ConfirmadorAlimentoActivity.getNombreCloud();
            } else {
                dd.setImageBitmap(ac.getImagen());
            }
        }
    }

    public static  Bitmap crupAndScale (Bitmap source,int scale){
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }

    /**
     * Set tiempo caducidad.
     *
     * @param tiempo_Caducidad the tiempo caducidad
     */
    public void setTiempo_Caducidad(int tiempo_Caducidad){
        this.tiempo_Caducidad = tiempo_Caducidad;
    }
    /**
     * Método para rellenar los elementos del drag & drop
     *
     */
    private void cargarDragAndDrop(){
        findViewById(R.id.ivCad1).setOnLongClickListener(new CustomOnLongClickListener(1));
        findViewById(R.id.ivCad2).setOnLongClickListener(new CustomOnLongClickListener(2));
        findViewById(R.id.ivCad3).setOnLongClickListener(new CustomOnLongClickListener(3));
        findViewById(R.id.ivCad4).setOnLongClickListener(new CustomOnLongClickListener(4));
        findViewById(R.id.ivCad5).setOnLongClickListener(new CustomOnLongClickListener(5));
        findViewById(R.id.ivCad6).setOnLongClickListener(new CustomOnLongClickListener(6));
        findViewById(R.id.ivCad7).setOnLongClickListener(new CustomOnLongClickListener(7));
        findViewById(R.id.relativeLayout).setOnDragListener(new CustomOnDragListener((ImageView) findViewById(R.id.ivDropZone),
                (LinearLayout) findViewById(R.id.linearLayout), this, this));
    }

    /**
     * Método para dar las características al WheelPicker.
     *
     * @param wheelPicker Wheelpicker al que le queremos dar las caractrísticas
     */
    public void wheel(WheelPicker wheelPicker){
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

    /**
     * Metodo que mostrará un dialog con la caducidad y las uds seleccionadas
     *
     * @param v Vista sobre la que se ejecutará
     */
    public void confirmarCaducidad(View v){
        Dialogos dialogos = new Dialogos(this,this);

        //Visualizamos el contenido de la bbdd
        Cursor c = adb.getAlimentos();
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                int id = c.getInt(0);
                String nombre= c.getString(1);
                int cantidad = c.getInt(2);
                int caducidad = c.getInt(3);
                String f_hoy = c.getString(4);
                String f_cad = c.getString(5);
                Log.d("tag", "Mi Nevera: " + nombre + ", " + cantidad + ", " + caducidad + ", "+ f_hoy + ", " + f_cad);
            } while(c.moveToNext());
        }
        c.close();

        Fecha fecha = new Fecha();
        //Convertimos en String la fecha del día de hoy
        String fecha_actual = fecha.fechaActual();
        //Verificamos si se ha seleccionado la caducidad por drag and drop o por el calendario
        if (controlDragAndDrop==-1){
            //Significa que se ha seleccionado la caducidad por medio del drag and drop
            String fecha_caducidad_alimento = fecha.diasAFecha(tiempo_Caducidad);
            //Creamos el objeto Alimento
            try{
                //Almacenamos en las variables los valores. Si venimos del escaner, tendremos el objeto. Si no, tendremos un NullPointer
                nombreAlimento = ac.getNomAlimento();
                imagenAlimento = ac.getImagen();
            } catch (NullPointerException e){
                //Cogerá los valores por defecto
                Log.d("seguimiento", "No venimos del escaner");
            }
            al = new Alimento(nombreAlimento, unidadesWheel, tiempo_Caducidad, fecha_actual, fecha_caducidad_alimento, imagenAlimento);
            //Mostramos el dialog con los datos
            dialogos.dialogCaducidad(unidadesWheel, tiempo_Caducidad, al, manual, cod_barras);
            Log.d("cod", "codigo 7: " + cod_barras);
        } else if (controlDragAndDrop==1){
            //Significa que se ha seleccionado la caducidad por medio del calendario
            //Metemos en la variable los dias que faltan para la caducidad
            int diasCaducidad = 0;
            try {
                diasCaducidad = fecha.fechaDias(fecha_final, this);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Toast.makeText(this, "dias para caducidad: " + diasCaducidad, Toast.LENGTH_LONG).show();
            //Creamos el objeto Alimento
            try{
                //Almacenamos en las variables los valores. Si venimos del escaner, tendremos el objeto. Si no, tendremos un NullPointer
                nombreAlimento = ac.getNomAlimento();
                imagenAlimento = ac.getImagen();
            } catch (NullPointerException e){
                //Entrará por aquí cuando vengamos de Cloud Vision
                imagenAlimento = ConfirmadorAlimentoActivity.getImagenCloud();
                if (imagenAlimento==null){
                    imagenAlimento = (Bitmap)intent.getExtras().get(getString(R.string.fotoB));
                }
            }
            al = new Alimento(nombreAlimento, unidadesWheel, diasCaducidad, fecha_actual, fecha_final, imagenAlimento);
            //Mostramos el dialog con los datos
            dialogos.dialogCaducidad(unidadesWheel, diasCaducidad, al, manual, cod_barras);
            Log.d("cod", "codigo 8: " + cod_barras);
        } else if (controlDragAndDrop==0){
            dialogos.dialogNoCaducidad();
        }
        //Toast.makeText(this, String.valueOf(controlDragAndDrop), Toast.LENGTH_SHORT).show();
    }

    /**
     * Método para mostrar el calendario cuando se pulsa el botón más
     *
     * @param view Vista sobre la que se muestra el botón
     */
    public void botonMas(View view){
        try {
            customDatePicker.obtenerFecha();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para poner las fechas inicial y final
     *
     * @param fecha_incial la fecha inicial
     * @param fecha_final  la fecha final
     */
    public void setFechas(String fecha_incial, String fecha_final){
        this.fecha_inicial = fecha_incial;
        this.fecha_final = fecha_final;
    }

    /**
     * Gets control drag and drop.
     *
     * @return the control drag and drop
     */
    public int getControlDragAndDrop() {
        return controlDragAndDrop;
    }

    /**
     * Sets control drag and drop.
     *
     * @param controlDragAndDrop the control drag and drop
     */
    public void setControlDragAndDrop(int controlDragAndDrop) {
        this.controlDragAndDrop = controlDragAndDrop;
    }

    /**
     * Método para poner las imágenes a los botones del drag and drop
     */
    public void ponerImagenes(){
        Drawable elemento;//Para almacenar los elementos drawable
        Bitmap bitmap;//Para convertir el drawable a bitmap
        RoundedBitmapDrawable redondo;//Para hacer la imagen redonda
        //Cogemos el drawable de la carpeta de recursos
        elemento = getResources().getDrawable(R.drawable.ic_1);
        //Convertimos el drawable a bitmap
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        //Hacemos ese bitmap redondeado
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        //Redondeamos los bordes
        redondo.setCornerRadius(bitmap.getHeight());
        //Cogemos la referencia al imageView
        ImageView iv1 = (ImageView)findViewById(R.id.ivCad1);
        iv1.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_2);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv2 = (ImageView)findViewById(R.id.ivCad2);
        iv2.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_3);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv3 = (ImageView)findViewById(R.id.ivCad3);
        iv3.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_4);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv4 = (ImageView)findViewById(R.id.ivCad4);
        iv4.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_5);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv5 = (ImageView)findViewById(R.id.ivCad5);
        iv5.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_6);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv6 = (ImageView)findViewById(R.id.ivCad6);
        iv6.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.ic_7);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv7 = (ImageView)findViewById(R.id.ivCad7);
        iv7.setImageDrawable(redondo);
        elemento = getResources().getDrawable(R.drawable.mas);
        bitmap = ((BitmapDrawable) elemento).getBitmap();
        redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        ImageView iv8 = (ImageView)findViewById(R.id.ivCadMas);
        iv8.setImageDrawable(redondo);
    }

    //En el onDestroy cerramos la bbdd
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adb.cerrarConexion();
        isInFront = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    /**
     * Método para mostrar el tutorial
     */
    private void mostrarTutorial(){
        final SharedPreferences tutorialShowcases = getSharedPreferences(getString(R.string.tutorialSP), MODE_PRIVATE);
        boolean run;
        run = tutorialShowcases.getBoolean(getString(R.string.tutorial1), true);

        if(run){//Comprobamos si ya se ha mostrado el tutorial en algún momento

            //Creamos un nuevo LayoutParms para cambiar el botón de posición
            final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            // Ponemos márgenes al botón
            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            lps.setMargins(margin, margin, margin, margin);

            //Creamos el ShowCase
            final ShowcaseView s = new ShowcaseView.Builder(this)
                    .setTarget( new ViewTarget( ((View) findViewById(R.id.ivCad4)) ) )
                    .setContentTitle(getString(R.string.dias_c))
                    .setContentText(getString(R.string.dias_c_t))
                    .hideOnTouchOutside()
                    .build();
            s.setButtonText(getString(R.string.siguiente));
            //s.setButtonPosition(lps);
            //Comprobamos que el botón del showCase se pulsa para hacer el switch. Se va acomprobar el contador para ver si se muestra el siguiente showcas
            s.overrideButtonClick(new View.OnClickListener() {
                int contadorS = 0;

                @Override
                public void onClick(View v) {
                    contadorS++;
                    switch (contadorS) {
                        case 1:
                            s.setButtonPosition(lps);
                            s.setTarget( new ViewTarget( ((View) findViewById(R.id.ivCadMas)) ) );
                            s.setContentTitle(getString(R.string.mas_d));
                            s.setContentText(getString(R.string.mas_d_t));
                            break;

                        case 2:

                            s.setTarget( new ViewTarget( ((View) findViewById(R.id.wheelUdsDetalles)) )  );
                            s.setContentTitle(getString(R.string.uds_a));
                            s.setContentText(getString(R.string.uds_a_t));
                            break;

                        case 3:
                            s.setTarget( new ViewTarget( ((View) findViewById(R.id.btOkCad)) )  );
                            s.setContentTitle(getString(R.string.b_acep));
                            s.setContentText(getString(R.string.b_acep_t));
                            break;
                        case 4:
                            //Cambiamos la variable en el sharedPreferences para que no se vuelva a mostrar el tutorial
                            SharedPreferences.Editor tutorialShowcasesEdit = tutorialShowcases.edit();
                            tutorialShowcasesEdit.putBoolean(getString(R.string.tutorial1), false);
                            tutorialShowcasesEdit.apply();
                            s.hide();
                            break;
                    }
                }
            });
        }
    }
}
