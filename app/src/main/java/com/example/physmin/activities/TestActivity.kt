package com.example.physmin.activities

import android.app.Fragment
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.transaction
import com.example.physmin.R
import com.example.physmin.fragments.FragmentTestHello
import com.example.physmin.fragments.tests.*
import kotlinx.android.synthetic.main.activity_test.*
import org.json.JSONObject

class TestActivity : AppCompatActivity()//,
        //FragmentTestHello.OnFragmentInteractionListener,
//        FragmentTestGraph2State.OnAllDoneListener,
//        FragmentTestGraph2Graph2.OnAllDoneListener,
//        FragmentTestSign2Relation.OnAllDoneListener,
//        FragmentTestGraph2Graph.OnAllDoneListener
{
    var tests = arrayListOf<JSONObject>()
    //var listener: FragmentTestHello.OnAllDoneListener? = null
    var buttonNext: Button? = null
    var currentTestIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        buttonNext = findViewById(R.id.button_test_next)

    val text = "{\n" +
            "  \"tests\": [\n" +
            "    {\n" +
            "      \"type\": \"graph2graph\",\n" +
            "      \"test_id\": \"0\",\n" +
            "      \"title\": \"Подберите пару для графика\",\n" +
            "      \"question\": {\n" +
            "        \"picture\": \"graph_x_1\",\n" +
            "        \"correct_id\": \"1\"\n" +
            "      },\n" +
            "      \"answers\": [\n" +
            "        {\n" +
            "          \"id\": \"1\",\n" +
            "          \"picture_name\": \"graph_v_1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"2\",\n" +
            "          \"picture_name\": \"graph_v_2\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"3\",\n" +
            "          \"picture_name\": \"graph_v_3\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"4\",\n" +
            "          \"picture_name\": \"graph_v_4\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"5\",\n" +
            "          \"picture_name\": \"graph_v_5\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"6\",\n" +
            "          \"picture_name\": \"graph_v_6\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"7\",\n" +
            "          \"picture_name\": \"graph_v_7\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"8\",\n" +
            "          \"picture_name\": \"graph_v_8\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"state2graph\",\n" +
            "      \"test_id\": \"1\",\n" +
            "      \"title\": \"Найдите соответсвите графиков и состояний\",\n" +
            "      \"question\": [\n" +
            "        {\n" +
            "          \"correct_id\": \"1\",\n" +
            "          \"picture_name\": \"graph_x_1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"correct_id\": \"2\",\n" +
            "          \"picture_name\": \"graph_x_2\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"correct_id\": \"3\",\n" +
            "          \"picture_name\": \"graph_x_3\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"correct_id\": \"4\",\n" +
            "          \"picture_name\": \"graph_x_4\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"answers\": [\n" +
            "        {\n" +
            "          \"id\": \"0\",\n" +
            "          \"state\": \"Движется вперед\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"1\",\n" +
            "          \"state\": \"Движется назад\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"2\",\n" +
            "          \"state\": \"Движется назад\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"3\",\n" +
            "          \"state\": \"Движется вперед\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"4\",\n" +
            "          \"state\": \"Движется назад\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"5\",\n" +
            "          \"state\": \"Движется вперед\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"6\",\n" +
            "          \"state\": \"Движется вперед\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"7\",\n" +
            "          \"state\": \"Движется вперед\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"graph2graph2\",\n" +
            "      \"test_id\": \"test_id\",\n" +
            "      \"title\": \"Найдите для графика два других\",\n" +
            "      \"question\": {\n" +
            "        \"picture\": \"graph_x_1\",\n" +
            "        \"correct_ids\": [\n" +
            "          \"1\", \"2\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"answers\": [\n" +
            "        {\n" +
            "          \"id\": \"1\",\n" +
            "          \"picture_name\": \"graph_v_1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"2\",\n" +
            "          \"picture_name\": \"graph_v_2\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"3\",\n" +
            "          \"picture_name\": \"graph_v_3\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"4\",\n" +
            "          \"picture_name\": \"graph_v_4\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"5\",\n" +
            "          \"picture_name\": \"graph_v_5\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"6\",\n" +
            "          \"picture_name\": \"graph_v_6\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"7\",\n" +
            "          \"picture_name\": \"graph_v_7\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"8\",\n" +
            "          \"picture_name\": \"graph_v_8\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"relationSings\",\n" +
            "      \"test_id\": \"test_id\",\n" +
            "      \"title\": \"Установите правильные неравенства\",\n" +
            "      \"task_picture\": \"screenshot1\",\n" +
            "      \"questions\": [\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"letter\": \"S\",\n" +
            "          \"left_index\": \"01\",\n" +
            "          \"right_index\": \"12\",\n" +
            "          \"correct_sign\": \"equal\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}"
        val data = JSONObject(text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1)).optJSONArray("tests")
        for (i in 0 until data!!.length())
            tests.add(data.getJSONObject(i))

        // Greeting
        supportFragmentManager.transaction {
            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
        }

        buttonNext?.setOnClickListener {
            if (currentTestIndex >= tests.size)
                return@setOnClickListener

            supportFragmentManager.transaction {
                replace(R.id.test_host_fragment, parseTest(tests[currentTestIndex++]))
            }
            hideButtonNext()
        }

}


    fun parseTest(test: JSONObject): androidx.fragment.app.Fragment {
        return when(test.getString("type")) {
            "relationSings" -> parseRS(test)
            "graph2graph2" -> parseG2G2(test)
            "state2graph" -> parseS2G(test)
            "graph2graph" -> parseG2G(test)
            // TODO: Log error
            else -> parseG2G2(test)
        }
    }

    fun parseRS(test: JSONObject): androidx.fragment.app.Fragment {
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

    fun parseG2G2(test: JSONObject): androidx.fragment.app.Fragment {
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

    fun parseG2G(test: JSONObject): androidx.fragment.app.Fragment {
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

    fun parseS2G(test: JSONObject): androidx.fragment.app.Fragment {
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

    fun showButtonNext() {
        buttonNext?.visibility = View.VISIBLE
    }

    fun hideButtonNext() {
        buttonNext?.visibility = View.GONE
    }

//    class TestResult {
//        var test_id: Int
//        var answers = List<Answer>()
//    }
//    class Answer {
//        var answer_id: Int
//        var question_id: Int?
//        var correct_answer_id: Int?
//    }
}
