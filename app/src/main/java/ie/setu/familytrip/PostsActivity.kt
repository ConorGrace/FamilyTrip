package ie.setu.familytrip

import TripsAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        val rvPosts: RecyclerView = findViewById(R.id.rvPosts)

        trips = mutableListOf()
        adapter = TripsAdapter(this, trips, this)

        rvPosts.adapter = adapter
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

        var postsReference = firestoreDb.collection("trips").limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
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
        val fabCreate: FloatingActionButton = findViewById(R.id.fabCreate)

        fabCreate.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trips, menu)
        return super.onCreateOptionsMenu(menu)
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
}
