package com.example.safetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
//import com.example.navigationdrawer.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var myMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(this@HomeFragment)
        } else {
            // Handle the null case, e.g. show an error message
            println("Error: Map fragment is null")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        myMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpMap()
            }
        }
    }

    private fun setUpMap(){
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            myMap.isMyLocationEnabled = true
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            lastLocation = location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            placeMarkerOnMap(currentLatLng)
                            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        }
                    }
                },
                Looper.myLooper()
            )
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("$currentLatLng")
        myMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }
}