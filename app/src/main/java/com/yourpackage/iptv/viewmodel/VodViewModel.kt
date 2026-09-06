package com.yourpackage.iptv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.iptv.data.models.VodContent
import com.yourpackage.iptv.data.repository.VodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VodViewModel(private val repository: VodRepository) : ViewModel() {
    private val _movies = MutableStateFlow<List<VodContent>>(emptyList())
    val movies: StateFlow<List<VodContent>> = _movies.asStateFlow()
    
    private val _series = MutableStateFlow<List<VodContent>>(emptyList())
    val series: StateFlow<List<VodContent>> = _series.asStateFlow()
    
    private val _favourites = MutableStateFlow<List<VodContent>>(emptyList())
    val favourites: StateFlow<List<VodContent>> = _favourites.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadContent() }

    fun loadContent() {
        viewModelScope.launch {
            repository.getMovies().collect { _movies.value = it }
            repository.getSeries().collect { _series.value = it }
            repository.getFavourites().collect { _favourites.value = it }
        }
    }

    fun toggleFavourite(content: VodContent) {
        viewModelScope.launch {
            repository.toggleFavourite(content)
        }
    }
    
    fun search(query: String) {
        viewModelScope.launch {
            repository.search(query).collect { results ->
                _movies.value = results.filter { it.type == "movie" }
                _series.value = results.filter { it.type == "series" }
            }
        }
    }
}
