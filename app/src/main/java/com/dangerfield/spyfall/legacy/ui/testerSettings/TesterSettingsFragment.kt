package com.dangerfield.spyfall.legacy.ui.testerSettings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.FragmentTesterSettingsBinding
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.util.DBCleaner
import com.dangerfield.spyfall.legacy.util.PreferencesService
import com.dangerfield.spyfall.legacy.util.viewBinding
import org.koin.android.ext.android.inject

class TesterSettingsFragment : Fragment(R.layout.fragment_tester_settings) {

    private val preferencesHelper: PreferencesService by inject()
    private val dbCleaner: DBCleaner by inject()
    private val binding by viewBinding(FragmentTesterSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgTestDb.check(if (preferencesHelper.getUseTestDbState()) R.id.rb_on else R.id.rb_off)

        binding.rgTestDb.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_off -> preferencesHelper.setUseTestDbState(false)
                R.id.rb_on -> preferencesHelper.setUseTestDbState(true)
            }
        }

        binding.btnCleanDb.setOnClickListener {
            showDbCleanLoading()
            dbCleaner.cleandb()
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> it.data?.let { s -> handleCleanDbMessage(s) }
                        is Resource.Error -> it.error?.let { e -> handleCleanDbMessage(e) }
                    }
                }
        }
    }

    private fun handleCleanDbMessage(it: String) {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        stopDbCleanLoading()
    }

    private fun showDbCleanLoading() {
        binding.apply {
            btnCleanDb.isClickable = false
            btnCleanDb.visibility = View.INVISIBLE
            pbCleanDb.visibility = View.VISIBLE
        }
    }

    private fun stopDbCleanLoading() {
        binding.apply {
            btnCleanDb.isClickable = true
            btnCleanDb.visibility = View.VISIBLE
            pbCleanDb.visibility = View.INVISIBLE
        }
    }
}
