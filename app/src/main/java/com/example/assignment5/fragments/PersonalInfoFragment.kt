package com.example.assignment5.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentPersonalInfoBinding

/**
 * Fragment for collecting personal information from the user.
 * This is the first data collection fragment in the registration flow.
 *
 * Collects: name, age, and student number
 * Communication: Uses PersonalInfoListener interface to send data back to MainActivity
 */
class PersonalInfoFragment : Fragment() {
    // ViewBinding variable - nullable because it's only valid between onCreateView and onDestroyView
    private var _binding: FragmentPersonalInfoBinding? = null
    // Non-nullable binding property - safe to use only when fragment view exists
    private val binding get() = _binding!!
    // Listener reference for communicating with MainActivity
    private var listener: PersonalInfoListener? = null

    /**
     * Interface for communicating fragment data back to the hosting activity.
     * MainActivity must implement this interface to receive personal information.
     *
     * @param name The user's full name
     * @param age The user's age (validated to be 15-100)
     * @param studentNumber The user's student identification number
     */
    interface PersonalInfoListener {
        fun onPersonalInfoSubmitted(name: String, age: Int, studentNumber: String)
    }

    /**
     * Called when fragment is attached to its hosting activity.
     * Initializes the listener by casting the context to PersonalInfoListener.
     *
     * @param context The hosting activity context
     * @throws RuntimeException if the hosting activity doesn't implement PersonalInfoListener
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Cast context to listener interface - ensures MainActivity implements the interface
        listener = context as? PersonalInfoListener
            // Throw exception if MainActivity doesn't implement required interface
            ?: throw RuntimeException("$context must implement PersonalInfoListener")
    }

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
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        // Return the root view from the binding
        return binding.root
    }

    /**
     * Called immediately after onCreateView.
     * Sets up UI components and event listeners.
     *
     * @param view The view returned by onCreateView
     * @param savedInstanceState Previously saved state (if any)
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set click listener on Next button to handle form submission
        binding.btnNext.setOnClickListener { handleNextButtonClick() }
    }

    /**
     * Handles the Next button click event.
     * Validates user input and sends data to MainActivity via listener callback.
     *
     * Validation rules:
     * - Name must not be empty
     * - Age must be a valid number between 15 and 100
     * - Student number must not be empty
     */
    private fun handleNextButtonClick() {
        // Extract and trim the name input to remove leading/trailing whitespace
        val name = binding.etName.text.toString().trim()
        // Extract age input as string for validation
        val ageText = binding.etAge.text.toString().trim()
        // Extract and trim student number input
        val studentNumber = binding.etStudentNumber.text.toString().trim()

        // Validate name is not empty
        if (name.isEmpty()) {
            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // Validate age input is not empty
        if (ageText.isEmpty()) {
            Toast.makeText(context, "Please enter your age", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // Validate student number is not empty
        if (studentNumber.isEmpty()) {
            Toast.makeText(context, "Please enter your student number", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // Convert age string to integer with error handling
        val age = try {
            ageText.toInt()
        } catch (e: NumberFormatException) {
            // Show error if age is not a valid number
            Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return // Exit early if conversion fails
        }

        // Validate age is within acceptable range (15-100)
        if (age < 15 || age > 100) {
            Toast.makeText(context, "Please enter a valid age (15-100)", Toast.LENGTH_SHORT).show()
            return // Exit early if age is out of range
        }

        // All validation passed - send data to MainActivity via interface callback
        listener?.onPersonalInfoSubmitted(name, age, studentNumber)
    }

    /**
     * Called when fragment is detached from its hosting activity.
     * Cleans up the listener reference to prevent memory leaks.
     */
    override fun onDetach() {
        super.onDetach()
        // Clear listener reference to avoid memory leaks
        listener = null
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
