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
    val showSpinner: Boolean = false,
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
            if (showSpinner) { // Only inflate the spinner layout if showSpinner is true
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_spinner, parent, false)
                ViewHolderSpinner(view)
            } else {
                // Return a dummy view holder if showSpinner is false
                DummyViewHolder(View(context))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showSpinner) trips.size + 1 else trips.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_SPINNER else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val trip = trips[position - 1]
            holder.bind(trip, onItemClickListener)
        } else if (holder is ViewHolderSpinner) {
            holder.bind(countries)
        }
    }

    inner class DummyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivPost: ImageView = itemView.findViewById(R.id.ivPost)
        val tvRelativeTime: TextView = itemView.findViewById(R.id.tvRelativeTime)
        val ivProfilePic: ImageView = itemView.findViewById(R.id.ivProfilePic)
        val tvCountry: TextView = itemView.findViewById(R.id.tvCountry)
        val tvFamilySize: TextView = itemView.findViewById(R.id.tvFamilySize)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)

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

            tvCountry.text = trip.spinnerCountries
            tvFamilySize.text = "${trip.numFamSize} people"
            tvRating.text = "${trip.numFamSize} stars"

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
