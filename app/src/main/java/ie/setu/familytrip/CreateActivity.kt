package ie.setu.familytrip


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ie.setu.familytrip.models.Location
import ie.setu.familytrip.models.Trip
import ie.setu.familytrip.models.User
import ie.setu.familytrip.R


private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 1234

private lateinit var imageView: ImageView
private lateinit var btnPickImage: Button
private lateinit var btnSubmit: Button
private lateinit var etDescription: EditText
private lateinit var btnLocation: Button
private lateinit var spinnerCountries: Spinner
class CreateActivity : AppCompatActivity() {
    private var signedInUser: User? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var location = Location(52.245696, -7.139102, 15f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false
        setContentView(R.layout.activity_create)
        storageReference = FirebaseStorage.getInstance().reference
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

        imageView = findViewById(R.id.imageView)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnLocation = findViewById(R.id.btnLocation)
        etDescription = findViewById(R.id.etDescription)
        spinnerCountries = findViewById(R.id.spinner_countries)

        val countriesArray = resources.getStringArray(R.array.countries)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountries.adapter = adapter

        val selectedTrip: Trip? = intent.getParcelableExtra("SELECTED_TRIP")

        if (intent.hasExtra("SELECTED_TRIP")) {
            edit = true
            if (selectedTrip != null) {
                etDescription.setText(selectedTrip.description)
            }
        }

        btnPickImage.setOnClickListener{
            Log.i(TAG, "Open up image picker on Device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        registerMapCallback()

        btnLocation.setOnClickListener{
            launchMapActivity()
        }

        btnSubmit.setOnClickListener{
            handleSubmitButtonClick()
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            location = result.data!!.extras?.getParcelable("location")!!
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }


    private fun handleSubmitButtonClick() {
        if(photoUri == null) {
            Toast.makeText(this, "No Photo Selected", Toast.LENGTH_SHORT).show()
            return
        }
        if(etDescription.text == null) {
            Toast.makeText(this, "Description cannot be null", Toast.LENGTH_SHORT).show()
            return
        }
        if(signedInUser == null) {
            Toast.makeText(this, "No signed In user, stop", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                val trip = Trip(
                    etDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDb.collection("trips").add(trip)
            }.addOnCompleteListener { tripCreationTask ->
                btnSubmit.isEnabled = true
                if (!tripCreationTask.isSuccessful) {
                    Log.e(TAG,"Exception during Firebase operations", tripCreationTask.exception)
                    Toast.makeText(this, "Failed to save trip", Toast.LENGTH_SHORT).show()
                }
                etDescription.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this,"Successful Trip Posting", Toast.LENGTH_SHORT).show()
                val postIntent = Intent(this, PostsActivity::class.java)
                startActivity(postIntent)
                finish()
            }
    }

    private fun launchMapActivity() {

        val launcherIntent = Intent(this, MapActivity::class.java)
            .putExtra("location", location)
        mapIntentLauncher.launch(launcherIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode, data)
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG, "photoUri $photoUri")
                imageView.setImageURI(photoUri)
            }
        }
            else {
            }
    }
}