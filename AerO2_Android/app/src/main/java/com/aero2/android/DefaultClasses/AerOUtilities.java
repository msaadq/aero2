package com.aero2.android.DefaultClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

/**
 * Created by Muddassir Ahmed on 1/31/2016.
 */
public class AerOUtilities {

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getColorFromSmogValue(double max, double value){
        int red;
        if(value>(max/2)){
            red=255;
        }else{
            red= (int) ((value/(max/2))*255);
        }
        int green;
        if(value<(max/2)){
            green=255;
        }else {
            green = (int) (255 - (((value-(max/2)) / (max/2)) * 255));
        }
        int blue=20;
        return Color.argb(150, red, green, blue);
    }

    public static int getDisplayWidth(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getDisplayHieght(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    static Double distanceFromLatLongDiff(Double lat1, Double long1, Double lat2, Double long2){
        double latMid, m_per_deg_lat, m_per_deg_lon, deltaLat, deltaLon,dist_m;

        latMid = (lat1+lat2 )/2;  // or just use Lat1 for slightly less accurate estimate


        m_per_deg_lat = 111132.954 - 559.822 * Math.cos( 2.0 * latMid ) + 1.175 * Math.cos( 4.0 * latMid);
        m_per_deg_lon = (3.14159265359/180 ) * 6367449 * Math.cos ( latMid );

        deltaLat = Math.abs(lat1 - lat2);
        deltaLon = Math.abs(long1 - long2);

        return Math.sqrt (  Math.pow( deltaLat * m_per_deg_lat,2) + Math.pow( deltaLon * m_per_deg_lon , 2) );
    }

    public static boolean isTheMarkerOutside(Context context, String LOG_TAG, LatLng latLng1, Double lat2, Double long2, MapView mapView){
        Double xDistance=Math.abs(distanceFromLatLongDiff(0.0,latLng1.getLongitude(),0.0,long2));
        Double yDistance=Math.abs(distanceFromLatLongDiff(latLng1.getLatitude(), 0.0, lat2, 0.0));
        Double distance=Math.abs(Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
        Double bearingAngleInRad=Math.abs(mapView.getBearing()*3.14159265359)/180;
        Double totalAngle=bearingAngleInRad+Math.abs(Math.atan(yDistance / xDistance));
        Double x=distance*Math.abs(Math.cos(totalAngle));
        Double y=distance*Math.abs(Math.sin(totalAngle));
        final double longInterval = 0.000215901261691 ;
        final double latInterval = 0.000179807875453;
        double latLngRatio=latInterval/longInterval;
        Double metersPerPixelY=mapView.getMetersPerPixelAtLatitude(latLng1.getLatitude());
        Double metersPerPixelX=metersPerPixelY/latLngRatio;
        Double xPixels=x*(1/(metersPerPixelX+0.25*metersPerPixelX));
        Double yPixels=y*(1/(metersPerPixelY+0.15*metersPerPixelY));
        Log.v(LOG_TAG, " " + mapView.getBearing());
        if(xPixels>(getDisplayWidth(context)/2)||(yPixels>(getDisplayHieght(context)/2))){
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Float getZoomLevelFromPlace(Place place){
        Float zoom=-1f;
        List<Integer> typeList=place.getPlaceTypes();
        for(int i=0;i<typeList.size();i++){
            if(zoom==-1f) {
                if(typeList.get(i) == 1005){
                    zoom=3f;
                }else if(typeList.get(i)==1001){
                    zoom=4.1f;
                }else if(typeList.get(i)==1009){
                    zoom=9f;
                }else if(typeList.get(i)==34||typeList.get(i)==1023){
                    zoom=12f;
                }else if(typeList.get(i)==1020) {
                    zoom = 13f;
                }else if(typeList.get(i)==1011){
                    zoom=14f;
                }else if(typeList.get(i)==1013){
                    return 15f;
                }
            }else{
                if(typeList.get(i) == 1005){
                    if(3f>zoom) {
                        zoom = 3f;
                    }
                }else if(typeList.get(i)==1001){
                    if(4.1f>zoom) {
                        zoom = 4.1f;
                    }
                }else if(typeList.get(i)==1009){
                    if(9f>zoom) {
                        zoom = 9f;
                    }
                }else if(typeList.get(i)==34||typeList.get(i)==1023){
                    if(12f>zoom) {
                        zoom = 12f;
                    }
                }else if(typeList.get(i)==1020) {
                    if(13f>zoom) {
                        zoom = 13f;
                    }
                }else if(typeList.get(i)==1011){
                    if(14f>zoom) {
                        zoom = 14f;
                    }
                }else if(typeList.get(i)==1013){
                    return 15f;
                }
            }
        }
        if(zoom!=-1f) {
            return zoom;
        }else{
            return 17f;
        }
    }


}
