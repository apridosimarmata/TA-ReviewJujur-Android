package id.sireto.reviewjujur.rv.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.sireto.reviewjujur.databinding.RestoMainCardBinding
import id.sireto.reviewjujur.main.business.BusinessDetailsActivity
import id.sireto.reviewjujur.models.BusinessResponse
import id.sireto.reviewjujur.utils.Constants
import java.io.Serializable

class BusinessMainCardAdapter(private val activity: Activity) : RecyclerView.Adapter<BusinessMainCardAdapter.RestoMainCardViewHolder>() {

    var businesses = arrayListOf<BusinessResponse>()

    inner class RestoMainCardViewHolder(private val binding : RestoMainCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(business : BusinessResponse){
            binding.restoMaiNRestoName.text = business.name
            binding.restoMainAddress.text = business.address
            binding.restoMainRating.text = if (business.reviewsCount == 0){
                "-"
            } else{
                "${(business.totalScore.toFloat()/business.reviewsCount.toFloat())} (${business.reviewsCount})"
            }

            Glide.with(binding.restoImage)
                .load(Constants.CDN + business.photo + ".png")
                .centerInside()
                .into(binding.restoImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RestoMainCardViewHolder(RestoMainCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RestoMainCardViewHolder, position: Int) {
        holder.bind(businesses[position])
        holder.itemView.setOnClickListener {
            activity.startActivity(Intent(activity, BusinessDetailsActivity::class.java).putExtra("business", businesses[position] as Serializable))
        }
    }

    override fun getItemCount(): Int = businesses.size
}