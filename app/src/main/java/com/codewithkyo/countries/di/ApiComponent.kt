package com.codewithkyo.countries.di

import com.codewithkyo.countries.model.CountriesService
import com.codewithkyo.countries.viewmodel.ListViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: CountriesService)

    fun inject(viewModel: ListViewModel)
}