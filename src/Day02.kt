private data class RGB(val r: Int, val g: Int, val b: Int)

fun main() {
    fun processInput(input: List<String>): List<Pair<Int, List<RGB>>> {
        return input.map { line ->
            val (game, runs) = line.split(": ") // looks like Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            val (_, id) = game.split(" ")

            val runsList = runs.split("; ").map { run ->
                val colors = run.trim().split(", ")
                val pairedColors = colors.map { color ->
                    val (amount, colorString) = color.split(" ")

                    colorString to amount.toInt()
                }

                val red = pairedColors.firstOrNull { it.first == "red" }?.second ?: 0
                val green = pairedColors.firstOrNull { it.first == "green" }?.second ?: 0
                val blue = pairedColors.firstOrNull { it.first == "blue" }?.second ?: 0

                RGB(red, green, blue)
            }

            id.toInt() to runsList
        }
    }

    fun part1(input: List<String>): Int {
        val maxRed = 12
        val maxGreen = 13
        val maxBlue = 14


        val games = processInput(input)

        val maxes = games.map { (id, runs) ->
            id to RGB(runs.maxOfOrNull { it.r } ?: 0,
                runs.maxOfOrNull { it.g } ?: 0,
                runs.maxOfOrNull { it.b } ?: 0)
        }

        return maxes.sumOf { (id, rgb) ->
            val (r, g, b) = rgb

            if (r > maxRed || g > maxGreen || b > maxBlue) 0
            else id
        }
    }

    fun part2(input: List<String>): Int {
        val games = processInput(input)

        return games.map { (_, runs) ->
            RGB(runs.maxOfOrNull { it.r } ?: 0,
                runs.maxOfOrNull { it.g } ?: 0,
                runs.maxOfOrNull { it.b } ?: 0)
        }.sumOf { (r, g, b) -> r * g * b }

    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
