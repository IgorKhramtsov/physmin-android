package com.example.physmin.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.physmin.R
import com.example.physmin.activities.TestActivity
import com.example.physmin.fragments.tests.FragmentTestGraph2Graph
import com.example.physmin.fragments.tests.FragmentTestGraph2Graph2
import com.example.physmin.fragments.tests.FragmentTestGraph2State
import com.example.physmin.fragments.tests.FragmentTestSign2Relation
import kotlinx.android.synthetic.main.fragment_test_hello.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestHello.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestHello.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestHello : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_test_hello, container, false)

        val tests = (activity as TestActivity).tests

        view.button_start_test.setOnClickListener {
            fragmentManager!!.beginTransaction()
                    .replace(R.id.my_nav_host_fragment, FragmentTestGraph2State.newInstance(
                            arrayOf("graph_x_1", "graph_x_2", "graph_x_3", "graph_x_4"),
                            arrayOf("Назад, ускоряясь вперед", "Назад, ускоряясь назад", "Назад, ускоряясь вперед",
                                    "Назад, ускоряясь вперед", "Назад, ускоряясь Назад", "Назад, ускоряясь Назад")
                    ))
                    .commit()
        }
        view.button_start_test_2.setOnClickListener {
            fragmentManager!!.beginTransaction()
                    .replace(R.id.my_nav_host_fragment, FragmentTestGraph2Graph.newInstance(
                            "graph_x_1",
                            arrayOf("graph_v_1", "graph_v_2", "graph_v_3", "graph_v_4",
                                    "graph_v_5", "graph_v_6", "graph_v_7", "graph_v_8")
                    ))
                    .commit()
        }
        view.button_start_test_2_2.setOnClickListener {
            fragmentManager!!.beginTransaction()
                    .replace(R.id.my_nav_host_fragment, FragmentTestGraph2Graph2.newInstance(
                            "graph_x_1",
                            arrayOf("graph_v_1", "graph_v_2", "graph_v_3", "graph_v_4",
                                    "graph_v_5", "graph_v_6", "graph_v_7", "graph_v_8")
                    ))
                    .commit()
        }
        view.button_start_test_3.setOnClickListener{
            fragmentManager!!.beginTransaction()
                    .replace(R.id.my_nav_host_fragment, FragmentTestSign2Relation.newInstance(
                            "graph_x_1",
                            arrayOf("graph_v_1", "graph_v_2", "graph_v_3", "graph_v_4",
                                    "graph_v_5", "graph_v_6", "graph_v_7", "graph_v_8")
                    ))
                    .commit()
        }

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentTestHello.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentTestHello().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
