package ie.setu.familytrip.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(@get:PropertyName("username") @set:PropertyName("username") var username: String = "",
                @get:PropertyName("country") @set:PropertyName("country") var country: String = "",
                @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "") : Parcelable