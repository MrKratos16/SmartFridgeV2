//Clase con los métodos para guardar una imagen realizada con la cámara en el almacenamieno interno

package net.ddns.smartfridge.smartfridgev2.persistencia;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import net.ddns.smartfridge.smartfridgev2.modelo.Fecha;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 *  Clase con los métodos para guardar una imagen realizada con la cámara en el almacenamieno interno del teléfono o de la tablet

 */

public class GestorAlmacenamientoInterno {

    private Context contexto;//Para obtener el contexto de la activity

    public GestorAlmacenamientoInterno(Context cont){
        this.contexto=cont;
    }

    //Método para comprobar si el almacenamiento externo está disponible
    public boolean disponible() {
        //Guardamos en un String el estado del almacenamiento externo
        String estado = Environment.getExternalStorageState();
        //Si está disponible, devolvemos true
        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            return true;
        }
        return false;
    }

    //Obtenemos el directorio donde se va a almacenar
    public String cogerDirectorio(){
        //Indiamos la cte DIRECTORY_PICTURES para que se guarde en la carpeta de imágenes del dispositivo
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Devolvemos el path con la ruta al fichero
        return file.getAbsolutePath();
    }

    //Método para guardar la imagen
    public void guardarImagen(Bitmap b){
        //Uri uriFoto = null;//Uri de la foto para almacenarla en la bbdd
        String directorioAlmcto;//Para darle el nombre a la imagen
        File fichero;
        //Creamos los streams para almacenar la imagen, que irá bomo byte[]
        FileOutputStream fileoutputstream = null;
        ByteArrayOutputStream bytearrayoutputstream=null;
        if (disponible()){
            //Si el directorio está disponible, se lo asociamos a la variable directorio de Almacenamiento
            directorioAlmcto = cogerDirectorio();
            //Creamos el fichero con el mismo nombre porque se tiene que sobreescribir y así sólo se almacena una
            fichero = new File(directorioAlmcto + "/imagenVision.png");

            //Log.d("fichero", "" + directorioAlmcto + "/imagenVision.png");
            //uriFoto = Uri.fromFile( fichero );
            //Log.i("fichero", "Creado fichero de imagen");
            //Almacenamos el fichero que hemos creado
            try {
                fileoutputstream = new FileOutputStream(fichero);
                bytearrayoutputstream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 50, bytearrayoutputstream);
                fileoutputstream.write(bytearrayoutputstream.toByteArray());
            } catch (FileNotFoundException e) {
                Toast.makeText(contexto, "No se ha podido almacenar la imagen, no se encuentra el fichero de destino", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(contexto, "No se ha podido almacenar la imagen", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    bytearrayoutputstream.close();
                    fileoutputstream.close();
                } catch (IOException e) {
                    Toast.makeText(contexto, "No se ha podido almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(contexto, "Almacenamiento externo no disponible", Toast.LENGTH_SHORT).show();
        }
        //return uriFoto;
        Fecha fecha = new Fecha();
        Log.d("ms", "ms: " + fecha.fechaActual());
    }
}
