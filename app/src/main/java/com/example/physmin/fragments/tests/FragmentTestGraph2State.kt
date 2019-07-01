package com.example.physmin.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.activities.QuestionParcelable
import com.example.physmin.activities.TextAnswerParcelable
import com.example.physmin.views.items.ImageViewSettable
import com.example.physmin.views.items.TextViewPickable
import com.example.physmin.views.dpToPx
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
class FragmentTestGraph2State: androidx.fragment.app.Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var questions: ArrayList<QuestionParcelable>? = null
    private var answers: ArrayList<TextAnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questions = it.getParcelableArrayList<QuestionParcelable>(ARG_QUESTS)
            answers = it.getParcelableArrayList<TextAnswerParcelable>(ARG_ANSWERS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_test_state2graph, container, false)

        val width = 140.dpToPx().toInt()
        val height = 85.dpToPx().toInt()
        var questPic: ImageViewSettable
        val picParams = ViewGroup.LayoutParams(width, height)

        questions?.forEach {
            questPic = ImageViewSettable(this.context!!, null).apply {
                correctAnswers = it.correctIDs.toIntArray()
                layoutParams = picParams
                graph.functions = it.functions
            }
            view.settable_group.addView(questPic)
        }

        val textParams = ViewGroup.LayoutParams(150.dpToPx().toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        var textView: TextViewPickable
        answers?.forEach {
            textView = TextViewPickable(this.context!!, null).apply {
                setPadding(6.dpToPx().toInt(), 3.dpToPx().toInt(), 6.dpToPx().toInt(), 3.dpToPx().toInt())
                answer = it.id
                layoutParams = textParams
                text = it.text
            }
            view.pickable_group.addView(textView)
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
