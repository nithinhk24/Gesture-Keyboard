package com.example.nithin.androidgk;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    // textview for connection status
    TextView textConnectionStatus;
    ListView pairedListView;

    Button create_dataset;
    //An EXTRA to take the device MAC to the next activity
    public static String EXTRA_DEVICE_ADDRESS;

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    public static final String TAG = "LibSVMExample";
    public static final String processId = Integer.toString(android.os.Process.myPid());

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textConnectionStatus = (TextView) findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        create_dataset = (Button) findViewById(R.id.button2);
        create_dataset.setOnClickListener(this);
        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        //It is best to check BT status at onResume in case something has changed while app was paused etc
        checkBTState();

        mPairedDevicesArrayAdapter.clear();// clears the array so items aren't duplicated when resuming from onPause

        textConnectionStatus.setText(" "); //makes the textview blank

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to pairedDevices list
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previously paired devices to the array
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            mPairedDevicesArrayAdapter.add("no devices paired");
        }
    }

    @Override
    public void onClick(View v)
    {
        Log.i("clicks","You clicked B1");
        Intent i = (new Intent(MainActivity.this, CreateDataset.class));
        startActivity(i);
    }
    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private void checkBTState()
    {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBtAdapter.isEnabled()) {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Set up on-click listener for the listview
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            textConnectionStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity while taking an extra which is the MAC address.
            Intent i = new Intent(MainActivity.this, ArduinoMain.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            i.putExtra("device_info", info);
            startActivity(i);
        }
    };

}
