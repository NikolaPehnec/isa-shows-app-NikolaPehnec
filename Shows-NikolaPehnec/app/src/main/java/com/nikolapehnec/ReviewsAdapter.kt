package com.nikolapehnec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nikolapehnec.databinding.ItemReviewBinding
import com.nikolapehnec.model.Review

class ReviewsAdapter(
    private var items: List<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        var binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setNewReviews(reviews: List<Review>) {
        items = reviews
        notifyDataSetChanged()
    }

    fun addReview(review: Review) {
        items += review
        notifyItemInserted(items.size)
    }


    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            if (item.user.imageUrl != null) {
                val options: RequestOptions = RequestOptions().centerCrop()
                Glide.with(itemView).load(item.user.imageUrl).apply(options)
                    .into(binding.reviewImage)
            }
            binding.reviewText.text = item.comment
            if (item.comment.equals(""))
                binding.reviewText.isVisible = false
            binding.reviewUsername.text = item.user.email
            binding.reviewGrade.text = item.rating.toString()
        }
    }


}