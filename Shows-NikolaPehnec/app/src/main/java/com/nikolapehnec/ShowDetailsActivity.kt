package com.nikolapehnec

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowDetailsBinding
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.databinding.ItemReviewBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ShowDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowDetailsBinding

    private var adapter: ReviewsAdapter? = null
    private var showId: Int = 0

    companion object {
        private const val EXTRA_SHOWID = "EXTRA_SHOWID"

        fun buildIntent(showId: String, context: Activity): Intent {
            val intent = Intent(context, ShowDetailsActivity::class.java)
            intent.putExtra(EXTRA_SHOWID, showId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        showId = intent.extras?.get(EXTRA_SHOWID).toString().toInt()

        loadUI()
        initRecyclerView()
        initListeners()
    }

    private fun loadUI() {
        val show = ShowsActivity.ShowsResource.shows[showId]
        binding.showName.text = show.name
        binding.longDescription.text = show.longDescription
        binding.showImage.setImageResource(show.imageResourceId)

        calculateAverageGrade(show)
    }

    private fun initListeners() {
        binding.toolbar.setOnClickListener {
            onBackPressed()
        }

        binding.newReviewButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initRecyclerView() {
        binding.reviewsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val show = ShowsActivity.ShowsResource.shows[showId]

        adapter = ReviewsAdapter(show.reviews)
        binding.reviewsRecyclerView.adapter = adapter

        if (adapter?.itemCount?.compareTo(0) == 0) {
            binding.reviewsVisible.isVisible=false
            binding.reviewsInvisible.isVisible = true
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.editReviewInput.requestFocus()

        val sharedPref = applicationContext.getSharedPreferences("1", Context.MODE_PRIVATE)
        val username = sharedPref.getString(getString(R.string.username), "")

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.ratingBarReview.rating.compareTo(0.0) == 0) {
                Toast.makeText(this, getString(R.string.mandatoryRating), Toast.LENGTH_SHORT).show()
            } else {
                val show = ShowsActivity.ShowsResource.shows[showId]
                val review:Review= Review(
                        username.toString(),
                        dialogBinding.editReviewInput.text.toString(),
                        dialogBinding.ratingBarReview.rating.toInt(),
                        R.drawable.ic_profile_placeholder)

                show.reviews+=review
                adapter?.reviewAdded(review)

                calculateAverageGrade(show)

                dialog.dismiss()

                if (adapter?.itemCount?.compareTo(0) != 0) {
                    binding.reviewsVisible.visibility = View.VISIBLE
                    binding.reviewsInvisible.visibility = View.GONE
                }

            }
        }

        dialogBinding.closeBottomSheet.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun calculateAverageGrade(show: Show) {
        val average: Float = show.reviews.map { r -> r.grade }.average().toFloat()
        binding.numReviews.text = String.format(
            getString(R.string.averageGrade), show.reviews.size, average
        )
        binding.ratingBar.rating = average
    }

}