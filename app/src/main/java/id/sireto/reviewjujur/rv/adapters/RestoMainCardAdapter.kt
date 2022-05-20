package id.sireto.reviewjujur.rv.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.sireto.reviewjujur.databinding.RestoMainCardBinding

class RestoMainCardAdapter() : RecyclerView.Adapter<RestoMainCardAdapter.RestoMainCardViewHolder>() {

    inner class RestoMainCardViewHolder(private val binding : RestoMainCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RestoMainCardViewHolder(RestoMainCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RestoMainCardViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}