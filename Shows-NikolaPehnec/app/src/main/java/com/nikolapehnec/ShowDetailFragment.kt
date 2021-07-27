package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowDetailsBinding
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.User

class ShowDetailFragment : Fragment() {

    private var _binding: ActivityShowDetailsBinding? = null
    private val binding get() = _binding!!

    //val args: ShowDetailFragmentArgs by navArgs()
    private var adapter: ReviewsAdapter? = null
    private var showId: Int? = 0
    private val detailViewModel: ShowsDetailsViewModel by viewModels()

    private var showTitle = ""
    private var showDescription = ""
    private var showImageUrl = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            showDescription = result.getString("showDesc").toString()
            showTitle = result.getString("showTitle").toString()
            showImageUrl = result.getString("showImg").toString()

            showId?.let { detailViewModel.getReviewsByShowId(it) }
        }

        detailViewModel.getReviewsLiveData().observe(viewLifecycleOwner, { reviews ->
            loadUI(reviews)
            initRecyclerView(reviews)
        })

        detailViewModel.getPostReviewResultLiveData().observe(viewLifecycleOwner, { isSuccesful ->
            if (isSuccesful) {
                showId?.let { detailViewModel.getReviewsByShowId(it) }
            }
        })

        if (context?.resources?.getBoolean(R.bool.isTablet) == true) removeAppBar()

        initListeners()
    }

    private fun removeAppBar() {
        binding.toolbar.visibility = View.GONE
    }


    private fun loadUI(reviews: List<Review>) {
        binding.showName.text = showTitle
        binding.longDescription.text = showDescription

        val options: RequestOptions = RequestOptions().centerCrop()
        this.view?.let {
            Glide.with(it).load(showImageUrl).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).apply(options).into(binding.showImage)
            binding.showImage.isVisible = true
        }

        detailViewModel.calculateAverageGrade()?.let { grade ->
            binding.numReviews.text = String.format(
                getString(R.string.averageGrade), reviews.size,
                grade
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

    private fun initRecyclerView(reviews: List<Review>) {
        binding.reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ReviewsAdapter(reviews)
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
                /*val review = Review(
                    username.toString(),
                    dialogBinding.editReviewInput.text.toString(),
                    dialogBinding.ratingBarReview.rating.toInt(),
                    R.drawable.ic_profile_placeholder,
                    User(111, username!!, null)
                )*/

                detailViewModel.postReview(
                    dialogBinding.ratingBarReview.rating.toInt(),
                    dialogBinding.editReviewInput.text.toString(),
                    showId.toString()
                )

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