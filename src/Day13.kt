fun main() {
    fun part1(input: List<String>): Int {
        val blanks = input.withIndex().filter { it.value == "" }.map { it.index }

        return (listOf(-1) + blanks + (input.size - 1)).windowed(2).map {
            input.subList(it[0] + 1, it[1])
        }.sumOf { grid ->
            val pattern = grid.withIndex().windowed(2).filter { it[0].value == it[1].value }.map {
                it[0].index to (grid.subList(0, it[1].index) to grid.subList(it[1].index, grid.size))
            }.firstOrNull { (_, it) ->
                it.first.reversed().zip(it.second).all { (a, b) -> a == b }
            }?.first

            if (pattern != null) {
                100 * (pattern + 1)
            } else {
                grid.map {
                    it.println()
                }
                val swapped = List(grid[0].length) { b -> grid.map { it[b] } }.map { it.joinToString("") }

                val vertPattern = swapped.withIndex().windowed(2).filter { it[0].value == it[1].value }.map {
                    it[0].index to (swapped.subList(0, it[1].index) to swapped.subList(it[1].index, swapped.size))
                }.firstOrNull { (_, it) ->
                    it.first.reversed().zip(it.second).all { (a, b) -> a == b }
                }?.first

                if (vertPattern != null) {
                    (vertPattern + 1)
                } else {
                    0
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
//    check(part2(testInput) == 2)

    val input = readInput("Day13")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
