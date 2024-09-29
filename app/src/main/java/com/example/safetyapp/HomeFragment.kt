package com.example.safetyapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    private var currentLocation: Location? = null
    private val LOCATION_UPDATE_INTERVAL = 10000 // 10 seconds
    private lateinit var locationUpdateHandler: Handler
    private lateinit var locationUpdateRunnable: Runnable

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun showAlertDialog(message: String, phoneNumber: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Emergency Service")
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
        alertDialog.setNegativeButton("No") { _, _ ->
            // Do nothing or handle the no case
        }
        alertDialog.show()
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

        button1.setOnClickListener {
            showAlertDialog("Are you in need of police related emergency service?", "9136820860") // Police emergency number in India
        }

        button2.setOnClickListener {
            showAlertDialog("Are you in need of fire related emergency service?", "101") // Fire emergency number in India
        }

        button3.setOnClickListener {
            showAlertDialog("Are you in need of medical related emergency service?", "102") // Medical emergency number in India
        }

        button4.setOnClickListener {
            showAlertDialog("Are you in need of flood related emergency service?", "1070") // Flood emergency number in India
        }

        button5.setOnClickListener {
            showAlertDialog("Are you in need of women related emergency service?", "181") // Women emergency number in India
        }

        button6.setOnClickListener {
            showAlertDialog("Are you in need of child related emergency service?", "1098") // Child emergency number in India
        }

        button7.setOnClickListener {
            showAlertDialog("Are you in need of railway related emergency service?", "138") // Railway emergency number in India
        }

        button8.setOnClickListener {
            showAlertDialog("Are you in need of emotional and mental related emergency service?", "9892839923") // suicide prevention emergency number in India
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(this@HomeFragment)
        } else {
            // Handle the null case, e.g. show an error message
            println ("Error: Map fragment is null")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        locationUpdateHandler = Handler(requireActivity().mainLooper)
        locationUpdateRunnable = object : Runnable {
            override fun run() {
                updateCurrentLocation()
                locationUpdateHandler.postDelayed(this, LOCATION_UPDATE_INTERVAL.toLong())
            }
        }

        locationUpdateHandler.post(locationUpdateRunnable)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        myMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
            val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.interval = LOCATION_UPDATE_INTERVAL.toLong()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            lastLocation = location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            placeMarkerOnMap(currentLatLng)
                            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            storeCurrentLocationInFirestore(location)
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

    private fun updateCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    storeCurrentLocationInFirestore(location)
                }
            }
        }
    }

    private fun storeCurrentLocationInFirestore(location: Location) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).update("location", location)
                .addOnSuccessListener {
                    Log.d("Location", "Current location updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Error updating current location", e)
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable)
    }
}