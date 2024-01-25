package ie.setu.familytrip
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ie.setu.familytrip.models.Trip
import java.math.BigInteger
import java.security.MessageDigest


class TripsAdapter (val context: Context, val trips:List<Trip>) :
    RecyclerView.Adapter<TripsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = trips.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trips[position])
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivPost: ImageView = itemView.findViewById(R.id.ivPost)
        val tvRelativeTime: TextView = itemView.findViewById(R.id.tvRelativeTime)
        val ivProfilePic: ImageView = itemView.findViewById(R.id.ivProfilePic)

        fun bind(trip: Trip) {
            val username = trip.user?.username as String
            tvUsername.text = trip.user?.username
            tvDescription.text = trip.description
            Glide.with(context).load(trip.imageUrl).into(ivPost)
            Glide.with(context).load(getProfilePicUrl(username)).into(ivProfilePic)
            tvRelativeTime.text = DateUtils.getRelativeDateTimeString(
                context,
                trip.creationTimeMs,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )
        }
        private fun getProfilePicUrl(username: String):String {
            val digest = MessageDigest.getInstance("MD5");
            val hash: ByteArray = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
            // code for this comes from this link - https://github.com/codepath/android_guides/wiki/Building-Simple-Chat-Client-with-Parse#9-create-custom-list-adapter
        }
    }

    }