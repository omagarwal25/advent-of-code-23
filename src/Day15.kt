sealed class Instruction(open val index: String) {
    fun toHash(): Int {
        return index.fold(0) { acc, c ->
            val ascii = c.code

            val inced = acc + ascii
            val muled = inced * 17

            muled % 256
        }
    }
}

data class Eq(override val index: String, val other: Int) : Instruction(index)
data class Minus(override val index: String) : Instruction(index)

fun main() {
    fun part1(input: List<String>): Int {
        return input.joinToString { it.trim() }.split(",").sumOf {
            it.fold(0) { acc: Int, c ->
                val ascii = c.code

                val inced = acc + ascii
                val muled = inced * 17

                muled % 256
            }
        }
    }

    fun part2(input: List<String>): Int {
        val instructions = input.joinToString { it.trim() }.split(",").map {
            if (it.last() == '-') {
                Minus(it.dropLast(1))
            } else {
                val (index, other) = it.split("=")
                Eq(index, other.toInt())
            }
        }

        return instructions.fold(List(256) { listOf<Pair<String, Int>>() }) { acc, instruction ->
            when (instruction) {
                is Eq -> {
                    val converted = instruction.toHash()

                    if (acc[converted].any { it.first == instruction.index }) {
                        acc.map { it.map { inner -> if (inner.first == instruction.index) inner.first to instruction.other else inner } }
                    } else {
                        acc.mapIndexed { index, entry -> if (index != instruction.toHash()) entry else entry + (instruction.index to instruction.other) }
                    }
                }

                is Minus -> {
                    acc.map { it.filter { inner -> inner.first != instruction.index } }
                }
            }
        }.mapIndexed { box, list ->
            list.mapIndexed { socket, entry ->
                (box + 1) * (socket + 1) * entry.second
            }.sum()
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
