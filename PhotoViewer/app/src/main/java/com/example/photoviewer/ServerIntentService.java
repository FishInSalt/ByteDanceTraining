package com.example.photoviewer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServerIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.photoviewer.action.FOO";
    private static final String ACTION_BAZ = "com.example.photoviewer.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.photoviewer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.photoviewer.extra.PARAM2";
    private Log Utils;

    public ServerIntentService() {
        super("ServerIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServerIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServerIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    private final String TAG = "Service";
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "No devices found");
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
//        }
        //Utils.d("receive start");
        try {

            Log.d(TAG, "try");
            ServerSocket serverSocket = new ServerSocket(7890);
             Socket socket = serverSocket.accept();
            final File f = new File(Environment.getExternalStorageDirectory() + "/123/" + System.currentTimeMillis()
                    + ".jpg");
            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();

//            final File f = new File(Environment.getExternalStorageDirectory() + "/123/" + System.currentTimeMillis()
//                    + ".jpg");
//            Log.d(TAG, f.getName());
//
//            File dirs = new File(f.getParent());
//            if (!dirs.exists())
//                dirs.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            int len;
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            long total = 0;
            while((len = in.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,len);
                total += len;
            }


            serverSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "err");

        }

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
