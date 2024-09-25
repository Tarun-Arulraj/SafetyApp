package com.example.safetyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private final lateinit var darkModeSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "dark_mode_pref"
    private val KEY_IS_NIGHT_MODE = "isNightMode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize shared preferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Get reference to the switch
        darkModeSwitch = view.findViewById(R.id.DarkMode)

        // Set the initial switch state based on stored preference
        darkModeSwitch.isChecked = sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false)

        // Set listener on the switch to toggle dark mode
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveModePreference(true)
            } else {
                // Enable light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveModePreference(false)
            }
        }

        return view
    }

    // Save the user's mode preference
    private fun saveModePreference(isNightMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_NIGHT_MODE, isNightMode)
        editor.apply()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

