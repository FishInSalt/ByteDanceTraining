package com.example.photoviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends AppCompatActivity implements  WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener{

    private final IntentFilter intentFilter = new IntentFilter();

    WifiP2pManager mManager;
    Channel mChannel;

    TextView textView ;
    Button button;
    Button button5;
    NumberPicker picker;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Toast.makeText(WifiActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }

    };

    private void discoverPeersTillSuccess(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG,"onSuccess");
                //textView.setText("onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, String.valueOf(reasonCode));
                //textView.setText(String.valueOf(reasonCode));
                discoverPeersTillSuccess();

            }
        });
    }

    private final String TAG = "Activity";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {


                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                } else {

                }

                //Log.d(TAG,"WIFI_P2P_STATE_CHANGED_ACTION");
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
                if (mManager != null) {
                    mManager.requestPeers(mChannel, WifiActivity.this);
                }
                Log.d(TAG, "P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
                if (mManager == null) {
                    return;
                }
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    textView.setText("连接成功");
                    mManager.requestConnectionInfo(mChannel, WifiActivity.this);



                }

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            }
        }
    };

    String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);




        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        picker = (NumberPicker) findViewById(R.id.pick);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        button = (Button)findViewById(R.id.button4);

        button.setEnabled(false);
        Intent intent = getIntent();
        currentImagePath = intent.getStringExtra("photoPath");

        peersName = new String[1];
        peersName[0]="No Devices";
        picker.setDisplayedValues(peersName);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);



        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {

            }
            public void onFailure(int reason) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = picker.getValue();
                connect(num);
            }
        });
        button5 = (Button)findViewById(R.id.button5);
        button5.setEnabled(false);

        button5.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                //Context context = this.getApplicationContext();
                InetAddress address = wifiInfo.groupOwnerAddress;
                final String host = address.getHostAddress();
                //Toast.makeText(WifiActivity.this, host, Toast.LENGTH_SHORT).show();
                //textView.setText(host);
                int port = 7890;
//        int len;
                //Socket socket;


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int len;
                            byte buf[] = new byte[1024];
                            Socket socket;
                            socket = new Socket(host, 7890);
                            OutputStream outputStream = socket.getOutputStream();
                            InputStream inputStream = new FileInputStream(currentImagePath);

                            while ((len = inputStream.read(buf)) != -1) {
                                outputStream.write(buf, 0, len);
                            }

                            outputStream.close();
                            inputStream.close();

                            Message message = new Message();
                            message.obj="传输成功！";
                            handler.sendMessage(message);



                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });




    }

    public void connect(final int num) {
        WifiP2pDevice device = peers.get(num);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"connect sucess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"connect fail");
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
        //discoverPeersTillSuccess();
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG,"onSuccess");
                textView.setText("监听设备中");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, String.valueOf(reasonCode));

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] peersName;

    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Log.d(TAG, "No devices found");
            textView.setVisibility(View.INVISIBLE);
            if (peersName.length>0){
                peersName[0]="No Devices";
            }else {
                peersName = new String[1];
                peersName[0]="No Devices";
            }
            return;
        }else{
            peersName = new String[peers.size()];
            int i=0;
            for(WifiP2pDevice device: peers){
                peersName[i++]=device.deviceName;
            }
            textView.setVisibility(View.VISIBLE);
        }
        picker.setDisplayedValues(peersName);
        button.setEnabled(true);
    }

    WifiP2pInfo wifiInfo;


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        wifiInfo = info;
        button5.setEnabled(true);
//        Context context = this.getApplicationContext();
//        InetAddress address = info.groupOwnerAddress;
//        String host = address.getHostAddress();
//        //Toast.makeText(WifiActivity.this, host, Toast.LENGTH_SHORT).show();
//        textView.setText(host);
//        int port = 7890;
////        int len;
//        //Socket socket;
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int len;
//                    byte buf[] = new byte[1024];
//                    Socket socket;
//                    socket = new Socket(host, 7890);
//                    //textView.setText("socket连接成功");
//
//                    OutputStream outputStream = socket.getOutputStream();
////                    ContentResolver cr = context.getContentResolver();
//                    //InputStream inputStream = null;
////                    //inputStream = cr.openInputStream(Uri.parse(currentImagePath));
//                    InputStream inputStream = new FileInputStream(currentImagePath);
//
//                    while ((len = inputStream.read(buf)) != -1) {
//                        outputStream.write(buf, 0, len);
//                    }
//
//                    outputStream.close();
//                    inputStream.close();
//
//
//
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
////        try {
////
////            //socket.bind(null);
//////            socket.connect((new InetSocketAddress(host, port)), 10000);
//////            socket = new Socket(host, 7890);
//////            textView.setText("socket连接成功");
//////
//////
////////            OutputStream outputStream = socket.getOutputStream();
////////            ContentResolver cr = context.getContentResolver();
////////            InputStream inputStream = null;
////////            //inputStream = cr.openInputStream(Uri.parse(currentImagePath));
////////            inputStream = new FileInputStream(currentImagePath);
////////            while ((len = inputStream.read(buf)) != -1) {
////////                outputStream.write(buf, 0, len);
////////                Toast.makeText(WifiActivity.this, "传输中", Toast.LENGTH_SHORT).show();
////////            }
////////
////////            Toast.makeText(WifiActivity.this, "传输结束", Toast.LENGTH_SHORT).show();
////////            outputStream.close();
////////            inputStream.close();
//////        } catch (FileNotFoundException e) {
//////            //catch logic
//////        } catch (IOException e) {
//////            //catch logic
//////        }
////
////
////
////
////
//////        finally {
//////            if (socket != null) {
//////                if (socket.isConnected()) {
//////                    try {
//////                        socket.close();
//////                    } catch (IOException e) {
//////                        //catch logic
//////                    }
//////                }
//////            }
//////        }
////    }

    }
}
