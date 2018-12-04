package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

import static android.app.Activity.RESULT_OK;

public class NuevoReclamoFragment extends Fragment {

    public interface OnNuevoLugarListener {
        public void obtenerCoordenadas();
    }

    public void setListener(OnNuevoLugarListener listener) {
        this.listener = listener;
    }

    static final int REQUEST_IMAGE_SAVE = 2;

    private Reclamo reclamoActual;
    private ReclamoDao reclamoDao;

    private EditText reclamoDesc;
    private EditText mail;
    private Spinner tipoReclamo;
    private TextView tvCoord;
    private Button buscarCoord;
    private Button btnGuardar;
    private Button btnFotoReclamo;
    private ImageView imgFotoReclamo;
    private Button btnAudioReclamo;
    private Button btnEscucharAudioReclamo;

    private OnNuevoLugarListener listener;

    private String pathFoto;

    private MediaRecorder mRecorder = null;
    private boolean audioPressed = false;
    private MediaPlayer mPlayer = null;
    private boolean audioPlaying = false;
    private String pathAudio;

    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    public NuevoReclamoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        reclamoDao = MyDatabase.getInstance(this.getActivity()).getReclamoDao();

        View v = inflater.inflate(R.layout.fragment_nuevo_reclamo, container, false);

        reclamoDesc = (EditText) v.findViewById(R.id.reclamo_desc);
        mail= (EditText) v.findViewById(R.id.reclamo_mail);
        tipoReclamo= (Spinner) v.findViewById(R.id.reclamo_tipo);
        tvCoord= (TextView) v.findViewById(R.id.reclamo_coord);
        buscarCoord= (Button) v.findViewById(R.id.btnBuscarCoordenadas);
        btnGuardar= (Button) v.findViewById(R.id.btnGuardar);
        btnFotoReclamo = (Button) v.findViewById(R.id.btnFotoReclamo);
        imgFotoReclamo = (ImageView) v.findViewById(R.id.imgFotoReclamo);
        btnAudioReclamo = (Button) v.findViewById(R.id.btnAudioReclamo);
        btnEscucharAudioReclamo = (Button) v.findViewById(R.id.btnEscucharAudioReclamo);

        tipoReclamoAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReclamo.setAdapter(tipoReclamoAdapter);

        int idReclamo =0;
        if(getArguments()!=null)  {
            idReclamo = getArguments().getInt("idReclamo",0);
        }

        cargarReclamo(idReclamo);

        boolean edicionActivada = !tvCoord.getText().toString().equals("0;0");
        reclamoDesc.setEnabled(edicionActivada );
        mail.setEnabled(edicionActivada );
        tipoReclamo.setEnabled(edicionActivada);
        btnGuardar.setEnabled(edicionActivada);
        btnFotoReclamo.setEnabled(edicionActivada);
        imgFotoReclamo.setEnabled(edicionActivada);
        btnAudioReclamo.setEnabled(edicionActivada);
        btnEscucharAudioReclamo.setEnabled(edicionActivada);

        buscarCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.obtenerCoordenadas();
            }
        });

        btnFotoReclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();

                    } catch(IOException e) {
                        Log.e("LAB06", e.toString());
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(
                                getActivity(),
                                "ar.edu.utn.frsf.isi.dam.laboratorio05.fileprovider",
                                photoFile
                        );

                        i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(i, REQUEST_IMAGE_SAVE);
                    }
                }
            }
        });

        btnAudioReclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!audioPressed) {
                    btnAudioReclamo.setText("Detener Grabación");
                    startRecording();

                } else {
                    btnAudioReclamo.setText("Comenzar Grabación");
                    stopRecording();
                }

                audioPressed = !audioPressed;
            }
        });

        btnEscucharAudioReclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathAudio == null || pathAudio.equals("")) { return; }
                if (audioPressed) { return; }

                if (audioPlaying) {
                    btnEscucharAudioReclamo.setText("Escuchar");
                    stopPlaying();

                } else {
                    btnEscucharAudioReclamo.setText("Detener");
                    startPlaying();
                }

                audioPlaying = !audioPlaying;
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrUpdateReclamo();
            }
        });
        return v;
    }

    private void cargarReclamo(final int id){
        if( id >0){
            Runnable hiloCargaDatos = new Runnable() {
                @Override
                public void run() {
                    reclamoActual = reclamoDao.getById(id);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mail.setText(reclamoActual.getEmail());
                            tvCoord.setText(reclamoActual.getLatitud()+";"+reclamoActual.getLongitud());
                            reclamoDesc.setText(reclamoActual.getReclamo());

                            String imagepath = reclamoActual.getImagePath();
                            if (imagepath != null && !imagepath.equals("")) {
                                pathFoto = imagepath;
                                Bitmap imageThumbnail = BitmapFactory.decodeFile(imagepath);
                                imgFotoReclamo.setImageBitmap(imageThumbnail);
                            }

                            String audiopath = reclamoActual.getAudioPath();
                            if (audiopath != null && !audiopath.equals("")) {
                                pathAudio = audiopath;
                            }

                            Reclamo.TipoReclamo[] tipos= Reclamo.TipoReclamo.values();
                            for(int i=0;i<tipos.length;i++) {
                                if(tipos[i].equals(reclamoActual.getTipo())) {
                                    tipoReclamo.setSelection(i);
                                    break;
                                }
                            }
                        }
                    });
                }
            };
            Thread t1 = new Thread(hiloCargaDatos);
            t1.start();
        }else{
            String coordenadas = "0;0";
            if(getArguments()!=null) coordenadas = getArguments().getString("latLng","0;0");
            tvCoord.setText(coordenadas);
            reclamoActual = new Reclamo();
        }

    }

    private void saveOrUpdateReclamo(){
        reclamoActual.setEmail(mail.getText().toString());
        reclamoActual.setReclamo(reclamoDesc.getText().toString());
        reclamoActual.setTipo(tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition()));

        if (pathFoto != null && !pathFoto.equals("")) {
            reclamoActual.setImagePath(pathFoto);
        }

        if (pathAudio != null && !pathAudio.equals("")) {
            reclamoActual.setAudioPath(pathAudio);
        }

        if(tvCoord.getText().toString().length()>0 && tvCoord.getText().toString().contains(";")) {
            String[] coordenadas = tvCoord.getText().toString().split(";");
            reclamoActual.setLatitud(Double.valueOf(coordenadas[0]));
            reclamoActual.setLongitud(Double.valueOf(coordenadas[1]));
        }
        Runnable hiloActualizacion = new Runnable() {
            @Override
            public void run() {

                if(reclamoActual.getId()>0) reclamoDao.update(reclamoActual);
                else reclamoDao.insert(reclamoActual);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // limpiar vista
                        mail.setText(R.string.texto_vacio);
                        tvCoord.setText(R.string.texto_vacio);
                        reclamoDesc.setText(R.string.texto_vacio);
                        imgFotoReclamo.setImageResource(0);
                        pathFoto = "";
                        getActivity().getFragmentManager().popBackStack();
                    }
                });
            }
        };
        Thread t1 = new Thread(hiloActualizacion);
        t1.start();
    }

    private File createImageFile() throws IOException {
        String  timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image =  File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                dir             /* directory */
        );

        pathFoto = image.getAbsolutePath();
        return image;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        pathAudio = generateAudioFilename();
        mRecorder.setOutputFile(pathAudio);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("LAB06", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private String generateAudioFilename() {
        String path = getActivity().getExternalCacheDir().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String suffix = ".3gp";

        Log.d("LAB06", path + "/" + timeStamp + suffix);

        return path + "/" + timeStamp + suffix;
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(pathAudio);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LAB06", "prepare() failed");
        }

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnEscucharAudioReclamo.setText("Escuchar");
            }
        });
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_SAVE && resultCode == RESULT_OK) {
            Log.d("LAB06", "picture taken, maybe");

            Bitmap imageThumbnail = BitmapFactory.decodeFile(pathFoto);
            imgFotoReclamo.setImageBitmap(imageThumbnail);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}