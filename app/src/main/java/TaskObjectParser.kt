import android.os.Parcel
import android.os.Parcelable
import com.physmin.android.TaskObject
import com.physmin.android.fragments.tasks.FragmentTestGraph2Graph
import com.physmin.android.fragments.tasks.FragmentTestGraph2State
import com.physmin.android.fragments.tasks.FragmentTestSign2Relation
import java.lang.Exception


fun parseTask(task: TaskObject): androidx.fragment.app.Fragment {
    return when (task["type"]) {
        "RS" -> parseRS(task)
        "S2G" -> parseS2G(task)
        "G2G" -> parseG2G(task)
        // TODO: Log error
        else -> throw Exception("Unknown test type: " + task["type"])
    }
}

private fun parseG2G(task: TaskObject): androidx.fragment.app.Fragment {
    val questionObject = task["question"] as HashMap<String, *>
    val answersObject = task["answers"] as ArrayList<HashMap<String, *>>

    val questions = ArrayList<QuestionParcelable>()
    val answers = ArrayList<FunctionAnswerParcelable>()

    questions.add(QuestionParcelable(questionObject))
    for (i in 0 until answersObject.count()) {
        answers.add(FunctionAnswerParcelable(answersObject[i]))
    }

    return FragmentTestGraph2Graph.newInstance(questions, answers)
}


private fun parseS2G(task: TaskObject): androidx.fragment.app.Fragment {
    val questionObject = task["question"] as ArrayList<HashMap<String, *>>
    val answersObject = task["answers"] as ArrayList<HashMap<String, *>>

    val question = ArrayList<QuestionParcelable>()
    val answers = ArrayList<TextAnswerParcelable>()

    for (i in 0 until questionObject.count())
        question.add(QuestionParcelable(questionObject[i]))
    for (i in 0 until answersObject.count())
        answers.add(TextAnswerParcelable(answersObject[i]))

    return FragmentTestGraph2State.newInstance(question, answers)
}

private fun parseRS(task: TaskObject): androidx.fragment.app.Fragment {
    val questionObject = task["question"] as ArrayList<HashMap<String, *>>
    val answersObject = task["answers"] as ArrayList<HashMap<String, *>>

    val question = ArrayList<QuestionParcelable>()
    val answers = ArrayList<RSAnswerParcelable>()

    for (i in 0 until questionObject.count())
        question.add(QuestionParcelable(questionObject[i]))
    for (i in 0 until answersObject.count())
        answers.add(RSAnswerParcelable(answersObject[i]))

    return FragmentTestSign2Relation.newInstance(question, answers)
}

fun getFloatFromNumber(x: Any?): Float {
    return when (x) {
        is Float -> x
        is Double -> x.toFloat()
        is Int -> x.toFloat()
        null -> 0f
        else -> throw Exception("Cant get float from number $x")
    }
}

class FunctionParcelable(): Parcelable {
    var x: Float = 0f
    var v: Float = 0f
    var a: Float = 0f
    var len: Float = 0f
    var funcType: String = ""

    constructor(obj: HashMap<String, *>): this() {
        (obj["params"] as HashMap<String, *>).let {
            this.x = getFloatFromNumber(it["x"])
            this.v = getFloatFromNumber(it["v"])
            this.a = getFloatFromNumber(it["a"])

            this.len = getFloatFromNumber(it["len"])
        }
        this.funcType = obj["funcType"] as String
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            x = readFloat()
            v = readFloat()
            a = readFloat()
            len = readFloat()
            funcType = readString() ?: throw Exception("Parsing funcType is null")
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
            writeFloat(len)
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
    var id: Int = 0
    var correctIDs = ArrayList<Int>()
    var functions = ArrayList<FunctionParcelable>()

    constructor(obj: HashMap<String, *>): this() {
        if (obj.contains("id"))
            id = obj["id"] as Int
        if (obj.contains("correctIDs"))
            (obj["correctIDs"] as ArrayList<Int>).let {
                for (i in 0 until it.count())
                    correctIDs.add(it[i])
            }
        (obj["graph"] as ArrayList<HashMap<String, *>>).let {
            for (i in 0 until it.count())
                functions.add(FunctionParcelable(it[i]))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = this.readInt()
            val intArray = createIntArray()!!
            correctIDs = intArray.toCollection(ArrayList())
            functions = createTypedArrayList(FunctionParcelable.CREATOR)!!
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
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

    constructor(obj: HashMap<String, *>): this() {
        id = obj["id"] as Int
        (obj["graph"] as ArrayList<HashMap<String, *>>).let {
            for (i in 0 until it.count())
                functions.add(FunctionParcelable(it[i]))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            functions = createTypedArrayList(FunctionParcelable.CREATOR)!!
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

class RSAnswerParcelable(): Parcelable {
    var id = 0
    var letter = ""
    var leftSegment = IntArray(2)
    var rightSegment = IntArray(2)
    var correctSign = 0

    constructor(obj: HashMap<String, *>): this() {
        obj.let {
            id = it["id"] as Int
            letter = it["letter"] as String
            leftSegment = (it["leftSegment"] as ArrayList<Int>).toIntArray()
            rightSegment = (it["rightSegment"] as ArrayList<Int>).toIntArray()
            correctSign = it["correctSign"] as Int
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            letter = readString() ?: throw Exception("Parsing funcType is null")
            val intArrayLeft = createIntArray()!!
            leftSegment = intArrayLeft.toCollection(ArrayList()).toIntArray()
            val intArrayRight = createIntArray()!!
            rightSegment = intArrayRight.toCollection(ArrayList()).toIntArray()
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
            writeIntArray(leftSegment)
            writeIntArray(rightSegment)
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

    constructor(obj: HashMap<String, *>): this() {
        id = obj["id"] as Int
        text = obj["text"] as String
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            text = readString() ?: throw Exception("Parsing funcType is null")
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