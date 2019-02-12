package com.example.physmin.fragments.tests

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.views.ImageViewSettable
import com.example.physmin.views.TextViewPickable
import kotlinx.android.synthetic.main.fragment_test_state2graph.view.*

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private val DEF_QUEST_PICTURES = arrayOf("@drawable/graph1", "@drawable/graph2", "@drawable/graph3", "@drawable/graph4")
private val DEF_ANSWERS = arrayOf("Назад, ускоряясь вперед", "Назад, ускоряясь вперед", "Назад, ускоряясь вперед",
        "Назад, ускоряясь вперед", "Назад, ускоряясь вперед", "Назад, ускоряясь вперед")
private const val ARG_QUESTS = "param1"
private const val ARG_CORR_ANSWS = "param3"
private const val ARG_ANSW_IDS = "param3"
private const val ARG_ANSW_STRS = "param4"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestGraph2State.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestGraph2State.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestGraph2State : Fragment() {
    // TODO: Rename and change types of parameters
    private var questPictures: Array<String>? = null
//    private var answers: Array<String>? = null
    private var listener: OnFragmentInteractionListener? = null
    private var correctAnswer: IntArray? = null
    private var answers: HashMap<Int,String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questPictures = it.getStringArray(ARG_QUESTS)
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

        val view:View = inflater.inflate(R.layout.fragment_test_state2graph, container, false)

        val width = (Resources.getSystem().displayMetrics.widthPixels / 2) - 40
        var questPic:ImageViewSettable
        val picParams = ViewGroup.LayoutParams(width, width)
        var id:Int
        questPictures?.forEach {
            questPic = ImageViewSettable(this.context!!, null)
            questPic.layoutParams = picParams
            id = resources.getIdentifier(it, "drawable", context!!.packageName)
            questPic.setImageDrawable(resources.getDrawable(id))
            view.settable_group.addView(questPic)
        }

        val textParams = ViewGroup.LayoutParams(150.toPx(), 40.toPx())
        var textView: TextViewPickable
        answers?.forEach {
            textView = TextViewPickable(this.context!!, null)
            textView.layoutParams = textParams
            textView.text = it.value
            textView.textSize = 16f
            textView.gravity = Gravity.CENTER

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
         * @return A new instance of fragment FragmentTestGraph2State.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Array<String>, param2: IntArray,
                        param3: IntArray, param4: Array<String>) =
                FragmentTestGraph2State().apply {
                    arguments = Bundle().apply {
                        putStringArray(ARG_QUESTS, param1)
                        putIntArray(ARG_CORR_ANSWS, param2)
                        putIntArray(ARG_ANSW_IDS, param3)
                        putStringArray(ARG_ANSW_STRS, param4)
                    }
                }
    }
}
