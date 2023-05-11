package dam.camarasmadrid09.objetos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dam.camarasmadrid09.R;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class Adaptador extends ArrayAdapter<String> {
    private ArrayList<String> camaras;
    private final LayoutInflater inflador;
    public Adaptador (Context contexto, ArrayList<String> camaras) {
        super (contexto,0, camaras); // Invocar al constructor de ArrayAdapter, pasando un 0 en el
        this.camaras = camaras; //2º parámetro (el layout a usar) porque ahora vamos a usar el nuestro
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int posicion, View vistaReciclada, ViewGroup padre ) {
        if (vistaReciclada==null) {
            vistaReciclada = inflador.inflate(R.layout.elemento_lista, padre,false);
        }
        TextView nombreCamara = vistaReciclada.findViewById(R.id.elementoLista);
        String nombre = getItem(posicion);
        nombreCamara.setText(nombre);
        vistaReciclada.setBackgroundColor(posicion % 2 == 0 ? Color.parseColor("#e9eda1") : Color.parseColor("#e9edc9"));
        return vistaReciclada;
    }
}