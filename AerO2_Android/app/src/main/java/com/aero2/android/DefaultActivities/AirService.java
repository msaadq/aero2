package com.aero2.android.DefaultActivities;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.aero2.android.DefaultClasses.DBWriter;
import com.aero2.android.DefaultClasses.Integrator;
import com.aero2.android.DefaultClasses.SQLiteDatabaseFunctions;
import com.aero2.android.DefaultClasses.SampleDataTable;

/**
 * Created by admin on 12/14/2015.
 */
public class AirService extends Service{
    Looper mServiceLooper;
    Handler mServiceHandler;
    Integrator integrator;
    DBWriter dbWriterLocal;
    SQLiteDatabaseFunctions sqLiteDatabaseFunctions=new SQLiteDatabaseFunctions(getApplicationContext());
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            //Every 2 seconds adding a new value to the database

            long endTime = System.currentTimeMillis() + 2 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

            SampleDataTable airTuple=integrator.integratedAir();
            sqLiteDatabaseFunctions.addAirValue(airTuple);
            if(sqLiteDatabaseFunctions.isOnline()){
                sqLiteDatabaseFunctions.uploadAir(dbWriterLocal);
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting...Recording Smog Values every 2 seconds", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "stop recording values, service closed", Toast.LENGTH_SHORT).show();
    }

    public class LocalBinder extends Binder implements IActivityCallingService {
        public AirService getService() {
            return AirService.this;
        }

        @Override
        public void StartListenActivity(Activity activity) {
            integrator=new Integrator(activity);
            dbWriterLocal=new DBWriter(activity);
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public interface IActivityCallingService {
        void StartListenActivity(Activity activity);

    }

}
