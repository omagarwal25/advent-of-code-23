fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.filter { c -> c.isDigit() } }.map { it.first().toString() + it.last() }
            .sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Int {
        val wordToNumber = mapOf(
            "zero" to 0,
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        return input.sumOf { line ->
            val front =
                line.fold("") { front, c ->
                    if (front.firstOrNull()?.isDigit() == true) front
                    else if (wordToNumber.keys.any { front.contains(it) })
                        wordToNumber[wordToNumber.keys.first { front.contains(it) }].toString()
                    else if (c.isDigit()) c.toString()
                    else front + c
                }

            val back = line.foldRight("") { c, back ->
                if (back.lastOrNull()?.isDigit() == true) back
                else if (wordToNumber.keys.any { back.reversed().contains(it) })
                    wordToNumber[wordToNumber.keys.first { back.reversed().contains(it) }].toString()
                else if (c.isDigit()) c.toString()
                else back + c
            }

            "${front}${back}".toInt()
        }
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
