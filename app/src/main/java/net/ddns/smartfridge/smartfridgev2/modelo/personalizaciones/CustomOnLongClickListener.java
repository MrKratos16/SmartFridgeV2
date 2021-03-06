package net.ddns.smartfridge.smartfridgev2.modelo.personalizaciones;

import android.content.ClipData;
import android.view.View;

import net.ddns.smartfridge.smartfridgev2.vista.actividades.ca.CaducidadAlimento;

/**
 * Clase para crear un listener OnLongClick personalizado
 */
public class CustomOnLongClickListener implements View.OnLongClickListener {

    private String tiempo;

    /**
     * Constructor
     *
     * @param tiempo int con el tiempo
     */
    public CustomOnLongClickListener(int tiempo){
        this.tiempo = String.valueOf(tiempo);
    }

    @Override
    public boolean onLongClick(View view) {
        // Creamos un "ClipData" debido a que es necesario pasar información al método
        // "StartDrag()" para que el elemento destinado al Drop reciba dicha información,
        // en este caso no necesitamos pasar ningún tipo de información.
        ClipData data = ClipData.newPlainText("tiempo", tiempo);
        // Este paso tan solo crea la sombra del elemento para dar la sensación que el elemento
        // está siendo levantado.
        View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);
        //Esta parte es la importante ya que iniciamos la parte de Arrastras ("Drag").
        view.startDrag(data, dsb, view, 0);
        //Hacemos el elemento, que se encuentra en el lugar fijo, invisible.
        //Así al pintarlo en distintos lugares no habrá un duplicado
        view.setVisibility(View.INVISIBLE);
        return true;
    }
}
