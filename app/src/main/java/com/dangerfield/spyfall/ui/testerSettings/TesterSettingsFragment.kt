package com.dangerfield.spyfall.ui.testerSettings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.PreferencesHelper
import kotlinx.android.synthetic.main.fragment_tester_settings.*
import org.koin.android.ext.android.inject

class TesterSettingsFragment : Fragment(R.layout.fragment_tester_settings) {

    val preferencesHelper : PreferencesHelper by inject()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rg_test_db.check(if(preferencesHelper.getUseTestDbState()) R.id.rb_on else R.id.rb_off)

        rg_test_db.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rb_off -> preferencesHelper.setUseTestDbState(false)
                R.id.rb_on -> preferencesHelper.setUseTestDbState(true)
            }
        }
    }
}