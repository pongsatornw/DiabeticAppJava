package com.example.yggdrasil.realdiabeticapp;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_UNKNOWN;
import static com.example.yggdrasil.realdiabeticapp.BluetoothChatService.MESSAGE_TOAST;

/**
 * Created by Yggdrasil on 21/3/2561.
 */

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener{

    private BluetoothChatService02 bluetoothChatService;
    private Dialog dialog;
    private Button btn_conn, btn_count, btn_read, btn_readall, btn_del, btn_time, btn_close;
    private TextView tv_btstatus;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private int num_rec;
    //private static final int STATE_MSG_RECIEVE = 5;
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final String DEVICE_OBJECT = "device_name";
    private byte[] temp_byte;
    private ArrayList<byte[]> arr_temp;
    // Hexadecimal Code for Glucose Meter
    public static final byte[] CMD_COUNT = {0x01};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        findViewByIds();

        num_rec = 0;
        temp_byte = new byte[6];
        arr_temp = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish(); //automatic close app if Bluetooth service is not available!
        }


    }

    private void findViewByIds(){
        tv_btstatus = findViewById( R.id.tv_connect);
        tv_btstatus.setText("Init");
        List<Button> list_bt = new ArrayList<>();
        list_bt.add( btn_conn = findViewById(R.id.bt_connect) );
        list_bt.add( btn_count = findViewById(R.id.bt_count) );
        list_bt.add( btn_read = findViewById(R.id.bt_single_read) );
        list_bt.add( btn_readall = findViewById(R.id.bt_all_read) );
        list_bt.add( btn_del = findViewById(R.id.bt_delete) );
        list_bt.add( btn_time = findViewById(R.id.bt_set_time) );
        list_bt.add( btn_close = findViewById(R.id.bt_close) );

        btn_readall.setEnabled(false);
        for(Button bt : list_bt)
            bt.setOnClickListener(this);
    }

    @Override
    public void onClick( View view) {
        if (view.getId() == R.id.bt_connect)
            showDiscoveryDevice();
        else if (view.getId() == R.id.bt_count)
            record_count();
        else if (view.getId() == R.id.bt_single_read){
            try {
                int chk = bluetoothChatService.getState();

                if (chk == 3) {
                    Boolean test = bluetoothChatService.checkStatus();
                    if (test) {
                        bluetoothChatService.GetRecorddata();
                        //mChatService.GetGlucodata();
                    } else {
                        bluetoothChatService.closeConnection();
                        Toast.makeText(this, "ข้อผิดพลาด กรุณาตรวจสอบเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                    }
                } else if (chk == 1) {
                    //mChatService.closeConnection();
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if(view.getId() == R.id.bt_all_read){
            try {
                int chk = bluetoothChatService.getState();

                if (chk == 3) {
                    Boolean test = bluetoothChatService.checkStatus();
                    if (test) {
                        bluetoothChatService.SyncReadAllRecord(num_rec);
                        //mChatService.GetGlucodata();
                    } else {
                        bluetoothChatService.closeConnection();
                        Toast.makeText(this, "ข้อผิดพลาด กรุณาตรวจสอบเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                    }
                } else if (chk == 1) {
                    //mChatService.closeConnection();
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if(view.getId() == R.id.bt_close) {
            try {
                int chk = bluetoothChatService.getState();

                if (chk == 3) {
                    Boolean test = bluetoothChatService.checkStatus();
                    if (test) {

                        bluetoothChatService.SyncCloseMeter();
                        //mChatService.GetGlucodata();
                    } else {
                        bluetoothChatService.closeConnection();
                        Toast.makeText(this, "ข้อผิดพลาด กรุณาตรวจสอบเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                    }
                } else if (chk == 1) {
                    //mChatService.closeConnection();
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.bt_set_time){
            try {
                int chk = bluetoothChatService.getState();

                if (chk == 3) {
                    Boolean test = bluetoothChatService.checkStatus();
                    if (test) {
                        byte  time[] = new byte[6];
                        Calendar calendar = Calendar.getInstance();
                        time[0] = (byte) ((calendar.get(Calendar.YEAR) - 2006) & 0xFF) ;
                        Log.d("Year: 2018-2006", String.valueOf(time[0]) );
                        time[1] = Byte.parseByte(decToHex(calendar.get(Calendar.MONTH) + 1) , 16);
                        time[2] = Byte.parseByte(decToHex(calendar.get(Calendar.DAY_OF_MONTH) ), 16);
                        time[3] = Byte.parseByte(decToHex(calendar.get(Calendar.HOUR_OF_DAY) +0), 16 );
                        time[4] = Byte.parseByte(decToHex(calendar.get(Calendar.MINUTE)), 16 );
                        time[5] = Byte.parseByte(decToHex(calendar.get(Calendar.SECOND)), 16);
                        bluetoothChatService.SyncTime(time);
                        //mChatService.GetGlucodata();
                    } else {
                        bluetoothChatService.closeConnection();
                        Toast.makeText(this, "ข้อผิดพลาด กรุณาตรวจสอบเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                    }
                } else if (chk == 1) {
                    //mChatService.closeConnection();
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if( view.getId() == R.id.bt_delete){
            try {
                int chk = bluetoothChatService.getState();

                if (chk == 3) {
                    Boolean test = bluetoothChatService.checkStatus();
                    if (test) {

                        bluetoothChatService.SyncDeleteAllRecord();
                        //mChatService.GetGlucodata();
                    } else {
                        bluetoothChatService.closeConnection();
                        Toast.makeText(this, "ข้อผิดพลาด กรุณาตรวจสอบเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                    }
                } else if (chk == 1) {
                    //mChatService.closeConnection();
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "กรุณาเชื่อมต่ออุปกรณ์", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void showDiscoveryDevice(){

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void connectToDevice( String deviceAddress){
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        //bluetoothChatService.start();
        switch (device.getType()) {
            case DEVICE_TYPE_CLASSIC:
                Log.d( "Device Type", "DEVICE_TYPE_CLASSIC");
                break;
            case DEVICE_TYPE_DUAL:
                Log.d( "Device Type", "DEVICE_TYPE_DUAL");
                break;
            case DEVICE_TYPE_LE:
                Log.d( "Device Type", "DEVICE_TYPE_LE");
                break;
            case DEVICE_TYPE_UNKNOWN:
                Log.d( "Device Type", "DEVICE_TYPE_UNKNOWN");
                break;
            default:
                tv_btstatus.setText("What's this?");
                break;
        }
        bluetoothChatService.connect( device, false);

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1){
                        case BluetoothChatService.STATE_NONE:
                            tv_btstatus.setText("None");
                            break;
                        case BluetoothChatService.STATE_LISTENING:
                            tv_btstatus.setText("Listening");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            tv_btstatus.setText("Connecting");
                            btn_conn.setEnabled(false);
                            break;
                        case BluetoothChatService.STATE_CONNECTED:
                            tv_btstatus.setText("Connected");
                            break;
                    }
                case Constants.MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;

                    //String writeMessage = new String(writeBuf);
                    //Toast.makeText( BluetoothActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //String readMessage = new String(readBuf, 0, 10);
                    Toast.makeText( BluetoothActivity.this, readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(BluetoothActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_CLOSE:
                    Toast.makeText(BluetoothActivity.this, "Closed Myglucohealth Wireless", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_RECORD_DELETE_ALL:
                    Toast.makeText(BluetoothActivity.this, "Delete all Records in Myglucohealth Wireless", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_RECORD_COUNT:
                    Toast.makeText(BluetoothActivity.this, "Count all Records in Myglucohealth Wireless", Toast.LENGTH_SHORT).show();
                    num_rec = msg.getData().getInt("num_rec", 9999);
                    Log.d("Record_All", String.valueOf(num_rec) );
                    if( num_rec > 0 && num_rec != 999){
                        btn_readall.setEnabled(true);
                    }
                    break;
                case Constants.MESSAGE_READ_START:
                    temp_byte = new byte[6];
                    arr_temp = new ArrayList<>();
                    break;
                case Constants.MESSAGE_READ_CONTINUE:
                    temp_byte =  msg.getData().getByteArray("value_byte[]");
                    arr_temp.add(temp_byte);
                    break;
                case Constants.MESSAGE_READ_END:
                    insertToRealm(arr_temp);
                    break;
            }
            return false;
        }
    });
    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)  throws IllegalArgumentException{
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    initial();
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    protected void initial(){
        bluetoothChatService = new BluetoothChatService02( this, handler);
        bluetoothChatService.start();
    }

    protected void sendCommand( byte[] command_code){
        if (bluetoothChatService.getState() != bluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }
            byte sendout = (byte)0x00;
            bluetoothChatService.write( command_code);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            bluetoothChatService = new BluetoothChatService02(this, handler);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( bluetoothChatService != null)
            bluetoothChatService.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if ( bluetoothChatService != null) {
            if ( bluetoothChatService.getState() == bluetoothChatService.STATE_NONE) {
                bluetoothChatService.start();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        try {
            unregisterReceiver(discoveryFinishReceiver);
        } catch( IllegalArgumentException ex){
            Log.e("Error", "Error");
        }
    }

    public static byte[] hexToByteArray(String hex) {
        hex = hex.length()%2 != 0?"0"+hex:hex;

        byte[] b = new byte[hex.length() / 2];

        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    private void record_count(){
        int curr_state = bluetoothChatService.getState();
        if( curr_state == 3){
            boolean status = bluetoothChatService.checkStatus();
            if ( status){
                bluetoothChatService.getNumSyncRecorddata();
            }
        }
        else
            return;
    }

    private String decToHex(int time){
        int inner_time = time;
        String hexaDecimal = "";
        int temp;
        char hexa[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        do{
            temp = inner_time % 16;
            hexaDecimal = hexa[temp] + hexaDecimal;
            inner_time = inner_time / 16;
            Log.d("10 -> 16", hexaDecimal);
        }while( inner_time > 0);
        return hexaDecimal;
    }

    private void insertToRealm(ArrayList<byte[]> list){
        String year, month, day, temperature, result, activity, hour, min;
        String date, time;
        for( byte[] byte_arr : list) {
            Log.d("Byte 0:", String.valueOf(byte_arr[0]) );
            Log.d("Byte 1:", String.valueOf(byte_arr[1]) );
            Log.d("Byte 2:", String.valueOf(byte_arr[2]) );
            Log.d("Byte 3:", String.valueOf(byte_arr[3]) );
            Log.d("Byte 4:", String.valueOf(byte_arr[4]) );
            Log.d("Byte 5:", String.valueOf(byte_arr[5]) );
            year = String.valueOf( (byte_arr[0]/2) );
            Log.d("Main Year: ", year);
            month = String.valueOf(
                    (( retUnsignbyte(byte_arr[0]) & 0b00000001 ) * 8) +
                            ( retUnsignbyte(byte_arr[1]) / 32)
            );
            Log.d("Main Month: ", month);

            day = String.valueOf(( retUnsignbyte(byte_arr[1]) & 0b00011111) );
            Log.d("Main Day: ", day);

            result = String.valueOf(
                    (( retUnsignbyte(byte_arr[2]) & 0b00000010 ) * 512) +
                    (( retUnsignbyte(byte_arr[2]) & 0b00000001 ) * 256) +
                    ( retUnsignbyte(byte_arr[3]) )
            );
            Log.d("Main Result: ", result);
            int temp_act;
            temp_act = ( retUnsignbyte(byte_arr[4]) ) & 0b11110000 ;

            // initial activity
            activity = "";
            if(temp_act == 128)
                activity = "Before Meal";
            else if(temp_act == 64)
                activity = "After Meal";
            else if(temp_act == 32)
                activity = "After Using Medicine";
            else if(temp_act == 16)
                activity = "Exercise";
            Log.d("Main Activity: ", activity );

            hour = String.valueOf(
                    (( retUnsignbyte(byte_arr[4]) & 0b00000100 ) * 16) +
                    (( retUnsignbyte(byte_arr[4]) & 0b00000010 ) * 8) +
                    (( retUnsignbyte(byte_arr[4]) & 0b00000001 ) * 4) +
                    + ( retUnsignbyte(byte_arr[5]) / 64)
            );
            Log.d("Main Hour: ", hour);

            min = String.valueOf( ( retUnsignbyte(byte_arr[5]) & 0b00111111) );
            Log.d("Main Minute: ", min);
            Log.d("", "\n\n\n\n");
        }

    }

    private int retUnsignbyte(byte b){
        if(b < 0)
            return b+256;
        else
            return  b;
    }
}
