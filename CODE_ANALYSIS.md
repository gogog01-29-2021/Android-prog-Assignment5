# Code Analysis: Activity and Fragment Data Sharing

## Overview

This document explains how MainActivity and Fragments share data in this application.

---

## 📊 Data Sharing Summary

### Communication Patterns:

1. **Fragment → Activity**: Uses **Interface Callback Pattern** (REQUIRED)
2. **Activity → Fragment**: Uses **Direct Method Call** (FLEXIBLE)

---

# Part 1: Fragment → Activity Data Transfer (Interface Pattern)

## Why Use Interface Pattern?

The interface pattern ensures:
- ✅ **Loose coupling** - Fragment doesn't need to know about MainActivity
- ✅ **Type safety** - Compile-time checking
- ✅ **Testability** - Easy to mock interfaces
- ✅ **Reusability** - Fragment can work with any activity implementing the interface

---

## Implementation 1: PersonalInfoFragment → MainActivity

### Step 1: Define Interface in Fragment

**File Path:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Lines:** 27-32

```kotlin
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
```

**Explanation:**
- Interface is defined **inside** PersonalInfoFragment class
- Defines a single callback method
- Documents parameters using KDoc

---

### Step 2: Fragment Declares Listener Variable

**File Path:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Lines:** 20-22

```kotlin
class PersonalInfoFragment : Fragment() {
    // ViewBinding variable - nullable because it's only valid between onCreateView and onDestroyView
    private var _binding: FragmentPersonalInfoBinding? = null
    // Non-nullable binding property - safe to use only when fragment view exists
    private val binding get() = _binding!!
    // Listener reference for communicating with MainActivity
    private var listener: PersonalInfoListener? = null
```

**Explanation:**
- `listener` is nullable because it may not always be attached
- Private variable to prevent external access

---

### Step 3: Fragment Attaches Listener (onAttach)

**File Path:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Lines:** 34-48

```kotlin
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
```

**Explanation:**
- `onAttach()` is called when fragment is attached to activity
- Casts `context` (which is MainActivity) to `PersonalInfoListener`
- If MainActivity doesn't implement interface → crashes with clear error message
- This enforces the contract at runtime

---

### Step 4: Fragment Sends Data via Listener

**File Path:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Lines:** 80-120

```kotlin
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
```

**Key Line:**
```kotlin
listener?.onPersonalInfoSubmitted(name, age, studentNumber)
```

**Explanation:**
- After validation, calls the interface method
- `?.` safe call operator - only calls if listener is not null
- **This is where data leaves the fragment and goes to MainActivity**

---

### Step 5: Fragment Cleans Up Listener (onDetach)

**File Path:** `app/src/main/java/com/example/assignment5/fragments/PersonalInfoFragment.kt`

**Lines:** 122-128

```kotlin
/**
 * Called when fragment is detached from its hosting activity.
 * Cleans up the listener reference to prevent memory leaks.
 */
override fun onDetach() {
    super.onDetach()
    // Clear listener reference to avoid memory leaks
    listener = null
}
```

**Explanation:**
- When fragment is detached, clear listener reference
- Prevents memory leaks by breaking the reference chain

---

### Step 6: MainActivity Implements Interface

**File Path:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Lines:** 26-28

```kotlin
class MainActivity : AppCompatActivity(),
    PersonalInfoFragment.PersonalInfoListener,  // ← Implements first interface
    AddressInfoFragment.AddressInfoListener {   // ← Implements second interface
```

**Explanation:**
- MainActivity **must** implement the interface
- Separated by commas for multiple interfaces
- This creates the contract

---

### Step 7: MainActivity Receives Data

**File Path:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Lines:** 109-125

```kotlin
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
```

**Explanation:**
- This method is **automatically called** when fragment calls `listener?.onPersonalInfoSubmitted()`
- Stores data in temporary variables
- Loads next fragment

---

## Implementation 2: AddressInfoFragment → MainActivity

### Same Pattern, Different Data

**File Path:** `app/src/main/java/com/example/assignment5/fragments/AddressInfoFragment.kt`

**Interface Definition (Lines 27-32):**
```kotlin
interface AddressInfoListener {
    fun onAddressInfoSubmitted(city: String, postalCode: String, address: String)
}
```

**Listener Variable (Line 22):**
```kotlin
private var listener: AddressInfoListener? = null
```

**Attach Listener (Lines 34-48):**
```kotlin
override fun onAttach(context: Context) {
    super.onAttach(context)
    listener = context as? AddressInfoListener
        ?: throw RuntimeException("$context must implement AddressInfoListener")
}
```

**Send Data (Lines 105-107):**
```kotlin
private fun handleDoneButtonClick() {
    val city = binding.etCity.text.toString().trim()
    val postalCode = binding.etPostalCode.text.toString().trim()
    val address = binding.etAddress.text.toString().trim()

    // ... validation code ...

    // Send data to MainActivity via interface callback
    listener?.onAddressInfoSubmitted(city, postalCode, address)
}
```

**MainActivity Receives (Lines 127-155):**
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
```

---

## 🔄 Complete Fragment → Activity Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│ PersonalInfoFragment                                             │
│                                                                  │
│ 1. User fills form and clicks "Next"                            │
│ 2. handleNextButtonClick() validates data                       │
│ 3. Calls: listener?.onPersonalInfoSubmitted(name, age, stuNum)  │
│                                                                  │
│    File: PersonalInfoFragment.kt:120                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Interface Callback
                             │ (PersonalInfoListener)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ MainActivity                                                     │
│                                                                  │
│ 4. onPersonalInfoSubmitted() receives data                      │
│ 5. Stores in temporary variables:                               │
│    - tempName = name                                            │
│    - tempAge = age                                              │
│    - tempStudentNumber = studentNumber                          │
│ 6. Calls loadAddressInfoFragment()                              │
│                                                                  │
│    File: MainActivity.kt:118-124                                │
└─────────────────────────────────────────────────────────────────┘
```

---

# Part 2: Activity → Fragment Data Transfer

## Implementation: MainActivity → SubFragment

**Note:** This pattern is **flexible** - we can use Bundle, ViewModel, or direct method call.
We chose **direct method call** for simplicity.

---

### Step 1: MainActivity Holds Fragment Reference

**File Path:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Lines:** 30-34

```kotlin
class MainActivity : AppCompatActivity() {

    // ViewBinding for type-safe access to activity views
    private lateinit var binding: ActivityMainBinding

    // Reference to SubFragment for updating its display
    private var subFragment: SubFragment? = null
```

**Explanation:**
- Holds a reference to SubFragment
- Nullable because fragment might not be created yet

---

### Step 2: MainActivity Initializes SubFragment

**File Path:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Lines:** 65-79

```kotlin
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
```

**Explanation:**
- Creates SubFragment instance and stores reference
- Adds fragment to bottom container
- Reference is saved for later communication

---

### Step 3: SubFragment Defines Public Method

**File Path:** `app/src/main/java/com/example/assignment5/fragments/SubFragment.kt`

**Lines:** 67-82

```kotlin
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
```

**Explanation:**
- **Public** method - can be called from MainActivity
- Accepts Student object parameter
- Updates UI to show registration data

---

### Step 4: MainActivity Calls Fragment Method

**File Path:** `app/src/main/java/com/example/assignment5/MainActivity.kt`

**Lines:** 150-151

```kotlin
override fun onAddressInfoSubmitted(city: String, postalCode: String, address: String) {
    // ... code to create Student object ...

    val student = Student(
        name = tempName!!,
        age = tempAge!!,
        studentNumber = tempStudentNumber!!,
        city = city,
        postalCode = postalCode,
        address = address
    )

    // Send complete student data to SubFragment for display
    subFragment?.displayStudentInfo(student)  // ← DIRECT METHOD CALL

    // Clear temporary data to free memory
    clearTemporaryData()
}
```

**Explanation:**
- `subFragment?.displayStudentInfo(student)` - direct method call
- `?.` safe call - only calls if subFragment is not null
- Passes Student object to fragment
- **This is where data goes from Activity to Fragment**

---

## 🔄 Complete Activity → Fragment Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│ MainActivity                                                     │
│                                                                  │
│ 1. onAddressInfoSubmitted() receives address data               │
│ 2. Creates Student object with all data:                        │
│    Student(                                                      │
│      name = tempName,                                           │
│      age = tempAge,                                             │
│      studentNumber = tempStudentNumber,                         │
│      city = city,                                               │
│      postalCode = postalCode,                                   │
│      address = address                                          │
│    )                                                             │
│ 3. Calls: subFragment?.displayStudentInfo(student)              │
│                                                                  │
│    File: MainActivity.kt:150                                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Direct Method Call
                             │ (Public Function)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ SubFragment                                                      │
│                                                                  │
│ 4. displayStudentInfo(student) receives Student object          │
│ 5. Updates UI:                                                  │
│    - Hides "Welcome..." message                                 │
│    - Shows "Registration Confirmed!"                            │
│    - Displays student name                                      │
│    - Displays "Please review your data!" (RED)                  │
│    - Displays formatted info: S12345 20 Seoul                   │
│                                                                  │
│    File: SubFragment.kt:67-82                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📋 Complete Data Flow Summary

### Chronological Order:

```
1. USER → PersonalInfoFragment
   ├─ User enters: name, age, studentNumber
   └─ Clicks "Next" button

2. PersonalInfoFragment → MainActivity (INTERFACE)
   ├─ File: PersonalInfoFragment.kt:120
   ├─ Code: listener?.onPersonalInfoSubmitted(name, age, studentNumber)
   └─ Pattern: Interface Callback

3. MainActivity (TEMPORARY STORAGE)
   ├─ File: MainActivity.kt:118-124
   ├─ Stores: tempName, tempAge, tempStudentNumber
   └─ Loads: AddressInfoFragment

4. USER → AddressInfoFragment
   ├─ User enters: city, postalCode, address
   └─ Clicks "Done!" button

5. AddressInfoFragment → MainActivity (INTERFACE)
   ├─ File: AddressInfoFragment.kt:107
   ├─ Code: listener?.onAddressInfoSubmitted(city, postalCode, address)
   └─ Pattern: Interface Callback

6. MainActivity (CREATE STUDENT OBJECT)
   ├─ File: MainActivity.kt:141-148
   ├─ Combines: temp data + address data
   └─ Creates: Student object (Parcelable)

7. MainActivity → SubFragment (DIRECT CALL)
   ├─ File: MainActivity.kt:151
   ├─ Code: subFragment?.displayStudentInfo(student)
   └─ Pattern: Direct Method Call

8. SubFragment (DISPLAY)
   ├─ File: SubFragment.kt:67-82
   ├─ Shows: Registration confirmation
   └─ Displays: S[ID] [Age] [City]
```

---

## 📊 Comparison: Interface vs Direct Call

| Aspect | Fragment → Activity (Interface) | Activity → Fragment (Direct) |
|--------|--------------------------------|------------------------------|
| **Pattern** | Interface Callback | Direct Method Call |
| **Coupling** | Loose (fragment doesn't know about activity) | Tight (activity knows about fragment) |
| **Requirement** | MUST use interface (assignment requirement) | Flexible (can use Bundle, ViewModel, etc.) |
| **File Locations** | Interface defined in fragment, implemented in activity | Public method in fragment, called from activity |
| **Null Safety** | `listener?.method()` safe call | `fragment?.method()` safe call |
| **Memory** | Must clear in `onDetach()` | Reference cleared automatically |
| **Testability** | Easy to mock interface | Need fragment instance |

---

## 🎯 Key Takeaways

### Fragment → Activity (Interface Pattern)
✅ **WHY:** Decouples fragment from specific activity implementation
✅ **HOW:** Fragment defines interface, activity implements it
✅ **WHEN:** Fragment needs to send data/events upward

### Activity → Fragment (Direct Call)
✅ **WHY:** Simple and straightforward for one-way data passing
✅ **HOW:** Fragment exposes public method, activity calls it
✅ **WHEN:** Activity needs to update fragment's state

---

## 📁 File Reference Summary

| Component | File Path | Key Lines |
|-----------|-----------|-----------|
| PersonalInfoFragment Interface | `fragments/PersonalInfoFragment.kt` | 27-32 |
| PersonalInfoFragment Attach | `fragments/PersonalInfoFragment.kt` | 34-48 |
| PersonalInfoFragment Send Data | `fragments/PersonalInfoFragment.kt` | 120 |
| AddressInfoFragment Interface | `fragments/AddressInfoFragment.kt` | 27-32 |
| AddressInfoFragment Attach | `fragments/AddressInfoFragment.kt` | 34-48 |
| AddressInfoFragment Send Data | `fragments/AddressInfoFragment.kt` | 107 |
| MainActivity Implements Interfaces | `MainActivity.kt` | 26-28 |
| MainActivity Temp Storage | `MainActivity.kt` | 36-39 |
| MainActivity Receive Personal | `MainActivity.kt` | 118-124 |
| MainActivity Receive Address | `MainActivity.kt` | 136-155 |
| MainActivity Hold SubFragment Ref | `MainActivity.kt` | 34 |
| MainActivity Call SubFragment | `MainActivity.kt` | 151 |
| SubFragment Public Method | `fragments/SubFragment.kt` | 67-82 |
| Student Data Class | `model/Student.kt` | 16-28 |

---

## End of Analysis

This document explains the complete data sharing mechanism between MainActivity and all Fragments in the application.
