package com.nikolapehnec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nikolapehnec.databinding.ViewShowItemBinding
import com.nikolapehnec.model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val onClickCallback: (String, String, String, String) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        var binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))

        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(shows: List<Show>) {
        items = shows
        notifyDataSetChanged()
    }


    inner class ShowViewHolder(private val binding: ViewShowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) {
            binding.showName.text = item.title
            binding.showDescription.text = item.description

            val options: RequestOptions = RequestOptions().centerCrop()
            Glide.with(itemView).load(item.imgUrl).apply(options).into(binding.showImage)

            if (item.description == null) item.description = ""
            binding.root.setOnClickListener {
                onClickCallback(item.id, item.title, item.description!!, item.imgUrl)
            }
        }
    }


}