package com.example.assignment5.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentSubBinding
import com.example.assignment5.model.Student

/**
 * Sub-fragment that displays registration status and user information.
 * This fragment is always visible in the bottom region of the main activity.
 *
 * Two states:
 * 1. Initial state: Shows "Welcome..." message
 * 2. Confirmation state: Shows complete registration information after data submission
 *
 * Communication: MainActivity calls displayStudentInfo() directly to pass Student data
 */
class SubFragment : Fragment() {
    // ViewBinding variable - nullable because it's only valid between onCreateView and onDestroyView
    private var _binding: FragmentSubBinding? = null
    // Non-nullable binding property - safe to use only when fragment view exists
    private val binding get() = _binding!!

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Uses ViewBinding to inflate the layout instead of traditional findViewById.
     *
     * @param inflater LayoutInflater to inflate the view
     * @param container Parent view that this fragment's UI will be attached to
     * @param savedInstanceState Previously saved state (if any)
     * @return The root view of the fragment layout
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate layout using ViewBinding - generates type-safe binding class
        _binding = FragmentSubBinding.inflate(inflater, container, false)
        // Return the root view from the binding
        return binding.root
    }

    /**
     * Called immediately after onCreateView.
     * Initializes the fragment to show the welcome state by default.
     *
     * @param view The view returned by onCreateView
     * @param savedInstanceState Previously saved state (if any)
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Display initial welcome message when fragment is first created
        showWelcomeState()
    }

    /**
     * Shows the initial welcome state.
     * Displays "Welcome..." message and hides the confirmation container.
     *
     * This is the default state before any user registration data is submitted.
     */
    private fun showWelcomeState() {
        // Make welcome TextView visible
        binding.tvWelcome.visibility = View.VISIBLE
        // Hide confirmation container (contains registration details)
        binding.confirmationContainer.visibility = View.GONE
    }

    /**
     * Public method called by MainActivity to display student registration information.
     * Switches from welcome state to confirmation state.
     *
     * Display format:
     * - "Registration Confirmed!"
     * - Student's name
     * - "Please review your data!" (in red color)
     * - Formatted info: "S[studentNumber] [age] [city]"
     *
     * @param student The Student object containing all registration information
     */
    fun displayStudentInfo(student: Student) {
        // Hide welcome message
        binding.tvWelcome.visibility = View.GONE
        // Show confirmation container with student details
        binding.confirmationContainer.visibility = View.VISIBLE
        // Display student's name
        binding.tvStudentName.text = student.name
        // Display formatted student information (S[ID] [Age] [City])
        binding.tvStudentInfo.text = student.getFormattedInfo()
    }

    /**
     * Called when fragment's view is being destroyed.
     * Cleans up ViewBinding reference to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}
