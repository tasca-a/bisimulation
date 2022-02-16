package com.example.bisimulation.game.views

import android.content.Context
import android.graphics.*
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
    private val graph = Graph().apply {
        val e1 = Edge(1, 1)
        val e2 = Edge(2, 2)
        val e3 = Edge(3, 3)
        val e4 = Edge(3, 1)

        val v1 = Vertex(e1, e2)
        val v2 = Vertex(e2, e3)
        val v3 = Vertex(e2, e4)
        val v4 = Vertex(e4, e1)

        addVertex(v1)
        addVertex(v2)
        addVertex(v3)
        addVertex(v4)
    }

    // Draw the graph
    private val EDGE_SIZE = 60f
    private val ARROW_SIZE = 60f
    private val ARROW_ANGLE = (PI / 4).toFloat()
    private val graphicEdge = RectF()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the updated components
        canvas?.apply {

            // For each edge, create the shape recycling an existing rectF and draw it as a circle
            // in the correct spot
            for (edge in graph.edges) {
                graphicEdge.left = edge.x - EDGE_SIZE
                graphicEdge.top = edge.y - EDGE_SIZE
                graphicEdge.right = edge.x + EDGE_SIZE
                graphicEdge.bottom = edge.y + EDGE_SIZE

                drawOval(graphicEdge, edgePaint)
            }

            // For each vertex, draw an arrow between the two associated edges
            for (vertex in graph.vertices) {
                // Draw the line
                drawLine(
                    vertex.from.x.toFloat(), vertex.from.y.toFloat(),
                    vertex.to.x.toFloat(), vertex.to.y.toFloat(),
                    arrowPaint
                )

                // Draw the arrow
                // The stick length is the length of the line from the start to the attach point
                val norm =
                    sqrt((vertex.to.x - vertex.from.x).toFloat().pow(2) +
                            (vertex.to.y - vertex.from.y).toFloat().pow(2))

                // The attach point is the point of the arrow touching the border of the edge
                val dirX = (vertex.to.x - vertex.from.x) / norm
                val dirY = (vertex.to.y - vertex.from.y) / norm

                val attachX = vertex.to.x - EDGE_SIZE * dirX
                val attachY = vertex.to.y - EDGE_SIZE * dirY

                // l1 is the point of the first side of the arrow
                val l1x = attachX + (ARROW_SIZE / norm) * (((vertex.from.x - attachX) * cos(ARROW_ANGLE)) + ((vertex.from.y - attachY) * sin(ARROW_ANGLE)))
                val l1y = attachY + (ARROW_SIZE / norm) * (((vertex.from.y - attachY) * cos(ARROW_ANGLE)) - ((vertex.from.x - attachX) * sin(ARROW_ANGLE)))

                // l2 is the point of the second side of the arrow
                val l2x = attachX + (ARROW_SIZE / norm) * (((vertex.from.x - attachX) * cos(ARROW_ANGLE)) - ((vertex.from.y - attachY) * sin(ARROW_ANGLE)))
                val l2y = attachY + (ARROW_SIZE / norm) * (((vertex.from.y - attachY) * cos(ARROW_ANGLE)) + ((vertex.from.x - attachX) * sin(ARROW_ANGLE)))

                // Draw the arrow lines
                drawLine(l1x, l1y, attachX, attachY, arrowPaint)
                drawLine(l2x, l2y, attachX, attachY, arrowPaint)
            }
        }
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