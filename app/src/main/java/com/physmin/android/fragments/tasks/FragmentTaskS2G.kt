package com.physmin.android.fragments.tasks

import com.physmin.android.QuestionParcelable
import com.physmin.android.TextAnswerParcelable
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.physmin.android.R

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


private const val ARG_QUESTS = "param1"
private const val ARG_ANSWERS = "param2"
private const val ARG_ID = "param4"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTaskGraph2State.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTaskGraph2State.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTaskGraph2State: FragmentTaskBase() {
    override var layoutResource = R.layout.fragment_test_state2graph
    override var taskType = "S2G"
    private var questions: ArrayList<QuestionParcelable>? = null
    private var answers: ArrayList<TextAnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questions = it.getParcelableArrayList<QuestionParcelable>(ARG_QUESTS)
            answers = it.getParcelableArrayList<TextAnswerParcelable>(ARG_ANSWERS)
            taskId = it.getInt(ARG_ID)
        }
    }

    override fun onCreateViewEvent(view: View) {
        questions?.forEach {
            settableGroup.addViewSettable(it.correctIDs.toIntArray(), it.functions, it.id)
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
        fun newInstance(questions: ArrayList<QuestionParcelable>,
                        answers: ArrayList<TextAnswerParcelable>,
                        taskId: Int) =
                FragmentTaskGraph2State().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_QUESTS, questions)
                        putParcelableArrayList(ARG_ANSWERS, answers)
                        putInt(ARG_ID, taskId)
                    }
                }
    }
}
