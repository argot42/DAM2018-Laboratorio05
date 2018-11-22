package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap miMapa;
    private OnMapaListener mapaListener;
    private int tipoMapa;

    public MapaFragment() {}

    public interface OnMapaListener {
        public void coordenadasSeleccionadas(LatLng c);
    }

    public void setListener(OnMapaListener listener) { this.mapaListener = listener; }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mapaListener = (OnMapaListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnMapaListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View rootView = super.onCreateView(inflater, container, savedInstaceState);

        tipoMapa = GoogleMap.MAP_TYPE_NORMAL;
        Bundle argumentos = getArguments();
        if (argumentos != null) {
            tipoMapa = argumentos.getInt("tipo_mapa", GoogleMap.MAP_TYPE_NORMAL);
        }

        getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        miMapa = map;
        miMapa.setMapType(tipoMapa);

        try {
            miMapa.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e("LAB05", e.toString());
        }

        miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mapaListener.coordenadasSeleccionadas(latLng);
            }
        });
    }
}
