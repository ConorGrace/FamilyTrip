package ie.setu.familytrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

private const val TAG = "UserActivity"
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}