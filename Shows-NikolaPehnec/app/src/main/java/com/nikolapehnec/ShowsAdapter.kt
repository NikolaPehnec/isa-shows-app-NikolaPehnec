package com.nikolapehnec

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolapehnec.model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val onClickCallback: (String, String, String, String) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        // var binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context), parent)
        val showCardView = ShowCardView(parent.context)
        /*showCardView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )*/

        return ShowViewHolder(showCardView)
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


    inner class ShowViewHolder(private val showCardView: ShowCardView) :
        RecyclerView.ViewHolder(showCardView.rootView) {

        fun bind(item: Show) {
            showCardView.setTitle(item.title)
            showCardView.setDescription(item.description)
            showCardView.setImage(item.imgUrl)

            showCardView.setClickListener(
                onClickCallback,
                item.id, item.title, item.description!!, item.imgUrl
            )

        }
    }


}