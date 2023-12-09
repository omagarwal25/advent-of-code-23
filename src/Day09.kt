fun main() {
    fun parse(input: List<String>): List<List<List<Int>>> {
        val numbers = input.map { it.split(" ").map { i -> i.toInt() } }

        return numbers.map { line ->
            generateSequence(line) { row ->
                val windowed = row.windowed(2, 1, false)
                val diff = windowed.map { it[1] - it[0] }
                diff
            }.takeWhile { arr -> arr.any { it != 0 } }.toList()
        }
    }

    fun part1(input: List<String>): Int {
        return parse(input).sumOf { it.reversed().fold(0) { acc: Int, list -> list.last() + acc } }
    }

    fun part2(input: List<String>): Int {
        return parse(input).sumOf { it.reversed().fold(0) { acc: Int, list -> list.first() - acc } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
