package es.epinanab.sensores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	/*private TextView acelerometro;
	
	private TextView magnetic;
	private TextView proximity;
	private TextView luminosidad;
	private TextView temperatura;
	private TextView gravedad;
	private TextView detecta;
	private TextView presion;*/
	private TextView giro;
	private TextView orientacion;

	float max_x = 0;
	float max_y = 0;
	float max_z = 0;

	DecimalFormat dosdecimales = new DecimalFormat("###.###");
	
	String estado = null;
	public static List<String> lista_estados = new ArrayList<String>(); 
	public static int rotation, before_rotation = 8;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Defino los botones
		//Button posicion = (Button) findViewById(R.id.posicion);
		Button tiempo = (Button) findViewById(R.id.tiempos);
		Button file = (Button) findViewById(R.id.file);
		Button grafico = (Button) findViewById(R.id.grafico);

		//Por ahora... TextView usado para colocar la lectura del archivo y mostrar
		orientacion = (TextView) findViewById(R.id.orientacion);
		
		//Posicin del celular -
		giro = (TextView) findViewById(R.id.giro);

		// Accedemos al servicio de sensores
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Boton que muestra el listado de los sensores disponibles
		tiempo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Parar_Sensores();
				
				Intent i = new Intent();
				i.setClass(MainActivity.this, TiemposDeSensor.class);
				i.putStringArrayListExtra("lista_estados", (ArrayList<String>) lista_estados);
				try {
					startActivity(i);
				} catch (Exception e) {
					Log.e("ERROR", "No se pudo iniciar la Actividad nueva" + e.toString());
				}
				
				
			}
		});
		
		// Boton que baja a unidad fsica el archivo con el listado de cambios del sensor
		file.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				MainActivity.this.bajarArchivo();				
				
			}
		});
		
		// Boton que mostraria un grfico con las variaciones del sensor
		grafico.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
								
				
			}
		});

	}

	// Metodo para iniciar el acceso a los sensores
	protected void Ini_Sensores() {

	/*	mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);*/

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_NORMAL);

	/*	mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
				SensorManager.SENSOR_DELAY_NORMAL);*/
	}

	// Metodo para parar la escucha de los sensores
	private void Parar_Sensores() {

		/*mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE));

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));*/

		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
/*
		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));*/
	}

	// Metodo que escucha el cambio de sensibilidad de los sensores
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	// Metodo que escucha el cambio de los sensores
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//String txt = "\n\nSensor: ";

		String txt = "";
	
		// Cada sensor puede lanzar un thread que pase por aqui
		// Para asegurarnos ante los accesos simultneos sincronizamos esto

		synchronized (this) {
			Log.d("sensor", event.sensor.getName());

			switch (event.sensor.getType()) {
		
			case Sensor.TYPE_ROTATION_VECTOR:
				
				// Creo objeto para saber como esta la pantalla
				Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay();
				rotation = display.getRotation();
								
								
				// El objeto devuelve 3 estados 0,1 y 3
				if (rotation == 0) {
					txt += "\n\n a Posicin: Vertical";
									
				} else if (rotation == 1) {
					txt += "\n\n a Posicin: Horizontal";
									
				} else if (rotation == 3) {
					txt += "\n\n a Posicin: Horizontal";
					
				}
				
				if (before_rotation != rotation){
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					String horario = sdf.format(new Date());
					lista_estados.add(txt+ " " + horario);
					before_rotation = rotation;
				}
						
				estado = txt;
				
				giro.setText(estado);
				 

				break;

	

			}

		}

	}

	public void bajarArchivo(){
		
		boolean mExternalStorageAvailable = false;
	    boolean mExternalStorageWriteable = false;
	    String state = Environment.getExternalStorageState();

	    // COMPROBACION DEL ALMACENAMIENTO EXTERNO
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        // Podremos leer y escribir en ella
	        mExternalStorageAvailable = mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        // En este caso solo podremos leer los datos
	        mExternalStorageAvailable = true;
	        mExternalStorageWriteable = false;
	    } else {
	        // No podremos leer ni escribir en ella
	        mExternalStorageAvailable = mExternalStorageWriteable = false;
	    }	
	    
	    
	    // CREACION DE ARCHIVOS PUBLICOS
	    if (mExternalStorageWriteable == true) {
	          
	        // Creamos una carpeta "MisImagenes" dentro del directorio "Pictures"
	        // Con el mtodo "mkdirs()" creamos el directorio si es necesario
	        File path = new File(Environment.getExternalStoragePublicDirectory(
	                Environment.DIRECTORY_DOWNLOADS), "SensorCapture");
	        if (!path.exists())
	        {
	        path.mkdirs();
	        }
	        
	        // Creamos un archivo dentro de la carpeta que hemos creado "prueba.txt"
	        File file = new File(path, "CapturasDeSensor.txt");
	          
	        // Comprobamos si el archivo que estamos creando ya existe
	        if (file.exists()) {
	            Log.d("ERROR", "El archivo ya existe");
	        } 
	             
	        // Escribimos algo en el archivo creado
	        try {
	         				        	
	        	//Se crea buffer para el append de los datos al arhivo. El mtodo FileWriter setea el modo
	        	//append
	        	BufferedWriter OSW = new BufferedWriter(new FileWriter(file, true));
	        				        	
	            Iterator<String> it = lista_estados.iterator();
	            while(it.hasNext()){
	            	OSW.append(it.next());
	            }
	            OSW.flush();
		        OSW.close();
		        lista_estados.clear();
		        /*Toast toast = Toast.makeText(getApplicationContext(), "El Archivo " + path + file.getName() + " ha sido actualizado", Toast.LENGTH_LONG);
		        toast.show();*/
	        } catch (FileNotFoundException e) {
	            Log.e("ERROR", "No ha sido posible crear el archivo" + e.toString());
	        } catch (IOException e) {
	            Log.e("ERROR", "No ha sido posible escribir en el archivo" 
	                    + e.toString());
	        }
			          
	    } else {
	        Log.d("ERROR", "No ha sido posible crear archivos/carpetas");
	    }
	    
	}
	
	@Override
	protected void onStop() {

		Parar_Sensores();
		
		//MainActivity.this.bajarArchivo();

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Parar_Sensores();
		
		//MainActivity.this.bajarArchivo();

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		Parar_Sensores();
		
		MainActivity.this.bajarArchivo();

		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub

		Ini_Sensores();

		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Ini_Sensores();

	}

}