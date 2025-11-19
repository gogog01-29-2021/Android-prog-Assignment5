package com.example.assignment5.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentAddressInfoBinding

/**
 * Fragment for collecting address information from the user.
 * This is the second data collection fragment in the registration flow.
 *
 * Collects: city, postal code, and street address
 * Communication: Uses AddressInfoListener interface to send data back to MainActivity
 */
class AddressInfoFragment : Fragment() {
    // ViewBinding variable - nullable because it's only valid between onCreateView and onDestroyView
    private var _binding: FragmentAddressInfoBinding? = null
    // Non-nullable binding property - safe to use only when fragment view exists
    private val binding get() = _binding!!
    // Listener reference for communicating with MainActivity
    private var listener: AddressInfoListener? = null

    /**
     * Interface for communicating fragment data back to the hosting activity.
     * MainActivity must implement this interface to receive address information.
     *
     * @param city The city where the user resides
     * @param postalCode The postal/zip code of the user's address
     * @param address The complete street address
     */
    interface AddressInfoListener {
        fun onAddressInfoSubmitted(city: String, postalCode: String, address: String)
    }

    /**
     * Called when fragment is attached to its hosting activity.
     * Initializes the listener by casting the context to AddressInfoListener.
     *
     * @param context The hosting activity context
     * @throws RuntimeException if the hosting activity doesn't implement AddressInfoListener
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Cast context to listener interface - ensures MainActivity implements the interface
        listener = context as? AddressInfoListener
            // Throw exception if MainActivity doesn't implement required interface
            ?: throw RuntimeException("$context must implement AddressInfoListener")
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
        _binding = FragmentAddressInfoBinding.inflate(inflater, container, false)
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
        // Set click listener on Done button to handle form submission
        binding.btnDone.setOnClickListener { handleDoneButtonClick() }
    }

    /**
     * Handles the Done button click event.
     * Validates user input and sends data to MainActivity via listener callback.
     *
     * Validation rules:
     * - City must not be empty
     * - Postal code must not be empty
     * - Address must not be empty
     */
    private fun handleDoneButtonClick() {
        // Extract and trim the city input to remove leading/trailing whitespace
        val city = binding.etCity.text.toString().trim()
        // Extract and trim postal code input
        val postalCode = binding.etPostalCode.text.toString().trim()
        // Extract and trim address input
        val address = binding.etAddress.text.toString().trim()

        // Validate city is not empty
        if (city.isEmpty()) {
            Toast.makeText(context, "Please enter city", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // Validate postal code is not empty
        if (postalCode.isEmpty()) {
            Toast.makeText(context, "Please enter postal code", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // Validate address is not empty
        if (address.isEmpty()) {
            Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
            return // Exit early if validation fails
        }

        // All validation passed - send data to MainActivity via interface callback
        listener?.onAddressInfoSubmitted(city, postalCode, address)
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
