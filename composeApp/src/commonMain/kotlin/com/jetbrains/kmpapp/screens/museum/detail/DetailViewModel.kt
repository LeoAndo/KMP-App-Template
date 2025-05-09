package com.jetbrains.kmpapp.screens.museum.detail

import androidx.lifecycle.ViewModel
import com.jetbrains.kmpapp.data.museum.MuseumObject
import com.jetbrains.kmpapp.data.museum.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
