package com.fleecast.stamina.todo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,PlaceSelectionListener,GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    private String latitude;
    private String longitude;
    private boolean errorFetchLocation=false;
    private TextView mPlaceDetailsText;
    private Marker mMarker;
    private TextView txtEventLocationConfirm;
    private TextView txtEventLocationCancel;
    private String addressInfo;
    private PlaceAutocompleteFragment autocompleteFragment;

    private PlaceInfo placeInfo = new PlaceInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        txtEventLocationConfirm = (TextView) findViewById(R.id.txtEventLocationConfirm);
        txtEventLocationCancel = (TextView) findViewById(R.id.txtEventLocationCancel);


        txtEventLocationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(latitude==null) {
                    Toast.makeText(MapsActivity.this,"Choose a place", Toast.LENGTH_LONG).show();
                    return;
                }
                if(latitude.isEmpty()){
                    Toast.makeText(MapsActivity.this,"Choose a place", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!errorFetchLocation) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.EXTRA_MAP_LAT, latitude);
                    returnIntent.putExtra(Constants.EXTRA_MAP_LNG, longitude);
                    returnIntent.putExtra(Constants.EXTRA_MAP_FULL_INFO, addressInfo);

                    if(placeInfo.getAddress()!=null)
                        returnIntent.putExtra(Constants.EXTRA_MAP_ADDRESS, placeInfo.getAddress());

                    if(placeInfo.getPhoneNumber()!=null)
                       returnIntent.putExtra(Constants.EXTRA_MAP_PHONENUMBER, placeInfo.getPhoneNumber());

                    if(placeInfo.getWebSite()!=null)
                        returnIntent.putExtra(Constants.EXTRA_MAP_WEBSITE, placeInfo.getWebSite());

                    if(placeInfo.getPlaceName()!=null)
                        returnIntent.putExtra(Constants.EXTRA_MAP_PLACENAME, placeInfo.getPlaceName());

                    setResult(Activity.RESULT_OK, returnIntent);

                }else {
                    Intent returnIntent = new Intent();
                    setResult(PlaceAutocomplete.RESULT_ERROR, returnIntent);
                }
                finish();
            }
        });

        txtEventLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Retrieve the PlaceAutocompleteFragment.
         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);


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
        mMap.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void moveToLocation(LatLng latLng){
        latitude = String.valueOf(latLng.latitude);
        longitude = String.valueOf(latLng.longitude);
        if(mMarker==null)
            mMarker =  mMap.addMarker(new MarkerOptions().position(latLng));
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(latLng);
        mMarker.setPosition(latLng);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    @Override
    public void onPlaceSelected(Place place) {
        errorFetchLocation = false;

        // Format the place's details and display them in the TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),place.getAddress(), place.getPhoneNumber(),
                place.getWebsiteUri()));

        addressInfo="";

        placeInfo = new PlaceInfo();

        if(place.getName()!=null && !place.getName().toString().trim().isEmpty()) {
            placeInfo.setPlaceName(place.getName().toString());
            addressInfo += "Place Name: " + place.getName();
        }
        if(place.getAddress()!=null && !place.getAddress().toString().trim().isEmpty()) {
            placeInfo.setAddress(place.getAddress().toString());
            addressInfo += "\nAddress: " + place.getAddress();
        }
        if(place.getPhoneNumber() !=null && !place.getPhoneNumber().toString().trim().isEmpty()) {
            placeInfo.setPhoneNumber(place.getPhoneNumber().toString());
            addressInfo += "\nTel: " + place.getPhoneNumber();
        }

        if(place.getWebsiteUri()!=null && !place.getWebsiteUri().toString().trim().isEmpty()) {
            placeInfo.setWebSite(place.getWebsiteUri().toString());
            addressInfo += "\nWeb: " +
                    place.getWebsiteUri();
        }

        placeInfo.setLatitude(String.valueOf(place.getLatLng().latitude));
        placeInfo.setLongitude(String.valueOf(place.getLatLng().longitude));

        moveToLocation(place.getLatLng());
                /*// Display attributions if required.
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
                } else {
                    mPlaceAttribution.setText("");
                }*/

    }

    public class PlaceInfo {
        private String placeName;
        private String Address;
        private String PhoneNumber;
        private String WebSite;

        private String longitude;
        private String latitude;

        public PlaceInfo() {
            this.Address = null;
            this.latitude= null;
            this.longitude= null;
            this.PhoneNumber= null;
            this.placeName= null;
            this.WebSite = null;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public String getWebSite() {
            return WebSite;
        }

        public void setWebSite(String webSite) {
            WebSite = webSite;
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }
    }

    @Override
    public void onError(Status status) {
        errorFetchLocation = true;
        addressInfo="";

        Log.e("DBG", "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }


    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("DBG", res.getString(R.string.place_details, name, address, phoneNumber,
                websiteUri));

        if(phoneNumber ==null || phoneNumber.toString().trim().isEmpty())
            phoneNumber = "N/A";

        if(websiteUri==null)
            websiteUri = Uri.parse("N/A");
        String tmp = res.getString(R.string.place_details, name, address, phoneNumber,
                websiteUri);

        return Utility.fromHTMLVersionCompat(tmp,Html.FROM_HTML_MODE_LEGACY);

    }
    @Override
    public void onMapClick(LatLng latLng) {
        autocompleteFragment.setText("");
        addressInfo="";

        placeInfo  = new PlaceInfo();
        placeInfo.setLongitude(String.valueOf(latLng.longitude));
        placeInfo.setLatitude(String.valueOf(latLng.latitude));

        moveToLocation(latLng);
        mPlaceDetailsText.setText("Lat/Lng: " + String.valueOf(latLng.latitude) + " / " + String.valueOf(latLng.longitude));

    }

}
