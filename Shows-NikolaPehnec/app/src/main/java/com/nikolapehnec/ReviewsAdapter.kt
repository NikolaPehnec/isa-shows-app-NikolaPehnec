package com.nikolapehnec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolapehnec.databinding.ItemReviewBinding
import com.nikolapehnec.databinding.ViewShowItemBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ReviewsAdapter(
    private var items: List<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        var binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context))

        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setNewReviews(reviews: List<Review>) {
        items =reviews
        notifyDataSetChanged()
    }

    fun addReview(review:Review){
        items+=review
        notifyItemInserted(items.size)
    }


    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            binding.reviewImage.setImageResource(item.imageResourceId)
            binding.reviewText.text=item.text
            binding.reviewUsername.text = item.user
            binding.reviewGrade.text = item.grade.toString()
        }
    }


}