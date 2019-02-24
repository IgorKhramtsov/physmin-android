package com.example.physmin.fragments

import android.content.Context
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
    private var param1: String? = null
    private var param2: String? = null
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_test_hello, container, false)

        val activity = activity as TestActivity
        val tests = (activity).tests


        activity.showButtonNext()
//        view.button_start_test.setOnClickListener {
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.test_host_fragment, parseTest(tests[0]))
//                    .commit()
//        }
//        view.button_start_test_2.setOnClickListener {
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.my_nav_host_fragment, parseTest(tests[1]))
//                    .commit()
//        }
//        view.button_start_test_2_2.setOnClickListener {
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.my_nav_host_fragment, parseTest(tests[2]))
//                    .commit()
//        }
//        view.button_start_test_3.setOnClickListener {
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.my_nav_host_fragment, parseTest(tests[3]))
//                    .commit()
//        }
        return view
    }

    fun parseTest(test: JSONObject): Fragment {
        return when(test.getString("type")) {
            "relationSings" -> parseRS(test)
            "graph2graph2" -> parseG2G2(test)
            "state2graph" -> parseS2G(test)
            "graph2graph" -> parseG2G(test)
            // TODO: Log error
            else -> parseG2G2(test)
        }
    }

    fun parseRS(test: JSONObject): Fragment {
        var cacheObj: JSONObject
        val taskPic = test.getString("task_picture")
        val questions = test.getJSONArray("questions")
        val letters = ArrayList<String>()
        val rIndex = ArrayList<String>()
        val lIndex = ArrayList<String>()
        val corrSign = ArrayList<Int>()

        var cacheSign: String
        questions.let {
            for (i in 0 until it.length()) {
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

        return FragmentTestSign2Relation.newInstance(taskPic,
                letters.toTypedArray(), lIndex.toTypedArray(), rIndex.toTypedArray(), corrSign.toTypedArray())
    }

    fun parseG2G2(test: JSONObject): Fragment {
        var cacheObj: JSONObject
        val question = test.getJSONObject("question")
        val answersDict = HashMap<Int, String>()
        val correctAnsws = ArrayList<Int>()

        question.getJSONArray("correct_ids").let {
            for (i in 0 until it.length())
                correctAnsws.add(it.getInt(i))
        }

        val answers = test.getJSONArray("answers")
        for (i in 0 until answers.length()) {
            cacheObj = answers.getJSONObject(i)
            answersDict.put(cacheObj.getInt("id"), cacheObj.getString("picture_name"))
        }

        return FragmentTestGraph2Graph2.newInstance(question.getString("picture"),
                correctAnsws.toIntArray(), answersDict)
    }

    fun parseG2G(test: JSONObject): Fragment {
        var _cacheObj: JSONObject
        val answersDict = HashMap<Int, String>()
        val question = test.getJSONObject("question")

        val answers = test.getJSONArray("answers")
        for (i in 0 until answers.length()) {
            _cacheObj = answers.getJSONObject(i)
            answersDict.put(_cacheObj.getInt("id"), _cacheObj.getString("picture_name"))
        }
        return FragmentTestGraph2Graph.newInstance(question.getString("picture"),
                question.getInt("correct_id"), answersDict)
    }

    fun parseS2G(test: JSONObject): Fragment {
        var _cacheObj: JSONObject
        val questionDict = HashMap<Int, String>()
        val answersDict = HashMap<Int, String>()

        val questions = test.getJSONArray("question")
        for (i in 0 until questions.length()) {
            _cacheObj = questions.getJSONObject(i)
            questionDict.put(_cacheObj.getInt("correct_id"), _cacheObj.getString("picture_name"))
        }

        val answers = test.getJSONArray("answers")
        for (i in 0 until answers.length()) {
            _cacheObj = answers.getJSONObject(i)
            answersDict.put(_cacheObj.getInt("id"), _cacheObj.getString("state"))
        }

        return FragmentTestGraph2State.newInstance(questionDict, answersDict)
    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

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
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

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
        fun newInstance() =
                FragmentTestHello().apply {
                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
