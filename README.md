# [MVVM](https://www.udemy.com/share/101svY3@dFMxQuzNvdUtskGTJ8qRvUskDkNLee1IOs6lWPbcOQHMt5FPsAYCQz0t-_OyAqFp/)
Learn the latest Android technologies including Dagger2, MVVM, Kotlin, RxJava, Retrofit, Mockito and Glide

##  Integrating the ViewModel and LiveData components 
Model
```kt
data class Country(val countryName: String?)
```

ViewModel
```kt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codewithkyo.countries.model.Country

class ListViewModel: ViewModel() {

    val countries = MutableLiveData<List<Country>>()
    val countryLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        val mockData = listOf(
            Country("CountryA"),
            Country("CountryB"),
            Country("CountryC"),
            Country("CountryD"),
            Country("CountryF"),
            Country("CountryG"),
            Country("CountryH"),
            Country("CountryI"),
            Country("CountryJ")
        )

        countryLoadError.value = false
        loading.value = false
        countries.value = mockData
    }
}
```

Adapter
```kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codewithkyo.countries.databinding.ItemCountryBinding
import com.codewithkyo.countries.model.Country

class CountryListAdapter(var countries: ArrayList<Country>): RecyclerView.Adapter<CountryListAdapter.CountryViewHolder>() {

    fun updateCountries(newCountries: List<Country>) {
        countries.clear()
        countries.addAll(newCountries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountryViewHolder(
        ItemCountryBinding.inflate(LayoutInflater.from(parent.context) , parent,false)
    )

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
       holder.bind(countries[position])
    }

    override fun getItemCount() = countries.size

    class CountryViewHolder(viewBinding: ItemCountryBinding): RecyclerView.ViewHolder(viewBinding.root) {

        private val countryName = viewBinding.name

        fun bind(country: Country) {
            countryName.text = country.countryName
        }
    }
}
```

View
```kt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codewithkyo.countries.databinding.ActivityMainBinding
import com.codewithkyo.countries.viewmodel.ListViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: ListViewModel
    private val countriesAdapter = CountryListAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        binding.countriesList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = countriesAdapter
        }

        observeViewMode()
    }

    private fun observeViewMode() {
        viewModel.countries.observe(this, Observer { countries ->
            countries?.let { countriesAdapter.updateCountries(it) }
        })

        viewModel.countryLoadError.observe(this, Observer { isError ->
            isError?.let { binding.listError.visibility = if (it) View.VISIBLE else View.GONE }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                binding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
                if (it) {
                    binding.listError.visibility = View.GONE
                    binding.countriesList.visibility = View.GONE
                }
            }
        })
    }
}
```
