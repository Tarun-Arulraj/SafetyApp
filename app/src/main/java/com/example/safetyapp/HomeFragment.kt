package com.example.safetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
import androidx.core.content.ContextCompat

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var myMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var button1: ImageButton
    private lateinit var button2: ImageButton
    private lateinit var button3: ImageButton
    private lateinit var button4: ImageButton
    private lateinit var button5: ImageButton
    private lateinit var button6: ImageButton
    private lateinit var button7: ImageButton
    private lateinit var button8: ImageButton

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button1 = view.findViewById(R.id.button1)
        button2 = view.findViewById(R.id.button2)
        button3 = view.findViewById(R.id.button3)
        button4 = view.findViewById(R.id.button4)
        button5 = view.findViewById(R.id.button5)
        button6 = view.findViewById(R.id.button6)
        button7 = view.findViewById(R.id.button7)
        button8 = view.findViewById(R.id.button8)

//        setButtonColorFilter(button1)
//        setButtonColorFilter(button2)
//        setButtonColorFilter(button3)
//        setButtonColorFilter(button4)
//        setButtonColorFilter(button5)
//        setButtonColorFilter(button6)
//        setButtonColorFilter(button7)
//        setButtonColorFilter(button8)

        button1.setOnClickListener {
            // Add click listener for button1
        }

        button2.setOnClickListener {
            // Add click listener for button2
        }

        button3.setOnClickListener {
            // Add click listener for button3
        }

        button4.setOnClickListener {
            // Add click listener for button4
        }

        button5.setOnClickListener {
            // Add click listener for button5
        }

        button6.setOnClickListener {
            // Add click listener for button6
        }

        button7.setOnClickListener {
            // Add click listener for button7
        }

        button8.setOnClickListener {
            // Add click listener for button8
        }

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

    private fun setButtonColorFilter(button: ImageButton) {
        button.setColorFilter(requireContext().getColor(R.color.lavender), PorterDuff.Mode.SRC_IN)
    }

}