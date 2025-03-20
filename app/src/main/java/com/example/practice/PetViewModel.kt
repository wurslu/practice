package com.example.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PetUiState>(PetUiState())
    val uiState = _uiState.asStateFlow()

    data class PetUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val petPost: List<PetPost> = emptyList<PetPost>()
    )

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val posts = withContext(Dispatchers.IO) {
                    ApiService.getPost()

                }
                _uiState.update { it.copy(petPost = posts, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "加载失败:${e.message}") }
            }
        }
    }
}
