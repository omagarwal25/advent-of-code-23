private enum class CardinalDirection {
    NORTH, EAST, SOUTH, WEST;

    fun transform(x: Int, y: Int) = when (this) {
        NORTH -> Pair(x, y - 1)
        EAST -> Pair(x + 1, y)
        SOUTH -> Pair(x, y + 1)
        WEST -> Pair(x - 1, y)
    }

    fun opposite() = when (this) {
        NORTH -> SOUTH
        EAST -> WEST
        SOUTH -> NORTH
        WEST -> EAST
    }

}

private enum class Pipe {
    HORIZONTAL, VERTICAL, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST, START, NONE;

    companion object {
        fun fromChar(c: Char): Pipe {
            return when (c) {
                '-' -> HORIZONTAL
                '|' -> VERTICAL
                'J' -> NORTH_WEST
                'L' -> NORTH_EAST
                '7' -> SOUTH_WEST
                'F' -> SOUTH_EAST
                'S' -> START
                '.' -> NONE
                else -> throw IllegalArgumentException("Unknown pipe: $c")
            }
        }
    }

    fun getDirections(): List<CardinalDirection> {
        return when (this) {
            HORIZONTAL -> listOf(CardinalDirection.EAST, CardinalDirection.WEST)
            VERTICAL -> listOf(CardinalDirection.NORTH, CardinalDirection.SOUTH)
            NORTH_WEST -> listOf(CardinalDirection.NORTH, CardinalDirection.WEST)
            NORTH_EAST -> listOf(CardinalDirection.NORTH, CardinalDirection.EAST)
            SOUTH_WEST -> listOf(CardinalDirection.SOUTH, CardinalDirection.WEST)
            SOUTH_EAST -> listOf(CardinalDirection.SOUTH, CardinalDirection.EAST)
            else -> emptyList()
        }
    }
}


fun main() {
    fun part1(input: List<String>): Int {
        val regGrid = input.map { line -> line.map { Pipe.fromChar(it) } }
        // pad grid with empty pipes
        val grid =
            listOf(List(regGrid[0].size + 2) { Pipe.NONE }) + regGrid.map { listOf(Pipe.NONE) + it + listOf(Pipe.NONE) } + listOf(
                List(regGrid[0].size + 2) { Pipe.NONE })

        grid.map { println(it) }

        val startLoc = grid.asSequence().mapIndexed { y, line -> line.mapIndexed { x, pipe -> Pair(x, y) to pipe } }
            .flatten()
            .filter { it.second == Pipe.START }
            .map { it.first }
            .first()

        val firstStep = listOf(
            CardinalDirection.NORTH,
            CardinalDirection.EAST,
            CardinalDirection.SOUTH,
            CardinalDirection.WEST
        ).map { it to it.transform(startLoc.first, startLoc.second) }.filter {
            grid[it.second.second][it.second.first] != Pipe.NONE
        }.first { (dir, loc) ->
            // we need to check if it has an opening on the opposite of the direction
            (grid[loc.second][loc.first].getDirections().any { it == dir.opposite() })
        }

        return generateSequence(firstStep) { (previousDir, coords) ->
            val (x, y) = coords
            val pipe = grid[y][x]
            val directions = pipe.getDirections().filter { it != previousDir.opposite() }

            println("Previous: $previousDir, coords: $coords, pipe: $pipe, directions: $directions")

            val nextDir = directions.first()
            val nextCoords = nextDir.transform(x, y)

            nextDir to nextCoords
        }.takeWhileInclusive {
            val (x, y) = it.second

            grid[y][x] != Pipe.START
        }.count().let { it / 2 }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
//    check(part2(testInput) == 2)

    val input = readInput("Day10")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
