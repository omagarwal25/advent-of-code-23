fun main() {
    fun numberLocations(input: List<String>): List<Pair<Int, Pair<Int, Int>>> {
        val numberLocations = input.flatMapIndexed { y, line ->
            val numbers = line.foldIndexed<Pair<Int?, List<Pair<Int, Int>>>>(null to emptyList()) { index, acc, c ->
                if (c.isDigit() && acc.first == null) {
                    index to acc.second
                } else if (!c.isDigit() && acc.first != null) {
                    null to acc.second + (acc.first!! to index)
                } else {
                    acc
                }
            }

            if (numbers.first != null) {
                numbers.second + (numbers.first!! to line.length)
            } else {
                numbers.second
            }.map { y to it }
        }
        return numberLocations
    }

    fun part1(input: List<String>): Int {
        val numberLocations = numberLocations(input)

        return numberLocations.filter { (y, xr) ->
            val (x1, x2) = xr
            // for all x1..x2, we need to check if there is a symbol above, below, left right, or diagonal

            ((x1-1)..x2).any {
                ((y-1)..(y+1)).any { y2 ->
                    y2 in input.indices && it in input[y2].indices && input[y2][it] != '.' && !input[y2][it].isDigit()
                }
            }
        }.map { (y, xr) ->
             input[y].subSequence(xr.first, xr.second).toString()
        }.sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Int {
        val numberLocations = numberLocations(input)

        val stars = input.flatMapIndexed { y, s ->
            s.mapIndexed { x, c ->
                y to x to c
            }.filter { (_, c) ->
                c == '*'
            }.map { (coords, _) ->
                coords
            }
        }

        return stars.mapNotNull { (y, x) ->
            numberLocations.filter { (y2, xr) ->
                x in (xr.first - 1)..xr.second && y2 in y - 1..y + 1
            }.let { if (it.size == 2) it else null }?.let { (a, b) ->
                input[a.first].subSequence(a.second.first, a.second.second).toString()
                    .toInt() * input[b.first].subSequence(b.second.first, b.second.second).toString().toInt()
            }
        }.sum()
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
