package com.ufm.project.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ufm.project.R

class BookBackDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traphieumuon, container, false)
    }

    companion object {
        fun newInstance(borrowBookId: String): BookBackDialogFragment {
            val fragment = BookBackDialogFragment()
            val args = Bundle()
            args.putString("borrowBookId", borrowBookId)
            fragment.arguments = args
            return fragment
        }
    }
}