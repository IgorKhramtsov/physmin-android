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
                .getHttpsCallable("getTest")
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
    var x: Float = 0f
    var v: Float = 0f
    var a: Float = 0f
    var funcType: String = ""

    constructor(jsonObject: JSONObject): this() {
        jsonObject.getJSONObject("params").let {
            if (it.has("x"))
                this.x = it.getDouble("x").toFloat()
            if (it.has("v"))
                this.v = it.getDouble("v").toFloat()
            this.a = it.getDouble("a").toFloat()
        }
        this.funcType = jsonObject.getString("funcType")
    }
    constructor(parcel: Parcel?) : this() {
        parcel?.apply {
            x = readFloat()
            v = readFloat()
            a = readFloat()
            funcType = readString()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeFloat(x)
            writeFloat(v)
            writeFloat(a)
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
        jsonObject.getJSONArray("correctIDs").let {
            for (i in 0 until it.length())
                correctIDs.add(it.getInt(i))

            function = FunctionParcelable(jsonObject.getJSONObject("graph"))
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
        function = FunctionParcelable(jsonObject.getJSONObject("graph"))
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