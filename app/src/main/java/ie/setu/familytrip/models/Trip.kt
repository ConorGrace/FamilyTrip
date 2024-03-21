package ie.setu.familytrip.models

import com.google.firebase.firestore.PropertyName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Trip (
    var description: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
    var user: User? = null
) : Parcelable