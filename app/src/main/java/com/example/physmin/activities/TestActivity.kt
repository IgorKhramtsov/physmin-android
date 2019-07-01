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
import com.example.physmin.views.layouts.TestConstraintLayout
import com.example.physmin.views.ProgressBarView
import com.example.physmin.views.TimerView
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.android.synthetic.main.activity_test.*
import org.json.JSONObject
import java.net.SocketTimeoutException

class TestActivity: AppCompatActivity(), FragmentTestBase.OnFragmentTestBaseListener//,
//FragmentTestHello.OnFragmentInteractionListener,
//        FragmentTestGraph2State.OnAllDoneListener,
//        FragmentTestGraph2Graph2.OnAllDoneListener,
//        FragmentTestSign2Relation.OnAllDoneListener,
//        FragmentTestGraph2Graph.OnAllDoneListener
{
    var tests = arrayListOf<JSONObject>()
    //var listener: FragmentTestHello.OnAllDoneListener? = null
    lateinit var buttonNext: Button
    var nextTestIndex = 0
    var getTestFunctionName = "getTest"
    lateinit var timerView: TimerView
    private lateinit var functions: FirebaseFunctions
    lateinit var progressBarView: ProgressBarView
    var testConstraintLayout: TestConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        buttonNext = findViewById(R.id.button_test_next)
        timerView = findViewById(R.id.Timer)
        functions = FirebaseFunctions.getInstance("europe-west1")
        hideButtonNext()
        loadingHorBar.show()
        intent.getStringExtra("GetTestFunctionName")?.let {
            getTestFunctionName = it
        }
        this.progressBarView = progressBar
        progressBarView.hide()

        getTest().addOnCompleteListener {
            loadingHorBar.hide()

            if (!it.isSuccessful)
                return@addOnCompleteListener

            processTests(it.result!!)
        }

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }

    override fun asd() {

    }

    fun parseTest(test: JSONObject): androidx.fragment.app.Fragment {
        return when (test.getString("type")) {
            "relationSings" -> parseRS(test)
            "graph2graph2" -> parseG2G2(test)
            "graph2state" -> parseS2G(test)
            "graph2graph" -> parseG2G(test)
            // TODO: Log error
            else -> parseG2G2(test)
        }
    }

    fun parseRS(test: JSONObject): androidx.fragment.app.Fragment {
        val questionJson = test.getJSONArray("question")
        val answersJson = test.getJSONArray("answers")

        var question = ArrayList<QuestionParcelable>()
        var answers = ArrayList<FunctionAnswerRelationSignParcelable>()

        for (i in 0 until questionJson.length())
            question.add(QuestionParcelable(questionJson.getJSONObject(i)))
        for (i in 0 until answersJson.length())
            answers.add(FunctionAnswerRelationSignParcelable(answersJson.getJSONObject(i)))

        return FragmentTestSign2Relation.newInstance(question, answers)
    }

    fun parseG2G2(test: JSONObject): androidx.fragment.app.Fragment {

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        val questions = ArrayList<QuestionParcelable>()
        val answers = ArrayList<FunctionAnswerParcelable>()

        questions.add(QuestionParcelable(questionJson))
        for (i in 0 until answersJson.length()) {
            answers.add(FunctionAnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph2.newInstance(questions, answers)
    }

    fun parseG2G(test: JSONObject): androidx.fragment.app.Fragment {

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        val questions = ArrayList<QuestionParcelable>()
        val answers = ArrayList<FunctionAnswerParcelable>()

        questions.add(QuestionParcelable(questionJson))
        for (i in 0 until answersJson.length()) {
            answers.add(FunctionAnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph.newInstance(questions, answers)
    }

    fun parseS2G(test: JSONObject): androidx.fragment.app.Fragment {
        val questionJson = test.getJSONArray("question")
        val answersJson = test.getJSONArray("answers")

        var question = ArrayList<QuestionParcelable>()
        var answers = ArrayList<TextAnswerParcelable>()

        for (i in 0 until questionJson.length())
            question.add(QuestionParcelable(questionJson.getJSONObject(i)))
        for (i in 0 until answersJson.length())
            answers.add(TextAnswerParcelable(answersJson.getJSONObject(i)))

        return FragmentTestGraph2State.newInstance(question, answers)
    }

    fun getTest(): Task<String> {
        return functions
                .getHttpsCallable(getTestFunctionName)
                .call()
                .continueWith { task ->
                    // TODO: Process timeout exception
                    if (task.exception is SocketTimeoutException)
                        Log.e("TestActivity", "getTest() - Timeout!")
                    else if (task.exception is FirebaseFunctionsException)
                        Log.e("TestActivity", "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")

                    val result = task.result?.data as String
                    result
                }
    }

    fun processTests(test: String) {
        showButtonNext()

        val data = JSONObject(test.substring(test.indexOf("{"), test.lastIndexOf("}") + 1)).optJSONArray("tests")
        for (i in 0 until data!!.length())
            tests.add(data.getJSONObject(i))

        progressBar.segmentCount = tests.count()

        buttonNext.setOnClickListener {
            if (nextTestIndex >= tests.size)
                return@setOnClickListener

            progressBarView.show()
            supportFragmentManager.transaction {
                onTestSwitch()
                replace(R.id.test_host_fragment, parseTest(tests[nextTestIndex++]))
                timerView.restart()
                hideButtonNext()
            }

        }
    }

    private fun onTestSwitch() {
        testConstraintLayout?.let {

            if (it.isAnswersCorrect())
                progressBarView.addSegment()
            else {
                tests.add(tests[nextTestIndex])

                // TODO: create custom arrayList
//            tests.removeAt(nextTestIndex)
//            nextTestIndex--
            }
        }
    }

    fun showButtonNext() {
        buttonNext.visibility = View.VISIBLE
    }

    fun hideButtonNext() {
        buttonNext.visibility = View.GONE
    }


}

class FunctionParcelable(): Parcelable {
    var x: Float = 0f
    var v: Float = 0f
    var a: Float = 0f
    var len: Int = 0
    var funcType: String = ""

    constructor(jsonObject: JSONObject): this() {
        jsonObject.getJSONObject("params").let {
            if (it.has("x"))
                this.x = it.getDouble("x").toFloat()
            if (it.has("v"))
                this.v = it.getDouble("v").toFloat()
            this.a = it.getDouble("a").toFloat()

            if (it.has("len"))
                this.len = it.getInt("len")
        }
        this.funcType = jsonObject.getString("funcType")
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            x = readFloat()
            v = readFloat()
            a = readFloat()
            len = readInt()
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
            writeInt(len)
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
    var functions = ArrayList<FunctionParcelable>()

    constructor(jsonObject: JSONObject): this() {
        if (jsonObject.has("correctIDs"))
            jsonObject.getJSONArray("correctIDs").let {
                for (i in 0 until it.length())
                    correctIDs.add(it.getInt(i))
            }
        jsonObject.getJSONArray("graph").let {
            for (i in 0 until it.length())
                functions.add(FunctionParcelable(it.optJSONObject(i)))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            val intArray = createIntArray()
//            readIntArray(intArray)
            correctIDs = intArray.toCollection(ArrayList())
            functions = createTypedArrayList(FunctionParcelable.CREATOR)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeIntArray(correctIDs.toIntArray())
            writeTypedList(functions)
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

class FunctionAnswerParcelable(): Parcelable {
    var id = 0
    var functions = ArrayList<FunctionParcelable>()

    constructor(jsonObject: JSONObject): this() {
        id = jsonObject.getInt("id")
        jsonObject.getJSONArray("graph").let {
            for (i in 0 until it.length())
                functions.add(FunctionParcelable(it.optJSONObject(i)))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            functions = createTypedArrayList(FunctionParcelable.CREATOR)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeTypedList(functions)
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

class FunctionAnswerRelationSignParcelable(): Parcelable {
    var id = 0
    var letter = ""
    var leftIndex = ""
    var rightIndex = ""
    var correctSign = 0

    constructor(jsonObject: JSONObject): this() {
        jsonObject.apply {
            id = getInt("id")
            letter = getString("letter")
            leftIndex = getString("leftIndex")
            rightIndex = getString("rightIndex")
            correctSign = getInt("correctSign")
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            letter = readString()
            leftIndex = readString()
            rightIndex = readString()
            correctSign = readInt()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeString(letter)
            writeString(leftIndex)
            writeString(rightIndex)
            writeInt(correctSign)
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

class TextAnswerParcelable(): Parcelable {
    var id = 0
    var text: String = ""

    constructor(jsonObject: JSONObject): this() {
        id = jsonObject.getInt("id")
        text = jsonObject.getString("text")
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            text = readString()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeString(text)
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