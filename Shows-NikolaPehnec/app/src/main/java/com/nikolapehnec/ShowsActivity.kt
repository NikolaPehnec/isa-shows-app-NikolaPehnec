package com.nikolapehnec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolapehnec.databinding.ActivityShowsBinding
import com.nikolapehnec.model.Show

class ShowsActivity : AppCompatActivity() {

    object ShowsResource {
        val shows = listOf(
            Show(
                "0",
                "The office",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "The Office is an American mockumentary sitcom television series that depicts the everyday" +
                        " work lives of office employees in the Scranton, Pennsylvania, branch of the fictional Dunder Mifflin Paper Company. " +
                        "It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons",
                listOf(), R.drawable.ic_office,
            ),
            Show(
                "1",
                "Stranger things",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "Stranger Things is an American science fiction horror drama television series created by the Duffer Brothers and " +
                        "streaming on Netflix. The brothers serve as showrunners and are executive producers along with Shawn Levy and Dan Cohen.",
                listOf(),
                R.drawable.ic_stranger_things
            ),
            Show(
                "2",
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

    private lateinit var binding: ActivityShowsBinding
