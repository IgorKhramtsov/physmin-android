package com.example.physmin.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physmin.R
import com.example.physmin.activities.TestActivity
import com.example.physmin.fragments.tests.FragmentTestGraph2Graph
import com.example.physmin.fragments.tests.FragmentTestGraph2Graph2
import com.example.physmin.fragments.tests.FragmentTestGraph2State
import com.example.physmin.fragments.tests.FragmentTestSign2Relation
import kotlinx.android.synthetic.main.fragment_test_hello.view.*
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentTestHello.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentTestHello.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentTestHello : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_test_hello, container, false)

        val tests = (activity as TestActivity).tests

        kotlin.run {
            val questions = tests[1].getJSONArray("question")
            var questionDict = HashMap<Int, String>()
            var cacheObj: JSONObject
            for (i in 0 until questions.length()) {
                cacheObj = questions.getJSONObject(i)
                questionDict.put(cacheObj.getInt("correct_id"), cacheObj.getString("picture_name"))
            }

            val answers = tests[1].getJSONArray("answers")
            var answersDict = HashMap<Int, String>()
            for (i in 0 until answers.length()) {
                cacheObj = answers.getJSONObject(i)
                answersDict.put(cacheObj.getInt("id"), cacheObj.getString("state"))
            }

            view.button_start_test.setOnClickListener {
                fragmentManager!!.beginTransaction()
                        .replace(R.id.my_nav_host_fragment, FragmentTestGraph2State.newInstance(questionDict, answersDict))
                        .commit()
            }
        }

        kotlin.run {
            var cacheObj: JSONObject
            var question = tests[0].getJSONObject("question")

            val answers = tests[0].getJSONArray("answers")
            var answersDict = HashMap<Int, String>()
            for (i in 0 until answers.length()) {
                cacheObj = answers.getJSONObject(i)
                answersDict.put(cacheObj.getInt("id"), cacheObj.getString("picture_name"))
            }

            view.button_start_test_2.setOnClickListener {
                fragmentManager!!.beginTransaction()
                        .replace(R.id.my_nav_host_fragment, FragmentTestGraph2Graph.newInstance(question.getString("picture"),
                                question.getInt("correct_id"),answersDict))
                        .commit()
            }
        }
        kotlin.run {
            var cacheObj: JSONObject
            val question = tests[2].getJSONObject("question")
            var correctAnsws = ArrayList<Int>()
            question.getJSONArray("correct_ids").let {
                for (i in 0 until it.length())
                    correctAnsws.add(it.getInt(i))
            }

            val answers = tests[2].getJSONArray("answers")
            var answersDict = HashMap<Int, String>()
            for (i in 0 until answers.length()) {
                cacheObj = answers.getJSONObject(i)
                answersDict.put(cacheObj.getInt("id"), cacheObj.getString("picture_name"))
            }

            view.button_start_test_2_2.setOnClickListener {
                fragmentManager!!.beginTransaction()
                        .replace(R.id.my_nav_host_fragment, FragmentTestGraph2Graph2.newInstance(question.getString("picture"),
                                correctAnsws.toIntArray(),answersDict))
                        .commit()
            }
        }
        kotlin.run {
            var cacheObj: JSONObject
            var taskPic = tests[3].getString("task_picture")
            var questions = tests[3].getJSONArray("questions")
            var letters = ArrayList<String>()
            var rIndex = ArrayList<String>()
            var lIndex = ArrayList<String>()
            var corrSign = ArrayList<Int>()

            // TODO CHECK PERFOMANCE OF CACHING IN LET
            var cacheSign: String
            questions.let {
                for(i in 0 until it.length()) {
                    cacheObj = it.getJSONObject(i)
                    letters.add(cacheObj.getString("letter"))
                    lIndex.add(cacheObj.getString("left_index"))
                    rIndex.add(cacheObj.getString("right_index"))
                    cacheSign = cacheObj.getString("correct_sign")
                    when (cacheSign) {
                        "equal" -> corrSign.add(0)
                        "more" -> corrSign.add(1)
                        "less" -> corrSign.add(-1)
                    }
                }
            }
            view.button_start_test_3.setOnClickListener {
                fragmentManager!!.beginTransaction()
                        .replace(R.id.my_nav_host_fragment, FragmentTestSign2Relation.newInstance(taskPic,
                                letters.toTypedArray(),lIndex.toTypedArray(),rIndex.toTypedArray(),corrSign.toTypedArray()))
                        .commit()
            }
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
         * @return A new instance of fragment FragmentTestHello.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentTestHello().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
