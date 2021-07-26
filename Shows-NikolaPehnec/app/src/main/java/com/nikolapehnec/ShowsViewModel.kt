package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.ShowsViewModel.ShowsResource.shows
import com.nikolapehnec.model.Show

class ShowsViewModel : ViewModel() {

    object ShowsResource {
        val shows = mutableListOf(
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

    private val showsLiveData: MutableLiveData<List<Show>> by lazy {
        MutableLiveData<List<Show>>()
    }

    fun getShowsLiveData(): LiveData<List<Show>> {
        return showsLiveData
    }

    fun initShows() {
        showsLiveData.value = shows
    }

    fun addShow(show: Show) {
        shows.add(show)
        initShows()
    }


}