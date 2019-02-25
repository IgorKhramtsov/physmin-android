package com.example.physmin.fragments.tests

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.views.RelationSignView
import kotlinx.android.synthetic.main.fragment_test_relation_signs.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_QUEST_PIC = "param1"
private const val ARG_QUEST_LTR = "param2"
private const val ARG_QUEST_LInd = "param3"
private const val ARG_QUEST_RInd = "param4"
private const val ARG_QUEST_CORR_SGN = "param5"

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
    private var questPicture: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var questions: ArrayList<Question>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            questPicture = it.getString(ARG_QUEST_PIC)

            val letters = it.getStringArray(ARG_QUEST_LTR)
            val leftIndex = it.getStringArray(ARG_QUEST_LInd)
            val rightIndex = it.getStringArray(ARG_QUEST_RInd)
            val sign = it.getIntArray(ARG_QUEST_CORR_SGN)

            questions = ArrayList()
            for(i in 0 until letters.count())
                questions!!.add(Question(letters[i], leftIndex[i], rightIndex[i], sign[i]))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test_relation_signs, container, false)

        view.imageView_rs_task.apply {
            val id = resources.getIdentifier(questPicture, "drawable", context!!.packageName)
            setImageDrawable(resources.getDrawable(id))
        }

        for (i in 0 until questions!!.size){
            val relationSignView = RelationSignView(this.context!!, questions!![i].letter,
                    questions!![i].leftIndex, questions!![i].rightIndex)
            relationSignView.correctAnsw = questions!![i].corrSign
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
        fun newInstance(param1: String, param2: Array<String>,
                        param3: Array<String>, param4: Array<String>,
                        param5: Array<Int>) =
                FragmentTestSign2Relation().apply {
                    arguments = Bundle().apply {
                        putString(ARG_QUEST_PIC, param1)
                        putStringArray(ARG_QUEST_LTR, param2)
                        putStringArray(ARG_QUEST_LInd, param3)
                        putStringArray(ARG_QUEST_RInd, param4)
                        putIntArray(ARG_QUEST_CORR_SGN, param5.toIntArray())

                    }
                }
    }
}
