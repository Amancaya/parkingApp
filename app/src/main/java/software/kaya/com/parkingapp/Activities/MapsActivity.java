package software.kaya.com.parkingapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import software.kaya.com.parkingapp.AdapterLista;
import software.kaya.com.parkingapp.Dialog.DialogDetalle;
import software.kaya.com.parkingapp.Modelo.CollectionRoutes;
import software.kaya.com.parkingapp.Modelo.Parkins;
import software.kaya.com.parkingapp.R;
import software.kaya.com.parkingapp.RetrofitMaps;
import software.kaya.com.parkingapp.ServiceMaps;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "Map";
    private static final String PARKING_NODE = "parking";

    private GoogleMap mMap;
    private Marker miMarker;
    private AlertDialog dialog;
    private LatLng miUbicacion;
    private DatabaseReference databaseReference;
    private int contar_acceso = 0, countChilds = 0, controlChild;
    private List<Parkins> parkinsList;
    private List<Marker> markerList;
    private Polyline polyline;
    private List<Parkins> parkingCloser;
    private CoordinatorLayout coordinatorLayout;
    private Parkins ClickParking;
    private FloatingActionButton floatingActionButton;
    private AlertDialog Closers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dialog = new SpotsDialog(this, "Cargando...");
        parkinsList = new ArrayList<>();
        markerList = new ArrayList<>();
        parkingCloser = new ArrayList<>();
        coordinatorLayout = findViewById(R.id.map_content);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            floatingActionButton = findViewById(R.id.fab);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            dialog.show();
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPariknCloser();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (polyline != null) polyline.remove();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //style of map
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map);
        mMap.setMapStyle(style);
        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Permisos_mapa();
        MarkerParking();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (final Parkins parkins : parkinsList){
                    if (parkins.getName().equals(marker.getTitle())){
                        ClickParking = parkins;
                        CalculateDistance(miUbicacion, new LatLng(parkins.getLatitude(),parkins.getLongitude()));
                    }
                }
                return false;
            }
        });
    }

    private void Permisos_mapa() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Permisos_mapa();
        } else {
            locationStart();
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setActivityNyam(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }

    //clase que calcula la localizacion
    public class Localizacion implements LocationListener {
        FragmentActivity activityNyam;

        public FragmentActivity getActivityNyam() {
            return activityNyam;
        }

        public void setActivityNyam(FragmentActivity activityNyam) {
            this.activityNyam = activityNyam;
        }

        @Override
        public void onLocationChanged(Location location) {
            location.getLatitude();
            location.getLongitude();

            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double log = location.getLongitude();
            miUbicacion = new LatLng(lat, log);
            if (miMarker != null) miMarker.remove();

            miMarker = mMap.addMarker(new MarkerOptions().position(miUbicacion).title("yo")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            if (contar_acceso == 0) {
                dialog.dismiss();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                contar_acceso++;
            }
        }
    }

    private void MarkerParking(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(PARKING_NODE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Log.e(TAG, dataSnapshot.toString());
                    Parkins parkins = dataSnapshot.getValue(Parkins.class);
                    parkinsList.add(parkins);
                    AddMarkerToMap(parkins, false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Parkins parkins = dataSnapshot.getValue(Parkins.class);
                    for (int i = 0; i<markerList.size(); i++ ){
                        if (markerList.get(i).getTitle().equals(parkins.getName())){
                            try {
                                Log.e(TAG, "Removiendo "+parkins.getName());
                                markerList.get(i).remove();
                                markerList.remove(i);
                                UpdateParkingList(parkins);
                                AddMarkerToMap(parkins, true);
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void AddMarkerToMap(Parkins parkins, boolean update){
        Log.e(TAG, "AddMarkerToMap disponibilidad"+parkins.isStatus()+" "+parkins.getName());
        float color;
        String available;
        LatLng CurrentMarker = new LatLng(parkins.getLatitude(), parkins.getLongitude());

        if (parkins.isStatus()){
            if (parkins.getSpaces_quantity()== 0) color = BitmapDescriptorFactory.HUE_RED;
            else color = BitmapDescriptorFactory.HUE_GREEN;
            available = parkins.getSpaces_quantity()+"";
        }else{
            color = BitmapDescriptorFactory.HUE_VIOLET;
            available = "Cerrado";
        }

        Marker marker = mMap.addMarker(new MarkerOptions().position(CurrentMarker).title(parkins.getName())
                .snippet("Disponibles: "+available)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));

        if (update) BounceMarker(marker);

        markerList.add(marker);
    }

    private void UpdateParkingList(Parkins parkins){
        for (int i = 0; i< parkinsList.size(); i++){
            if (parkinsList.get(i).getName().equals(parkins.getName())){
                parkinsList.remove(i);
                parkinsList.add(parkins);
            }
        }
    }

    private void CalculateDistance(LatLng origen, LatLng destino){
        ServiceMaps serviceMaps = RetrofitMaps.getRetrofit().create(ServiceMaps.class);
        Call<CollectionRoutes> routesCall = serviceMaps.getDistanceDuration("metric", origen.latitude + "," + origen.longitude, destino.latitude + "," + destino.longitude, "driving");
        routesCall.enqueue(new Callback<CollectionRoutes>() {
            @Override
            public void onResponse(Call<CollectionRoutes> call, Response<CollectionRoutes> response) {
                try{
                    if (polyline != null) polyline.remove();

                    for (int i = 0; i < response.body().getRoutes().size(); i++){
                        String encodeString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();

                        List<LatLng> latLngList = decodePoly(encodeString);
                        polyline = mMap.addPolyline(new PolylineOptions()
                                .addAll(latLngList)
                                .width(15)
                                .color(Color.CYAN)
                                .geodesic(true));

                        if (ClickParking != null) {
                            Snackbar.make(coordinatorLayout, "Tiempo a tardar:  "+
                                    response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText(), 5000)
                                    .setAction("INFO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogDetalle.newInstance(ClickParking)
                                                    .show(getSupportFragmentManager(), null);
                                        }
                                    }).setActionTextColor(Color.CYAN).show();

                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CollectionRoutes> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void IfParkerIsCloser(final Parkins parkins){
        ServiceMaps serviceMaps = RetrofitMaps.getRetrofit().create(ServiceMaps.class);
        Call<CollectionRoutes> routesCall = serviceMaps.getDistanceDuration("metric", miUbicacion.latitude + "," + miUbicacion.longitude, parkins.getLatitude() + "," + parkins.getLongitude(), "driving");
        routesCall.enqueue(new Callback<CollectionRoutes>() {
            @Override
            public void onResponse(Call<CollectionRoutes> call, Response<CollectionRoutes> response) {
                try{
                    countChilds = countChilds + 1;
                    for (int i = 0; i < response.body().getRoutes().size(); i++){
                        String distance =response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        Log.e(TAG, "onResponse "+distance+" "+parkins.getName());
                        if (Double.parseDouble(distance.substring(0, distance.length()-2)) < 2){
                            if (parkins.getSpaces_quantity() > 0) {
                                parkins.setTiempo(time);
                                parkins.setKilometros(distance);
                                parkingCloser.add(parkins);
                            }
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }

                ShowList();
            }
            @Override
            public void onFailure(Call<CollectionRoutes> call, Throwable t) {
                Log.e(TAG, "Error al consultar todos los parkings cercanos");
            }
        });
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }
        return poly;
    }

    public AlertDialog createSimpleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_list, null);
        ListView listView = (ListView) v.findViewById(R.id.lista);
        if (parkingCloser!= null && !parkingCloser.isEmpty())
            listView.setAdapter(new AdapterLista(this, SortList(parkingCloser)));
        builder.setView(v);
        builder.setTitle("PARKINGS MAS CERCANOS");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView latitude = (TextView) view.findViewById(R.id.latitude);
                TextView longitud = (TextView) view.findViewById(R.id.longitud);
                LatLng destiny = new LatLng(Double.parseDouble(latitude.getText().toString()), Double.parseDouble(longitud.getText().toString()));
                CalculateDistance(miUbicacion, destiny);
                Closers.dismiss();
            }
        });
        return builder.create();
    }

    private void AddPariknCloser(){
        dialog.show();
        controlChild = 0;
        countChilds = 0;
        if (!parkingCloser.isEmpty()) parkingCloser.clear();
        for (Parkins parkins: parkinsList){
            if (parkins.isStatus()) {
                controlChild ++;
                IfParkerIsCloser(parkins);
            }
        }

    }

    private synchronized void ShowList(){
        if (countChilds == controlChild){
            dialog.dismiss();
            Closers = createSimpleDialog();
            Closers.show();
        }
    }

    private List<Parkins> SortList(List<Parkins> parkinsList){
        for (int i = 0 ; i < parkinsList.size() - 1 ; i++) {
            int min = i;

            for (int j = i + 1 ; j < parkinsList.size() ; j++) {
                String distance1 = parkinsList.get(j).getKilometros();
                String distance2 = parkinsList.get(min).getKilometros();
                if (Double.parseDouble(distance1.substring(0, distance1.length()-2)) < Double.parseDouble(distance2.substring(0, distance2.length()-2))) {
                    min = j;
                }
            }

            if (i != min) {
                Parkins parkins = parkinsList.get(i);
                parkinsList.set(i, parkinsList.get(min));
                parkinsList.set(min, parkins);
            }
        }

        return parkinsList;
    }

    private void BounceMarker(final Marker marker){

        marker.showInfoWindow();
        //Make the marker bounce
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
