package com.example.bisimulation.model

import android.graphics.Color
import android.util.Log

class Graph {
    val vertices: MutableList<Vertex> = mutableListOf()
    val edges: MutableList<Edge> = mutableListOf()

    fun addEdge(edge: Edge){
        // Check vertices and add them to the internal vertices list
        if (!vertices.contains(edge.from))
            vertices.add(edge.from)

        if (!vertices.contains(edge.to))
            vertices.add(edge.to)

        // Add the edge to the edges list
        edges.add(edge)
    }

    fun selectVertex(id: Int){
        // Deselect the currently selected vertex
        for (e in vertices){
            if (e.selected){
                e.selected = false
            }
        }

        // Find the vertex to select
        val internalEdge = vertices.find {
            it.id == id
        }

        // Select the vertex
        internalEdge?.selected = true
    }

    // Find all the paths from source to destination
    fun findAllPaths(source: Vertex, destination: Vertex): List<IntArray> {

        paths.clear()

        // Build an array with the adjacency list for the graph using vertices ids.
        // For each vertex, create it's own adjacency list
        val adj = Array(vertices.size){ vertexIndex ->
            val tmpEdgeList = mutableListOf<Int>()

            // Add the current vertex as the start of the adj list
            tmpEdgeList.add(vertices[vertexIndex].id)

            // For each edge, if it's starting vertex is the current vertex
            // add it to the adj list
            for (e in edges){
                if (e.from == vertices[vertexIndex])
                    tmpEdgeList.add(e.to.id)
            }

            // Create the complete list for the current vertex
            tmpEdgeList.toList()
        }

        val isVisited = Array(vertices.size){false}
        val pathList = ArrayList<Int>()

        // Add the source to the path
        pathList.add(source.id)

        // Call recursive utility
        findAllPathsUtil(source.id, destination.id, isVisited, pathList, adj)

        //Return the list of all the paths
        return paths.toList()
    }

    // Recursive function to find all paths
    private val paths = mutableListOf<IntArray>()
    private fun findAllPathsUtil(
        source: Int,
        destination: Int,
        isVisited: Array<Boolean>,
        localPathList: ArrayList<Int>,
        adj: Array<List<Int>>
    ){
        if (source == destination){
            Log.i("GRAPH", localPathList.toString())

            val copy = localPathList.toIntArray()

            paths.add(copy)
            return
        }

        val sourceIndex = vertices.indexOf(vertices.find { it.id == source })
        isVisited[sourceIndex] = true

        for (v in adj[sourceIndex]){
            if (!isVisited[adj.indexOfFirst { it[0] == v }]){
                localPathList.add(v)
                findAllPathsUtil(v, destination, isVisited, localPathList, adj)
                localPathList.remove(v)
            }
        }

        isVisited[sourceIndex] = false
    }

    class Vertex(val id: Int, var x: Int, var y: Int, var selected: Boolean = false){
        constructor() : this(0,0,0,false)

        override fun equals(other: Any?): Boolean {
            return (other is Vertex) && id == other.id && x == other.x && y == other.y
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + x
            result = 31 * result + y
            result = 31 * result + selected.hashCode()
            return result
        }
    }
    class Edge(val from: Vertex, val to: Vertex, val color: Int = Color.BLACK){
        constructor() : this(Vertex(), Vertex())

        override fun equals(other: Any?): Boolean {
            return (other is Edge) && from == other.from && to == other.to
        }

        override fun hashCode(): Int {
            var result = from.hashCode()
            result = 31 * result + to.hashCode()
            result = 31 * result + color
            return result
        }
    }
}