package com.nikolapehnec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.model.Show

class ShowsActivity : AppCompatActivity() {

    private val shows = listOf(
        Show(
            "1", "The office",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
            R.drawable.ic_office
        ),
        Show(
            "2",
            "Stranger things",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
            R.drawable.ic_stranger_things
        ),
        Show(
            "3",
            "Krv nije voda",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
            R.drawable.krv_nije_voda_1
        )
    )

    private lateinit var binding: ActivityShowsBinding

    private var adapter: ShowsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initRecyclerView()
        initListeners()
    }

    private fun initRecyclerView() {
        binding.showsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = ShowsAdapter(shows) {
        }

        binding.showsRecycler.adapter = adapter
    }

    private fun initListeners() {
        binding.hideShowsButton.setOnClickListener {
            if (adapter?.itemCount?.compareTo(0) != 0) {
                adapter?.setItems(listOf())
                binding.showsRecycler.visibility = View.GONE
                binding.noShowsLayout.visibility = View.VISIBLE
                binding.hideShowsButton.text = getString(R.string.showShows)
            } else {
                adapter?.setItems(shows)
                binding.showsRecycler.visibility = View.VISIBLE
                binding.noShowsLayout.visibility = View.GONE
                binding.hideShowsButton.text = getString(R.string.hideShows)
            }
        }

    }
}