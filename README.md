# [MVVM](https://www.udemy.com/share/101svY3@dFMxQuzNvdUtskGTJ8qRvUskDkNLee1IOs6lWPbcOQHMt5FPsAYCQz0t-_OyAqFp/)
Learn the latest Android technologies including Dagger2, MVVM, Kotlin, RxJava, Retrofit, Mockito and Glide

## Functionality
### Loading Image
 <img width="300" src="https://github.com/YamamotoDesu/MVVM/blob/master/app/src/main/java/com/codewithkyo/countries/gif/loadingImage.gif">
 
 ProgressDrawable with an image loading extention
 ```kt
 import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithkyo.countries.R

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher_round)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)

}
 ```
 
 Adapter
 ```kt
     class CountryViewHolder(viewBinding: ItemCountryBinding): RecyclerView.ViewHolder(viewBinding.root) {

        private val imageView = viewBinding.imageView
        private val countryName = viewBinding.name
        private val countryCapital = viewBinding.capital
        private val progressDrawable = getProgressDrawable(viewBinding.root.context)

        fun bind(country: Country) {
            countryName.text = country.countryName
            countryCapital.text = country.capital
            imageView.loadImage(country.flag, progressDrawable)
        }
    }
 ```
 
### Refreshing
 <img width="300" src="https://github.com/YamamotoDesu/MVVM/blob/master/app/src/main/java/com/codewithkyo/countries/gif/refreshing.gif">
 
 ```kt
         binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.refresh()
        }

 ```

##  1. Integrating the ViewModel and LiveData components 
 <img width="300" src="https://user-images.githubusercontent.com/47273077/166446733-42b0a068-3746-4119-9095-855e87ac2303.png">

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

## 2. Integrating Retrofit with RxJava
<img width="300" src="https://user-images.githubusercontent.com/47273077/166620007-96232a8a-f609-4764-80b4-e4b53f8a405d.png">

### Model
Model(Data)
```kt
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name")
    val countryName: String?,
    @SerializedName("capital")
    val capital: String?,
    @SerializedName("flagPNG")
    val flag: String?
)
```

Model(API Interface)
```kt
interface CountriesApi {

    @GET("DevTides/countries/master/countriesV2.json")
    fun getCountries(): Single<List<Country>>
}
```

Model(Service)
```kt
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CountriesService {

    private val BASE_URL = "https://raw.githubusercontent.com"
    private val api: CountriesApi

    init {
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CountriesApi::class.java)
    }

    fun getCountries(): Single<List<Country>> {
        return api.getCountries()
    }
```

### ViewModel
ViewModel  
```kt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codewithkyo.countries.model.CountriesService
import com.codewithkyo.countries.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel: ViewModel() {

    private val countriesService = CountriesService()
    private val disposable = CompositeDisposable()

    val countries = MutableLiveData<List<Country>>()
    val countryLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        loading.value = true
        disposable.addAll(
            countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Country>>(){
                    override fun onSuccess(value: List<Country>?) {
                        countries.value = value
                        countryLoadError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        countryLoadError.value = true
                        loading.value = false
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
```

### View
Adapter  
```kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codewithkyo.countries.databinding.ItemCountryBinding
import com.codewithkyo.countries.model.Country
import com.codewithkyo.countries.util.getProgressDrawable
import com.codewithkyo.countries.util.loadImage

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

        private val imageView = viewBinding.imageView
        private val countryName = viewBinding.name
        private val countryCapital = viewBinding.capital
        private val progressDrawable = getProgressDrawable(viewBinding.root.context)

        fun bind(country: Country) {
            countryName.text = country.countryName
            countryCapital.text = country.capital
            imageView.loadImage(country.flag, progressDrawable)
        }
    }
}
```

Activity
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

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.refresh()
        }

        observeViewMode()
    }

    private fun observeViewMode() {
        viewModel.countries.observe(this, Observer { countries ->
            countries?.let {
                binding.countriesList.visibility = View.VISIBLE
                countriesAdapter.updateCountries(it) }
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

### Implement Dagger
ApiModule
```kt
import com.codewithkyo.countries.model.CountriesApi
import com.codewithkyo.countries.model.CountriesService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    private val BASE_URL = "https://raw.githubusercontent.com"

    @Provides
    fun provideCountriesApi(): CountriesApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CountriesApi::class.java)

    }

    @Provides
    fun provideSeCountriesService(): CountriesService {
        return CountriesService()
    }
}
```

Component
```kt
import com.codewithkyo.countries.model.CountriesService
import com.codewithkyo.countries.viewmodel.ListViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: CountriesService)

    fun inject(viewModel: ListViewModel)
}
```

Service
```kt
    @Inject
    lateinit var api: CountriesApi

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getCountries(): Single<List<Country>> {
        return api.getCountries()
    }
```

ViewModel
```kt
    @Inject
    lateinit var countriesService: CountriesService

    init {
        DaggerApiComponent.create().inject(this)
    }
  ```
  
