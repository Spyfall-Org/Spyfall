package com.dangerfield.spyfall.legacy.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.util.ReviewHelper
import kotlinx.android.synthetic.main.fragment_require_update.btn_positive
import org.koin.android.ext.android.inject

class RequireUpdateFragment : Fragment(R.layout.fragment_require_update) {

    private val reviewHelper: ReviewHelper by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_positive.setOnClickListener {
            reviewHelper.openStoreForReview()
        }
    }
}
