package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap miMapa;
    private OnMapaListener mapaListener;
    private int tipoMapa;
    private int idReclamo;
    private String tipoReclamo;

    private ReclamoDao reclamoDao;

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

        tipoMapa = 0;
        idReclamo = -1;
        tipoReclamo = null;
        Bundle argumentos = getArguments();
        if (argumentos != null) {
            tipoMapa = argumentos.getInt("tipo_mapa", 0);
            idReclamo = argumentos.getInt("idReclamo", -1);
            tipoReclamo = argumentos.getString("tipo_reclamo", null);
        }

        reclamoDao = MyDatabase.getInstance(this.getActivity()).getReclamoDao();

        getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        miMapa = map;

        try {
            miMapa.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e("LAB05", e.toString());
            return;
        }

        switch(tipoMapa) {
            case 1: {
                miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        mapaListener.coordenadasSeleccionadas(latLng);
                    }
                });
                break;
            }

            case 2: {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        final List<Reclamo> listaReclamos = reclamoDao.getAll();

                        if (listaReclamos.isEmpty()) return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                for (Reclamo reclamo : listaReclamos) {
                                    LatLng point = new LatLng(reclamo.getLatitud(), reclamo.getLongitud());

                                    builder.include(point);

                                    miMapa.addMarker(new MarkerOptions()
                                            .position(point)
                                            .title(reclamo.getReclamo())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    );
                                }

                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.10);

                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                miMapa.animateCamera(cu);
                            }
                        });
                    }
                };

                Thread t = new Thread(r);
                t.start();
                break;
            }

            case 3: {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        final Reclamo reclamo = reclamoDao.getById(idReclamo);

                        if (reclamo == null) return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng point = new LatLng(reclamo.getLatitud(), reclamo.getLongitud());

                                miMapa.addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(reclamo.getReclamo())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                );

                                miMapa.addCircle(new CircleOptions()
                                        .center(point)
                                        .radius(500)
                                        .strokeColor(Color.RED)
                                        .fillColor(0x22ff000d)
                                        .strokeWidth(5)
                                );

                                miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f));
                            }
                        });
                    }
                };

                Thread t = new Thread(r);
                t.start();
                break;
            }

            case 4: {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        final List<Reclamo> reclamoList = reclamoDao.getAll();

                        if (reclamoList.isEmpty()) return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<LatLng> pointList = new ArrayList<>();
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                for (Reclamo reclamo : reclamoList) {
                                    LatLng point = new LatLng(reclamo.getLatitud(), reclamo.getLongitud());

                                    pointList.add(point);

                                    builder.include(point);
                                }

                                HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                        .data(pointList)
                                        .build();

                                miMapa.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.10);

                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                miMapa.animateCamera(cu);
                            }
                        });
                    }
                };

                Thread t = new Thread(r);
                t.start();
                break;
            }

            case 5: {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (tipoReclamo == null) return;

                        final List<Reclamo> reclamoList = reclamoDao.getByTipo(tipoReclamo);

                        if (reclamoList.isEmpty()) return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<LatLng> pointList = new ArrayList<>();
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                for (Reclamo reclamo : reclamoList) {
                                    LatLng point = new LatLng(reclamo.getLatitud(), reclamo.getLongitud());

                                    miMapa.addMarker(new MarkerOptions()
                                            .position(point)
                                            .title(reclamo.getReclamo())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    );

                                    pointList.add(point);

                                    builder.include(point);
                                }

                                miMapa.addPolyline(new PolylineOptions()
                                        .addAll(pointList)
                                        .width(5)
                                        .color(Color.RED)
                                );

                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.10);

                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                miMapa.animateCamera(cu);
                            }
                        });
                    }
                };

                Thread t = new Thread(r);
                t.start();
                break;
            }
        }

    }
}
