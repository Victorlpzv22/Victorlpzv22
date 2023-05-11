package dam.camarasmadrid09.descarga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.FileUtils;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class DescargaKML extends AsyncTask<String, Void, String> {
  String respuesta;
  Context context;
  URL url;
  HttpURLConnection urlConnection;
  String kml;

  public DescargaKML(Context context){
    this.context = context;
  }

  @Override
  protected String doInBackground(String... urls) {
    try {
      url = new URL(urls[0]);
      urlConnection = (HttpURLConnection) url.openConnection();
      if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        urlConnection.disconnect();
        url = new URL(urlConnection.getHeaderField("Location"));
        urlConnection = (HttpURLConnection) url.openConnection();
      }
      if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        respuesta = "Correcto";
        InputStream is = urlConnection.getInputStream();
        final StringBuilder contenido = new StringBuilder();
        String linea;
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        kml = "";
        linea=bufferedReader.readLine();
        while (linea != null && !isCancelled()) {
          contenido.append(linea).append("\n");
          linea = bufferedReader.readLine();
        }
        kml = contenido.toString();
        writeFileOnInternalStorage(context, "CamarasMadrid.kml", kml);
        is.close();
        reader.close();
        bufferedReader.close();
        urlConnection.disconnect();
        SystemClock.sleep(3000);
      } else {
        respuesta = "Ha fallado la conexión a " + urls[0];
      }
    } catch (Exception e) {
      respuesta = "Se ha producido esta excepción: " + e.toString();
    }
    return respuesta;
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
      File dir = new File(mcoContext.getFilesDir(), "camaras");
      if(!dir.exists()){
        dir.mkdir();
      }

      try {
        File gpxfile = new File(dir, sFileName);
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(sBody);
        writer.flush();
        writer.close();
      } catch (Exception e) {}
    }
  }