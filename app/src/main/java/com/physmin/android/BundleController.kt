package com.physmin.android

import com.physmin.android.fragments.tasks.FragmentTaskBase
import com.physmin.android.fragments.tasks.TaskController
import com.physmin.android.views.TimerView

class BundleController(levelBundle: HashMap<String, *>, isExam: Boolean, timerView: TimerView) {
    private val bundleID = levelBundle["bundleId"]
    private val isExam = isExam
    private val timerView = timerView
    private val tasksList = TasksList(levelBundle["tasks"] as ArrayList<TaskObject>)


    private var taskController: TaskController? = null
    private var listener: BundleControllerListener? = null
    private val userAnswers = HashMap<String, HashMap<String, out Any>>() // id: {type, tries}

    fun getTasksCount() = tasksList.count
    fun setListener(receiver: BundleControllerListener) {
        listener = receiver
    }

    private fun checkCurrentTask() {
        val taskController = taskController ?: return
        val taskId = taskController.taskId.toString()

        // this will create stat if task appear first time, or use old val if task was previously pushed to end of taskList
        if (isExam)
            userAnswers[taskId] = userAnswers[taskId]
                    ?: (hashMapOf(
                            "taskType" to taskController.taskType,
                            "times" to ArrayList<Int>(),
                            "answers" to HashMap<String, String>()
                    ))
        else
            userAnswers[taskId] = userAnswers[taskId]
                    ?: (hashMapOf(
                            "taskType" to taskController.taskType,
                            "tries" to 1,
                            "times" to ArrayList<Int>()
                    ))

        val isAnswerCorrect = (taskController.isTaskCompleted && taskController.isAnswersCorrect())
                || isDev() || isExam

        if (!isAnswerCorrect) {
            tasksList.pushCurrentToBack()
            (userAnswers[taskId]!!["tries"] as Int).inc() // TODO: FIX: it may not work..
        }
        (userAnswers[taskId]!!["times"] as ArrayList<Int>).add(60 - timerView.getTime())
        if (isExam) {
            val hashMap = (userAnswers[taskId]!!["answers"] as HashMap<String, String>)
            for ((_key, _value) in taskController.getAnswers()) {
                hashMap[_key.toString()] = _value.toString()
            }
        }
        listener?.onTaskChecked(isAnswerCorrect)
    }

    fun getNextTask(taskObj: TaskObject? = null): FragmentTaskBase? {
        checkCurrentTask()

        if (tasksList.isEnd()) {
            listener?.onBundleComplete()
            return null
        } else {
            val fragment = parseTask(taskObj
                    ?: tasksList.pop())
            taskController = fragment

            return fragment
        }
    }

    fun getStats(): HashMap<String, Any> {
        return hashMapOf(
                "bundleId" to (bundleID ?: ""),
                "isExam" to isExam,
                "payload" to userAnswers
        )
    }

    interface BundleControllerListener {

        /**
         * Return true if task correct OR if bundle is Exam
         **/
        fun onTaskChecked(isAnswerCorrect: Boolean)

        fun onBundleComplete()
    }
}