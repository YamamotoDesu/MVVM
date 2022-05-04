package com.codewithkyo.countries.view

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