package com.dangerfield.spyfall.testerSettings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.fragment_tester_settings.*

class TesterSettingsFragment : Fragment(R.layout.fragment_tester_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rg_test_db.check(if(getUseTestDbState()) R.id.rb_on else R.id.rb_off)

        rg_test_db.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rb_off -> setUseTestDbState(false)
                R.id.rb_on -> setUseTestDbState(true)
            }
        }
    }

    private val preferences by lazy {
        requireContext().getSharedPreferences(
            requireContext().resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
    }

    private fun getUseTestDbState(): Boolean {
         return preferences.getBoolean(
            requireContext().resources.getString(R.string.shared_preferences_test_db),
            true
        )
    }

    private fun setUseTestDbState(useTest: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(requireContext().resources.getString(R.string.shared_preferences_test_db), useTest)
        editor.apply()
    }

}