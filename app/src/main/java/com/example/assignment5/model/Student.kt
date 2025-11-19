package com.example.assignment5.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a student with personal and address information.
 *
 * @Parcelize annotation automatically implements Parcelable interface,
 * allowing this object to be passed between Android components (fragments, activities).
 *
 * @property name The student's full name
 * @property age The student's age (validated to be between 15-100)
 * @property studentNumber The student's unique identification number
 * @property city The city where the student resides
 * @property postalCode The postal/zip code of the student's address
 * @property address The complete street address of the student
 */
@Parcelize
data class Student(
    val name: String,
    val age: Int,
    val studentNumber: String,
    val city: String,
    val postalCode: String,
    val address: String
) : Parcelable {
    /**
     * Formats student information for display in the SubFragment.
     * Format: "S[studentNumber] [age] [city]"
     * Example: "S12345 20 Seoul"
     *
     * @return Formatted string containing student number (with 'S' prefix), age, and city
     */
    fun getFormattedInfo(): String = "S$studentNumber $age $city"
}
