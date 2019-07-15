package com.example.physmin.views.layouts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.activities.FunctionAnswerRelationSignParcelable
import com.example.physmin.activities.FunctionParcelable
import com.example.physmin.views.*
import com.example.physmin.views.items.ImageViewSettable
import com.example.physmin.views.items.ImageViewSettableBlank
import com.example.physmin.views.items.RelationSignView
import kotlin.math.max

const val ONE_COLUMN = 1
const val TWO_COLUMNS = 2
const val ONE_TWO_COLUMNS = 3

class GroupSettable(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var parentTestConstraintLayout: TestConstraintLayout

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)

    var horizontalSpacing = 0
    var verticalSpacing = 0
    var layoutType: Int = TWO_COLUMNS
    var blurRadius = 2.dpToPx()
    var cornerRadius = 2.dpToPx()
    var backPanelBitmap: Bitmap? = null

    private var _deviceWidth: Int = 0

    init {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.GroupSettable, 0, 0)
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingHorizontal, 0)
        verticalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingVertical, 0)
        layoutType = when (val type = a.getString(R.styleable.GroupSettable_gs_layoutType)) {
            "one_column" -> ONE_COLUMN
            "two_columns" -> TWO_COLUMNS
            "one_two_columns" -> ONE_TWO_COLUMNS
            else -> {
                Log.e("GroupSettable", "cant parse layout type: $type")
                ONE_COLUMN
            }
        }

        a.recycle()

        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()
        display.getSize(displaySize)
        _deviceWidth = displaySize.x

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))

        this.post {
            backPanelBitmap = generateShadowPanel(width + 10, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this)
        }

        setOnHierarchyChangeListener(this)
    }

    fun addRelationSignView(answer: FunctionAnswerRelationSignParcelable) {
        val relationSignView = RelationSignView(this.context!!, null, answer.letter,
                answer.leftIndex, answer.rightIndex)
        relationSignView.correctAnswers = answer.correctSign
        this.addView(relationSignView)
    }

    fun addQuestionGraphic(functions: ArrayList<FunctionParcelable>) {
        val graphView = GraphView(context, null)
        graphView.functions = functions
        graphView.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(graphView)
    }

    fun addViewSettable(correctIds: IntArray, functions: ArrayList<FunctionParcelable>) {
        val questView = ImageViewSettable(this.context, null).apply {
            correctAnswers = correctIds
            layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())
            graph.functions = functions
        }
        this.addView(questView)
    }

    fun addQuestionBlankView(correctAnswers: IntArray) {
        val imageViewSettableBlank = ImageViewSettableBlank(context, null)
        imageViewSettableBlank.correctAnswers = correctAnswers
        imageViewSettableBlank.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(imageViewSettableBlank)
    }

    fun setParent(parent: TestConstraintLayout) {
        this.parentTestConstraintLayout = parent
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, _width: Int, p4: Int) {
        if (this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop: Int

        var maxWidth = this.getChildAt(0).measuredWidth
        var maxHeight = this.getChildAt(0).measuredHeight

        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom

        var childInRow = (contentRight - contentLeft) / maxWidth
        var childSpacing = ((contentRight - contentLeft) - childInRow * maxWidth) / (childInRow + 1)
        var curChildInRow = 1

        curLeft = _width / 2
        curTop = contentTop
        var maxHeightInRow = 0

        var child: View
        for (i in 0 until count) {
            child = getChildAt(i)

            if ((count % 2 != 0 && i == 0 && layoutType == ONE_TWO_COLUMNS) || layoutType == ONE_COLUMN) {
                curLeft -= child.measuredWidth / 2
                child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + child.measuredHeight)
                curLeft += child.measuredWidth / 2
                curTop += child.measuredHeight + horizontalSpacing
                continue
            }

            if (curChildInRow % 2 == 1)
                curLeft -= (child.measuredWidth + verticalSpacing / 2)
            else
                curLeft += verticalSpacing / 2

            maxHeightInRow = Math.max(maxHeightInRow, child.measuredHeight)
            child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + maxHeightInRow)
            curLeft = _width / 2
            if (curChildInRow % 2 == 0) {
                curTop += maxHeightInRow + horizontalSpacing
                maxHeightInRow = 0
            }

            curChildInRow++
        }
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        when (child) {
            is Settable -> {
                child.setParent(this)
                if (child !is RelationSignView)
                    child.setOnClickListener(this)
            }
        }
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childCount = childCount
        val width = _deviceWidth
        var height = 0
        var maxHeightRow = 0
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec)

            maxHeightRow = max(childAt.measuredHeight, maxHeightRow)
            when (layoutType) {
                ONE_COLUMN -> height += childAt.measuredHeight + horizontalSpacing
                TWO_COLUMNS -> {
                    if (i % 2 == 0) {
                        height += maxHeightRow + horizontalSpacing
                        maxHeightRow = 0
                    }
                }
                ONE_TWO_COLUMNS -> {
                    if ((i % 2 == 0 && i != 2) || i == 1) {
                        height += maxHeightRow + horizontalSpacing
                        maxHeightRow = 0
                    }
                }
            }
        }
        height += paddingTop + paddingBottom - horizontalSpacing // remove spacing from last element

        height = View.resolveSizeAndState(height, heightMeasureSpec, 0)

        setMeasuredDimension(width, height)
    }

    override fun onClick(clickedChild: View?) {
        if (clickedChild !is Settable) return

        clickedChild.answerView = parentTestConstraintLayout.takePickedItem()
    }

    fun isAllChecked(): Boolean {
        var child: View
        for (i in 0 until this.childCount) {
            child = getChildAt(i)
            if (child is Settable)
                if (child.answerView == null)
                    return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backPanelBitmap?.let {
            canvas.drawBitmap(it, -5f, 0f, null)
        }
    }

}