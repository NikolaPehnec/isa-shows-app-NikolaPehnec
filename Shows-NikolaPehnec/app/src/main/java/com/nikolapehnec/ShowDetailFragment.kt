package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowDetailsBinding
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ShowDetailFragment : Fragment() {

    private var _binding: ActivityShowDetailsBinding? = null
    private val binding get() = _binding!!

    //val args: ShowDetailFragmentArgs by navArgs()

    private var adapter: ReviewsAdapter? = null
    private var showId: Int? = 0
    private val detailViewModel: ShowsDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().supportFragmentManager.setFragmentResultListener(
            "showId",
            viewLifecycleOwner
        ) { key, result ->
            showId = result.getString("showId")?.toInt()
            showId?.let {
                detailViewModel.initShow(it)
            }
            //Toast.makeText(requireContext(), showId?.toString(), Toast.LENGTH_SHORT).show()
        }

        detailViewModel.getShowsLiveData().observe(viewLifecycleOwner, { show ->
            loadUI(show)
            initRecyclerView(show)
        })

        if (context?.resources?.getBoolean(R.bool.isTablet) == true) removeAppBar()


        initListeners()
    }

    private fun removeAppBar() {
        binding.toolbar.visibility = View.GONE
    }


    private fun loadUI(show: Show) {
        binding.showName.text = show.name
        binding.longDescription.text = show.longDescription
        binding.showImage.setImageResource(show.imageResourceId)

        detailViewModel.calculateAverageGrade()?.let { grade ->
            binding.numReviews.text = String.format(
                getString(R.string.averageGrade), show.reviews.size, grade
            )
            binding.ratingBar.rating = grade
        }

    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            //Finish fragment, pop -> showsFragment
            //findNavController().navigateUp()
            //Isto pop, ako nema nista na backstacku finish
            activity?.onBackPressed()
        }

        binding.newReviewButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initRecyclerView(show: Show) {
        binding.reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ReviewsAdapter(show.reviews)
        binding.reviewsRecyclerView.adapter = adapter

        if (adapter?.itemCount?.compareTo(0) == 0) {
            binding.reviewsVisible.visibility = View.GONE
            binding.reviewsInvisible.visibility = View.VISIBLE
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
                Toast.makeText(requireContext(), "Rating is mandatory!", Toast.LENGTH_SHORT).show()
            } else {
                val review = Review(
                    username.toString(),
                    dialogBinding.editReviewInput.text.toString(),
                    dialogBinding.ratingBarReview.rating.toInt(),
                    R.drawable.ic_profile_placeholder
                )
                detailViewModel.addReview(review)

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


}