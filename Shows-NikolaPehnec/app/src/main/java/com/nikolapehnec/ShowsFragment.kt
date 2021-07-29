package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolapehnec.databinding.FragmentShowsBinding
import com.nikolapehnec.model.Show

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

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private var adapter: ShowsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isTablet = context?.resources?.getBoolean(R.bool.isTablet)
        if (isTablet == true) {
            initTabletRecyclerView()
            initListeners()
        } else {
            initRecyclerView()
            initListeners()
        }

    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initRecyclerView()
        initListeners()
    }
*/
    private fun initRecyclerView() {
        binding.showsRecycler?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(ShowsResource.shows) { id ->
            run {
                ShowsFragmentDirections.actionShowToDetail(id.toInt()).also { action ->
                    findNavController().navigate(action)
                }
            }
        }

        binding.showsRecycler?.adapter = adapter
    }

    private fun initTabletRecyclerView() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.detailShowFragmentContainer) as NavHostFragment
        navHostFragment.navController.navigate(R.id.showDetail)

        binding.showsRecycler?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(ShowsResource.shows) { id ->
            run {
                val sharedPref =
                    activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
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

        binding.showsRecycler?.adapter = adapter
    }


    private fun initListeners() {
        binding.hideShowsButton?.setOnClickListener {
            if (adapter?.itemCount?.compareTo(0) != 0) {
                adapter?.setItems(listOf())
                binding.showsRecycler?.visibility = View.GONE
                binding.noShowsLayout?.visibility = View.VISIBLE
                binding.hideShowsButton?.text = getString(R.string.showShows)
            } else {
                adapter?.setItems(ShowsResource.shows)
                binding.showsRecycler?.visibility = View.VISIBLE
                binding.noShowsLayout?.visibility = View.GONE
                binding.hideShowsButton?.text = getString(R.string.hideShows)
            }
        }

        binding.logoutButton?.setOnClickListener {
            findNavController().navigate(R.id.actionLogout)
        }

    }
}