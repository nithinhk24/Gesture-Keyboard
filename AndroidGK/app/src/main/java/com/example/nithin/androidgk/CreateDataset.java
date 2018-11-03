package com.example.nithin.androidgk;

/**
 * Created by nithin on 3/2/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


import umich.cse.yctung.androidlibsvm.LibSVM;


public class CreateDataset extends Activity implements SensorEventListener {

    LibSVM svm;
    TableLayout mTableLayout;
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
    Scanner datasetReader;
    StringBuilder recordedData, detailssb;
    //Declare buttons & editText
    private Button startButton, trainButton;


    FileWriter mFileWriter;
    private Sensor accelSensor;
    private Sensor gyroSensor;
    private SensorManager SM;

    private CheckBox showDetails;
    private EditText characterInput;
    //private EditText editText2;

    private String character = "a";
    //private String batch = "0";
    private TextView classname;
    private TextView representation;
    private TextView noClass;
    private LayoutInflater inflater;
    //private static TreeMap<String,Integer> dataMap;
    //private static final String[] TABLE_HEADERS = { "Class", "Representation", "Samples"};
    //private static final String[][] DATA_TO_SHOW = { { "a", "97", "10" }, { "b", "98", "20" } };
    private int[] details;
    private File detailsFile;
    FileWriter datasetWriter;

    //private BufferedReader detailsReader;

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

        mTableLayout = (TableLayout) findViewById(R.id.datasetTable);
        //tableView.setColumnCount(3);
        //TableColumnWeightModel columnModel = new TableColumnWeightModel(3);
        //columnModel.setColumnWeight(1, 1);
        //columnModel.setColumnWeight(2, 2);
        //columnModel.setColumnWeight(3, 1);
        //tableView.setColumnModel(columnModel);
        //tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        //tableView.setDataAdapter(new SimpleTableDataAdapter(this, DATA_TO_SHOW));
        //int colorEvenRows = getResources().getColor(R.color.white);
        //int colorOddRows = getResources().getColor(R.color.gray);
        //tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));




        characterInput = (EditText) findViewById(R.id.charInput);
        //editText2 = (EditText) findViewById(R.id.editText2);
        status = (TextView) findViewById(R.id.messageTextView);
        //textview2 = (TextView) findViewById(R.id.textView2);
        fileCountField = (TextView) findViewById(R.id.classCountTextView);
        showDetails = (CheckBox) findViewById(R.id.showDetails);

        status.setText("");
        fileCountField.setText("");
        myExternalFile = new File(appFolderPath, filename);


        //Initialising buttons in the view
        //mDetect = (Button) findViewById(R.id.mDetect);
        startButton = (Button) findViewById(R.id.startButton);
        trainButton = (Button) findViewById(R.id.trainButton);


        details = new int[128];


        //init();


        showDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(initTable())
                        mTableLayout.setVisibility(View.VISIBLE);
                }
                else
                    mTableLayout.setVisibility(View.INVISIBLE);
            }
        });

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
                        switch(character)
                        {
                            case "DEL": classno = 127;
                                        break;
                            case "BS": classno = 8;
                                        break;
                            case "HT": classno = 9;
                                        break;
                            case "NL": classno = 10;
                                        break;
                            case "VT": classno = 11;
                                        break;
                            case "NP": classno = 12;
                                        break;
                            case "CR": classno = 13;
                                        break;
                            default: classno = character.getBytes(StandardCharsets.US_ASCII)[0];
                                        break;
                        }

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
                        status.setText("Recording values...");
                        fileCountField.setText("");
                        start = true;

                    }


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (flag) {
                        start = false;
                        status.setText("Saved sample");
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
                        fileCountField.setText(character + ":" + (++details[classno]));
                        Toast.makeText(getApplicationContext(), "Values for character '" + character + "' recorded successfully!", Toast.LENGTH_SHORT).show();
                        if(showDetails.isChecked())
                            initTable();
                    }
                }
                return true;
            }
        });

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File[] necessary_File = appFolderPath.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        if(s.matches("androidGK_dataset.txt"))
                            return true;
                        else
                            return false;
                    }
                });
                if(necessary_File.length == 0)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(CreateDataset.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Dataset file not found!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    status.setText("Training is in progress...");
                    probability = 1;
                    svm.train("-t 0 -c 0.01 -r 0.0 -m 200 -wi 0 -b " + probability + " " + appFolderPath + "/androidGK_dataset.txt " + appFolderPath + "/androidGK_model.model");
                    Toast.makeText(getApplicationContext(), "SVM Train has executed successfully!", Toast.LENGTH_SHORT).show();
                    status.setText("Training successful");
                }

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

        //new Thread().start();


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
        //dataMap = new TreeMap<String, Integer>();
        File[] necessary_File = appFolderPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(s.matches("androidGK_datasetdetails.txt")) {
                    Log.d("DEBUGGING", s + ":TRUE");
                    return true;
                }
                else{
                    Log.d("DEBUGGING",s + ":FALSE");
                    return false;
                }
            }
        });
        Log.d("DEBUGGING", "value of necessary_File: " + necessary_File);
        detailsFile = new File(appFolderPath + "/androidGK_datasetdetails.txt");
        if(necessary_File.length == 0)
        {
            try {
                datasetWriter = new FileWriter(detailsFile);
                datasetWriter.write("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int x = 0;
        try {
            datasetReader = new Scanner(detailsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(datasetReader.hasNext())
        {
            int classStr = datasetReader.nextInt();
            details[x++] = classStr;
            Log.d("DatasetDetailsValue:" , "" + classStr);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            datasetWriter = new FileWriter(detailsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        detailssb = new StringBuilder();
        try {
            for (int i : details) {
                detailssb.append(i + " ");
                Log.d("Updating values:", "" + i);
            }
            detailssb.append("\n");
            Log.d("Updated values:", detailssb.toString());
            datasetWriter.write(detailssb.toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        try {
            datasetWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    /*public int classCount(byte cls) {
        int count = 0;
        try {
            datasetReader = new Scanner(myExternalFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(datasetReader.hasNextLine())
        {
            String classStr = datasetReader.nextLine().split(" ")[0];
            if(classStr.equals("" + cls))
                count++;
        }
        return count;
    }
    */
    public boolean initTable()
    {
        boolean colorRow = false, datasetCheck = false;
        int color = getResources().getColor(R.color.gray);
        mTableLayout = (TableLayout) findViewById(R.id.datasetTable);
        mTableLayout.removeAllViews();
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerRow = inflater.inflate(R.layout.test_tableviewheader, null);
        mTableLayout.addView(headerRow);

        for(int clsCount=0; clsCount<128; clsCount++)
        {
            if (details[clsCount] != 0)
            {
                datasetCheck = true;

                View dataRow = inflater.inflate(R.layout.test_tableview, null);
                TextView tv1 = (TextView) dataRow.findViewById(R.id.classname);
                TextView tv2 = (TextView) dataRow.findViewById(R.id.representation);
                TextView tv3 = (TextView) dataRow.findViewById(R.id.samples);
                tv1.setText("" + (char)clsCount);
                tv2.setText("" + clsCount);
                tv3.setText("" + details[clsCount]);
                if(colorRow)
                {
                    tv1.setBackgroundColor(color);
                    tv2.setBackgroundColor(color);
                    tv3.setBackgroundColor(color);
                    colorRow = false;
                }
                else
                    colorRow = true;
                mTableLayout.addView(dataRow);
            }
        }
        if(!datasetCheck)
        {
            mTableLayout.removeAllViews();
            showDetails.setChecked(false);
            AlertDialog alertDialog = new AlertDialog.Builder(CreateDataset.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Dataset is Empty");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        return datasetCheck;
    }
    /*
    public void readLogcat(Context context, String title){
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
    }
    public void begin ()
    {
        dataMap = new TreeMap<String, Integer>();
        try {
            datasetReader = new Scanner(myExternalFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(datasetReader.hasNextLine())
        {
            String classStr = datasetReader.nextLine().split(" ")[0];
            if(dataMap.containsKey(classStr))
                dataMap.put(classStr, dataMap.get(classStr)+1);
            else
                dataMap.put(classStr,1);
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

    /*private class mThread extends Thread{
        public void run ()
        {
            if(dataMap!=null) {
                dataMap = new TreeMap<String, Integer>();
                try {
                    datasetReader = new Scanner(myExternalFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (datasetReader.hasNextLine()) {
                    String classStr = datasetReader.nextLine().split(" ")[0];
                    if (dataMap.containsKey(classStr))
                        dataMap.put(classStr, dataMap.get(classStr) + 1);
                    else
                        dataMap.put(classStr, 1);
                }
            }
        }
    }*/
}

