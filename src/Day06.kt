import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {

    fun part1(input: List<String>): Int {
        val times = input[0].split(":").last().trim().split(" ").mapNotNull { it.toIntOrNull() }
        val distances = input[1].split(":").last().trim().split(" ").mapNotNull { it.toIntOrNull() }

        val timesToDistances = times.zip(distances).toMap()

        return timesToDistances.map { (time, distance) ->
            val c = (0..time).count {
                val timeLeft = time - it
                val traveled = it * timeLeft


                traveled > distance
            }


            c
        }.fold(1) { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        val time = input[0].split(":").last().trim().replace(" ", "").toDouble()
        val distances = input[1].split(":").last().trim().replace(" ", "").toDouble()

        val solA = ceil(((-time + sqrt((time * time) - (4 * distances))) / (-2)))
        val solB = floor((-time - sqrt((time * time) - (4 * distances))) / (-2))

        return solB.toInt() - solA.toInt() + 1
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")

    timing { part1(input).println() }
    timing { part2(input).println() }
}
