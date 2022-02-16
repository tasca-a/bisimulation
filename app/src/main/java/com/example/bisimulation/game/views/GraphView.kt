package com.example.bisimulation.game.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.bisimulation.R
import com.example.bisimulation.model.Graph
import kotlin.math.*

class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var showExample: Boolean = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GraphView,
            0, 0
        ).apply {
            try {
                showExample = getBoolean(R.styleable.GraphView_showExample, false)
            } finally {
                recycle()
            }
        }
    }

    // Attribute management
    fun isShowExample(): Boolean = showExample
    fun setShowExample(show: Boolean) {
        showExample = show
        invalidate()
        requestLayout()
    }


    private var graph = Graph()
    fun setGraph(graph: Graph) {
        this.graph = graph
        invalidate()
    }

    // Calculate here all dimensions and stuff
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Update various component

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
            edge.x = (w * (edge.x / maxX)).roundToInt()
            edge.y = (h * (edge.y / maxY)).roundToInt()
        }
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

    // Draw the graph
    private val EDGE_SIZE = 60f
    private val ARROW_SIZE = 40f
    private val ARROW_ANGLE = (PI / 4).toFloat()
    private val LOOP_WIDTH = 50f   // This is inverted!
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
                                newPointX += LOOP_WIDTH
                            } else {
                                newPointX -= LOOP_WIDTH
                            }
                        } else {
                            newPointX = v.from.x.toFloat()
                            newPointY = (2 * midPointY) - v.from.y + (abs(v.to.y - v.from.y) / 2f)

                            if (newPointX < v.to.x) {
                                newPointX += LOOP_WIDTH
                            } else {
                                newPointX -= LOOP_WIDTH
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
                graphicEdge.left = edge.x - EDGE_SIZE
                graphicEdge.top = edge.y - EDGE_SIZE
                graphicEdge.right = edge.x + EDGE_SIZE
                graphicEdge.bottom = edge.y + EDGE_SIZE

                drawOval(graphicEdge, edgePaint)
            }
        }
    }

    //private fun Canvas.drawArrow(v: Graph.Vertex) {
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

        val attachX = toX - EDGE_SIZE * dirX
        val attachY = toY - EDGE_SIZE * dirY

        // l1 is the point of the first side of the arrow
        val l1x =
            attachX + (ARROW_SIZE / norm) * (((fromX - attachX) * cos(ARROW_ANGLE)) + ((fromY - attachY) * sin(
                ARROW_ANGLE
            )))
        val l1y =
            attachY + (ARROW_SIZE / norm) * (((fromY - attachY) * cos(ARROW_ANGLE)) - ((fromX - attachX) * sin(
                ARROW_ANGLE
            )))

        // l2 is the point of the second side of the arrow
        val l2x =
            attachX + (ARROW_SIZE / norm) * (((fromX - attachX) * cos(ARROW_ANGLE)) - ((fromY - attachY) * sin(
                ARROW_ANGLE
            )))
        val l2y =
            attachY + (ARROW_SIZE / norm) * (((fromY - attachY) * cos(ARROW_ANGLE)) + ((fromX - attachX) * sin(
                ARROW_ANGLE
            )))

        // Draw the arrow lines
        drawLine(l1x, l1y, attachX, attachY, arrowPaint)
        drawLine(l2x, l2y, attachX, attachY, arrowPaint)
    }

    // Define all the Paint object we need
    private val STROKE_WIDTH = 8f
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = STROKE_WIDTH
        color = Color.BLACK
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
        color = Color.BLACK
    }
}