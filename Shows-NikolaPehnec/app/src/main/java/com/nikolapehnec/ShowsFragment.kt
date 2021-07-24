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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.databinding.DialogShowsMenuBinding
import com.nikolapehnec.model.Show
import java.io.File


class ShowsFragment : Fragment() {

    object ShowsResource {
        val shows = listOf(
            Show(
                "1",
                "The office",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "The Office is an American mockumentary sitcom television series that depicts the everyday" +
                        " work lives of office employees in the Scranton, Pennsylvania, branch of the fictional Dunder Mifflin Paper Company. " +
                        "It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons",
                listOf(), R.drawable.ic_office,
            ),
            Show(
                "2",
                "Stranger things",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "Stranger Things is an American science fiction horror drama television series created by the Duffer Brothers and " +
                        "streaming on Netflix. The brothers serve as showrunners and are executive producers along with Shawn Levy and Dan Cohen.",
                listOf(),
                R.drawable.ic_stranger_things
            ),
            Show(
                "3",
                "Krv nije voda",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "Krv nije voda je hrvatska televizijska serija snimana od 2011. do 2013. godine.Serija je nadahnuta svakodnevnim životnim " +
                        "pričama koje pogađaju mnoge obitelji, poput nestanka člana obitelji, upadanja u zamku nagomilanih dugova, iznenadnog kraha braka" +
                        " zbog varanja supružnika, borbe oko skrbništva nad djecom, ovisnosti o kockanju ili problema s nestašnom djecom.",
                listOf(),
                R.drawable.krv_nije_voda_1
            )
        )
    }

    private var _binding: ActivityShowsBinding? = null
    private val binding get() = _binding!!
    private var _dialogBinding: DialogShowsMenuBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private var sharedPref: SharedPreferences? = null
    private var adapter: ShowsAdapter? = null
    private var profileImage: File? = null

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

        initListeners()
        populateUI()
    }

    private fun populateUI() {
        if (profileImage != null) {
            binding.profilePicture?.setImageBitmap(BitmapFactory.decodeFile(profileImage?.path))
        }
    }

    private fun initRecyclerView() {
        binding.showsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(ShowsResource.shows) { id ->
            run {
                ShowsFragmentDirections.actionShowToDetail(id.toInt()).also { action ->
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

        adapter = ShowsAdapter(ShowsResource.shows) { id ->
            run {
                with(sharedPref?.edit()) {
                    this?.putString(
                        getString(R.string.showID),
                        id
                    )
                    this?.apply()
                }

                navHostFragment.navController.navigate(R.id.showDetail)
            }
        }

        binding.showsRecycler.adapter = adapter
    }


    private fun initListeners() {
        binding.hideShowsButton?.setOnClickListener {
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
        }

        binding.profilePicture?.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        _dialogBinding = DialogShowsMenuBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.userEmail.text = sharedPref?.getString(getString(R.string.username), "")
        if (profileImage != null) {
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