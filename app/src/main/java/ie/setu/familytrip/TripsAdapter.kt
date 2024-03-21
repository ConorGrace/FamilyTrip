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
import ie.setu.familytrip.databinding.ItemPostBinding
import ie.setu.familytrip.models.Trip
import java.math.BigInteger
import java.security.MessageDigest

class TripsAdapter(
    val context: Context,
    val trips: List<Trip>,
    val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<TripsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = trips.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = trips[holder.adapterPosition]
        holder.bind(trip, onItemClickListener)
    }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivPost: ImageView = itemView.findViewById(R.id.ivPost)
        val tvRelativeTime: TextView = itemView.findViewById(R.id.tvRelativeTime)
        val ivProfilePic: ImageView = itemView.findViewById(R.id.ivProfilePic)

        fun bind(trip: Trip, onItemClickListener: OnItemClickListener?) {
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

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(trip)
            }
        }

        private fun getProfilePicUrl(username: String): String {
            val digest = MessageDigest.getInstance("MD5")
            val hash: ByteArray = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }
}
