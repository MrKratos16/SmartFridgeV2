package net.ddns.smartfridge.smartfridgev2.modelo.servicios;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.ddns.smartfridge.smartfridgev2.modelo.basico.Alimento;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Fecha;
import net.ddns.smartfridge.smartfridgev2.persistencia.gestores.AlimentoDB;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;

import static java.lang.Thread.sleep;


/**
 * IntentService para comprobar periódicamente la caducidad de los alimentos
 */
public class ComprobarCaducidadIntentService extends IntentService {
    private Cursor cursor;//Para almacenar la consulta a la bbdd
    private String fechaCaducidad;//La fecha de caducidad recuperada de la bbdd
    private Alimento alimento;//Para construir el objeto Alimento y trabajar con él
    private static final int DIAS_CADUCIDAD=2;//La diferencia máxima que puede haber entre la fecha actual y la de caducidad, para que salte así la notificación
    private int diasParaCaducidad;//Vamos a almacenar los días que falten para que caduque el alimento
    private Fecha fecha;//Para usar el método que calcula los días que hay de diferencia entre dos fechas
    private Bitmap bm;//Para construir el Bitmap a partir de los datos de la bbdd

    public ComprobarCaducidadIntentService() {
        super("ComprobarCaducidadIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //Hacemos la conexión a la bbddd
        AlimentoDB alimentoDB = new AlimentoDB(this);
        fecha = new Fecha();
        //Hacemos el while(true) para que siempre esté en ejecución
        while(true){
            //Recogemos todos los alimentos y los almacenamos en el cursor
            cursor = alimentoDB.getAlimentos();
            //Recorremos el cursor
            if (cursor.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    //Recogemos la fecha de caducidad del alimento de la bbdd
                    fechaCaducidad = cursor.getString(5);
                    Log.d("servicio", "fecha de caducidad: " + fechaCaducidad);
                    //Calculamos los días de diferencia
                    try{
                        diasParaCaducidad = fecha.fechaDias(fechaCaducidad, this);
                    } catch (ParseException e){
                        Log.d("servicio", "alimento caducado");
                    }

                    Log.d("servicio", "dias para caducidad: " + diasParaCaducidad);
                    //Comprobamos si quedan <2 días para que caduque. Si es así, se lanzará la notificación
                    if (diasParaCaducidad<=DIAS_CADUCIDAD) {
                        byte[] byteArrayFoto;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        byteArrayFoto = cursor.getBlob(6);
                        try {
                            bm = BitmapFactory.decodeByteArray(byteArrayFoto, 0, byteArrayFoto.length);
                            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        } catch (NullPointerException e){
                            //No hay imagen en la bbdd
                        }
                        //Creamos el objeto alimento
                        alimento = new Alimento(cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                bm);
                        //Lanzamos la notificación
                        Log.d("servicio", "Faltan: " + diasParaCaducidad + " días para que caduque.");
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(cursor.moveToNext());
            }
        }
    }
}
