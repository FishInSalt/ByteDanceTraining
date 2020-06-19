package com.example.photoviewer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class AcceptActivity extends AppCompatActivity  implements  WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener{

    private final IntentFilter intentFilter = new IntentFilter();
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Toast.makeText(AcceptActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }

    };

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    private final String TAG = "Accept";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {


                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                } else {

                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
                if (mManager != null) {
                    mManager.requestPeers(mChannel, AcceptActivity.this);
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
                    mManager.requestConnectionInfo(mChannel, AcceptActivity.this);
                }

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            }
        }
    };






    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Log.d("makedir", "on acceptactivity onCreate: ");
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        textView = (TextView) findViewById(R.id.textView2);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);


        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG, "disconnect onFailure:" + reasonCode);
                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "createGroup onSuccess");

                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "createGroup onFailure: " + reason);
                        //textView.setText("createGroup onFailure: " + reason);
                    }
                });
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "disconnect onSuccess");
                ;
                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "createGroup onSuccess");

                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "createGroup onFailure: " + reason);
                        //textView.setText("createGroup onFailure: " + reason);
                    }
                });
            }
        });


        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    while(true){
                        ServerSocket serverSocket = new ServerSocket(7890);
                        Socket socket = serverSocket.accept();

                       // ActivityCompat.requestPermissions(AcceptActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        Log.d("makedir", "on accept run(): ");
                        File appDir = new File(Environment.getExternalStorageDirectory(),"myphoto");

                        if (!appDir.exists()) {
                            Log.d("makedir", "making dir: "+appDir.getPath());
                            boolean makeDirRes = appDir.mkdir();
                            Log.d("makedir", "making dir result: "+makeDirRes);
                        }

                        String fileName = System.currentTimeMillis() + ".jpg";
                        File f = new File(appDir, fileName);


//                        final File f = new File(Environment.getExternalStorageDirectory() + "/AcceptPhoto/" + System.currentTimeMillis()
//                                + ".jpg");
//
//                        File dirs = new File(f.getParent());
//                        if (!dirs.exists())
//                            dirs.mkdirs();
                        FileOutputStream fileOutputStream = new FileOutputStream(f);
                        int len;
                        InputStream in = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        long total = 0;
                        while((len = in.read(buffer))!=-1){
                            fileOutputStream.write(buffer,0,len);
                            total += len;
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        serverSocket.close();
                        Message message = new Message();
                        message.obj="传输成功！";
                        handler.sendMessage(message);

                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(f.getPath()))));
                    }


                } catch (IOException e) {
                    Log.d(TAG, "err");

                }
            }
        });
        t1.start();

    }


    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG,"onSuccess");
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

    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        textView.setText("本设备处于可被发现状态");
    }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info){
        textView.setText("连接成功");
    }

}
