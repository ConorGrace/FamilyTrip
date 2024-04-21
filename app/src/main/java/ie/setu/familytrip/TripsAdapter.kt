import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ie.setu.familytrip.R
import ie.setu.familytrip.databinding.ItemPostBinding
import ie.setu.familytrip.models.Trip
import java.math.BigInteger
import java.security.MessageDigest

class TripsAdapter(
    val context: Context,
    val trips: List<Trip>,
    val onItemClickListener: OnItemClickListener? = null,
    val countries: Array<String> = emptyArray()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_spinner, parent, false)
            ViewHolderSpinner(view)
        }
    }

    override fun getItemCount() = trips.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_SPINNER else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val trip = trips[position - 1] // Subtract 1 to adjust for spinner
            holder.bind(trip, onItemClickListener)
        } else if (holder is ViewHolderSpinner) {
            holder.bind(countries)
        }
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

    inner class ViewHolderSpinner(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val spinner: Spinner = itemView.findViewById(R.id.spinner_countries)

        fun bind(countries: Array<String>) {
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, countries)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_SPINNER = 1
    }
}
