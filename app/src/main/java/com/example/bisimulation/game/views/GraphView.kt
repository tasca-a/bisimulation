package com.example.bisimulation.game.views

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
import com.example.bisimulation.model.Graph.Edge
import com.example.bisimulation.model.Graph.Vertex
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

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GraphView,
            0, 0
        ).apply {
            try {
                showExample = getBoolean(R.styleable.GraphView_showExample, false)
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
                val e1 = Edge(1, 2, 3, true)
                val e2 = Edge(2, 1, 2)
                val e3 = Edge(3, 3, 2)
                val e4 = Edge(4, 1, 1)
                val e5 = Edge(5, 3, 1)

                addVertex(Vertex(e1, e2, Color.RED))
                addVertex(Vertex(e1, e3, Color.RED))
                addVertex(Vertex(e2, e1, Color.GREEN))
                addVertex(Vertex(e3, e1, Color.GREEN))
                addVertex(Vertex(e2, e4, Color.BLUE))
                addVertex(Vertex(e3, e5))
                addVertex(Vertex(e5, e4, Color.BLUE))
            }
        }
    }

    fun setGraph(graph: Graph) {
        this.graph = graph
        invalidate()
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
        for (edge in graph.edges) {
            if (edge.x > maxX) maxX = edge.x.toFloat()
            if (edge.y > maxY) maxY = edge.y.toFloat()

            if (edge.x < minX) minX = edge.x.toFloat()
            if (edge.y < minY) minY = edge.y.toFloat()
        }

        // Add padding to the maxX and maxY
        maxX += minX
        maxY += minY

        // Scale all the edges to the current height and width
        for (edge in graph.edges) {
            val newX = (w * (edge.x / maxX)).roundToInt()
            val newY = (h * (edge.y / maxY)).roundToInt()

            scaleVertices(edge, newX, newY)

            edge.x = newX
            edge.y = newY
        }
    }

    fun scaleVertices(edge: Edge, newX: Int, newY: Int) {
        for (v in graph.vertices) {
            if (v.from.x == edge.x && v.from.y == edge.y) {
                v.from.x = newX
                v.from.y = newY
            }
            if (v.to.x == edge.x && v.to.y == edge.y) {
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
            graph.vertices.forEachIndexed { vIndex, v ->

                arrowPaint.color = v.color

                // TODO: gestisci cappi
                // TODO: gestisci doppio disegno
                var loop = false
                graph.vertices.forEachIndexed { xIndex, x ->

                    // If you find a loop, draw a curved line
                    if (x.from == v.to && x.to == v.from) {
                        loop = true
                        // Find the point in the middle
                        val midPointX = (v.from.x + v.to.x) / 2f
                        val midPointY = (v.from.y + v.to.y) / 2f

                        // Move a point on the perpendicular line
                        // midpoint in the center of the arc!
                        var newPointX: Float
                        val newPointY: Float
                        if (xIndex < vIndex) {
                            newPointX = (2 * midPointX) - v.to.x
                            newPointY = v.to.y.toFloat() - (abs(v.to.y - v.from.y) / 2f)

                            if (newPointX < v.to.x) {
                                newPointX += loopWidth
                            } else {
                                newPointX -= loopWidth
                            }
                        } else {
                            newPointX = v.from.x.toFloat()
                            newPointY = (2 * midPointY) - v.from.y + (abs(v.to.y - v.from.y) / 2f)

                            if (newPointX < v.to.x) {
                                newPointX += loopWidth
                            } else {
                                newPointX -= loopWidth
                            }
                        }

                        // Draw 2 separate segments
                        drawLine(
                            v.from.x.toFloat(), v.from.y.toFloat(),
                            newPointX, newPointY,
                            arrowPaint
                        )
                        drawLine(
                            newPointX, newPointY,
                            v.to.x.toFloat(), v.to.y.toFloat(),
                            arrowPaint
                        )
                        drawArrow(newPointX.roundToInt(), newPointY.roundToInt(), v.to.x, v.to.y)
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
            for (edge in graph.edges) {
                graphicEdge.left = edge.x - edgeSize
                graphicEdge.top = edge.y - edgeSize
                graphicEdge.right = edge.x + edgeSize
                graphicEdge.bottom = edge.y + edgeSize

                if (edge.selected)
                    drawOval(graphicEdge, edgeSelectedPaint)
                else
                    drawOval(graphicEdge, edgePaint)
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
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        color = Color.BLACK
    }
    private val edgeSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f

        if (event?.action == MotionEvent.ACTION_DOWN) {
            // Detect where the user clicked and if is a button
            for (e in graph.edges) {
                val isInside =
                    ((x - e.x) * (x - e.x)) + ((y - e.y) * (y - e.y)) < (edgeSize * edgeSize)

                if (isInside) {
                    // Notify the listener with the node selected
                    eventListener?.onNodeClicked(e.id)
                    invalidate()
                }
            }
        }

        return false
    }
}

interface GraphEventListener {
    fun onNodeClicked(nodeId: Int)
}

// TEST GRAPH
//    private val graph = Graph().apply {
//
//        val e1 = Edge(2, 3)
//        val e2 = Edge(1, 2)
//        val e3 = Edge(3, 2)
//        val e4 = Edge(1, 1)
//        val e5 = Edge(3, 1)
//
//        addVertex(Vertex(e1, e2))
//        addVertex(Vertex(e1, e3))
//        addVertex(Vertex(e2, e1))
//        addVertex(Vertex(e3, e1))
//        addVertex(Vertex(e2, e4))
//        addVertex(Vertex(e3, e5))
//        addVertex(Vertex(e5, e4))
//
////        val rEdgeCount = Random.nextInt(3, 6)
////        val edges = arrayListOf<Graph.Edge>()
////        for (i in 1..rEdgeCount){
////            edges.add(Edge(
////                Random.nextInt(1, rEdgeCount),
////                Random.nextInt(1, rEdgeCount)
////            ))
////        }
////
////        // (rEdgeCount * (rEdgeCount - 1))
////        val rVertexCount = Random.nextInt(3, (rEdgeCount * (rEdgeCount - 1)))
////        for (i in 1..rVertexCount){
////            val isVertex = Random.nextBoolean()
////            if (isVertex){
////                addVertex(
////                    Vertex(
////                        edges[Random.nextInt(1, rEdgeCount - 1)],
////                        edges[Random.nextInt(1, rEdgeCount - 1)]
////                    )
////                )
////            }
////        }
//
//        // Rearrange edges in a grid
////        val size = edges.size
////        val sideSize = sqrt(size.toFloat()).roundToInt()
////
////        var column = 0
////        var row = 0
////
////        for (e in edges){
////            if (column < sideSize){
////                e.x = column + 1
////                e.y = row + 1
////                column++
////            } else {
////                row++
////                column = 0
////                e.x = column + 1
////                e.y = row + 1
////                column++
////            }
////        }
//    }