package dam.camarasmadrid09.objetos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dam.camarasmadrid09.FragmentoListado;
import dam.camarasmadrid09.descarga.DescargaKML;
import dam.camarasmadrid09.manejador.ManejadorXML;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class HiloAnalisis implements Runnable {
    private String fichero;
    private ListaCamaras camaras;

    private FragmentoListado instanciaFragmentoListado;
    private SharedPreferences almacen;
    private boolean descargar;
    private boolean oculto;

    public HiloAnalisis(String fichero, FragmentoListado instanciaFragmentoListado, boolean descargar, boolean oculto) {
        this.fichero = fichero;
        this.instanciaFragmentoListado = instanciaFragmentoListado;
        this.descargar = descargar;
        this.oculto = oculto;
    }

    @Override
    public void run() {
        Context context = instanciaFragmentoListado.getContext();
        almacen = context.getSharedPreferences("datos", Context.MODE_PRIVATE);
        SAXParserFactory fabrica = SAXParserFactory.newInstance();
        fabrica.setNamespaceAware(true);
        try {
            if(descargar){
                if (!oculto)
                    instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instanciaFragmentoListado.cambiarTextoCarga("Espera mientras se descarga la lista de cámaras");
                        }
                    });
                else
                    instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Descargando lista de cámaras en segundo plano", Toast.LENGTH_SHORT).show();
                        }
                    });
                DescargaKML descargaWeb = new DescargaKML(context);
                descargaWeb.execute("http://informo.madrid.es/informo/tmadrid/CCTV.kml");
                while(descargaWeb.getStatus() != AsyncTask.Status.FINISHED){}
                //Hacer que espere hasta que el fichero se haya descargado

                final SharedPreferences.Editor editor = almacen.edit();
                Date fecha = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                editor.putString("fecha", sdf.format(fecha));
                editor.commit();
            }
            if (!oculto)
                instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        instanciaFragmentoListado.cambiarTextoCarga("Obteniendo cámaras del fichero KML descargado");
                    }
                });

            File kml = new File(context.getFilesDir() + "/camaras/CamarasMadrid.kml");
            InputStream inputStream = new FileInputStream(kml);
            SAXParser analizadorSAX = fabrica.newSAXParser();
            ManejadorXML manejadorXML = new ManejadorXML(instanciaFragmentoListado);
            analizadorSAX.parse(new InputSource(inputStream), manejadorXML);    // Ejecución del analizador
            camaras = manejadorXML.getResultado(); // Recoger los datos del analizador

            // Se necesita invocar al método actualizaListaCamaras() de FragmentoListado
            // Como ese método hace uso de vistas de la UI no se puede invocar directamente.
            // Se tiene que hacer mediante el método runOnUiThread() que tiene la actividad a la que pertenece el fragmento
            instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instanciaFragmentoListado.actualizaListaCamaras(camaras);
                }
            });
            if(oculto)
                instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Mostrando lista de cámaras descargada", Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Log.d("Errores SAX", "Se ha producido un error: " + e.toString());
        }
    }
}
