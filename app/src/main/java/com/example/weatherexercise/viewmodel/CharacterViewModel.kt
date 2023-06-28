package com.example.weatherexercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherexercise.SimpsonModel.SimpsonsData
import com.example.weatherexercise.repo.remote.CharacterRepo
import com.example.weatherexercise.util.ApiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CharacterViewModel : ViewModel () {
    private val _characterState = MutableLiveData<ApiState<SimpsonsData>>()
    val characterState: LiveData<ApiState<SimpsonsData>>

            get() = _characterState

    fun makeCharacterFetch(show: String) {
        viewModelScope.launch {
            CharacterRepo.getCharactersState(show)
                .collect { characterState -> _characterState.postValue(characterState) }
            

        }
    }

}