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
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.databinding.DialogShowsMenuBinding
import com.nikolapehnec.model.Show
import java.io.File


class ShowsFragment : Fragment() {

    private var _binding: ActivityShowsBinding? = null
    private val binding get() = _binding!!
    private var _dialogBinding: DialogShowsMenuBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private var sharedPref: SharedPreferences? = null
    private var adapter: ShowsAdapter? = null
    private var profileImage: File? = null

    private val viewModel: ShowsViewModel by viewModels()

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
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityShowsBinding.inflate(inflater, container, false)
        sharedPref = activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
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

        viewModel.getShowsLiveData().observe(viewLifecycleOwner, { shows ->
            updateShows(shows)
        })

        viewModel.initShows()

        initListeners()
        populateUI()
    }

    private fun populateUI() {
        if (FileUtil.getImageFile(requireContext()) != null) {
            profileImage = FileUtil.getImageFile(requireContext())
            binding.profilePicture?.setImageBitmap(BitmapFactory.decodeFile(profileImage?.path))
        }
    }

    private fun updateShows(shows: List<Show>) {
        adapter?.setItems(shows)

        if (adapter?.itemCount?.compareTo(0) == 0) {
            binding.showsRecycler.isVisible = false
            binding.noShowsLayout.isVisible = true
        } else {
            binding.showsRecycler.isVisible = true
            binding.noShowsLayout.isVisible = false
        }
    }

    private fun initRecyclerView() {
        binding.showsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(emptyList()) { id ->
            run {
                ShowsFragmentDirections.actionShowToDetail(id.toInt()).also { action ->

                    requireActivity().supportFragmentManager.setFragmentResult(
                        "showId",
                        bundleOf("showId" to id)
                    )
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

        adapter = ShowsAdapter(emptyList()) { id ->
            run {

                setFragmentResult("showId", bundleOf("showId" to id))
                navHostFragment.navController.navigate(R.id.showDetail)
            }
        }

        binding.showsRecycler.adapter = adapter
    }


    private fun initListeners() {
        /*binding.hideShowsButton?.setOnClickListener {
            if (adapter?.itemCount?.compareTo(0) != 0) {
                adapter?.setItems(listOf())
                binding.showsRecycler.isVisible = false
                binding.noShowsLayout.isVisible = true
                binding.hideShowsButton?.text = getString(R.string.showShows)
            } else {
                adapter?.setItems(ShowsResource.shows)
                binding.showsRecycler.isVisible = true
                binding.noShowsLayout.isVisible = false
                binding.hideShowsButton?.text = getString(R.string.hideShows)
            }
        }*/

        binding.profilePicture?.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        _dialogBinding = DialogShowsMenuBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.userEmail.text = sharedPref?.getString(getString(R.string.username), "")

        if (FileUtil.getImageFile(requireContext()) != null) {
            profileImage = FileUtil.getImageFile(requireContext())
            dialogBinding.profilePicture.setImageBitmap(BitmapFactory.decodeFile(profileImage?.path))
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
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        dialogBinding.changeProfilePhotoButton.setOnClickListener {
            cameraPermissionForProfilePicture.launch(arrayOf(android.Manifest.permission.CAMERA))
        }
    }
}