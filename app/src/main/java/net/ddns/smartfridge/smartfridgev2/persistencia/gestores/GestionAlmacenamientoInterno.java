//Clase con los métodos para guardar una imagen realizada con la cámara en el almacenamieno interno

package net.ddns.smartfridge.smartfridgev2.persistencia.gestores;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase con los métodos para guardar una imagen realizada con la cámara en el almacenamieno interno del teléfono o de la tablet
 */
public class GestionAlmacenamientoInterno {

    private Context contexto;//Para obtener el contexto de la activity
    private String path;//Para el path de la foto

    /**
     * Constructor de la clase
     *
     * @param cont el contexto de la Activity
     */
    public GestionAlmacenamientoInterno(Context cont){
        this.contexto=cont;
    }

    /**
     * Método para comprobar si el almacenamiento externo está disponible
     *
     * @return booleano que indica si el almacenamiento está disponible o no
     */
    public boolean disponible() {
        //Guardamos en un String el estado del almacenamiento externo
        String estado = Environment.getExternalStorageState();
        //Si está disponible, devolvemos true
        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            return true;
        }
        return false;
    }

    /**
     * Método para obtener el directorio donde se va a almacenar
     *
     * @return directorio donde se almacenará internamente el elemento
     */
    public String cogerDirectorio(){
        //Indiamos la cte DIRECTORY_PICTURES para que se guarde en la carpeta de imágenes del dispositivo
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Devolvemos el path con la ruta al fichero
        return file.getAbsolutePath();
    }

    /**
     * Método para guardar la imagen en almacenamiento interno
     *
     * @param b El bitmap con la imagen que queremos almacenar
     * @return ruta del almacenamiento interno
     */
    public String guardarImagen(Bitmap b){
        String directorioAlmcto;//Para darle el nombre a la imagen
        File fichero;
        //Creamos los streams para almacenar la imagen, que irá bomo byte[]
        FileOutputStream fileoutputstream = null;
        ByteArrayOutputStream bytearrayoutputstream=null;
        if (disponible()){
            //Si el directorio está disponible, se lo asociamos a la variable directorio de Almacenamiento
            directorioAlmcto = cogerDirectorio();
            //Creamos el fichero con el nombre equivalente a los ms del sistema para que no se repita
            fichero = new File(directorioAlmcto + "/imagenVision.png");
            Log.d("seguimiento", ""+ fichero.getAbsolutePath());
            //Almacenamos el fichero que hemos creado
            try {
                fileoutputstream = new FileOutputStream(fichero);
                bytearrayoutputstream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 50, bytearrayoutputstream);
                fileoutputstream.write(bytearrayoutputstream.toByteArray());
                path = MediaStore.Images.Media.insertImage(contexto.getContentResolver(), b, "cosa", null);
            } catch (FileNotFoundException e) {
                Toast.makeText(contexto, "No se ha podido almacenar la imagen, no se encuntra el fichero de destino", Toast.LENGTH_SHORT).show();
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
        return path;
    }

}
