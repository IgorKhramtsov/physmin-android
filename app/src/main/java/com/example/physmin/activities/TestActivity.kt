package com.example.physmin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.transaction
import com.example.physmin.R
import com.example.physmin.fragments.tests.*
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import org.json.JSONObject
import java.net.SocketTimeoutException

class TestActivity: AppCompatActivity()//,
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
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        buttonNext = findViewById(R.id.button_test_next)
        functions = FirebaseFunctions.getInstance("europe-west1")
        hideButtonNext()

        var text: String
        getTest().addOnCompleteListener {
            if (!it.isSuccessful)
                return@addOnCompleteListener

            text = it.result!!
            proccessTests(text)
        }

//    val text = "{\n" +
//            "  \"tests\": [\n" +
//            "    {\n" +
//            "      \"type\": \"graph2graph\",\n" +
//            "      \"test_id\": \"0\",\n" +
//            "      \"title\": \"Подберите пару для графика\",\n" +
//            "      \"question\": {\n" +
//            "        \"picture\": \"graph_x_1\",\n" +
//            "        \"correct_id\": \"1\"\n" +
//            "      },\n" +
//            "      \"answers\": [\n" +
//            "        {\n" +
//            "          \"id\": \"1\",\n" +
//            "          \"picture_name\": \"graph_v_1\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"2\",\n" +
//            "          \"picture_name\": \"graph_v_2\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"3\",\n" +
//            "          \"picture_name\": \"graph_v_3\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"4\",\n" +
//            "          \"picture_name\": \"graph_v_4\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"5\",\n" +
//            "          \"picture_name\": \"graph_v_5\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"6\",\n" +
//            "          \"picture_name\": \"graph_v_6\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"7\",\n" +
//            "          \"picture_name\": \"graph_v_7\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"8\",\n" +
//            "          \"picture_name\": \"graph_v_8\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    },\n" +
//            "    {\n" +
//            "      \"type\": \"state2graph\",\n" +
//            "      \"test_id\": \"1\",\n" +
//            "      \"title\": \"Найдите соответсвите графиков и состояний\",\n" +
//            "      \"question\": [\n" +
//            "        {\n" +
//            "          \"correct_id\": \"1\",\n" +
//            "          \"picture_name\": \"graph_x_1\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"correct_id\": \"2\",\n" +
//            "          \"picture_name\": \"graph_x_2\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"correct_id\": \"3\",\n" +
//            "          \"picture_name\": \"graph_x_3\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"correct_id\": \"4\",\n" +
//            "          \"picture_name\": \"graph_x_4\"\n" +
//            "        }\n" +
//            "      ],\n" +
//            "      \"answers\": [\n" +
//            "        {\n" +
//            "          \"id\": \"0\",\n" +
//            "          \"state\": \"Движется вперед\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"1\",\n" +
//            "          \"state\": \"Движется назад\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"2\",\n" +
//            "          \"state\": \"Движется назад\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"3\",\n" +
//            "          \"state\": \"Движется вперед\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"4\",\n" +
//            "          \"state\": \"Движется назад\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"5\",\n" +
//            "          \"state\": \"Движется вперед\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"6\",\n" +
//            "          \"state\": \"Движется вперед\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"7\",\n" +
//            "          \"state\": \"Движется вперед\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    },\n" +
//            "    {\n" +
//            "      \"type\": \"graph2graph2\",\n" +
//            "      \"test_id\": \"test_id\",\n" +
//            "      \"title\": \"Найдите для графика два других\",\n" +
//            "      \"question\": {\n" +
//            "        \"picture\": \"graph_x_1\",\n" +
//            "        \"correct_ids\": [\n" +
//            "          \"1\", \"2\"\n" +
//            "        ]\n" +
//            "      },\n" +
//            "      \"answers\": [\n" +
//            "        {\n" +
//            "          \"id\": \"1\",\n" +
//            "          \"picture_name\": \"graph_v_1\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"2\",\n" +
//            "          \"picture_name\": \"graph_v_2\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"3\",\n" +
//            "          \"picture_name\": \"graph_v_3\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"4\",\n" +
//            "          \"picture_name\": \"graph_v_4\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"5\",\n" +
//            "          \"picture_name\": \"graph_v_5\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"6\",\n" +
//            "          \"picture_name\": \"graph_v_6\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"7\",\n" +
//            "          \"picture_name\": \"graph_v_7\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"id\": \"8\",\n" +
//            "          \"picture_name\": \"graph_v_8\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    },\n" +
//            "    {\n" +
//            "      \"type\": \"relationSings\",\n" +
//            "      \"test_id\": \"test_id\",\n" +
//            "      \"title\": \"Установите правильные неравенства\",\n" +
//            "      \"task_picture\": \"screenshot1\",\n" +
//            "      \"questions\": [\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"letter\": \"S\",\n" +
//            "          \"left_index\": \"01\",\n" +
//            "          \"right_index\": \"12\",\n" +
//            "          \"correct_sign\": \"equal\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    }\n" +
//            "  ]\n" +
//            "}"

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }


    fun parseTest(test: JSONObject): androidx.fragment.app.Fragment {
        return when (test.getString("type")) {
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

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        var question = QuestionParcelable(questionJson)
        var answers = ArrayList<AnswerParcelable>()

        for (i in 0 until answersJson.length()){
            answers.add(AnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph2.newInstance(question, answers)
    }

    fun parseG2G(test: JSONObject): androidx.fragment.app.Fragment {

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        var question = QuestionParcelable(questionJson)
        var answers = ArrayList<AnswerParcelable>()

        for (i in 0 until answersJson.length()){
            answers.add(AnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph.newInstance(question, answers)
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

    fun getTest(): Task<String> {
        return functions
                .getHttpsCallable("getTestNew")
                .call()
                .continueWith { task ->
                    // TODO: Proccess timeout exception
                    if(task.exception is SocketTimeoutException)
                        Log.e("TestActivity", "getTest() - Timeout!")

                    val result = task.result?.data as String
                    result
                }
    }

    fun proccessTests(test: String) {
        showButtonNext()

        val data = JSONObject(test.substring(test.indexOf("{"), test.lastIndexOf("}") + 1)).optJSONArray("tests")
        for (i in 0 until data!!.length())
            tests.add(data.getJSONObject(i))

        buttonNext?.setOnClickListener {
            if (currentTestIndex >= tests.size)
                return@setOnClickListener

            supportFragmentManager.transaction {
                replace(R.id.test_host_fragment, parseTest(tests[currentTestIndex++]))
                hideButtonNext()
            }
        }
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

class FunctionParcelable() : Parcelable {
    var x: Int = 0
    var v: Int = 0
    var a: Int = 0
    var funcType: String = ""

    constructor(jsonObject: JSONObject): this() {
        jsonObject.getJSONObject("params").let {
            this.x = it.getInt("x")
            this.v = it.getInt("v")
            this.a = it.getInt("a")
        }
        this.funcType = jsonObject.getString("funcType")
    }
    constructor(parcel: Parcel?) : this() {
        parcel?.apply {
            x = readInt()
            v = readInt()
            a = readInt()
            funcType = readString()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(x)
            writeInt(v)
            writeInt(a)
            writeString(funcType)
        }
    }

    companion object CREATOR: Parcelable.Creator<FunctionParcelable> {
        override fun createFromParcel(source: Parcel?): FunctionParcelable {
            return FunctionParcelable(source)
        }

        override fun newArray(size: Int): Array<FunctionParcelable?> {
            return arrayOfNulls(size)
        }
    }

}

class QuestionParcelable(): Parcelable {
    var correctIDs = ArrayList<Int>()
    var function = FunctionParcelable()

    constructor(jsonObject: JSONObject): this() {
        jsonObject.getJSONArray("correctID").let {
            for (i in 0 until it.length())
                correctIDs.add(it.getInt(i))

            function = FunctionParcelable(jsonObject)
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            val intArray = createIntArray()
//            readIntArray(intArray)
            correctIDs = intArray.toCollection(ArrayList())
            function = readParcelable(FunctionParcelable.javaClass.classLoader)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeIntArray(correctIDs.toIntArray())
            writeParcelable(function, flags)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class AnswerParcelable(): Parcelable {
    var id = 0
    var function = FunctionParcelable()

    constructor(jsonObject: JSONObject): this(){
        id = jsonObject.getInt("id")
        function = FunctionParcelable(jsonObject)
    }
    constructor(parcel: Parcel?): this(){
        parcel?.apply {
            id = readInt()
            function = readParcelable(FunctionParcelable.javaClass.classLoader)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeParcelable(function, flags)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}