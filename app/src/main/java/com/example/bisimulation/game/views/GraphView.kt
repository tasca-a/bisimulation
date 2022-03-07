package com.example.bisimulation.game.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.bisimulation.R
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Graph.Vertex
import com.example.bisimulation.model.Graph.Edge
import kotlin.math.*

class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var graph = Graph()

    // Attributes
    private var showExample: Boolean
    private var edgeSize: Float
    private var arrowSize: Float
    private var arrowAngle: Float
    private var loopWidth: Float  // This is inverted!
    private val lineWidth: Float
    var enableClick: Boolean

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GraphView,
            0, 0
        ).apply {
            try {
                showExample = getBoolean(R.styleable.GraphView_showExample, false)
                enableClick = getBoolean(R.styleable.GraphView_enableClick, true)
                edgeSize = getFloat(R.styleable.GraphView_edgeSize, 60f)
                arrowSize = getFloat(R.styleable.GraphView_arrowSize, 40f)
                arrowAngle = getFloat(R.styleable.GraphView_arrowAngle, (PI / 4).toFloat())
                loopWidth = getFloat(R.styleable.GraphView_loopWidth, 50f)
                lineWidth = getFloat(R.styleable.GraphView_lineWidth, 8f)
            } finally {
                recycle()
            }
        }

        // Show the example graph in the preview while editing
        if (isInEditMode) showExample = true

        if (showExample) {
            graph.apply {
                val e1 = Vertex(1, 2, 3, true)
                val e2 = Vertex(2, 1, 2)
                val e3 = Vertex(3, 3, 2)
                val e4 = Vertex(4, 1, 1)
                val e5 = Vertex(5, 3, 1)

                addEdge(Edge(e1, e2, Color.RED))
                addEdge(Edge(e1, e3, Color.RED))
                addEdge(Edge(e2, e1, Color.GREEN))
                addEdge(Edge(e3, e1, Color.GREEN))
                addEdge(Edge(e2, e4, Color.BLUE))
//                addEdge(Edge(e4, e2, Color.BLUE))
                addEdge(Edge(e3, e5))
                addEdge(Edge(e5, e4, Color.BLUE))
//                addEdge(Edge(e4, e5, Color.BLUE))
            }
        }
    }

    fun updateGraph(graph: Graph) {
        this.graph = graph
        scaleGraph(wd, ht)
        invalidate()
    }

    // Calculate here all dimensions and stuff
    private var wd: Int = 0
    private var ht: Int = 0
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Save dimensions for later use
        wd = w
        ht = h

        // Update various component
        scaleGraph(w, h)
    }

    private fun scaleGraph(w: Int, h: Int) {
        // Find max and min X and Y of the graph
        var maxX = 0f
        var maxY = 0f
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        for (edge in graph.vertices) {
            if (edge.x > maxX) maxX = edge.x.toFloat()
            if (edge.y > maxY) maxY = edge.y.toFloat()

            if (edge.x < minX) minX = edge.x.toFloat()
            if (edge.y < minY) minY = edge.y.toFloat()
        }

        // Add padding to the maxX and maxY
        maxX += minX
        maxY += minY

        // Scale all the edges to the current height and width
        for (edge in graph.vertices) {
            var newX = 0
            var newY = 0

            // Check if is NaN to avoid runtime crashes (for reasons idk about)
            if (!(w * (edge.x / maxX)).isNaN() && !(h * (edge.y / maxY)).isNaN()) {
                newX = (w * (edge.x / maxX)).roundToInt()
                newY = (h * (edge.y / maxY)).roundToInt()
            }

            scaleVertices(edge, newX, newY)

            edge.x = newX
            edge.y = newY
        }
    }

    private fun scaleVertices(vertex: Vertex, newX: Int, newY: Int) {
        for (v in graph.edges) {
            if (v.from.x == vertex.x && v.from.y == vertex.y) {
                v.from.x = newX
                v.from.y = newY
            }
            if (v.to.x == vertex.x && v.to.y == vertex.y) {
                v.to.x = newX
                v.to.y = newY
            }
        }
    }

    // Draw the graph
    private val graphicEdge = RectF()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the updated components
        canvas?.apply {

            // For each vertex, draw an arrow between the two associated edges
            graph.edges.forEachIndexed { vIndex, v ->

                arrowPaint.color = v.color

                // TODO: Manage loops!
                var loop = false
                graph.edges.forEachIndexed { xIndex, x ->

                    // If you find a loop, draw a curved line
                    if (x.from == v.to && x.to == v.from) {
                        loop = true

                        // Denominate the point for better comprehension
                        val fromX = v.from.x.toFloat()
                        val fromY = v.from.y.toFloat()
                        val toX = v.to.x.toFloat()
                        val toY = v.to.y.toFloat()

                        // Find the point in the middle
                        val midX = (fromX + toX) / 2f
                        val midY = (fromY + toY) / 2f

                        // Move a point on the perpendicular line
                        val newX: Float
                        val newY: Float

                        // Differentiate between the first and the second time the loop is encountered
                        if (xIndex < vIndex) {
                            // Differentiate between four cases
                            if (fromX == toX){
                                newX = midX + loopWidth
                                newY = midY
                            } else if (fromY == toY){
                                newX = midX
                                newY = midY + loopWidth
                            } else{
                                val m = (toY - fromY)/(toX - fromX)
                                val mPerp = -(1/m)

                                newX = (loopWidth / sqrt(1 + (mPerp * mPerp))) + midX
                                newY = (loopWidth * (mPerp / sqrt(1 + (mPerp * mPerp)))) + midY
                            }
                        } else {
                            // Differentiate between four cases
                            if (fromX == toX){
                                newX = midX - loopWidth
                                newY = midY
                            } else if (fromY == toY){
                                newX = midX
                                newY = midY - loopWidth
                            } else {
                                val m = (toY - fromY)/(toX - fromX)
                                val mPerp = -(1/m)

                                newX = ((- loopWidth) / sqrt(1 + (mPerp * mPerp))) + midX
                                newY = ((- loopWidth) * (mPerp / sqrt(1 + (mPerp * mPerp)))) + midY
                            }
                        }

                        // Draw 2 separate segments
                        drawLine(
                            v.from.x.toFloat(), v.from.y.toFloat(),
                            newX, newY,
                            arrowPaint
                        )
                        drawLine(
                            newX, newY,
                            v.to.x.toFloat(), v.to.y.toFloat(),
                            arrowPaint
                        )
                        drawArrow(newX.roundToInt(), newY.roundToInt(), v.to.x, v.to.y)
                    }
                }

                if (!loop) {
                    // Draw the line
                    drawLine(
                        v.from.x.toFloat(), v.from.y.toFloat(),
                        v.to.x.toFloat(), v.to.y.toFloat(),
                        arrowPaint
                    )
                    drawArrow(v.from.x, v.from.y, v.to.x, v.to.y)
                }
            }

            // For each edge, create the shape recycling an existing rectF and draw it as a circle
            // in the correct spot
            for (edge in graph.vertices) {
                graphicEdge.left = edge.x - edgeSize
                graphicEdge.top = edge.y - edgeSize
                graphicEdge.right = edge.x + edgeSize
                graphicEdge.bottom = edge.y + edgeSize

                if (edge.selected)
                    drawOval(graphicEdge, vertexSelectedPaint)
                else
                    drawOval(graphicEdge, vertexPaint)
            }
        }
    }

    private fun Canvas.drawArrow(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        // Draw the arrow
        // The norm is the length of the line from the start to the attach point
        val norm =
            sqrt(
                (toX - fromX).toFloat().pow(2) +
                        (toY - fromY).toFloat().pow(2)
            )

        // The attach point is the point of the arrow touching the border of the edge
        val dirX = (toX - fromX) / norm
        val dirY = (toY - fromY) / norm

        val attachX = toX - edgeSize * dirX
        val attachY = toY - edgeSize * dirY

        // l1 is the point of the first side of the arrow
        val l1x =
            attachX + (arrowSize / norm) * (((fromX - attachX) * cos(arrowAngle)) + ((fromY - attachY) * sin(
                arrowAngle
            )))
        val l1y =
            attachY + (arrowSize / norm) * (((fromY - attachY) * cos(arrowAngle)) - ((fromX - attachX) * sin(
                arrowAngle
            )))

        // l2 is the point of the second side of the arrow
        val l2x =
            attachX + (arrowSize / norm) * (((fromX - attachX) * cos(arrowAngle)) - ((fromY - attachY) * sin(
                arrowAngle
            )))
        val l2y =
            attachY + (arrowSize / norm) * (((fromY - attachY) * cos(arrowAngle)) + ((fromX - attachX) * sin(
                arrowAngle
            )))

        // Draw the arrow lines
        drawLine(l1x, l1y, attachX, attachY, arrowPaint)
        drawLine(l2x, l2y, attachX, attachY, arrowPaint)
    }

    // Define all the Paint object we need
    private val vertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        color = Color.BLACK
    }
    private val vertexSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = lineWidth
        color = Color.LTGRAY
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        color = Color.BLACK
    }

    // Manage touch
    private var eventListener: GraphEventListener? = null
    fun addGraphEventListener(graphEventListener: GraphEventListener) {
        eventListener = graphEventListener
    }

    // I have to suppress this because there is no easy way to detect where
    // the user is clicking without x and y coordinates, so performClick is useless
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        // If click is not enabled, exit immediately
        if (!enableClick) return false

        val x = event?.x ?: 0f
        val y = event?.y ?: 0f

        if (event?.action == MotionEvent.ACTION_DOWN) {
            // Detect where the user clicked and if is a button
            for (vertex in graph.vertices) {
                val isInside =
                    ((x - vertex.x) * (x - vertex.x)) + ((y - vertex.y) * (y - vertex.y)) < (edgeSize * edgeSize)

                if (isInside) {
                    // Notify the listener with the node selected
                    eventListener?.onVertexClicked(vertex)
                    invalidate()
                }
            }
        }

        return true
    }
}

interface GraphEventListener {
    fun onVertexClicked(vertex: Vertex)
}