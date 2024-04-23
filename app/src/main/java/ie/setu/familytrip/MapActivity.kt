package ie.setu.familytrip

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.familytrip.databinding.ActivityMapBinding
import ie.setu.familytrip.models.Location
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var location = Location()
    private val markerList: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        location = intent.extras?.getParcelable<Location>("location")!!
        Places.initialize(applicationContext, getString(R.string.google_map_api_key))

        // Load markers based on the selected trip
        loadMarkers()

        setupAutocompleteFragment()
        setupMapFragment()
    }

    private fun setupAutocompleteFragment() {
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                val add = place.address
                val id = place.id
                val marker = addMarker(latLng)
                marker.title = "$add"
                marker.snippet = "$id"
                zoomOnMap(latLng)
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@MapActivity, "Error while searching: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loc = LatLng(location.lat, location.lng)
        val options = MarkerOptions()
            .title("Trip")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.setOnMarkerDragListener(this)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
    }

    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {}

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
    }

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        map.animateCamera(newLatLngZoom)
    }

    private fun addMarker(position:LatLng):Marker {
        val marker = map?.addMarker(MarkerOptions()
            .position(position)
            .title("Marker"))

        markerList.add(marker!!) // Add the marker to the list

        saveMarkerToFirestore(marker)

        return marker!!
    }

    private fun saveMarkerToFirestore(marker: Marker?) {
        if (marker != null) {
            val markerData = hashMapOf(
                "latitude" to marker.position.latitude,
                "longitude" to marker.position.longitude,
                // Add any other details you want to save
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("markers")
                .add(markerData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        this@MapActivity,
                        "Marker added to Firestore",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@MapActivity,
                        "Error adding marker to Firestore",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun loadMarkers() {
        val firestoreDb = FirebaseFirestore.getInstance()
        val markersCollection = firestoreDb.collection("markers")

        markersCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val latLng = LatLng(document.getDouble("latitude")!!, document.getDouble("longitude")!!)
                    val marker = addMarker(latLng)
                    // You can customize marker properties here if needed
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }
}

