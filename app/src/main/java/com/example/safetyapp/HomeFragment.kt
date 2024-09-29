package com.example.safetyapp

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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
import org.json.JSONObject

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var myMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationUpdateHandler: Handler
    private lateinit var locationUpdateRunnable: Runnable
    private lateinit var button1: ImageButton
    private lateinit var button2: ImageButton
    private lateinit var button3: ImageButton
    private lateinit var button4: ImageButton
    private lateinit var button5: ImageButton
    private lateinit var button6: ImageButton
    private lateinit var button7: ImageButton
    private lateinit var button8: ImageButton
    private var currentLocation: Location? = null

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        private const val LOCATION_UPDATE_INTERVAL = 60000L // 1 minute
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun showAlertDialog(message: String, phoneNumber: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Emergency Service")
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Yes") { _, _ ->
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
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
        if (view.findViewById<View>(R.id.button1) != null) {
            button1 = view.findViewById(R.id.button1)
            button1.setOnClickListener {
                showAlertDialog("Are you in need of police related emergency service?", "9136820860") // Police emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button2) != null) {
            button2 = view.findViewById(R.id.button2)
            button2.setOnClickListener {
                showAlertDialog("Are you in need of fire related emergency service?", "101") // Fire emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button3) != null) {
            button3 = view.findViewById(R.id.button3)
            button3.setOnClickListener {
                showAlertDialog("Are you in need of medical related emergency service?", "102") // Medical emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button4) != null) {
            button4 = view.findViewById(R.id.button4)
            button4.setOnClickListener {
                showAlertDialog("Are you in need of flood related emergency service?", "1070") // Flood emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button5) != null) {
            button5 = view.findViewById(R.id.button5)
            button5.setOnClickListener {
                showAlertDialog("Are you in need of women related emergency service?", "181") // Women emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button6) != null) {
            button6 = view.findViewById(R.id.button6)
            button6.setOnClickListener {
                showAlertDialog("Are you in need of child related emergency service?", "1098") // Child emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button7) != null) {
            button7 = view.findViewById(R.id.button7)
            button7.setOnClickListener {
                showAlertDialog("Are you in need of railway related emergency service?", "138") // Railway emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        if (view.findViewById<View>(R.id.button8) != null) {
            button8 = view.findViewById(R.id.button8)
            button8.setOnClickListener {
                showAlertDialog("Are you in need of emotional and mental related emergency service?", "9892839923") // suicide prevention emergency number in India
                getCurrentLocation(message = "got location")
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(this@HomeFragment)
        } else {
            // Handle the null case, e.g. show an error message
            println("Error: Map fragment is null")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        locationUpdateHandler = Handler(requireActivity().mainLooper)
        locationUpdateRunnable = object : Runnable {
            override fun run() {
                updateCurrentLocation()
                locationUpdateHandler.postDelayed(this, LOCATION_UPDATE_INTERVAL)
            }
        }

        locationUpdateHandler.post(locationUpdateRunnable)
    }

    private fun getCurrentLocation(message: String) {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    sendNotification(location, message)
                } else {
                    Log.e("Location", "Unable to get current location")
                }
            }.addOnFailureListener { e ->
                Log.e("Location", "Error getting current location: $e")
            }
        } else {
            Log.e("Location", "Location services permission denied")
        }
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
            } else {
                Log.e("Location", "Location services permission denied")
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
            locationRequest.interval = LOCATION_UPDATE_INTERVAL
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            lastLocation = location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            myMap.clear() // Clear all markers
                            placeMarkerOnMap(currentLatLng) // Add a new marker
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

    private fun sendNotification(location: Location, message: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("notifications").add(mapOf(
                "userId" to userId,
                "location" to location,
                "message" to message
            )).addOnSuccessListener {
                Log.d("Notification", "Notification sent successfully")
                val url = "https://fcm.googleapis.com/fcm/send"
                val topic = "/topics/department-topic"
                val title = "Emergency Notification"
                val body = "User $userId needs assistance at location $location"

                val jsonObject = JSONObject()
                jsonObject.put("to", topic)
                jsonObject.put("priority", "high")

                val data = JSONObject()
                data.put("title", title)
                data.put("body", body)
                jsonObject.put("data", data)

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    { response ->
                        Log.d("Notification", "Notification sent successfully")
                    },
                    { error ->
                        Log.e("Notification", "Error sending notification", error)
                    }
                )

                val queue = Volley.newRequestQueue(requireContext())
                queue.add(jsonObjectRequest)
            }.addOnFailureListener { e ->
                Log.e("Notification", "Error sending notification", e)
            }
        }
    }
}