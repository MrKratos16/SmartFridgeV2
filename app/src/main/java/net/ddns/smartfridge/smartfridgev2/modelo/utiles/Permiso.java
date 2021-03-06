package net.ddns.smartfridge.smartfridgev2.modelo.utiles;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Clase que representa los permisos que se le solicitan explícitamente al usuario como el de la cámara y el de lectura y escritura
 */
public class Permiso {
    /**
     * Constante que sirve para indicar la posición del array para el permiso de la cámara
     */
    public static final int PERM_FOTO=1;//Lo usaremos para indicar la posición del array para el permiso de la cámara
    /**
     * Constante que sirve para indicar la posición del array para el permiso de lectura en almcto interno
     */
    public static final int PERM_LECTURA=2;//Lo usaremos para indicar la posición del array para el permiso de lectura en almcto interno
    /**
     * Constante que sirve para indicar la posición del array para el permiso de escritura en almcto interno
     */
    public static final int PERM_ESCRITURA=3;//Lo usaremos para indicar la posición del array para el permiso de escritura en almcto interno
    /**
     * Constante que sirve para indicar la posición del array para el otro permiso de la cámara
     */
    public static final int PERM_FOTO2 = 4;

    /**
     * Metodo para comprobar si tiene concedido el permiso para acceder a la cámara
     *
     * @param contexto  Contexto donde se ejecuta el permiso
     * @param actividad Activity sobre la que se va a ejecutar el permiso
     * @return boolean que si está a true significa que tiene el permiso
     */
//Metodo para comprobar si tiene concedido el permiso para acceder a la cámara
    public boolean permisoCamara(Context contexto, Activity actividad){
        boolean permisoC = false;//Declaramos la variable donde vamos a almacenar el permiso, la inicializamos como false
        //Declaramos una variable de tipo int donde se almacenará si se han dado o no permisos
        int permiso = ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.CAMERA);
        if (permiso == PackageManager.PERMISSION_GRANTED){//El usuario ha aceptado el permiso
            permisoC=true;
        } else {
            permisoC=false;
        }
        return permisoC;
    }

    /**
     * Metodo para comprobar si tiene concedido el permiso para lectura del almacenamiento interno
     *
     * @param contexto  Contexto donde se ejecuta el permiso
     * @param actividad Activity sobre la que se va a ejecutar el permiso
     * @return boolean que si está a true significa que tiene el permiso
     */
//Metodo para comprobar si tiene concedido el permiso para lectura del almacenamiento interno
    public boolean permisoLectura(Context contexto, Activity actividad){
        boolean permisoL = false;//Declaramos la variable donde vamos a almacenar el permiso, la inicializamos como false
        //Declaramos una variable de tipo int donde se almacenará si se han dado o no permisos
        int permisoLectura = ContextCompat.checkSelfPermission(contexto, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permisoLectura == PackageManager.PERMISSION_GRANTED){//El usuario ha aceptado el permiso
            permisoL=true;
        } else {
            //Si el usuario deniega el permiso, se le mostrará la explicación y se le dará la oportunidad de volver a aceptarlo
            if(ActivityCompat.shouldShowRequestPermissionRationale(actividad, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(contexto, "Es necesario el acceso al almacenamiento interno para realizar las fotografías", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_LECTURA);
            }
            permisoL=false;
        }
        return permisoL;
    }

    /**
     * Metodo para comprobar si tiene concedido el permiso para escritura del almacenamiento interno
     *
     * @param contexto  Contexto donde se ejecuta el permiso
     * @param actividad Activity sobre la que se va a ejecutar el permiso
     * @return boolean que si está a true significa que tiene el permiso
     */
//Metodo para comprobar si tiene concedido el permiso para escritura del almacenamiento interno
    public boolean permisoEscritura(Context contexto, Activity actividad){
        boolean permisoE = false;//Declaramos la variable donde vamos a almacenar el permiso, la inicializamos como false
        //Declaramos una variable de tipo int donde se almacenará si se han dado o no permisos
        int permisoEscritura = ContextCompat.checkSelfPermission(contexto, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permisoEscritura == PackageManager.PERMISSION_GRANTED){//El usuario ha aceptado el permiso
            permisoE=true;
        } else {
            //Si el usuario deniega el permiso, se le mostrará la explicación y se le dará la oportunidad de volver a aceptarlo
            if(ActivityCompat.shouldShowRequestPermissionRationale(actividad, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(contexto, "Es necesario el acceso al almacenamiento interno para guardar las fotografías", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_ESCRITURA);
            }
            permisoE=false;
        }
        return permisoE;
    }

    /**
     * Metodo que sirve para solicitar permiso para la cámara
     *
     * @param activity Activity sobre la que se va a ejecutar el permiso
     * @param context  Contexto donde se ejecuta el permiso
     * @return boolean que si está a true significa que tiene el permiso
     */
    public boolean permisoCamara2(Activity activity, Context context){
        boolean permisoCamara = false;

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(context, "Es necesario el acceso a la cámara para realizar las fotografías",
                        Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, PERM_FOTO2);

            }
        }else{
            permisoCamara = true;
        }

        return permisoCamara;
    }
}
