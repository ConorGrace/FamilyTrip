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

        fun bind(trip: Trip) {
            tvUsername.text = trip.user?.username
            tvDescription.text = trip.description
            Glide.with(context).load(trip.imageUrl).into(ivPost)
            tvRelativeTime.text = DateUtils.getRelativeDateTimeString(
                context,
                trip.creationTimeMs,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )
        }
    }

    }