package id.sireto.reviewjujur.rv.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.sireto.reviewjujur.databinding.RestoMainCardBinding
import id.sireto.reviewjujur.models.BusinessResponse

class BusinessMainCardAdapter() : RecyclerView.Adapter<BusinessMainCardAdapter.RestoMainCardViewHolder>() {

    var businesses = arrayListOf<BusinessResponse>()

    inner class RestoMainCardViewHolder(private val binding : RestoMainCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(business : BusinessResponse){
            binding.restoMaiNRestoName.text = business.name
            binding.restoMainAddress.text = business.address
            binding.restoMainRating.text = if (business.reviewsCount == 0){
                "-"
            } else{
                "${(business.totalScore/business.reviewsCount)} (${business.reviewsCount})"
            }

            Glide.with(binding.restoImage)
                .load("https://www.pinhome.id/info-area/wp-content/uploads/2022/03/cafe-di-Banda-Aceh.jpg")
                .into(binding.restoImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RestoMainCardViewHolder(RestoMainCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RestoMainCardViewHolder, position: Int) {
        holder.bind(businesses[position])
    }

    override fun getItemCount(): Int = businesses.size
}