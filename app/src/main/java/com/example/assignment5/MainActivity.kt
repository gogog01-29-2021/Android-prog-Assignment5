package com.example.assignment5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityMainBinding
import com.example.assignment5.fragments.AddressInfoFragment
import com.example.assignment5.fragments.PersonalInfoFragment
import com.example.assignment5.fragments.SubFragment
import com.example.assignment5.model.Student

/**
 * Main Activity that serves as the container for all fragments.
 * Implements listener interfaces from both data collection fragments.
 *
 * Architecture:
 * - Top region: Title and "Add new member" button
 * - Middle region: Fragment container for PersonalInfo and AddressInfo fragments
 * - Bottom region: SubFragment (always visible, shows welcome or confirmation)
 *
 * Data flow:
 * 1. PersonalInfoFragment → interface callback → MainActivity (stores temp data)
 * 2. MainActivity loads AddressInfoFragment
 * 3. AddressInfoFragment → interface callback → MainActivity (creates Student object)
 * 4. MainActivity → direct method call → SubFragment (displays confirmation)
 */
class MainActivity : AppCompatActivity(),
    PersonalInfoFragment.PersonalInfoListener,
    AddressInfoFragment.AddressInfoListener {

    // ViewBinding for type-safe access to activity views
    private lateinit var binding: ActivityMainBinding

    // Reference to SubFragment for updating its display
    private var subFragment: SubFragment? = null

    // Temporary storage for personal info until address info is collected
    private var tempName: String? = null
    private var tempAge: Int? = null
    private var tempStudentNumber: String? = null

    /**
     * Called when activity is first created.
     * Sets up the UI and initializes the SubFragment.
     *
     * @param savedInstanceState Previously saved instance state (null on first creation)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding - generates binding class from activity_main.xml
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Set the content view to the root view from binding
        setContentView(binding.root)

        // Only initialize SubFragment on first creation (not on configuration changes)
        if (savedInstanceState == null) {
            initializeSubFragment()
        }

        // Set click listener on "Add new member" button
        binding.btnAddMember.setOnClickListener {
            loadPersonalInfoFragment()
        }
    }

    /**
     * Initializes and displays the SubFragment in the bottom container.
     * This fragment remains visible throughout the app lifecycle.
     * Initially shows "Welcome..." message until registration is complete.
     */
    private fun initializeSubFragment() {
        // Create new instance of SubFragment
        subFragment = SubFragment()
        // Begin fragment transaction to add SubFragment to bottom container
        supportFragmentManager.beginTransaction()
            // Replace any existing fragment in fragmentContainerSub with SubFragment
            .replace(R.id.fragmentContainerSub, subFragment!!)
            // Commit the transaction to apply changes
            .commit()
    }

    /**
     * Loads PersonalInfoFragment into the middle container.
     * Called when user clicks "Add new member" button.
     * This starts the registration flow.
     */
    private fun loadPersonalInfoFragment() {
        // Begin fragment transaction to add PersonalInfoFragment
        supportFragmentManager.beginTransaction()
            // Replace any existing fragment in middle container with PersonalInfoFragment
            .replace(R.id.fragmentContainerMain, PersonalInfoFragment())
            // Commit the transaction to apply changes
            .commit()
    }

    /**
     * Loads AddressInfoFragment into the middle container.
     * Called after personal information is successfully submitted.
     * Replaces PersonalInfoFragment with AddressInfoFragment.
     */
    private fun loadAddressInfoFragment() {
        // Begin fragment transaction to add AddressInfoFragment
        supportFragmentManager.beginTransaction()
            // Replace PersonalInfoFragment with AddressInfoFragment in middle container
            .replace(R.id.fragmentContainerMain, AddressInfoFragment())
            // Commit the transaction to apply changes
            .commit()
    }

    /**
     * Interface callback method from PersonalInfoFragment.
     * Receives and stores personal information temporarily.
     * Automatically loads AddressInfoFragment to continue registration.
     *
     * @param name The user's full name
     * @param age The user's age (validated 15-100)
     * @param studentNumber The user's student ID
     */
    override fun onPersonalInfoSubmitted(name: String, age: Int, studentNumber: String) {
        // Store personal info in temporary variables
        tempName = name
        tempAge = age
        tempStudentNumber = studentNumber
        // Proceed to address information collection
        loadAddressInfoFragment()
    }

    /**
     * Interface callback method from AddressInfoFragment.
     * Receives address information and combines it with stored personal info.
     * Creates complete Student object and displays it in SubFragment.
     *
     * @param city The user's city
     * @param postalCode The user's postal/zip code
     * @param address The user's street address
     */
    override fun onAddressInfoSubmitted(city: String, postalCode: String, address: String) {
        // Verify that personal information was previously collected
        if (tempName == null || tempAge == null || tempStudentNumber == null) return

        // Create Student object combining personal and address information
        val student = Student(
            name = tempName!!,       // Non-null assertion safe due to null check above
            age = tempAge!!,         // Non-null assertion safe due to null check above
            studentNumber = tempStudentNumber!!,  // Non-null assertion safe due to null check above
            city = city,
            postalCode = postalCode,
            address = address
        )

        // Send complete student data to SubFragment for display
        subFragment?.displayStudentInfo(student)

        // Clear temporary data to free memory
        clearTemporaryData()
    }

    /**
     * Clears temporary personal information storage.
     * Called after Student object is created to prevent memory leaks.
     */
    private fun clearTemporaryData() {
        tempName = null
        tempAge = null
        tempStudentNumber = null
    }
}