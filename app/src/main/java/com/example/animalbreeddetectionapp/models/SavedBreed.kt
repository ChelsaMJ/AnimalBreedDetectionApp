package com.example.animalbreeddetectionapp.models

data class SavedBreed(
    var id: String = "",
    var breedName: String = "",
    var imageUrl: String? = null,
    var confidence: Double = 0.0,
    var timestamp: Long = 0L
)
