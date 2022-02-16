package com.example.bisimulation.model

import android.graphics.Color

class Graph() {
    val edges: MutableList<Edge> = mutableListOf()
    val vertices: MutableList<Vertex> = mutableListOf()

    fun addVertex(vertex: Vertex){
        // Check edges and add them to the internal edges list
        if (!edges.contains(vertex.from))
            edges.add(vertex.from)

        if (!edges.contains(vertex.to))
            edges.add(vertex.to)

        // Add the vertex to the vertices list
        vertices.add(vertex)
    }

    inner class Edge(var x: Int, var y: Int)
    inner class Vertex(val from: Edge, val to: Edge, val color: Int = Color.BLACK)
}