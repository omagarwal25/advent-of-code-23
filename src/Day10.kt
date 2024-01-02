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

    companion object {
        fun toPipe(dirs: Pair<CardinalDirection, CardinalDirection>): Pipe {
            val (dir1, dir2) = dirs
            return when (dir1) {
                NORTH -> when (dir2) {
                    EAST -> Pipe.NORTH_EAST
                    WEST -> Pipe.NORTH_WEST
                    SOUTH -> Pipe.VERTICAL
                    else -> throw IllegalArgumentException("Invalid direction combination: $dir1, $dir2")
                }

                SOUTH -> when (dir2) {
                    EAST -> Pipe.SOUTH_EAST
                    WEST -> Pipe.SOUTH_WEST
                    NORTH -> Pipe.VERTICAL
                    else -> throw IllegalArgumentException("Invalid direction combination: $dir1, $dir2")
                }

                EAST -> when (dir2) {
                    NORTH -> Pipe.NORTH_EAST
                    SOUTH -> Pipe.SOUTH_EAST
                    WEST -> Pipe.HORIZONTAL
                    else -> throw IllegalArgumentException("Invalid direction combination: $dir1, $dir2")
                }

                WEST -> when (dir2) {
                    NORTH -> Pipe.NORTH_WEST
                    SOUTH -> Pipe.SOUTH_WEST
                    EAST -> Pipe.HORIZONTAL
                    else -> throw IllegalArgumentException("Invalid direction combination: $dir1, $dir2")
                }
            }
        }
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

    fun getExpanded(): List<List<BoolPipe>> {
        return when (this) {
            HORIZONTAL -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Pipe, BoolPipe.Pipe, BoolPipe.Pipe),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty)
            )

            VERTICAL -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty)
            )

            NORTH_WEST -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Pipe, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty)
            )

            NORTH_EAST -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Pipe),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty)
            )

            SOUTH_WEST -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Pipe, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty)
            )

            SOUTH_EAST -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Pipe),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty)
            )

            NONE -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty)
            )

            START -> listOf(
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Pipe, BoolPipe.Empty),
                listOf(BoolPipe.Empty, BoolPipe.Empty, BoolPipe.Empty)
            )
        }
    }
}

private enum class BoolPipe {
    Pipe, Empty, Outer
}


fun main() {
    fun generateGrid(input: List<String>): List<List<Pipe>> {
        val regGrid = input.map { line -> line.map { Pipe.fromChar(it) } }
        // pad grid with empty pipes
        return listOf(List(regGrid[0].size + 2) { Pipe.NONE }) + regGrid.map { listOf(Pipe.NONE) + it + listOf(Pipe.NONE) } + listOf(
            List(regGrid[0].size + 2) { Pipe.NONE })
    }

    fun getStartLoc(grid: List<List<Pipe>>): Pair<Int, Int> {
        return grid.asSequence().mapIndexed { y, line -> line.mapIndexed { x, pipe -> Pair(x, y) to pipe } }
            .flatten()
            .filter { it.second == Pipe.START }
            .map { it.first }
            .first()
    }

    fun findPipes(grid: List<List<Pipe>>, startLoc: Pair<Int, Int>): Sequence<Pair<Int, Int>> {
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

            val nextDir = directions.first()
            val nextCoords = nextDir.transform(x, y)

            nextDir to nextCoords
        }.takeWhileInclusive {
            val (x, y) = it.second

            grid[y][x] != Pipe.START
        }.map { it.second }
    }

    fun part1(input: List<String>): Int {
        val grid = generateGrid(input)

        val startLoc = getStartLoc(grid)

        return findPipes(grid, startLoc).count() / 2
    }

    fun part2(input: List<String>): Int {
        val originalGrid = generateGrid(input)
        val startLoc = getStartLoc(originalGrid)
        val path = findPipes(originalGrid, startLoc).toList()

        val firstStep = listOf(
            CardinalDirection.NORTH,
            CardinalDirection.EAST,
            CardinalDirection.SOUTH,
            CardinalDirection.WEST
        ).map { it to it.transform(startLoc.first, startLoc.second) }.filter {
            originalGrid[it.second.second][it.second.first] != Pipe.NONE && originalGrid[it.second.second][it.second.first].getDirections()
                .any { dir ->
                    dir == it.first.opposite()
                }
        }.map {
            it.first
        }.let { it[0] to it[1] }.let(CardinalDirection::toPipe)

        val cleanedGrid = originalGrid.mapIndexed { y, line ->
            line.mapIndexed { x, pipe ->
                if (path.any { it.first == x && it.second == y }) {
                    pipe
                } else {
                    Pipe.NONE
                }
            }
        }.map {
            it.map { pipe ->
                if (pipe == Pipe.START) {
                    firstStep
                } else {
                    pipe
                }
            }
        }

        val expandedGrid = cleanedGrid.map { line ->
            line.map { pipe ->
                pipe.getExpanded()
            }
        }.flatMap {
            (0..2).map { y ->
                it.flatMap { line ->
                    line[y]
                }
            }
        }

        // now we run a flood fill

        val filled = generateSequence(expandedGrid to listOf(0 to 0)) { prev ->
            val (grid, locList) = prev
            val first = locList.first()

            val (y, x) = first

            val markedGrid = grid.mapIndexed { y1, line ->
                line.mapIndexed { x1, status ->
                    if (x1 == x && y1 == y) {
                        BoolPipe.Outer
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
                markedGrid[it.first][it.second] == BoolPipe.Empty
            }.filter {
                locList.none { a -> a.first == it.first && a.second == it.second }
            }

            markedGrid to (locList.drop(1) + nextLocs)
        }.takeWhileInclusive {
            it.second.isNotEmpty()
        }.last().first


        return filled.chunked(3).sumOf {
            // change hte indexer
            it[0].indices.map { x ->
                it.indices.map { y ->
                    it[y][x]
                }
            }.chunked(3).map { inner -> inner.flatten() }.count { cube ->
                cube.all { f -> f == BoolPipe.Empty }
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
//    check(part1(testInput) == 8)
    check(part2(testInput) == 10)

    val input = readInput("Day10")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
