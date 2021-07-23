package com.nikolapehnec

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowDetailsBinding
import com.nikolapehnec.databinding.DialogAddReviewBinding
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ShowDetailFragment : Fragment() {

    private var _binding: ActivityShowDetailsBinding? = null
    private val binding get() = _binding!!

    val args: ShowDetailFragmentArgs by navArgs()

    private var adapter: ReviewsAdapter? = null
    private var showId: Int = 0

    /*companion object {
        private const val EXTRA_SHOWID = "EXTRA_SHOWID"

        fun buildIntent(showId: String, context: Activity): Intent {
            val intent = Intent(context, ShowDetailsActivity::class.java)
            intent.putExtra(EXTRA_SHOWID, showId)
            return intent
        }
    }*/

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
        val fm: FragmentManager? = fragmentManager
        for (entry in 0 until fm!!.getBackStackEntryCount()) {
            Log.i(ContentValues.TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).toString())
        }

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
        binding.toolbar.visibility = View.GONE
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        showId = intent.extras?.get(EXTRA_SHOWID).toString().toInt() - 1

        loadUI()
        initRecyclerView()
        initListeners()
    }*/

    private fun loadUI() {
        val show = ShowsFragment.ShowsResource.shows[showId]
        //binding.showName.text = show.name

        binding.showName.text=show.name
        binding.longDescription.text = show.longDescription
        binding.showImage.setImageResource(show.imageResourceId)

        calculateAverageGrade(show)
        calculateRecyclerSize(show)
    }

    private fun initListeners() {

        //listener na sliku
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

    private fun initRecyclerView() {
        binding.reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val show = ShowsFragment.ShowsResource.shows[showId]

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
                val show = ShowsFragment.ShowsResource.shows[showId]
                show.addReview(
                    Review(
                        username.toString(),
                        dialogBinding.editReviewInput.text.toString(),
                        dialogBinding.ratingBarReview.rating.toInt(),
                        R.drawable.ic_profile_placeholder
                    )
                )

                adapter?.setNewReviews(show.reviews)

                calculateAverageGrade(show)
                calculateRecyclerSize(show)

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

    private fun calculateRecyclerSize(show: Show) {
        if (show.reviews.size == 2) {
            binding.reviewsRecyclerView.layoutParams.height = 400
        } else if (show.reviews.size >= 3 && binding.reviewsRecyclerView.layoutParams.height != 700) {
            binding.reviewsRecyclerView.layoutParams.height = 700
        }
    }
}