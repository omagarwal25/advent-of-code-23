import kotlin.math.pow

fun main() {
    fun parseGames(input: List<String>): List<Pair<List<Int>, List<Int>>> {
        return input.map { line ->
            line.split(": ")[1].split(" | ").let { (winning, ours) ->
                winning.chunked(3).map { it.trim().toInt() } to ours.chunked(3).map { it.trim().toInt() }
            }
        }
    }


    fun part1(input: List<String>): Int {
        return parseGames(input)
            .map { (winning, ours) ->
                ours.count { it in winning }
            }.sumOf {
                if (it == 0) 0 else 2.0.pow(it - 1).toInt()
            }
    }

    fun part2(input: List<String>): Int {
        val wins = input.indices.toMutableList()
        val games = parseGames(input).map { (winning, ours) ->
            ours.count { it in winning }
        }

        var count = 0

        while (wins.isNotEmpty()) {
            val win = wins.min()
            val numOfMin = wins.count { it == win }

            wins.removeAll { it == win }
            count += numOfMin

            val numberOfWins = games[win]
            val newTickets = (win+1)..(win+numberOfWins)


            val temp = newTickets.flatMap { num -> List(numOfMin) {num} }

            wins.addAll(temp)
        }
        return count
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
