package com.physmin.android.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.physmin.android.R
import kotlinx.android.synthetic.main.fragment_test_complete.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_STATUS = "param1"
private const val ARG_TOPIC = "param2"

const val RC_SUCC = 1
const val RC_FAIL = -1

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentTestComplete.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestComplete: Fragment() {
    // Success or fail
    private var status: String? = null
    private var topicPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            status = it.getString(ARG_STATUS)
            topicPath = it.getString(ARG_TOPIC)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_complete, container, false)
        view.button_test_completed.setOnClickListener {
            requireActivity().let {
                val intent = Intent()
                intent.putExtra("topicPath", topicPath)
                it.setResult(if (status == "Success") RC_SUCC else RC_FAIL, intent)
                it.finish()
            }

        }
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentTestComplete().apply {
                    arguments = Bundle().apply {
                        putString(ARG_STATUS, param1)
                        putString(ARG_TOPIC, param2)
                    }
                }
    }
}
