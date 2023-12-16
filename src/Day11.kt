import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val unmodified = input.map { it.toList().map { i -> i.toString() } }

        val gridExpandedVertically = unmodified.flatMap {
            if (it.all { f -> f == "." }) {
                listOf(List(it.size) { "." }, List(it.size) { "." })
            } else {
                listOf(it)
            }
        }

        gridExpandedVertically.println()

        val rotateGrid = List(gridExpandedVertically.first().size) { idx -> gridExpandedVertically.map { it[idx] } }

        val grid = rotateGrid.flatMap {
            if (it.all { f -> f == "." }) {
                listOf(List(it.size) { "." }, List(it.size) { "." })
            } else {
                listOf(it)
            }
        }

        val hashes = grid.mapIndexed { y, line -> line.mapIndexed { x, pipe -> Pair(x, y) to pipe } }.flatten()
            .filter { it.second == "#" }.map { it.first }.toList()

        return hashes.sumOf { original ->
            hashes.sumOf { second ->
                val (x1, y1) = original
                val (x2, y2) = second

                abs(x1 - x2) + abs(y1 - y2)
            }
        } / 2
    }

    fun part2(input: List<String>): Long {
        val grid = input.map { it.toList().map { i -> i.toString() } }

        val horzSkips =
            grid.mapIndexed { y, line -> y to line }.filter { it.second.all { f -> f == "." } }.map { it.first }
        val vertSkips = List(grid.first().size) { idx -> grid.map { it[idx] } }.mapIndexed { x, line -> x to line }
            .filter { it.second.all { f -> f == "." } }.map { it.first }

        println(horzSkips)
        println(vertSkips)

        val hashes = grid.mapIndexed { y, line -> line.mapIndexed { x, pipe -> Pair(x, y) to pipe } }.flatten()
            .filter { it.second == "#" }.map { it.first }.toList()

        return hashes.sumOf {
            println("-----")
            println("${hashes.size} boop")
            hashes.sumOf { second ->
                val (x1, y1) = it
                val (x2, y2) = second

                println("x1: $x1, y1: $y1, x2: $x2, y2: $y2")

                val withHorizSkips = horzSkips.count { it in min(y1, y2)..max(y1, y2) } * (1000000L - 1)
                val withVertSkips = vertSkips.count { it in min(x1, x2)..max(x1, x2) } * (1000000L - 1)

                (abs(x1.toLong() - x2) + abs(y1.toLong() - y2) + withVertSkips + withHorizSkips).apply { println(this) }
            }.apply { println("$this sum") }

        } / 2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)
//    check(part2(testInput) == 1030L)

    val input = readInput("Day11")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
