package ie.setu.familytrip

import ie.setu.familytrip.CreateActivity
import TripsAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ie.setu.familytrip.models.Trip
import ie.setu.familytrip.models.User

private const val TAG = "PostsActivity"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"
private const val SELECTED_TRIP = "SELECTED_TRIP"

open class PostsActivity : AppCompatActivity(), TripsAdapter.OnItemClickListener {

    private var signedInUser: User? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var trips: MutableList<Trip>
    private lateinit var adapter: TripsAdapter
    private lateinit var spinnerCountries: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        val rvPosts: RecyclerView = findViewById(R.id.rvPosts)


        spinnerCountries = findViewById(R.id.spinnerCountries)

        trips = mutableListOf()
        adapter = TripsAdapter(this, trips, this, false)
        rvPosts.adapter = adapter
        val countriesArray = resources.getStringArray(R.array.countries)
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountries.adapter = spinnerAdapter

        rvPosts.layoutManager = LinearLayoutManager(this)
        firestoreDb = FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }


        val username = intent.getStringExtra(EXTRA_USERNAME)
        var postsReference = firestoreDb.collection("trips").limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        if (username != null) {
            supportActionBar?.title = username
            postsReference = postsReference.whereEqualTo("user.username", username)
        }

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying trips", exception)
                return@addSnapshotListener
            }
            val tripList = snapshot.toObjects(Trip::class.java)
            trips.clear()
            trips.addAll(tripList)
            adapter.notifyDataSetChanged()
            for (trip in tripList) {
                Log.i(TAG, "Document ${trip}")
            }
        }

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Exception when querying trips", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val tripList = snapshot.toObjects(Trip::class.java)
                trips.clear()
                trips.addAll(tripList)
                adapter.notifyDataSetChanged()

                Log.d(TAG, "Filtered Trip List Size: ${trips.size}")
            } else {
                Log.d(TAG, "Snapshot is null")
            }
        }

        val fabCreate: FloatingActionButton = findViewById(R.id.fabCreate)

        fabCreate.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        val btnApplyFilter: Button = findViewById(R.id.btnApplyFilter)
        btnApplyFilter.setOnClickListener {
            applyFilters()
            Toast.makeText(this, "Tilt the phone to reset filter", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trips, menu)
        return true
    }

    override fun onItemClick(trip: Trip) {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("SELECTED_TRIP", trip)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyFilters() {
        val selectedCountry = spinnerCountries.selectedItem?.toString() ?: ""
        Log.d(TAG, "Selected Country: $selectedCountry")

        var query: Query = firestoreDb.collection("trips")

        if (selectedCountry.isNotEmpty()) {
            query = query.whereEqualTo("spinnerCountries", selectedCountry)
            Log.d(TAG, "Query: $query")
        }

        // Execute query and update RecyclerView
        query.get()
            .addOnSuccessListener { snapshot ->
                val tripList = snapshot.toObjects(Trip::class.java)
                trips.clear()
                trips.addAll(tripList)
                adapter.notifyDataSetChanged()
                Log.d(TAG, "Filtered Trip List Size: ${tripList.size}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Exception when querying trips", exception)
            }
        adapter.notifyDataSetChanged()
    }


}