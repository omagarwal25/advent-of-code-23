private enum class Tile {
    FIXED, MOVING, EMPTY
}

private fun List<List<Tile>>.rolled(): List<List<Tile>> {
    return this.map { col ->
        col.fold(listOf(listOf<Tile>())) { acc, tile ->
            if (acc.first().isEmpty()) listOf(listOf(tile))
            else if ((acc.last().last() == Tile.FIXED && tile == Tile.FIXED) || (acc.last()
                    .last() != Tile.FIXED && tile != Tile.FIXED)
            ) {
                acc.dropLast(1) + listOf(acc.last() + tile)
            } else {
                acc + listOf(listOf(tile))
            }
        }.map {
            it.sorted()
        }.flatten()
    }
}

private fun List<List<Tile>>.print() {
    this.forEach { row ->
        row.forEach { tile ->
            when (tile) {
                Tile.EMPTY -> print(".")
                Tile.FIXED -> print("#")
                Tile.MOVING -> print("O")
            }
        }
        print("\n")
    }
}

private fun List<List<Tile>>.rotate(): List<List<Tile>> {
    return List(this.first().size) { idx -> this.map { it[idx] } }.map {
        it.reversed()
    }
}


//private fun List<List<Tile>>.rotate

fun main() {
    fun processInput(input: List<String>) = input.map {
        it.map { c ->
            when (c) {
                '.' -> Tile.EMPTY
                '#' -> Tile.FIXED
                else -> Tile.MOVING
            }
        }
    }


    fun part1(input: List<String>): Int {
        val raw = processInput(input)
        // just rotate it so that columns
        val grid = List(raw.first().size) { idx -> raw.map { it[idx] } }
        val rolled = grid.rolled()

        return List(rolled.first().size) { idx -> rolled.map { it[idx] } }.mapIndexed { idx, row ->
            val amnt = row.count { it == Tile.MOVING }

            (grid[0].size - idx) * amnt
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val raw = processInput(input)

        val initial = raw.rotate().rotate().rotate()

        val iterations = generateSequence(listOf(initial)) {
            val new = ((0 until 4).fold(it.last()) { acc, _ ->
                val rolled = acc.rolled()
                rolled.rotate()
            })

            it + listOf(new)
        }.takeWhileInclusive {
            it.dropLast(1).none { other -> other == it.last() }
        }.last()

        val last = iterations.last()
        val previousLast = iterations.indexOfFirst { it == last }

        val diff = iterations.size - previousLast - 1
        val remaining = 1_000_000_000 - previousLast
        val idx = remaining % diff

        return iterations[previousLast + idx].rotate().apply { this.print() }.mapIndexed { y, row ->
            val amnt = row.count { it == Tile.MOVING }

            (initial[0].size - y) * amnt
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
