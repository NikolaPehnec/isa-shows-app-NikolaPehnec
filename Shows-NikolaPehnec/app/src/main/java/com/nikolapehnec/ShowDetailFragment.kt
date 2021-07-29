package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.databinding.FragmentShowDetailsBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ShowDetailFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    val args: ShowDetailFragmentArgs by navArgs()

    private var adapter: ReviewsAdapter? = null
    private var showId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        try {
            showId = args.showId - 1
        } catch (e: Exception) {
            showId = 0
        }

        val sharedPref =
            activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        val showIdPref = sharedPref?.getString(getString(R.string.showID), "-1")
        if (showIdPref != null && showIdPref != "-1") {
            showId = showIdPref.toInt() - 1
            removeAppBar()
        }

        loadUI()
        initRecyclerView()
        initListeners()
    }

    private fun removeAppBar() {
        binding.toolbar.isVisible = false
    }

    private fun loadUI() {
        val show = ShowsFragment.ShowsResource.shows[showId]
        binding.showName.text = show.name
        binding.longDescription.text = show.longDescription
        binding.showImage.setImageResource(show.imageResourceId)

        calculateAverageGrade(show)
    }

    private fun initListeners() {
        binding.toolbar.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.newReviewButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initRecyclerView() {
        binding.reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val show = ShowsFragment.ShowsResource.shows[showId]

        adapter = ReviewsAdapter(show.reviews)
        binding.reviewsRecyclerView.adapter = adapter

        if (adapter?.itemCount?.compareTo(0) == 0) {
            binding.reviewsVisible.isVisible = false
            binding.reviewsInvisible.isVisible = true
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.editReviewInput.requestFocus()

        val sharedPref =
            activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        val username = sharedPref?.getString(getString(R.string.username), "")

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.ratingBarReview.rating.compareTo(0.0) == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.mandatoryRating),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val show = ShowsFragment.ShowsResource.shows[showId]
                val review: Review = Review(
                    username.toString(),
                    dialogBinding.editReviewInput.text.toString(),
                    dialogBinding.ratingBarReview.rating.toInt(),
                    R.drawable.ic_profile_placeholder
                )

                show.reviews += review
                adapter?.reviewAdded(review)

                calculateAverageGrade(show)

                dialog.dismiss()

                if (adapter?.itemCount?.compareTo(0) != 0) {
                    binding.reviewsVisible.isVisible = true
                    binding.reviewsInvisible.isVisible = false
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