import kotlin.math.max
import kotlin.math.min

private enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

private enum class DigStatus {
    DUG, LEVEL, EXPLORED
}

fun main() {
    fun part1(input: List<String>): Int {
        val instructions = input.map { it.split(" ") }.map {
            when (it[0]) {
                "U" -> Direction.UP to it[1].toInt()
                "D" -> Direction.DOWN to it[1].toInt()
                "L" -> Direction.LEFT to it[1].toInt()
                "R" -> Direction.RIGHT to it[1].toInt()
                else -> throw IllegalArgumentException("Unknown direction: ${it[0]}")
            }
        }

        // so the problem is that we don't know the size of the grid, so we need to find the max and min of each axis

        val maxLeft = instructions
            .filter { (direction, _) -> direction == Direction.LEFT || direction == Direction.RIGHT }
            .map { (dir, value) -> if (dir == Direction.LEFT) value else -value }
            .fold(listOf(0)) { acc, value -> acc + (acc.last() + value) }.max()

        val maxRight = instructions
            .filter { (direction, _) -> direction == Direction.LEFT || direction == Direction.RIGHT }
            .map { (dir, value) -> if (dir == Direction.RIGHT) value else -value }
            .fold(listOf(0)) { acc, value -> acc + (acc.last() + value) }.max()

        val maxUp = instructions
            .filter { (direction, _) -> direction == Direction.UP || direction == Direction.DOWN }
            .map { (dir, value) -> if (dir == Direction.UP) value else -value }
            .fold(listOf(0)) { acc, value -> acc + (acc.last() + value) }.max()

        val maxDown = instructions
            .filter { (direction, _) -> direction == Direction.DOWN || direction == Direction.UP }
            .map { (dir, value) -> if (dir == Direction.DOWN) value else -value }
            .fold(listOf(0)) { acc, value -> acc + (acc.last() + value) }.max()

        val start = Pair(maxUp, maxLeft)
        val initialGrid = List(maxUp + maxDown + 1) { List(maxLeft + maxRight + 1) { DigStatus.LEVEL } }.let {
            it.mapIndexed { y, line ->
                List(line.size) { x ->
                    if (x == start.second && y == start.first) {
                        DigStatus.DUG
                    } else {
                        DigStatus.LEVEL
                    }
                }
            }
        }

        val dug = instructions.fold(initialGrid to start) { acc, it ->
            val (dir, amnt) = it
            val (grid, loc) = acc

            val (y, x) = loc

            if (dir == Direction.UP || dir == Direction.DOWN) {
                val newY = if (dir == Direction.UP) y - amnt else y + amnt
                val modifierRange = min(y, newY)..max(y, newY)

                grid.mapIndexed { y1, line ->
                    if (y1 in modifierRange) {
                        line.mapIndexed { x, status ->
                            if (x == loc.second) {
                                DigStatus.DUG
                            } else {
                                status
                            }
                        }
                    } else {
                        line
                    }
                } to Pair(newY, x)
            } else {
                val newX = if (dir == Direction.LEFT) x - amnt else x + amnt
                val modifierRange = min(x, newX)..max(x, newX)

                grid.mapIndexed { y1, line ->
                    line.mapIndexed { x, status ->
                        if (x in modifierRange) {
                            if (y1 == loc.first) {
                                DigStatus.DUG
                            } else {
                                status
                            }
                        } else {
                            status
                        }
                    }
                } to Pair(y, newX)
            }
        }.first


        dug.forEach {
            it.forEach { s ->
                print(if (s == DigStatus.DUG) "X" else ".")
            }
            println()
        }

        val paddedDug = (listOf(List(dug[0].size + 2) { DigStatus.LEVEL }) + dug.map {
            listOf(DigStatus.LEVEL) + it + listOf(DigStatus.LEVEL)
        } + listOf(
            List(dug[0].size + 2) { DigStatus.LEVEL }))

        paddedDug.forEach {
            it.forEach { s ->
                print(if (s == DigStatus.DUG) "X" else if (s == DigStatus.EXPLORED) "b" else ".")
            }
            println()
        }

        val explored = generateSequence(paddedDug to listOf(0 to 0)) { prev ->
            val (grid, locList) = prev
            val first = locList.first()

            val (y, x) = first

            val markedGrid = grid.mapIndexed { y1, line ->
                line.mapIndexed { x1, status ->
                    if (x1 == x && y1 == y) {
                        DigStatus.EXPLORED
                    } else {
                        status
                    }
                }
            }

            val nextLocs = listOf(
                (y - 1) to x,
                (y + 1) to x,
                y to (x - 1),
                y to (x + 1)
            ).filter { (y, x) ->
                y in grid.indices && x in grid[y].indices
            }.filter {
                markedGrid[it.first][it.second] == DigStatus.LEVEL
            }.filter {
                locList.none { a -> a.first == it.first && a.second == it.second }
            }

            markedGrid to (locList.drop(1) + nextLocs)
        }.takeWhileInclusive {
            it.second.isNotEmpty()
        }.last().first

        explored.forEach {
            it.forEach { s ->
                print(if (s == DigStatus.DUG) "X" else if (s == DigStatus.EXPLORED) "b" else ".")
            }
            println()
        }



        return explored.sumOf {
            it.count { s -> s != DigStatus.EXPLORED }
        }
    }

    fun part2(input: List<String>): Int {
        val start = Pair(0, 0)

        val instructions = input.map { it.split(" ") }.map { it[2] }
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
//    check(part2(testInput) == 145)

    val input = readInput("Day18")
    timing { part1(input).println() }
//    timing { part2(input).println() }
}
