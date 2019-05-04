package com.example.physmin.fragments.tests

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.activities.FunctionAnswerRelationSignParcelable
import com.example.physmin.activities.QuestionParcelable
import com.example.physmin.views.RelationSignView
import kotlinx.android.synthetic.main.fragment_test_relation_signs.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_QUESTS = "param1"
private const val ARG_ANSWERS = "param2"

data class Question(val letter: String, val leftIndex: String, val rightIndex: String, val corrSign: Int)

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestSign2Relation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestSign2Relation.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestSign2Relation : androidx.fragment.app.Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var questions: ArrayList<QuestionParcelable>? = null
    private var answers: ArrayList<FunctionAnswerRelationSignParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            questions = it.getParcelableArrayList<QuestionParcelable>(ARG_QUESTS)
            answers = it.getParcelableArrayList<FunctionAnswerRelationSignParcelable>(ARG_ANSWERS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test_relation_signs, container, false)

        view.graphView_rs_task.functions = questions!![0].functions

        for(answer in answers!!) {
            val relationSignView = RelationSignView(this.context!!, null, answer.letter,
                    answer.leftIndex, answer.rightIndex)
            relationSignView.correctAnswers = answer.correctSign
            view.settableGroup_rs.addView(relationSignView)
        }

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
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
         * @return A new instance of fragment TestFramgent_relation_signs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(questions: ArrayList<QuestionParcelable>, answers: ArrayList<FunctionAnswerRelationSignParcelable>) =
                FragmentTestSign2Relation().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_QUESTS, questions)
                        putParcelableArrayList(ARG_ANSWERS, answers)

                    }
                }
    }
}
