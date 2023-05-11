package dam.camarasmadrid09;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dam.camarasmadrid09.viewmodel.MenuViewModel;

/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class FragmentoDetalle extends Fragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_ALL_COORDENADAS = "allCordenadas";
    private static final String ARG_COORDENADA = "coordenadas";
    private static final String ARG_URL = "url";
    private String mParamNombre, mParamCoordenada, mParamUrl;
    private String mParamAllCordenadas[];
    private MenuViewModel menuViewModel;
    ImageView ivImagen;
    private String mostrar = "mostrarUna";
    View view;
    LinearLayout layoutPBCircular;
    FragmentContainerView contenedorDetalle;
    DescargaImagen descargaIm;
    TextView textoError;
    Button buttonUbicacion, buttonCancelar, buttonUpdate;
    public FragmentoDetalle() {
    }

    public static FragmentoDetalle newInstance(int param1, String param2) {
        FragmentoDetalle fragment = new FragmentoDetalle();
        Bundle args = new Bundle();
        args.putInt(ARG_NOMBRE, param1);
        args.putString(ARG_COORDENADA, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuViewModel = new ViewModelProvider(getActivity()).get(MenuViewModel.class);
        //setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParamNombre = getArguments().getString(ARG_NOMBRE);
            mParamCoordenada = getArguments().getString(ARG_COORDENADA);
            mParamUrl = getArguments().getString(ARG_URL);
            mParamAllCordenadas = getArguments().getStringArray(ARG_ALL_COORDENADAS);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmento_detalle, container, false);
        layoutPBCircular = view.findViewById(R.id.layoutPBCircular);
        contenedorDetalle = view.findViewById(R.id.contenedorDetalleCamaras);
        ivImagen = view.findViewById(R.id.imagen);
        buttonUbicacion = view.findViewById(R.id.buttonUbicacion);
        buttonCancelar = view.findViewById(R.id.buttonClose);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        descargaIm = new DescargaImagen();
        buttonUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("coordenada", mParamCoordenada);
                intent.putExtra("nombre", mParamNombre);
                intent.putExtra("allCordenadas", mParamAllCordenadas);
                intent.putExtra("mostrar", menuViewModel.getModoMostrar());
                intent.putExtra("mostrarUbicacion", menuViewModel.getMostrarUbicacion());
                startActivity(intent);
            }
        });
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentoListado fragmentoListado = (FragmentoListado) fm.findFragmentById(R.id.contenedorListaCamaras);
                fragmentoListado.ocultarDetalle();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescargaImagen descargaUp = new DescargaImagen();
                int inicioSrc = mParamUrl.indexOf("src=") + 4; // Obtener la posición inicial del atributo src
                int finSrc = mParamUrl.indexOf(" ", inicioSrc); // Obtener la posición final del atributo src
                String urlImagen = mParamUrl.substring(inicioSrc, finSrc); // Extraer la URL de la imagen
                descargaUp.execute(urlImagen);
            }
        });
        int inicioSrc = mParamUrl.indexOf("src=") + 4; // Obtener la posición inicial del atributo src
        int finSrc = mParamUrl.indexOf(" ", inicioSrc); // Obtener la posición final del atributo src
        String urlImagen = mParamUrl.substring(inicioSrc, finSrc); // Extraer la URL de la imagen
        descargaIm.execute(urlImagen);
        return view;
    }

    private class DescargaImagen extends AsyncTask<String, Void, String> {
        String respuesta;
        Bitmap imagenBitmap;

        @Override
        protected String doInBackground(String... urls) {
            try {
                ivImagen.post(new Runnable() {
                    @Override
                    public void run() {
                        ivImagen.setVisibility(View.GONE);
                    }
                });
                layoutPBCircular.post(new Runnable() {
                    @Override
                    public void run() {
                        layoutPBCircular.setVisibility(View.VISIBLE);
                    }
                });

                SystemClock.sleep(500);
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    url = new URL(urlConnection.getHeaderField("Location"));
                    urlConnection.disconnect();
                    urlConnection = (HttpURLConnection) url.openConnection();
                }
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = urlConnection.getInputStream();
                    imagenBitmap = BitmapFactory.decodeStream(is, null, null);
                    is.close();
                }else{
                    textoError.setText("Error en conexion la url es: "+urls[0]);
                }
                urlConnection.disconnect();
                } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            return respuesta;
        }
        @Override
        protected void onPostExecute(final String resultado) {
            layoutPBCircular.setVisibility(View.GONE);
            ivImagen.setImageBitmap(imagenBitmap);
            ivImagen.setVisibility(View.VISIBLE);
        }
    }
}


