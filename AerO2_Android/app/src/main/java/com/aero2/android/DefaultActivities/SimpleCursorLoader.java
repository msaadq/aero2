package com.aero2.android.DefaultActivities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by admin on 12/31/2015.
 */
public abstract class SimpleCursorLoader extends AsyncTaskLoader<Cursor> {
    private Cursor mCursor;
    GoogleMap googleMap;

    public SimpleCursorLoader(Context context,GoogleMap map) {
        super(context);
        googleMap=map;
    }

    /* Runs on a worker thread */
    @Override
    public abstract Cursor loadInBackground();

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p/>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        AirAzureDbHelper airAzureDbHelper=new AirAzureDbHelper(getContext());
        SQLiteDatabase db=airAzureDbHelper.getReadableDatabase();
        String[] projection=new String[]{
                AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,
                AirAzureContract.AirAzureEntry.COLUMN_LAT,
                AirAzureContract.AirAzureEntry.COLUMN_LONG
        };

        mCursor=db.query(AirAzureContract.AirAzureEntry.TABLE_NAME,projection,null,null,null,null,null);
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
}