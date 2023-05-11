package dam.camarasmadrid09.objetos;

import android.net.Uri;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class Camara implements Comparable{

    String nombre;
    String coordenadas;
    Uri URL;

    public Camara(String nombre, String coordenadas, String URL){
        this.nombre = nombre;
        this.coordenadas = coordenadas;
        this.URL = Uri.parse(URL);
    }

    public String getNombre() { return nombre; }
    public String getCoordenadas() { return coordenadas; }
    public String getURL() { return URL.toString(); }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Camara) {
            Camara other = (Camara) o;
            return this.nombre.compareTo(other.nombre);
        }
        throw new ClassCastException("No se puede comparar una Camara con un objeto de otra clase");
    }

}
