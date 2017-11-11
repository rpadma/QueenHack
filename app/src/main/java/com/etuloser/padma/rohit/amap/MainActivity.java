package com.etuloser.padma.rohit.amap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.etuloser.padma.rohit.amap.Model.Place;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edxsrc;
    EditText edxdes;
    Button btnshow;

    int PLACE_PICKER_REQUEST_SRC = 1;
    int PLACE_PICKER_REQUEST_DES = 2;
    ArrayList<LatLng> latLngs=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Sample test data*/

        try {
            latLngs=readFromAssets(this,"textdata.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        edxdes=(EditText)findViewById(R.id.edxdsc);
        edxsrc=(EditText)findViewById(R.id.edxsrc);
btnshow=(Button)findViewById(R.id.btnshow);

btnshow.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        edxdes=(EditText)findViewById(R.id.edxdsc);
        edxsrc=(EditText)findViewById(R.id.edxsrc);
        String strsrc=edxsrc.getText().toString();
        String strdesc=edxdes.getText().toString();

        ArrayList<Place> placelist=new ArrayList<>();

              if(strsrc.length()>0 && strdesc.length()>0) {
        String[] srcarr = strsrc.split(",");
        Place p = new Place();
        p.setLatitude(srcarr[0]);
        p.setLongitude(srcarr[1]);
        placelist.add(p);

        String[] descarr = strdesc.split(",");
        Place p1 = new Place();
        p1.setLatitude(descarr[0]);
        p1.setLongitude(descarr[1]);
        placelist.add(p1);

        Intent i=new Intent(MainActivity.this,MapsActivity.class);

                  Bundle args = new Bundle();
                  args.putSerializable("Placeobj",(Serializable)placelist);
                  args.putSerializable("lllist",(Serializable)latLngs);
                  i.putExtra("BUNDLE",args);

        startActivity(i);

        }
        else {
            Toast.makeText(getApplicationContext(),"Select both  Source and Destination",Toast.LENGTH_SHORT).show();
        }


    }
});


      //  Intent i=new Intent(this,MapsActivity.class);
     //   startActivity(i);
    }

    public static ArrayList<LatLng> readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        ArrayList<LatLng> latLngs=new ArrayList<>();
        // do reading, usually loop until end of file reading
        String mLine = reader.readLine();
        while (mLine != null) {

            double lat= Double.valueOf((mLine.split(","))[0].toString().trim());
            double log= Double.valueOf((mLine.split(","))[1].toString().trim());
            LatLng latLng=new LatLng(lat,log);
            latLngs.add(latLng);
            mLine = reader.readLine();

        }
        reader.close();
        return latLngs;
    }


    public void onDestClick(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST_DES);

    }

    public void onSrcClick(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST_SRC);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_SRC) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);
                final Place pl = new Place();
                pl.setPlacename(place.getName().toString());
                pl.setLatitude(String.valueOf(place.getLatLng().latitude));
                pl.setLongitude(String.valueOf(place.getLatLng().longitude));

                edxsrc.setText(pl.getLatitude()+","+pl.getLongitude());

            }
        }

        if (requestCode == PLACE_PICKER_REQUEST_DES) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);
                final Place pl = new Place();
                pl.setPlacename(place.getName().toString());
                pl.setLatitude(String.valueOf(place.getLatLng().latitude));
                pl.setLongitude(String.valueOf(place.getLatLng().longitude));

                edxdes.setText(pl.getLatitude()+","+pl.getLongitude());

            }
        }

    }
}
