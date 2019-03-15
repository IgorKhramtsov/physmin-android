package com.example.physmin.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Debug
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
import com.example.physmin.views.ImageViewSettable
import kotlinx.android.synthetic.main.fragment_test_graph2graph.*
import kotlinx.android.synthetic.main.fragment_test_graph2graph.view.*
import kotlinx.android.synthetic.main.fragment_test_graph2graph_2.view.*
import kotlinx.android.synthetic.main.fragment_test_state2graph.view.*
import kotlin.collections.HashMap


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
class FragmentTestGraph2Graph : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters

    private var question: QuestionParcelable? = null

    private var answers: ArrayList<AnswerParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getParcelable(ARG_QUESTION)
            answers = it.getParcelableArrayList(ARG_ANSWERS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_test_graph2graph, container, false)

        view.settable_group_g2g.GraphicView_g2g_question.function = question!!.function

        view.imageView_g2g_blank1.correctAnsw = question!!.correctIDs.toIntArray()

        val width = (Resources.getSystem().displayMetrics.widthPixels / 2) - 40
        var answerPic: ImageViewPickable
        val picParams = ViewGroup.LayoutParams(width, width)
        answers?.forEach {

            answerPic = ImageViewPickable(this.context!!, null)
            answerPic.apply {
                layoutParams = picParams
                function = it.function
                answer = it.id


                if(question!!.correctIDs.contains(it.id))
                    backColor = ResourcesCompat.getColor(resources, R.color.alpha_green, null) // TODO: DEBUG ONLY
            }
            view.pickableGroup_g2g.addView(answerPic)
        }

        val mScrollGroup = view.pickableGroup_g2g as? GroupScrollable
        mScrollGroup?.setHorizontalOrVertical(false)
                ?.setStartEndScroll(true)
                ?.setDuration(300)
                ?.setInvalidate()

        return view
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
        fun newInstance(question: QuestionParcelable, answers: ArrayList<AnswerParcelable>) =
                FragmentTestGraph2Graph().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_QUESTION, question)
                        putParcelableArrayList(ARG_ANSWERS, answers)
                    }
                }
    }
}
