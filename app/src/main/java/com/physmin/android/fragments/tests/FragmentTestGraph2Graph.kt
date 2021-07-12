package com.physmin.android.fragments.tests

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.physmin.android.R
import com.physmin.android.activities.FunctionAnswerParcelable
import com.physmin.android.activities.QuestionParcelable
import com.physmin.android.views.layouts.GroupScrollable


// TODO: Rename parameter arguments, choose names which match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_QUESTION = "param1"
private const val ARG_ANSWERS = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestGraph2Graph.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestGraph2Graph.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class FragmentTestGraph2Graph: FragmentTestBase() {

    override var layoutResource = R.layout.fragment_test_graph2graph
    private var question: ArrayList<QuestionParcelable>? = null
    private var answers: ArrayList<FunctionAnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            question = it.getParcelableArrayList(ARG_QUESTION)
            answers = it.getParcelableArrayList(ARG_ANSWERS)
        }
    }

    override fun onCreateViewEvent(view: View) {
        question?.forEach {
            settableGroup.addQuestionGraphic(it.functions)
            for (i in 0 until it.correctIDs.count())
                settableGroup.addQuestionBlankView(it.correctIDs.toIntArray())
        }

        answers?.forEach {
            pickableGroup.addImageViewPickable(it, question!![0].correctIDs.contains(it.id))
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnAllDoneListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        listener = null
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

    //    interface OnAllDoneListener {
//        fun onAllDone()
//        fun onResetPressed()
//    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentTestGraph2Graph.
         */
        @JvmStatic
        fun newInstance(question: ArrayList<QuestionParcelable>, answers: ArrayList<FunctionAnswerParcelable>) =
                FragmentTestGraph2Graph().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_QUESTION, question)
                        putParcelableArrayList(ARG_ANSWERS, answers)
                    }
                }
    }
}
