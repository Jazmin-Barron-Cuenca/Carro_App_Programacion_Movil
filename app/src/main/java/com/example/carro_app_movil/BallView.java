package com.example.carro_app_movil;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class BallView extends View implements SensorEventListener {
    private static final float RADIUS = 80f;
    private float x;
    private float y;
    private Paint paint;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private float referenceX;
    private float referenceY;

    private static boolean isCalibrated = false;

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        x = 0;
        y = 0;
        paint = new Paint();
        paint.setColor(Color.RED);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void onResume() {
        sensorManager.registerListener( this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        sensorManager.unregisterListener(this);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, RADIUS, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere hacer nada aquí
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (!isCalibrated) {
                // No se ha realizado la calibración, no realizar acciones
                return;
            }else{
                // Obtén los cambios de la orientación del dispositivo en los ejes X, Y y Z

                referenceX = event.values[0];
                referenceY = event.values[1];
                float deltaX = event.values[1];
                float deltaY = event.values[0];
                float deltaZ = event.values[2];

                // Calcula las nuevas coordenadas de la pelota según los cambios en la orientación
                x += deltaX * 100;
                y += deltaY * 100;

                // Limita las coordenadas dentro de los límites de la pantalla
                if (x < RADIUS) {
                    x = RADIUS;
                } else if (x > getWidth() - RADIUS) {
                    x = getWidth() - RADIUS;
                }

                if (y < RADIUS) {
                    y = RADIUS;
                } else if (y > getHeight() - RADIUS) {
                    y = getHeight() - RADIUS;
                }

                // Verifica los movimientos en cada dirección y ejecuta las acciones correspondientes
                if (deltaX > 0) {
                    // Movimiento hacia la derecha
                    // Ejecutar acción cuando se mueve hacia la derecha
                    // ...
                    Toast.makeText(getContext(), "derecha", Toast.LENGTH_SHORT).show();
                } else if (deltaX < 0) {
                    // Movimiento hacia la izquierda
                    // Ejecutar acción cuando se mueve hacia la izquierda
                    // ...
                    Toast.makeText(getContext(), "izquierda", Toast.LENGTH_SHORT).show();
                }

                if (deltaY > 0) {
                    // Movimiento hacia abajo
                    // Ejecutar acción cuando se mueve hacia abajo
                    // ...
                    Toast.makeText(getContext(), "abajo", Toast.LENGTH_SHORT).show();
                } else if (deltaY < 0) {
                    // Movimiento hacia arriba
                    // Ejecutar acción cuando se mueve hacia arriba
                    // ...
                    Toast.makeText(getContext(), "arriba", Toast.LENGTH_SHORT).show();
                }

                // Invalida la vista para actualizar la posición de la pelota
                invalidate();
            }
        }
        /*

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (!isCalibrated) {
                // No se ha realizado la calibración, no realizar acciones

                return;
            }else {

                float deltaX = event.values[0]; // Aceleración en el eje X
                float deltaY = event.values[1]; // Aceleración en el eje Y

                // Calcula las nuevas coordenadas de la pelota según la aceleración
                x += deltaX * 40;
                y += deltaY * 40;

                // Limita las coordenadas dentro de los límites de la pantalla
                if (x < RADIUS) {
                    x = RADIUS;
                } else if (x > getWidth() - RADIUS) {
                    x = getWidth() - RADIUS;
                }

                if (y < RADIUS) {
                    y = RADIUS;
                } else if (y > getHeight() - RADIUS) {
                    y = getHeight() - RADIUS;
                }

                // Verifica los movimientos en cada dirección y ejecuta las acciones correspondientes
                if (deltaX > 0) {
                    // Movimiento hacia la derecha
                    // Ejecutar acción cuando se mueve hacia la derecha
                    // ...
                    Toast.makeText(getContext(), "derecha", Toast.LENGTH_SHORT).show();
                } else if (deltaX < 0) {
                    // Movimiento hacia la izquierda
                    // Ejecutar acción cuando se mueve hacia la izquierda
                    // ...
                    Toast.makeText(getContext(), "izquierda", Toast.LENGTH_SHORT).show();
                }

                if (deltaY > 0) {
                    // Movimiento hacia abajo
                    // Ejecutar acción cuando se mueve hacia abajo
                    // ...
                    Toast.makeText(getContext(), "abajo", Toast.LENGTH_SHORT).show();
                } else if (deltaY < 0) {
                    // Movimiento hacia arriba
                    // Ejecutar acción cuando se mueve hacia arriba
                    // ...
                    Toast.makeText(getContext(), "arriba", Toast.LENGTH_SHORT).show();
                }


                // Invalida la vista para actualizar la posición de la pelota
                invalidate();

            }
          }      */



    }

    public void iniciar_sensor() {
        isCalibrated = true;
        Toast.makeText(getContext(), "sensor inciado", Toast.LENGTH_SHORT).show();
    }
    public void parar_sensor() {
        isCalibrated = false;
        Toast.makeText(getContext(), "sensor detenido", Toast.LENGTH_SHORT).show();
    }
}
