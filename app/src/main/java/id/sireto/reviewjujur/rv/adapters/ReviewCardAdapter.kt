package id.sireto.reviewjujur.rv.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.sireto.reviewjujur.databinding.ReviewCardBinding
import id.sireto.reviewjujur.models.ReviewResponse

class ReviewCardAdapter : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    var reviews = arrayListOf<ReviewResponse>()

    inner class ReviewCardViewHolder(private val binding: ReviewCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(reviewResponse: ReviewResponse){
            binding.reviewCardCreatedAt.text = ""
            binding.reviewCardStatus.text = reviewResponse.status
            binding.reviewCardText.text = reviewResponse.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder =
        ReviewCardViewHolder(ReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size
}