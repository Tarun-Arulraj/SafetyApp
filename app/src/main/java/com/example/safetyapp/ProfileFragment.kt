package com.example.safetyapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import com.example.safetyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference

class ProfileFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private lateinit var userProfileSubCollectionRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views here
        val tvPersonalInfo: TextView = view.findViewById(R.id.tv_personal_info)
        val etFullName: EditText = view.findViewById(R.id.et_full_name)
        val etDateOfBirth: EditText = view.findViewById(R.id.et_date_of_birth)
        val etGender: EditText = view.findViewById(R.id.et_gender)

        val tvContactInfo: TextView = view.findViewById(R.id.tv_contact_info)
        val etPhoneNumber: EditText = view.findViewById(R.id.et_phone_number)

        val tvAddressInfo: TextView = view.findViewById(R.id.tv_address_info)
        val etHomeAddress: EditText = view.findViewById(R.id.et_home_address)
        val etWorkAddress: EditText = view.findViewById(R.id.et_work_address)
        val etOtherFrequentLocations: EditText = view.findViewById(R.id.et_other_frequent_locations)

        val tvEmergencyContacts: TextView = view.findViewById(R.id.tv_emergency_contacts)
        val etPrimaryEmergencyContact: EditText = view.findViewById(R.id.et_primary_emergency_contact)
        val etSecondaryEmergencyContact: EditText = view.findViewById(R.id.et_secondary_emergency_contact)

        val tvMedicalInfo: TextView = view.findViewById(R.id.tv_medical_info)
        val etMedicalConditions: EditText = view.findViewById(R.id.et_medical_conditions)
        val etAllergies: EditText = view.findViewById(R.id.et_allergies)
        val etBloodType: EditText = view.findViewById(R.id.et_blood_type)
        val etMedications: EditText = view.findViewById(R.id.et_medications)

        val btnEdit: Button = view.findViewById(R.id.editProfileButton)

        // Initialize Firestore and get the current user's UID
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Create a subCollection reference
        val usersCollectionRef = db.collection("users")
        userProfileSubCollectionRef = usersCollectionRef.document(uid).collection("profile")

        // Retrieve profile data from the subCollection
        userProfileSubCollectionRef.document("profile").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val profileData = task.result.data
                if (profileData != null) {
                    etFullName.setText(profileData["fullName"]?.toString())
                    etDateOfBirth.setText(profileData["dateOfBirth"]?.toString())
                    etGender.setText(profileData["gender"]?.toString())
                    etPhoneNumber.setText(profileData["phoneNumber"]?.toString())
                    etHomeAddress.setText(profileData["homeAddress"]?.toString())
                    etWorkAddress.setText(profileData["workAddress"]?.toString())
                    etOtherFrequentLocations.setText(profileData["otherFrequentLocations"]?.toString())
                    etPrimaryEmergencyContact.setText(profileData["primaryEmergencyContact"]?.toString())
                    etSecondaryEmergencyContact.setText(profileData["secondaryEmergencyContact"]?.toString())
                    etMedicalConditions.setText(profileData["medicalConditions"]?.toString())
                    etAllergies.setText(profileData["allergies"]?.toString())
                    etBloodType.setText(profileData["bloodType"]?.toString())
                    etMedications.setText(profileData["medications"]?.toString())
                }
            } else {
                Log.d("Error", "Error getting profile data: ${task.exception}")
            }
        }

        // Disable editing by default
        etFullName.isEnabled = false
        etDateOfBirth.isEnabled = false
        etGender.isEnabled = false
        etPhoneNumber.isEnabled = false
        etHomeAddress.isEnabled = false
        etWorkAddress.isEnabled = false
        etOtherFrequentLocations.isEnabled = false
        etPrimaryEmergencyContact.isEnabled = false
        etSecondaryEmergencyContact.isEnabled = false
        etMedicalConditions.isEnabled = false
        etAllergies.isEnabled = false
        etBloodType.isEnabled = false
        etMedications.isEnabled = false

        // Edit button click listener
        btnEdit.setOnClickListener {
            if (btnEdit.text == "Edit") {
                // Enable editing
                etFullName.isEnabled = true
                etDateOfBirth.isEnabled = true
                etGender.isEnabled = true
                etPhoneNumber.isEnabled = true
                etHomeAddress.isEnabled = true
                etWorkAddress.isEnabled = true
                etOtherFrequentLocations.isEnabled = true
                etPrimaryEmergencyContact.isEnabled = true
                etSecondaryEmergencyContact.isEnabled = true
                etMedicalConditions.isEnabled = true
                etAllergies.isEnabled = true
                etBloodType.isEnabled = true
                etMedications.isEnabled = true

                // Update button text to "Save"
                btnEdit.text = "Save"
            } else {
                // Save button logic
                val profileData = hashMapOf(
                    "fullName" to etFullName.text.toString().ifEmpty { null },
                    "dateOfBirth" to etDateOfBirth.text.toString().ifEmpty { null },
                    "gender" to etGender.text.toString().ifEmpty { null },
                    "phoneNumber" to etPhoneNumber.text.toString().ifEmpty { null },
                    "homeAddress" to etHomeAddress.text.toString().ifEmpty { null },
                    "workAddress" to etWorkAddress.text.toString().ifEmpty { null },
                    "otherFrequentLocations" to etOtherFrequentLocations.text.toString().ifEmpty { null },
                    "primaryEmergencyContact" to etPrimaryEmergencyContact.text.toString().ifEmpty { null },
                    "secondaryEmergencyContact" to etSecondaryEmergencyContact.text.toString().ifEmpty { null },
                    "medicalConditions" to etMedicalConditions.text.toString().ifEmpty { null },
                    "allergies" to etAllergies.text.toString().ifEmpty { null },
                    "bloodType" to etBloodType.text.toString().ifEmpty { null },
                    "medications" to etMedications.text.toString().ifEmpty { null }
                )

                userProfileSubCollectionRef.document("profile").set(profileData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Update button text to "Edit"
                            btnEdit.text = "Edit"
                            // Disable editing
                            etFullName.isEnabled = false
                            etDateOfBirth.isEnabled = false
                            etGender.isEnabled = false
                            etPhoneNumber.isEnabled = false
                            etHomeAddress.isEnabled = false
                            etWorkAddress.isEnabled = false
                            etOtherFrequentLocations.isEnabled = false
                            etPrimaryEmergencyContact.isEnabled = false
                            etSecondaryEmergencyContact.isEnabled = false
                            etMedicalConditions.isEnabled = false
                            etAllergies.isEnabled = false
                            etBloodType.isEnabled = false
                            etMedications.isEnabled = false
                        } else {
                            // Handle error
                            Log.w(TAG, "Error saving profile data", task.exception)
                        }
                    }
            }
        }
    }
}