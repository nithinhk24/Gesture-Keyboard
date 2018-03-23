package com.example.nithin.androidgk;

/**
 * Created by nithin on 3/2/2018.
 */

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import umich.cse.yctung.androidlibsvm.LibSVM;


public class CreateDataset extends Activity implements SensorEventListener {

    LibSVM svm;
    public File appFolderPath;
    private boolean start = false;
    private boolean flag;
    //private int number = -1;
    private float[] values;
    private double[][] arrays,new_arrays;
    private TextView status, fileCountField;

    byte classno;
    private int fno, length = 0;
    private int probability;

    private String filename = "androidGK_dataset.txt";
    private String filepath = "data";
    File myExternalFile;
    StringBuilder recordedData;
    //Declare buttons & editText
    private Button startButton, trainButton;


    FileWriter mFileWriter;
    private Sensor accelSensor;
    private Sensor gyroSensor;
    private SensorManager SM;


    private EditText characterInput;
    //private EditText editText2;

    private String character = "a";
    //private String batch = "0";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dataset);
        // Create our Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        accelSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Register sensor Listener






        SM.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

        svm = new LibSVM();
        values = new float[6];

        //systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        //appFolderPath = systemPath + "data/"; // your datasets folder
        appFolderPath = getExternalFilesDir(filepath); // your datasets folder


        characterInput = (EditText) findViewById(R.id.charInput);
        //editText2 = (EditText) findViewById(R.id.editText2);
        status = (TextView) findViewById(R.id.messageTextView);
        //textview2 = (TextView) findViewById(R.id.textView2);
        fileCountField = (TextView) findViewById(R.id.classCountTextView);

        status.setText("");
        fileCountField.setText("");
        myExternalFile = new File(appFolderPath, filename);

        //Initialising buttons in the view
        //mDetect = (Button) findViewById(R.id.mDetect);
        startButton = (Button) findViewById(R.id.startButton);
        trainButton = (Button) findViewById(R.id.trainButton);




        //getting the bluetooth adapter value and calling checkBTstate function

        /**************************************************************************************************************************8
         *  Buttons are set up with onclick listeners so when pressed a method is called
         *  In this case send data is called with a value and a toast is made
         *  to give visual feedback of the selection made
         */

        startButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    flag = true;
                    /*if(editText1.getText().toString().matches("") && editText2.getText().toString().matches("")) {
                        Toast.makeText(getApplicationContext(), "Enter the values in fields", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }*/
                    if (characterInput.getText().toString().matches("")) {
                        Toast.makeText(getApplicationContext(), "Enter the value in 'Character' field", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                    /*else if (editText2.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(), "Enter the value in 'Batch' field", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }*/

                    if (flag) {
                        //number++;
                        arrays = new double[6][500];
                        new_arrays = new double[6][500];
                        character = characterInput.getText().toString();
                        classno = character.getBytes(StandardCharsets.US_ASCII)[0];
                        //batch = editText2.getText().toString();
                        //number = 0;
                        try {
                            mFileWriter = new FileWriter(myExternalFile, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("FileNotFoundException occured!");
                        }


                        //filename = "" + character + "_sample_" + batch + "_" + number + ".txt";

                        //textview4.setText(filename + " ...");
                        recordedData = new StringBuilder();
                        fno = 0;
                        length = 0;
                        status.setText("Recording values in file...");
                        start = true;

                    }


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (flag) {
                        start = false;
                        status.setText("predicting");
                        //textview4.setText("");

                        fno = 0;
                        double[] ref_values = linspace(0,arrays[0].length,arrays[0].length);
                        double[] new_ref_values = linspace(0,arrays[0].length,50);
                        for(int j = 0; j < 6; j++) {
                            try {
                                new_arrays[j] = linearInterp(ref_values, arrays[j], new_ref_values);
                                Log.d("After processing: ", "" + new_arrays[j] + "\n\n");
                            } catch (ArgumentOutsideDomainException e) {
                                e.printStackTrace();
                            }
                            Log.d("nithin man what is : ", "\n ");
                            for (int p = 0; p < new_arrays[j].length; p++) {
                                recordedData.append(" " + (++fno) + ":" + new_arrays[j][p]);
                                Log.d("", "" + new_arrays[j][p] + " ");
                            }
                        }
                        try {
                            mFileWriter.write(classno + recordedData.toString() + "\n");
                            Log.d("DATA: ", classno + recordedData.toString() + "\n");
                            mFileWriter.close();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Values for character '" + character + "' recorded successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                probability = 1;
                svm.train("-t 0 -c 0.01 -r 0.0 -m 200 -wi 0 -b " + probability + " " + appFolderPath + "/androidGK_dataset.txt " + appFolderPath + "/androidGK_model.model");
                status.setText("Model is learning your styles...");
                Toast.makeText(getApplicationContext(), "SVM Train has executed successfully!", Toast.LENGTH_SHORT).show();
                status.setText("");

            }
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            startButton.setEnabled(false);
        }

        /*functionDelete.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(myExternalFile == null)
                        Toast.makeText(getApplicationContext(), "No file has been created yet", Toast.LENGTH_SHORT).show();
                    else {
                        myExternalFile.delete();
                        Toast.makeText(getApplicationContext(), "File  " + filename + " deleted", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });*/

    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            values[0] = event.values[0];
            values[1] = event.values[1];
            values[2] = event.values[2];


        }else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            values[3] = event.values[0];
            values[4] = event.values[1];
            values[5] = event.values[2];

            if(start)
            {
                Log.d("Length: ", "" + length);
                for(int i=0;i<6;i++){
                    arrays[i][length] = values[i];
                    Log.d("Sensor Data: ", " " + values[i]);
                }

                length++;

            }
        }
    }


    public static double[] linspace(double min, double max, int points) {
        double[] d = new double[points];
        for (int i = 0; i < points; i++){
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    public static double[] linearInterp(double[] x, double[] y, double[] xi) throws ArgumentOutsideDomainException {
        LinearInterpolator li = new LinearInterpolator(); // or other interpolator
        PolynomialSplineFunction psf = li.interpolate(x, y);

        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            yi[i] = psf.value(xi[i]);
        }
        return yi;
    }

    /*public void readLogcat(Context context, String title){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(MainActivity.processId) && line.contains("LibSvm")) {
                    if (line.contains("=======")){
                        log.append("==================\n");
                    } else if (line.contains("Start of SVM")){
                        log.append(line.substring(line.indexOf("Start"))+"\n");
                    } else if (line.contains("End of SVM")) {
                        log.append(line.substring(line.indexOf("End"))+"\n");
                    } else {
                        int indexOfProcessId = line.lastIndexOf(MainActivity.processId);
                        String newLine = line.substring(indexOfProcessId);
                        log.append(newLine+"\n\n");
                    }
                }
            }
            Toast.makeText(context, log.toString(),Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, "readLogcat: failed to read from logcat logger.");
        }
    }*/

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

