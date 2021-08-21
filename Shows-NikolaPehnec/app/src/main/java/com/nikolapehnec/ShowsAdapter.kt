package com.nikolapehnec

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nikolapehnec.model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val tablet: Boolean,
    private val onClickCallback: (String, String, String, String) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    private var landscape: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val showCardView = ShowCardView(parent.context)
        return ShowViewHolder(showCardView)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position], position, tablet)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(shows: List<Show>) {
        items = shows
        notifyDataSetChanged()
    }

    fun verticalLayout() {
        landscape = false
    }

    fun horizontalLayout() {
        landscape = true
    }


    inner class ShowViewHolder(private val showCardView: ShowCardView) :
        RecyclerView.ViewHolder(showCardView.rootView) {

        fun bind(item: Show, position: Int, tablet: Boolean) {
            showCardView.setTitle(item.title)
            showCardView.setDescription(item.description)
            showCardView.setImage(item.imgUrl)
            showCardView.setClickListener(
                onClickCallback,
                item.id, item.title, item.description!!, item.imgUrl
            )

            //Prvi show odabran u tablet modeu
            if(position==0 && tablet){
                showCardView.performClick()
            }
            showCardView.binding.showDescription.isVisible = landscape
        }
    }


}