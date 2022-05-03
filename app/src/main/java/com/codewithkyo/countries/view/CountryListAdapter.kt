package com.codewithkyo.countries.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewithkyo.countries.R
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