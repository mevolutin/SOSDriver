package com.example.coordinate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private Button mLogout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String Nome;
    String Telefono;
    String UserId;
    private GoogleApiClient mGoogleApiClient;
    String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataFirebase();
        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    DataFirebase();
                    startLocationService();




                }
            } else {

                stopLocationService();
                onStop();
            }
        });
        Button button = (Button) findViewById(R.id.LogOut);
        button.setOnClickListener(v -> {
            DataFirebase();

        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }else {
                Toast.makeText(this,"Devi abilitare i permessi!",Toast.LENGTH_SHORT).show();
            }
        }
    }




    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /*private void startDataService(){
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        intent.setAction(Constants.ACTION_START_DATAFIRE_SERVICE);
        startService(intent);
    }

    private void  stopDataService(){
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        intent.setAction(Constants.ACTION_STOP_DATAFIRE_SERVICE);
        startService(intent);
    }*/

    private void startLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Inizio condivisione posizione",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if (isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Condivisione posizione terminata",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Driver");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        //ref1.removeValue();

        //FirebaseAuth.getInstance().signOut(); LOGOUT
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private  void DataFirebase(){
        String userId = FirebaseAuth.getInstance().getUid();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Nome = String.valueOf(document.get("Nome"));
                        Telefono = String.valueOf(document.get("Telefono"));
                        Log.d("document", "DocumentSnapshot data: " + Telefono);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Driver");
        databaseReference.child(userId).child("Nome").setValue(Nome);
        databaseReference.child(userId).child("Telefono").setValue(Telefono);


    }



}