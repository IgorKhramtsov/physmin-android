package com.physmin.android.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.physmin.android.R
import com.physmin.android.activities.QuestionParcelable
import com.physmin.android.activities.TextAnswerParcelable
import com.physmin.android.views.items.ImageViewSettable
import com.physmin.android.views.items.TextViewPickable
import com.physmin.android.views.dpToPx
import kotlinx.android.synthetic.main.fragment_test_state2graph.view.*

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


private const val ARG_QUESTS = "param1"
private const val ARG_ANSWERS = "param2"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestGraph2State.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestGraph2State.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestGraph2State: FragmentTestBase() {
    override var layoutResource = R.layout.fragment_test_state2graph
    private var questions: ArrayList<QuestionParcelable>? = null
    private var answers: ArrayList<TextAnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questions = it.getParcelableArrayList<QuestionParcelable>(ARG_QUESTS)
            answers = it.getParcelableArrayList<TextAnswerParcelable>(ARG_ANSWERS)
        }
    }

    override fun onCreateViewEvent(view: View) {
        questions?.forEach {
            settableGroup.addViewSettable(it.correctIDs.toIntArray(), it.functions)
        }

        answers?.forEach {
            val correctIds = ArrayList<Int>()
            questions?.forEach { question -> if (question.correctIDs.contains(it.id)) correctIds.add(question.id) }

            pickableGroup.addTextViewPickable(it, correctIds.toIntArray())
        }
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
         * @param questions Question dictionary.
         * @param answers Answers dictionary.
         * @return A new instance of fragment FragmentTestGraph2State.
         */
        @JvmStatic
        fun newInstance(questions: ArrayList<QuestionParcelable>, answers: ArrayList<TextAnswerParcelable>) =
                FragmentTestGraph2State().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_QUESTS, questions)
                        putParcelableArrayList(ARG_ANSWERS, answers)
                    }
                }
    }
}
