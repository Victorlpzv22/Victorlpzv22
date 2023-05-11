package dam.camarasmadrid09.manejador;

import android.os.SystemClock;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dam.camarasmadrid09.FragmentoListado;
import dam.camarasmadrid09.R;
import dam.camarasmadrid09.objetos.Camara;
import dam.camarasmadrid09.objetos.ListaCamaras;

// Clase que se encarga de hacer el parser SAX mediante la invocación de los métodos definidos en la interfaz  DefaultHandler
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class ManejadorXML extends DefaultHandler {
    private String nombre, coordenadas, url;
    private StringBuilder contenido;
    private boolean esNombre, esCoordenadas, esDescription;
    private ListaCamaras listaCamaras;
    private TextView progresoContador;

    private int contadorCamarasActual = 0;      // Para poder contar las cámaras y publicar los resultados
    private final boolean retraso = true;   // Constante para meter un retardo grande y así poder ver las barras de progreso

    public ManejadorXML(FragmentoListado instanciaClaseTarea) {
        progresoContador = instanciaClaseTarea.getActivity().findViewById(R.id.progresoContador);
    }

    // Método invocado desde el método run() de HiloAnalisis para recoger el listado de las cámaras
    public ListaCamaras getResultado() {
        return listaCamaras;
    }

    // Los siguientes son los métodos que hay que definir de la interfaz DefaultHandler
    @Override
    public void startDocument() throws SAXException {       // Inicializar variables y objetos
        super.startDocument();
        listaCamaras = new ListaCamaras();
        contenido = new StringBuilder();
        esNombre = false;
        esDescription = false;
        esCoordenadas = false;
    }

    @Override
    public void startElement(String namespaceURI, String nombreLocal, String nombreCualif, Attributes atributos) throws SAXException {
        super.startElement(namespaceURI, nombreLocal, nombreCualif, atributos);
        contenido.setLength(0);
        switch (nombreLocal) {
            case "Data":
                if (atributos.getValue(0).equals("Nombre")) {
                    esNombre = true;
                }
                break;
            case "coordinates":
                esCoordenadas = true;
                break;
            case "description":
                esDescription = true;
                break;
        }
    }

    @Override
    public void characters(char ch[], int comienzo, int longitud) throws SAXException {
        super.characters(ch, comienzo, longitud);
        contenido.append(ch, comienzo, longitud);
    }

    @Override
    public void endElement(String namespaceURI, String nombreLocal, String nombreCualif) throws SAXException {
        super.endElement(namespaceURI, nombreLocal, nombreCualif);
        switch (nombreLocal) {  // Procesar las etiquetas que  interesan
            case "Placemark":
                listaCamaras.addCamara(new Camara(nombre.replace("�", ""), coordenadas, url));
                progresoContador.post(new Runnable() {
                    @Override
                    public void run() {
                        progresoContador.setText(Integer.toString(contadorCamarasActual++));
                    }
                });
                if (retraso)
                    SystemClock.sleep(10);
                break;
            case "Value":
                if (esNombre) {
                    nombre = contenido.toString().trim();
                    esNombre = false;
                }
                break;
            case "coordinates":
                if(esCoordenadas) {
                    coordenadas = contenido.toString().trim();
                    esCoordenadas = false;
                }
                break;
            case "description":
                if(esDescription) {
                    url = contenido.toString().trim();
                    esDescription = false;
                }
                break;
        }
        contenido.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}

