package com.example.physmin.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.R
import com.example.physmin.activities.AnswerParcelable
import com.example.physmin.activities.QuestionParcelable
import com.example.physmin.views.GroupScrollable
import com.example.physmin.views.ImageViewPickable
import com.example.physmin.views.dpToPx
import kotlinx.android.synthetic.main.fragment_test_graph2graph_2.view.*

private const val ARG_QUESTION = "param1"
private const val ARG_ANSWERS = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestGraph2Graph2.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestGraph2Graph2.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestGraph2Graph2 : androidx.fragment.app.Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var question: QuestionParcelable? = null
    private var answers: ArrayList<AnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getParcelable<QuestionParcelable>(ARG_QUESTION)
            answers = it.getParcelableArrayList<AnswerParcelable>(ARG_ANSWERS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_graph2graph_2, container, false)

        view.settableGroup_g2g2.GraphicView_g2g2_question.function = question!!.function

//        val width = (Resources.getSystem().displayMetrics.widthPixels / 2) - 40
        val height = 85.dpToPx().toInt()
        val width = 140.dpToPx().toInt()
        var answerPic: ImageViewPickable
        val picParams = ViewGroup.LayoutParams(width, height)

        view.imageView_g2g2_blank1.correctAnsw = question!!.correctIDs.toIntArray()
        view.imageView_g2g2_blank2.correctAnsw = question!!.correctIDs.toIntArray()

        answers?.forEach {
            answerPic = ImageViewPickable(this.context!!, null)
            answerPic.apply {
                layoutParams = picParams
                answerPic.function = it.function
                answer = it.id

                if(question!!.correctIDs.contains(it.id))
                    backColor = ResourcesCompat.getColor(resources, R.color.alpha_green, null) // TODO: DEBUG ONLY
            }
            view.pickableGroup_g2g2.addView(answerPic)
        }

        val mScrollGroup = view.pickableGroup_g2g2 as? GroupScrollable
        mScrollGroup?.setHorizontalOrVertical(false)
                ?.setStartEndScroll(true)
                ?.setDuration(300)
                ?.setInvalidate()

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
         * @return A new instance of fragment FragmentTestGraph2Graph2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(question: QuestionParcelable, answers: ArrayList<AnswerParcelable>) =
                FragmentTestGraph2Graph2().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_QUESTION, question)
                        putParcelableArrayList(ARG_ANSWERS, answers)
                    }
                }
    }
}
