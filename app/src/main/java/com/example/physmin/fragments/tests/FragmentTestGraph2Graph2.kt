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
import kotlinx.android.synthetic.main.fragment_test_graph2graph.view.*
import kotlinx.android.synthetic.main.fragment_test_graph2graph_2.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_QUEST = "param1"
private const val ARG_CORR_ANSWS = "param2"
private const val ARG_ANSW_IDS = "param3"
private const val ARG_ANSW_STRS = "param4"

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
    // TODO: Rename and change types of parameters
    private var questPicture: String? = null
//    private var answers: Array<String>? = null
    private var listener: OnFragmentInteractionListener? = null
    private var correctAnswer: IntArray? = null
    private var answers: HashMap<Int,String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questPicture = it.getString(ARG_QUEST)
            correctAnswer = it.getIntArray(ARG_CORR_ANSWS)
            answers = HashMap()

            var answersStrings = it.getStringArray(ARG_ANSW_STRS)
            var answersInt = it.getIntArray(ARG_ANSW_IDS)

            for (i in 0 until (answersStrings.count()))
                answers?.put(answersInt[i], answersStrings[i])


        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_graph2graph_2, container, false)


        var id = resources.getIdentifier(questPicture, "drawable", context!!.packageName)
        view.settableGroup_g2g2.imageView_g2g2_question.setImageDrawable(resources.getDrawable(id))

        val width = (Resources.getSystem().displayMetrics.widthPixels / 2) - 40
        var answerPic: ImageViewPickable
        val picParams = ViewGroup.LayoutParams(width, width)
        answers?.forEach {
            answerPic = ImageViewPickable(this.context!!, null)
            answerPic.layoutParams = picParams
            id = resources.getIdentifier(it.value, "drawable", context!!.packageName)
            answerPic.answer = it.key.toShort()
            answerPic.setImageDrawable(resources.getDrawable(id))
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
        fun newInstance(param1: String, param2: IntArray, param3: IntArray, param4: Array<String>) =
                FragmentTestGraph2Graph2().apply {
                    arguments = Bundle().apply {
                        putString(ARG_QUEST, param1)
                        putIntArray(ARG_CORR_ANSWS, param2)
                        putIntArray(ARG_ANSW_IDS, param3)
                        putStringArray(ARG_ANSW_STRS, param4)
                    }
                }
    }
}
