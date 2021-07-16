package com.nikolapehnec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolapehnec.databinding.ViewShowItemBinding
import com.nikolapehnec.model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val onClickCallback: (String) -> Unit
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
            binding.showName.text = item.name
            binding.showDescription.text = item.description
            binding.showImage.setImageResource(item.imageResourceId)
        }
    }


}