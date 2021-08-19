package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.databinding.FragmentShowDetailsBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.viewModel.ShowsDetailsSharedViewModel

class ShowDetailFragment : Fragment() {
    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private var adapter: ReviewsAdapter? = null

    //Ne treba factory jer je vec kreiran u ShowsFragmentu
    private val detailViewModel: ShowsDetailsSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInternetConnection()
        detailViewModel.getReviewsByShowId()

        detailViewModel.getReviewsLiveData().observe(viewLifecycleOwner, { reviews ->
            binding.progressCircular.isVisible = false

            val reviewsModel: List<Review> = reviews.map {
                Review(it.id.toString(), it.comment, it.rating, it.showId.toInt(), it.user)
            }
            loadUI(reviewsModel)
            initRecyclerView(reviewsModel)
        })

        detailViewModel.getPostReviewResultLiveData().observe(viewLifecycleOwner, { isSuccesful ->
            binding.progressCircular.isVisible = false

            if (isSuccesful) {
                detailViewModel.getReviewsByShowId()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.postingReviewUnsuccesful))
                if (detailViewModel.getMessage() != null) {
                    builder.setMessage(detailViewModel.getMessage())
                } else {
                    builder.setMessage(getString(R.string.postingReviewUnsuccesfuMess))
                }
                builder.setPositiveButton(getString(R.string.Ok)) { _, _ ->
                }

                builder.show()
            }
        })

        if (context?.resources?.getBoolean(R.bool.isTablet) == true) removeAppBar()

        initListeners()
    }

    private fun removeAppBar() {
        binding.toolbar.visibility = View.GONE
    }


    private fun loadUI(reviews: List<Review>) {
        binding.showName.text = detailViewModel.showTitle
        binding.longDescription.text = detailViewModel.showDesc

        val options: RequestOptions = RequestOptions().centerCrop()
        this.view?.let {
            Glide.with(it).load(detailViewModel.imgUrl).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).apply(options).into(binding.showImage)
            binding.showImage.isVisible = true
        }

        detailViewModel.calculateAverageGrade()?.let { grade ->
            binding.numReviews.text = String.format(
                getString(R.string.averageGrade), reviews.size, grade
            )
            binding.ratingBar.rating = grade
        }

    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
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

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.ratingBarReview.rating.compareTo(0.0) == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.ratingMandatory),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                checkInternetConnection()

                detailViewModel.postReview(
                    dialogBinding.ratingBarReview.rating.toInt(),
                    dialogBinding.editReviewInput.text.toString(),
                    detailViewModel.showId.toString(),
                    activity?.getPreferences(Context.MODE_PRIVATE)!!
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

    private fun checkInternetConnection() {
        val networkChecker = NetworkChecker(requireContext())
        if (!networkChecker.isOnline()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.notification))
            builder.setMessage(getString(R.string.noInternet))

            builder.setPositiveButton(getString(R.string.Ok)) { _, _ ->
            }

            builder.show()
        } else {
            binding.progressCircular.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}