# Android Fragment Assignment

A Fragment-based Android application demonstrating fragment communication patterns and data management.

## 📋 Project Overview

This application implements a student registration system using Android Fragments with a focus on:
- Fragment-to-Activity communication using **Interface callbacks**
- Data storage and management
- Material Design UI components

---

## 🏗️ Architecture

### Components:
1. **MainActivity** - Fragment container and data coordinator
2. **PersonalInfoFragment** - Collects name, age, student number
3. **AddressInfoFragment** - Collects city, postal code, address
4. **SubFragment** - Displays registration confirmation
5. **Student** - Parcelable data model

---

## 📡 Data Transfer Implementation

### ✅ Fragment → Activity Communication (Interface Pattern)

#### 1. PersonalInfoFragment Interface

**File:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Interface Definition:**
```kotlin
interface PersonalInfoListener {
    fun onPersonalInfoSubmitted(name: String, age: Int, studentNumber: String)
}
```

**Fragment Implementation:**
```kotlin
class PersonalInfoFragment : Fragment() {
    private var listener: PersonalInfoListener? = null

    // Attach listener when fragment is attached to activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? PersonalInfoListener
            ?: throw RuntimeException("$context must implement PersonalInfoListener")
    }

    // Send data to MainActivity via interface callback
    private fun handleNextButtonClick() {
        val name = binding.etName.text.toString().trim()
        val age = ageText.toInt()
        val studentNumber = binding.etStudentNumber.text.toString().trim()

        // Call interface method to send data to MainActivity
        listener?.onPersonalInfoSubmitted(name, age, studentNumber)
    }
}
```

---

#### 2. AddressInfoFragment Interface

**File:** `app/src/main/java/com/example/assignment5/fragments/AddressInfoFragment.kt`

**Interface Definition:**
```kotlin
interface AddressInfoListener {
    fun onAddressInfoSubmitted(city: String, postalCode: String, address: String)
}
```

**Fragment Implementation:**
```kotlin
class AddressInfoFragment : Fragment() {
    private var listener: AddressInfoListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? AddressInfoListener
            ?: throw RuntimeException("$context must implement AddressInfoListener")
    }

    private fun handleDoneButtonClick() {
        val city = binding.etCity.text.toString().trim()
        val postalCode = binding.etPostalCode.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        // Call interface method to send data to MainActivity
        listener?.onAddressInfoSubmitted(city, postalCode, address)
    }
}
```

---

#### 3. MainActivity Implements Both Interfaces

**File:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

```kotlin
class MainActivity : AppCompatActivity(),
    PersonalInfoFragment.PersonalInfoListener,  // Implement interface
    AddressInfoFragment.AddressInfoListener {   // Implement interface

    // Interface callback - receives personal info from fragment
    override fun onPersonalInfoSubmitted(name: String, age: Int, studentNumber: String) {
        // Store data temporarily
        tempName = name
        tempAge = age
        tempStudentNumber = studentNumber
        // Load next fragment
        loadAddressInfoFragment()
    }

    // Interface callback - receives address info from fragment
    override fun onAddressInfoSubmitted(city: String, postalCode: String, address: String) {
        // Combine with stored data to create Student object
        val student = Student(
            name = tempName!!,
            age = tempAge!!,
            studentNumber = tempStudentNumber!!,
            city = city,
            postalCode = postalCode,
            address = address
        )
        // Display in SubFragment
        subFragment?.displayStudentInfo(student)
    }
}
```

---

## 💾 Data Storage Implementation

### Storage Strategy:

1. **Temporary Storage** → Store personal info until address info is collected
2. **Permanent Storage** → Combine into Student object
3. **Display** → Pass Student object to SubFragment

---

### 1. Temporary Data Storage

**File:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Location:** MainActivity class properties (Lines 33-38)

```kotlin
class MainActivity : AppCompatActivity() {

    // Temporary storage for personal info until address info is collected
    private var tempName: String? = null
    private var tempAge: Int? = null
    private var tempStudentNumber: String? = null

    override fun onPersonalInfoSubmitted(name: String, age: Int, studentNumber: String) {
        // Store personal info in temporary variables
        tempName = name
        tempAge = age
        tempStudentNumber = studentNumber
        loadAddressInfoFragment()
    }
}
```

**Why temporary storage?**
- Personal info arrives first (from PersonalInfoFragment)
- Address info arrives later (from AddressInfoFragment)
- Need to hold personal info until we have complete data

---

### 2. Student Data Model (Parcelable)

**File:** `app/src/main/java/com/example/assignment5/model/Student.kt`

```kotlin
@Parcelize
data class Student(
    val name: String,
    val age: Int,
    val studentNumber: String,
    val city: String,
    val postalCode: String,
    val address: String
) : Parcelable {
    fun getFormattedInfo(): String = "S$studentNumber $age $city"
}
```

**Purpose:**
- Immutable data holder for complete student information
- Parcelable allows passing between Android components
- Contains formatting method for display

---

### 3. Final Data Assembly

**File:** `app/src/main/java/com/example/assignment5/MainActivity.kt` (Lines 136-154)

```kotlin
override fun onAddressInfoSubmitted(city: String, postalCode: String, address: String) {
    // Verify that personal information was previously collected
    if (tempName == null || tempAge == null || tempStudentNumber == null) return

    // Create Student object combining personal and address information
    val student = Student(
        name = tempName!!,
        age = tempAge!!,
        studentNumber = tempStudentNumber!!,
        city = city,
        postalCode = postalCode,
        address = address
    )

    // Send complete student data to SubFragment for display
    subFragment?.displayStudentInfo(student)

    // Clear temporary data to free memory
    clearTemporaryData()
}

private fun clearTemporaryData() {
    tempName = null
    tempAge = null
    tempStudentNumber = null
}
```

---

## 🔄 Complete Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      USER INTERACTION                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  PersonalInfoFragment                                        │
│  - User enters: name, age, studentNumber                     │
│  - Clicks "Next" button                                      │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ ① Interface Callback
                            │ (PersonalInfoListener)
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  MainActivity                                                │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ TEMPORARY STORAGE (Lines 33-38)                        │ │
│  │ - tempName = "John Doe"                                │ │
│  │ - tempAge = 20                                         │ │
│  │ - tempStudentNumber = "12345"                          │ │
│  └────────────────────────────────────────────────────────┘ │
│  Then: loadAddressInfoFragment()                            │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  AddressInfoFragment                                         │
│  - User enters: city, postalCode, address                    │
│  - Clicks "Done!" button                                     │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ ② Interface Callback
                            │ (AddressInfoListener)
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  MainActivity                                                │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ CREATE STUDENT OBJECT (Lines 141-148)                  │ │
│  │                                                         │ │
│  │ val student = Student(                                 │ │
│  │     name = tempName!!,          // "John Doe"          │ │
│  │     age = tempAge!!,            // 20                  │ │
│  │     studentNumber = tempStudentNumber!!, // "12345"    │ │
│  │     city = city,                // "Seoul"             │ │
│  │     postalCode = postalCode,    // "12345"             │ │
│  │     address = address           // "123 Main St"       │ │
│  │ )                                                       │ │
│  └────────────────────────────────────────────────────────┘ │
│  Then: clearTemporaryData()                                 │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ ③ Direct Method Call
                            │ subFragment?.displayStudentInfo(student)
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  SubFragment                                                 │
│  - Displays: "Registration Confirmed!"                       │
│  - Shows: name                                               │
│  - Shows: "Please review your data!" (RED)                   │
│  - Shows: S12345 20 Seoul                                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
app/src/main/java/com/example/assignment5/
├── MainActivity.kt                    # Main activity, implements both interfaces
├── fragments/
│   ├── PersonalInfoFragment.kt       # Defines PersonalInfoListener interface
│   ├── AddressInfoFragment.kt        # Defines AddressInfoListener interface
│   └── SubFragment.kt                # Displays final confirmation
└── model/
    └── Student.kt                     # Parcelable data class

app/src/main/res/layout/
├── activity_main.xml                  # Main layout with 3 containers
├── fragment_personal_info.xml         # Personal info form (light purple)
├── fragment_address_info.xml          # Address info form (light purple)
└── fragment_sub.xml                   # Confirmation display
```

---

## 🎯 Key Implementation Details

### Interface Communication Pattern:
- **Fragment → Activity**: MUST use interface callbacks (required)
- **Activity → Fragment**: Direct method call (flexible)

### Data Validation:
- Age: Must be between 15-100
- All fields: Cannot be empty
- Toast messages for validation errors

### UI Features:
- MaterialComponents theme
- Rounded MaterialButtons (purple #6A5ACD)
- Light purple backgrounds (#F3E5F5)
- ScrollView for accessibility
- Red text (#FF0000) for review prompt

---

## 🚀 How to Run

1. Clone the repository
2. Open in Android Studio
3. Build and run on emulator or device (API 33+)

---

## 📝 Features

✅ Fragment-based architecture
✅ Interface callback pattern for communication
✅ Temporary data storage in Activity
✅ Parcelable Student model
✅ Input validation
✅ Material Design UI
✅ Comprehensive code comments

---

## 📱 Screenshots

### Flow:
1. Main Screen → "Add new member" button
2. Personal Information → Name, Age, Student Number → "Next"
3. Address Information → City, Postal Code, Address → "Done!"
4. Confirmation → Shows all data with format: S[ID] [Age] [City]

---

## 👨‍💻 Author

Implemented with comprehensive comments on all important code lines.

## 📄 License

Educational project for Android development learning.
