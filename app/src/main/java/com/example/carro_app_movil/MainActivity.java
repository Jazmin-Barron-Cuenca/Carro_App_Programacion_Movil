package com.example.carro_app_movil;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingenieriajhr.blujhr.BluJhr;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_PERMISSION_BLUETOOTH = 1;
    BluJhr blue;
    List<String> requiredPermissions;
    ArrayList<String> devicesBluetooth = new ArrayList<String>();
    LinearLayout viewConn;
    ListView listDeviceBluetooth;
    Button buttonSend;
    TextView consola;
    EditText edtTx;

    private BallView ballView;
    public TextView textViewX;
    public TextView textViewY;
    public TextView textViewZ;

    public TextView titulo;

    public float ValueX, ValueY, ValueZ;
    public Handler handler;
    public Runnable sendRunnable;
    public SensorManager sensorManager;
    public Sensor gyroscope, acelerometro;

    public  Boolean bluecheckk =false;

    public  Boolean controlGYRO=false;


    private ImageButton btnarriba, btnabajo, btnizquierda, btnderecha, btnstop, btnauto;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Verificar si los permisos están concedidos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_PERMISSION_BLUETOOTH);
        } else {

            // El permiso ya está concedido, puedes realizar las operaciones necesarias
            Toast.makeText(this, "BT CONCEDIDO", Toast.LENGTH_SHORT).show();

        }

        blue = new BluJhr(this);
        blue.onBluetooth();


        // Inicializar el sensor del gyro
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            // gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        }

        listDeviceBluetooth = findViewById(R.id.listDeviceBluetooth);
        viewConn = findViewById(R.id.viewConn);
        buttonSend = findViewById(R.id.buttonSend);
        consola = findViewById(R.id.consola);
        edtTx = findViewById(R.id.edtTx);

        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);
        titulo = findViewById(R.id.textViewtitulo);

        listDeviceBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!devicesBluetooth.isEmpty()){
                    blue.connect(devicesBluetooth.get(i));
                    blue.setDataLoadFinishedListener(new BluJhr.ConnectedBluetooth() {
                        @Override
                        public void onConnectState(@NonNull BluJhr.Connected connected) {
                            if (connected == BluJhr.Connected.True){
                                Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_SHORT).show();
                                listDeviceBluetooth.setVisibility(View.GONE);
                                titulo.setText("Controla el Carro");
                                viewConn.setVisibility(View.VISIBLE);

                                // Obtener el BluetoothSocket después de la conexión exitosa
                                UUID uuid = blue.getMyUUID();
                                // Obtener el UUID del BluetoothSocket
                                //Toast.makeText(MainActivity.this, "::"+uuid.toString(), Toast.LENGTH_SHORT).show();
                                bluecheckk =true;
                                // Configurar el envío constante de la coordenada X del acelerómetro


                                rxReceived();
                            }else{
                                if (connected == BluJhr.Connected.Pending){
                                    Toast.makeText(getApplicationContext(),"conectando",Toast.LENGTH_SHORT).show();
                                }else{
                                    if (connected == BluJhr.Connected.False){
                                        Toast.makeText(getApplicationContext(),"No conectado",Toast.LENGTH_SHORT).show();
                                        bluecheckk =false;
                                        controlGYRO =false;
                                    }else{
                                        if (connected == BluJhr.Connected.Disconnect){
                                            Toast.makeText(getApplicationContext(),"desconectado",Toast.LENGTH_SHORT).show();
                                            listDeviceBluetooth.setVisibility(View.VISIBLE);
                                            titulo.setText("Lista de sipositivos bluetooth");
                                            viewConn.setVisibility(View.GONE);
                                            bluecheckk =false;
                                            controlGYRO =false;
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blue.bluTx(edtTx.getText().toString());
            }
        });

        buttonSend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

        ballView = findViewById(R.id.ballView);
        Button btniniciar = findViewById(R.id.btniniciar);
        btniniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlGYRO=true;
                ballView.iniciar_sensor();
            }
        });


        Button btnparar = findViewById(R.id.btnParar);
        btnparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlGYRO = false;
                ballView.parar_sensor();
            }
        });

        handler = new Handler();
        sendRunnable = new Runnable() {
            @Override
            public void run() {

                if (bluecheckk ){
                    Enviarordencarro();
                    handler.postDelayed(this, 500); //inicial 1 miliseguo// Intervalo de envío (en milisegundos)
                }else {
                    //Toast.makeText(MainActivity.this, "Bluetooth desconectado", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this, 500); //inicial 1 miliseguo// Intervalo de envío (en milisegundos)
                }


            }
        };

        btnarriba = findViewById(R.id.btnarriba);
        btnarriba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluecheckk){
                    blue.bluTx("a");
                }
            }
        });

        btnabajo = findViewById(R.id.btnabajo);
        btnabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluecheckk){
                    blue.bluTx("t");
                }
            }
        });

        btnderecha = findViewById(R.id.btnderecha);
        btnderecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluecheckk){
                    blue.bluTx("d");
                }
            }
        });

        btnizquierda = findViewById(R.id.btnIzquierda);
        btnizquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluecheckk){
                    blue.bluTx("i");
                }
            }
        });

        btnstop = findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluecheckk){
                    blue.bluTx("s");
                }
            }
        });


    }


    public void Enviarordencarro(){
        if (controlGYRO){
            String Envio = "";

            //calibracion
            if(ValueX >=0 && ValueX <3.5){
                Envio ="a";

            } else if (ValueX <=0 &&  ValueX >-3.5 ) {
                Envio ="t";

            } else if (ValueX >= 5) {
                Envio ="i";

            }
            else if (ValueX <= -5) {
                Envio ="d";

            }
            else{
                Envio ="s";

            }
            blue.bluTx(Envio);

        }else{
            Toast.makeText(this, "desactivado", Toast.LENGTH_SHORT).show();
        }

    }



    private void rxReceived() {

        blue.loadDateRx(new BluJhr.ReceivedData() {
            @SuppressLint("SetTextI18n")
            @Override
            public void rxDate(@NonNull String s) {
                consola.setText(consola.getText().toString()+s);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            blue.initializeBluetooth();
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth();
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth();
        }else{
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth();
                if (!devicesBluetooth.isEmpty()){

                    ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,devicesBluetooth);
                    listDeviceBluetooth.setAdapter(adapter);

                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show();
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        ballView.onResume();

        handler.post(sendRunnable);



    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        ballView.onPause();
        handler.removeCallbacks(sendRunnable);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere hacer nada aquí
    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // Rotación en el eje X
            float y = event.values[1]; // Rotación en el eje Y
            float z = event.values[2]; // Rotación en el eje Z

            // Actualizar los TextViews con los valores del giroscopio
            textViewX.setText("X: " + x);
            textViewY.setText("Y: " + y);
            textViewZ.setText("Z: " + z);

            ValueX =  event.values[0];
            ValueY =  event.values[1];


        }
    }


}
