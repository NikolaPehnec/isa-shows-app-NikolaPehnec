package com.nikolapehnec

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.databinding.DialogShowsMenuBinding
import com.nikolapehnec.model.Show
import com.nikolapehnec.viewModel.ShowDetailsViewModelFactory
import com.nikolapehnec.viewModel.ShowsDetailsSharedViewModel
import com.nikolapehnec.viewModel.ShowsViewModel
import com.nikolapehnec.viewModel.ShowsViewModelFactory
import java.io.File

class ShowsFragment : Fragment() {
    private var _binding: ActivityShowsBinding? = null
    private val binding get() = _binding!!
    private var _dialogBinding: DialogShowsMenuBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private var sharedPref: SharedPreferences? = null
    private var adapter: ShowsAdapter? = null
    private var profileImage: File? = null

    private var landscapeOrientation: Boolean = true
    private var topRatedShows: Boolean = false
    private var changeShows: Boolean = false

    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((activity?.application as ShowsApp).showsDatabase!!, requireContext())
    }

    private val detailViewModel: ShowsDetailsSharedViewModel by activityViewModels() {
        ShowDetailsViewModelFactory(
            (activity?.application as ShowsApp).showsDatabase!!,
            requireContext()
        )
    }

    private val cameraPermissionForProfilePicture =
        preparePermissionsContract(onPermissionsGranted = {
            val imageFile: File? = FileUtil.createImageFile(requireContext())

            val avatarUri: Uri? = imageFile?.let { file ->
                FileProvider.getUriForFile(
                    requireContext(),
                    activity?.applicationContext?.packageName.toString() + ".fileprovider",
                    file
                )
            }

            cameraContract.launch(avatarUri)
        })

    private val cameraContract = prepareCameraContract(onSuccess = {
        profileImage = FileUtil.getImageFile(requireContext())

        dialogBinding.profilePicture.setImageBitmap(BitmapFactory.decodeFile(profileImage?.path))
        binding.profilePicture?.setImageBitmap(BitmapFactory.decodeFile(profileImage?.path))

        val id = sharedPref?.getString(getString(R.string.user_id), "")
        val email = sharedPref?.getString(getString(R.string.email), "")

        if (id != null && email != null && profileImage != null) {
            viewModel.sendPicture(profileImage!!.path, sharedPref!!)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityShowsBinding.inflate(inflater, container, false)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isTablet = context?.resources?.getBoolean(R.bool.isTablet)
        if (isTablet == true) {
            initTabletRecyclerView()
        } else {
            initRecyclerView()
        }

        viewModel.getShowsEntityLiveData().observe(viewLifecycleOwner, { shows ->
            if (shows != null) {
                if (shows.size == 0) {
                    binding.showsRecycler.isVisible = false
                    binding.noShowsLayout.isVisible = true
                } else {
                    binding.showsRecycler.isVisible = true
                    binding.noShowsLayout.isVisible = false
                    updateShows(shows.map {
                        Show(
                            it.id,
                            it.avgRating,
                            it.description,
                            it.imgUrl,
                            it.numOfReviews,
                            it.title
                        )
                    })
                }
            }

            binding.progressCircular?.isVisible = false
        })

        binding.progressCircular?.isVisible = true
        viewModel.getShows()

        initListeners()
        populateUI()
        checkInternetConnection()
    }

    private fun populateUI() {
        val imgUrl = sharedPref?.getString(getString(R.string.imgUrl), "null")
        if (imgUrl != "null") {
            binding.profilePicture?.let { Glide.with(this).load(imgUrl).into(it) }
        }
    }

    private fun updateShows(shows: List<Show>) {
        if (!changeShows && adapter?.itemCount?.compareTo(0) != 0) return

        adapter?.setItems(shows)

        if (adapter?.itemCount?.compareTo(0) == 0) {
            binding.showsRecycler.isVisible = false
            binding.noShowsLayout.isVisible = true
        } else {
            binding.showsRecycler.isVisible = true
            binding.noShowsLayout.isVisible = false
        }

        changeShows = false
    }

    private fun initRecyclerView() {
        binding.showsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(emptyList(),false) { id, title, description, imageurl ->
            run {
                ShowsFragmentDirections.actionShowToDetail(id.toInt()).also { action ->
                    detailViewModel.showId = id.toInt()
                    detailViewModel.showTitle = title
                    detailViewModel.showDesc = description
                    detailViewModel.imgUrl = imageurl

                    findNavController().navigate(action)
                }
            }
        }

        binding.showsRecycler.adapter = adapter
    }

    private fun initTabletRecyclerView() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.detailShowFragmentContainer) as NavHostFragment
        navHostFragment.navController.navigate(R.id.showDetail)

        binding.showsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(emptyList(),true) { id, title, description, imageurl ->
            run {
                detailViewModel.showId = id.toInt()
                detailViewModel.showTitle = title
                detailViewModel.showDesc = description
                detailViewModel.imgUrl = imageurl
                navHostFragment.navController.navigate(R.id.showDetail)
            }
        }

        binding.showsRecycler.adapter = adapter
    }


    private fun initListeners() {
        binding.profilePicture?.setOnClickListener {
            showBottomSheet()
        }

        initTopRatedListeners()
        initOrientationListeners()
    }

    private fun initOrientationListeners() {
        binding.orientationFab?.setOnClickListener {
            if (landscapeOrientation) {
                binding.orientationFab?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_fab_vertical,
                        null
                    )
                )
                binding.showsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
                adapter?.verticalLayout()
                landscapeOrientation = false
            } else {
                binding.orientationFab?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_fab_landscape,
                        null
                    )
                )
                adapter?.horizontalLayout()

                binding.showsRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                landscapeOrientation = true
            }
        }
    }

    private fun initTopRatedListeners() {
        binding.topRatedShowsButton?.apply {
            setOnClickListener {
                changeShows = true

                if (!topRatedShows) {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.background)
                    iconTint = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    topRatedShows = true

                    binding.progressCircular?.isVisible = true
                    viewModel.getTopRatedShows()
                } else {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.background))
                    backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.white)
                    iconTint = ContextCompat.getColorStateList(requireContext(), R.color.background)
                    topRatedShows = false

                    binding.progressCircular?.isVisible = true
                    viewModel.getShows()
                }
            }
        }
    }

    private fun showBottomSheet() {
        val dialog =
            BottomSheetDialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        _dialogBinding = DialogShowsMenuBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.userEmail.text = sharedPref?.getString(getString(R.string.username), "")

        //inace se ne vidi dobro na tabletu
        dialog.behavior.peekHeight = 2000

        binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.gray))

        dialog.setOnDismissListener {
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }

        val imgUrl = sharedPref?.getString(getString(R.string.imgUrl), "null")
        if (imgUrl != "null") {
            dialogBinding.profilePicture.let { Glide.with(this).load(imgUrl).into(it) }
        }

        initBottomSheetListeners(dialogBinding, dialog)
        dialog.show()
    }

    private fun initBottomSheetListeners(
        dialogBinding: DialogShowsMenuBinding,
        bottomSheetDialog: BottomSheetDialog
    ) {
        dialogBinding.logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.notification))
            builder.setMessage(getString(R.string.logoutMessage))

            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                with(sharedPref?.edit()) {
                    this?.putBoolean(getString(R.string.remember_me), false)
                    this?.apply()

                    bottomSheetDialog.dismiss()
                    findNavController().navigate(R.id.actionLogout)
                }
            }
            builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                bottomSheetDialog.dismiss()
            }

            builder.show()
        }

        dialogBinding.changeProfilePhotoButton.setOnClickListener {
            cameraPermissionForProfilePicture.launch(arrayOf(android.Manifest.permission.CAMERA))
        }
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}