package net.ddns.smartfridge.smartfridgev2.modelo.basico;

/**
 * Clase que representa un alimento nuevo, creado a partir de insertar manualmente
 */
public class Alimento_Nuevo {
    private int id;//Representa el id del alimento que proviene de la tabla Alimentos de MiNevera(Foreign Key)
    private String nombre_ali_nuevo;//Representa el nombre del alimento
    private String fecha_alta;//Fecha en la que se insertó el alimento en la bbdd

    /**
     * Constructor
     *
     * @param nombre_ali_nuevo nombre del alimento
     * @param fecha_alta       fecha de alta en la bbdd del alimento
     * @param id               id del alimento
     */
    public Alimento_Nuevo(String nombre_ali_nuevo, String fecha_alta, int id) {
        this.nombre_ali_nuevo = nombre_ali_nuevo;
        this.fecha_alta = fecha_alta;
        this.id = id;
    }
    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets nombre ali nuevo.
     *
     * @return the nombre ali nuevo
     */
    public String getNombre_ali_nuevo() {
        return nombre_ali_nuevo;
    }

    /**
     * Gets fecha alta.
     *
     * @return the fecha alta
     */
    public String getFecha_alta() {
        return fecha_alta;
    }

}
