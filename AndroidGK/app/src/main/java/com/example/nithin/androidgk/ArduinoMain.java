/**
 * Created by nithin on 3/2/2018.
 */

package com.example.nithin.androidgk;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import com.example.nithin.androidgk.hinter;

import umich.cse.yctung.androidlibsvm.LibSVM;

//import static com.example.nithin.androidgk.CreateDataset.linearInterp;
//import static com.example.nithin.androidgk.CreateDataset.linspace;

public class ArduinoMain extends Activity implements SensorEventListener {
    private boolean start = false;
    LibSVM svm;
    File appFolderPath;
    private float[] values;
    private double[][] arrays,new_arrays;
    byte classno;
    private int fno, length = 0;
    private int probability;

    private String filename = "androidGK_test.txt";
    private String filepath = "data";
    File mytestFile;
    StringBuilder recordedData;
    private String character = " ";
    FileWriter mFileWriter;

    private TextView status, outputField, connectionStatus;
    Button predictButton;
    private CheckBox speak,autocorrect;
    private Sensor accelSensor;
    private Sensor gyroSensor;
    private SensorManager SM;
    //Declare buttons & editText




    //Memeber Fields
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // UUID service - This is the type of Bluetooth device that the BT module is
    // It is very likely yours will be the same, if not google UUID for your manufacturer
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module
    public String newAddress = null;
    public String deviceName = null;

    TextToSpeech t1;
    hinter mHinter;
    static StringBuilder last_word;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arduino_main);

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
        appFolderPath = getExternalFilesDir(filepath); // your datasets folder
        mytestFile = new File(appFolderPath, "androidGK_test.txt");
        // Assign TextView

        last_word = new StringBuilder();

        status = (TextView) findViewById(R.id.status);
        outputField = (TextView) findViewById(R.id.output);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        speak = (CheckBox) findViewById(R.id.speak);
        autocorrect = (CheckBox) findViewById(R.id.autocorrect);
        status.setText("");
        outputField.setText("");
        Intent mIntent = getIntent();
        deviceName = mIntent.getStringExtra("device_info");
        connectionStatus.setText("Connected to: " + deviceName.split("\n")[0]);


        //Initialising buttons in the view
        //mDetect = (Button) findViewById(R.id.mDetect);
        predictButton = (Button) findViewById(R.id.predict);

        //getting the bluetooth adapter value and calling checkBTstate function
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        autocorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    File[] necessary_File = appFolderPath.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if (s.matches("english.txt")) {
                                Log.d("DEBUGGING", s + ":TRUE");
                                return true;
                            } else {
                                Log.d("DEBUGGING", s + ":FALSE");
                                return false;
                            }
                        }
                    });
                    if (necessary_File.length == 0) {
                        Log.d("DEBUGGING", "NECESSARY_FILE = NULL");
                        AlertDialog alertDialog = new AlertDialog.Builder(ArduinoMain.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("English dictionary file not found!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        autocorrect.setChecked(false);
                                    }
                                });
                        alertDialog.show();
                    } else {
                        mHinter = new hinter();
                        mHinter.words = hinter.load_english_dict(new File(appFolderPath, "english.txt"));
                    }
                }
            }
        });
        /**************************************************************************************************************************8
         *  Buttons are set up with onclick listeners so when pressed a method is called
         *  In this case send data is called with a value and a toast is made
         *  to give visual feedback of the selection made
         */

        predictButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    arrays = new double[6][500];
                    new_arrays = new double[6][500];
                    classno = character.getBytes(StandardCharsets.US_ASCII)[0];
                    //batch = editText2.getText().toString();
                    //number = 0;
                    try {
                        mFileWriter = new FileWriter(mytestFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("FileNotFoundException occured!");
                    }


                    //filename = "" + character + "_sample_" + batch + "_" + number + ".txt";

                    //textview4.setText(filename + " ...");
                    recordedData = new StringBuilder();
                    fno = 0;
                    length = 0;

                    status.setText("Recording values...");
                    clearOutput();
                    start = true;

                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
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
                    Log.d(MainActivity.TAG, "==================\nStart of SVM PREDICT\n==================");
                    probability = 1;
                    svm.predict("-b " + probability + " " + appFolderPath + "/androidGK_test.txt " + appFolderPath + "/androidGK_model.model " + appFolderPath + "/output.txt");
                    String message = displayOutput();
                    sendData(message);
                    if(speak.isChecked())
                        t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                    Log.d(MainActivity.TAG, "==================\nEnd of SVM PREDICT\n==================");
                    //readLogcat(getApplicationContext(), "SVM-Predict Results");

                }
                return true;
            }
        });


    }

    public void clearOutput(){
        File output = new File(appFolderPath + "/androidGK_test.txt");
        FileWriter outputWriter = null;
        try{
            outputWriter = new FileWriter(output);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try{
            outputWriter.write("");
            outputWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String displayOutput(){
        File output = new File(appFolderPath + "/output.txt");
        Scanner mScanner = null;
        //FileReader outputReader = null;
        char outputLetter = '\0';
        byte outputByte =0;
        int i = 0;
        try{
            //outputReader = new FileReader(output);
            mScanner = new Scanner(output);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        if(mScanner.hasNext()) {
            if (probability == 0) {
                outputByte = Byte.parseByte(mScanner.nextLine());
                Log.d("byte val", "" + outputByte);
                outputLetter = (char) outputByte;
            } else if (probability == 1) {
                mScanner.nextLine();
                if (mScanner.hasNext()) {
                    outputByte = Byte.parseByte(mScanner.nextLine().split(" ")[0]);
                    Log.d("byte val", "" + outputByte);
                    outputLetter = (char) outputByte;
                }
            }
        }
        if(Character.isLowerCase(outputLetter) && autocorrect.isChecked()) {
            Log.d("EXACT PREDICTED CHAR", "" + outputLetter);
            char cross_calc_char = mHinter.most_probable_letter(output, last_word.toString());
            Log.d("CROSS CALCULATED C VAL", "" + cross_calc_char);
            if (cross_calc_char != '\0') {
                outputLetter = cross_calc_char;
                Log.d("CROSS CALCULATED CHAR", "" + outputLetter);
            }

            if (outputByte == 32 || outputByte == 10 || outputByte == 12)
                last_word.delete(0, last_word.length());
            else
                last_word.append(outputLetter);

            Log.d("After concatenation", last_word.toString());
        }

        status.setText("predicted letter");
        String outputLetterInString = String.valueOf(outputLetter);
        outputField.setText(outputLetterInString);
        mScanner.close();
        return outputLetterInString;
    }


    @Override
    public void onResume() {
        super.onResume();

        File[] necessary_File = appFolderPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                 if(s.matches("androidGK_model.model")) {
                     Log.d("DEBUGGING",s + ":TRUE");
                     return true;
                 }
                 else {
                     Log.d("DEBUGGING",s + ":FALSE");
                     return false;
                 }
            }
        });
        if(necessary_File.length == 0)
        {
            Log.d("DEBUGGING","NECESSARY_FILE = NULL");
            AlertDialog alertDialog = new AlertDialog.Builder(ArduinoMain.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Necessary files not found!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(ArduinoMain.this, MainActivity.class);
                            startActivity(i);
                        }
                    });
            alertDialog.show();
        }

        // connection methods are best here in case program goes into the background etc

        //connection status shows connection is re-establishing
        connectionStatus.setText("Connected to: " + deviceName.split("\n")[0] + "(re-establishing)");
        //Get MAC address from MainActivity
        Intent intent = getIntent();
        newAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        // Set up a pointer to the remote device using its address.
        BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);

        //Attempt to create a bluetooth socket for comms
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e1) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
        }

        // Establish the connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();        //If IO exception occurs attempt to close socket
            } catch (IOException e2) {
                Toast.makeText(getBaseContext(), "ERROR - Could not close Bluetooth socket", Toast.LENGTH_SHORT).show();
            }
        }

        // Create a data stream so we can talk to the device
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth outstream", Toast.LENGTH_SHORT).show();
        }
        connectionStatus.setText("Connected to: " + deviceName.split("\n")[0]);
        //When activity is resumed, attempt to send a piece of junk data ('x') so that it will fail if not connected
        // i.e don't wait for a user to press button to recognise connection failure
    }

    @Override
    public void onPause() {
        super.onPause();
        //Pausing can be the end of an app if the device kills it or the user doesn't open it again
        //close all connections so resources are not wasted


        //Close BT socket to device
        try     {
            btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(getBaseContext(), "ERROR - Failed to close Bluetooth socket", Toast.LENGTH_SHORT).show();
        }
    }
    //takes the UUID and creates a comms socket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    //same as in device list activity
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "ERROR - Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Method to send data
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            //attempt to place data on the outstream to the BT device
            outStream.write(msgBuffer);
        } catch (IOException e) {
            //if the sending fails this is most likely because device is no longer there
            Toast.makeText(getBaseContext(), "ERROR - Device not found", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                for(int i=0;i<6;i++){
                    arrays[i][length] = values[i];
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
