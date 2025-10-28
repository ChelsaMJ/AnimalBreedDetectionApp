package com.example.animalbreeddetectionapp.models

data class SavedFact(
    val id: String = "",
    val fact: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
