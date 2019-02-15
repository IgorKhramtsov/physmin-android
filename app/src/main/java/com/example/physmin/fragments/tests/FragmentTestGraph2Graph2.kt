package com.example.physmin.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.views.GroupScrollable
import com.example.physmin.views.ImageViewPickable
import kotlinx.android.synthetic.main.fragment_test_graph2graph_2.view.*

private const val ARG_QUEST = "param1"
private const val ARG_CORR_ANSWS = "param2"
private const val ARG_ANSWERS = "param3"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestGraph2Graph2.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestGraph2Graph2.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestGraph2Graph2 : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var questPicture: String? = null
    private var correctAnswer: IntArray? = null
    private var answers: HashMap<Int,String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questPicture = it.getString(ARG_QUEST)
            correctAnswer = it.getIntArray(ARG_CORR_ANSWS)

            answers = it.getSerializable(ARG_ANSWERS) as HashMap<Int, String>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_graph2graph_2, container, false)


        var picId = resources.getIdentifier(questPicture, "drawable", context!!.packageName)
        view.settableGroup_g2g2.imageView_g2g2_question.setImageDrawable(resources.getDrawable(picId))

        val width = (Resources.getSystem().displayMetrics.widthPixels / 2) - 40
        var answerPic: ImageViewPickable
        val picParams = ViewGroup.LayoutParams(width, width)

        view.imageView_g2g2_blank1.correctAnsw = correctAnswer
        view.imageView_g2g2_blank2.correctAnsw = correctAnswer

        answers?.forEach {
            answerPic = ImageViewPickable(this.context!!, null)
            answerPic.apply {
                layoutParams = picParams
                picId = resources.getIdentifier(it.value, "drawable", context!!.packageName)
                answer = it.key
                setImageDrawable(resources.getDrawable(picId))
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
         * @return A new instance of fragment FragmentTestGraph2Graph2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(questionPic: String, correctAnsw: IntArray, answers: HashMap<Int, String>) =
                FragmentTestGraph2Graph2().apply {
                    arguments = Bundle().apply {
                        putString(ARG_QUEST, questionPic)
                        putIntArray(ARG_CORR_ANSWS, correctAnsw)
                        putSerializable(ARG_ANSWERS, answers)
                    }
                }
    }
}
