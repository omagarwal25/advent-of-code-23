enum class Direction {
    LEFT, RIGHT
}


fun main() {
    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b

        return generateSequence(larger) {
            it + larger
        }.takeWhile { it <= maxLcm }.first {
            (it % a == 0L && it % b == 0L)
        }
    }

    fun parseInput(input: List<String>): Pair<List<Direction>, Map<String, Pair<String, String>>> {
        val instructions = input[0].map { if (it == 'L') Direction.LEFT else Direction.RIGHT }
        val rest = input.drop(2)
            .map { it.split(" = ") }.associate { (name, value) ->
                name to (value
                    .removePrefix("(").removeSuffix(")")
                    .split(", ").let { (f, s) -> f to s })
            }
        return instructions to rest
    }

    fun part1(input: List<String>): Int {
        val (instructions, rest) = parseInput(input)

        return generateSequence("AAA" to 0) { (loc, i) ->
            val direction = instructions[i % instructions.size]
            val (f, s) = rest[loc]!!
            val newLoc = if (direction == Direction.LEFT) f else s
            newLoc to (i + 1)
        }.takeWhile { (loc, _) -> !loc.endsWith("ZZZ") }.count()
    }

    fun part2(input: List<String>): Long {
        val (instructions, rest) = parseInput(input)

        val starts = rest.filter { (name, _) -> name.endsWith("A") }

        return starts.map {
            generateSequence(it.key to 0) { (loc, i) ->
                // use i to index into the instruction loop
                val direction = instructions[i % instructions.size]
                val (f, s) = rest[loc]!!
                val newLoc = if (direction == Direction.LEFT) f else s
                newLoc to (i + 1)
            }.takeWhile { (loc, _) -> !loc.endsWith("Z") }.count().toLong()
        }.reduce { acc, i -> findLCM(acc, i) }
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
//    check(part1(testInput) == 2)
    check(part2(testInput) == 6L)

    val input = readInput("Day08")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
