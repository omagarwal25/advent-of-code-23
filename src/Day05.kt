fun main() {
    fun createRules(input: List<String>): List<List<List<Long>>> {
        val splitAt = input
            .withIndex()
            .filter { it.value == ("") }
            .map { it.index }

        // Create sublists.
        return splitAt
            .windowed(2, step = 1, partialWindows = true)
            .map {
                input.subList(
                    it[0] + 2,
                    if (it.count() == 1) input.size else it[1]
                )
            }.map {
                it.map { l ->
                    l.split(" ").map { n -> n.toLong() }
                }
            }
    }

    fun part1(input: List<String>): Int {
        val rules = createRules(input)

        rules.map { it.size }.println()

        val first = input[0].split(": ").last().split(" ").map { it.toLong() }

        return rules.fold(first) { acc, rule ->
            acc.map { value ->
                rule.firstNotNullOfOrNull { (to, from, range) ->
                    if (value in from until (from + range)) value + (to - from) else null
                } ?: value
            }
        }.min().toInt()
    }

    fun part2(input: List<String>): Int {
        val rules = createRules(input)
        val firstLine = input[0]
            .split(": ")
            .last()
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { (first, second) -> first.until(first + second) }


        val options = rules.foldRight(setOf<Long>()) { rule, acc ->
            val new = (rule.flatMap { (_, from, len) ->
                listOf(
                    from,
                    from + len - 1
                )
            } + (rule.minOf { (_, from, _) -> from } - 1) + (rule.maxOf { (_, from, len) -> from + len }) + 0 + Long.MAX_VALUE)
                .filter { it >= 0 }.toSet()

            val old = acc.map { value ->
                rule.firstNotNullOfOrNull { (to, from, range) ->
                    if (value in to until (to + range)) (value + (from - to)) else null
                } ?: value
            }

            println("old: ${(old+new).toSet().sorted()}")

            new + old
        }.filter { firstLine.any { r -> it in r } }

        return rules.fold(options) { acc, rule ->
            acc.map { value ->
                rule.firstNotNullOfOrNull { (to, from, range) ->
                    if (value in from until (from + range)) value + (to - from) else null
                } ?: value
            }
        }.min().toInt()

//        return rules.fold(firstLine) { acc, rule ->
//
//            // gameplan: because its a range, we need to be able to efficently chunk it.
//            acc.println()
//            acc.sumOf { it.last - it.first + 1 }.println()
//            rule.fold(acc to listOf<LongRange>()) { (range, listOfRanges), (to, from, len) ->
//                val rangesMatched = range.filter {
//                    from.until(from + len).contains(it.first) || from.until(from + len).contains(it.last)
//                }
//
//                val rangesNotMatched = range.filterNot {
//                    from.until(from + len).contains(it.first) || from.until(from + len).contains(it.last)
//                }
//
//                // for every match ranges, we want to split it into three ranges, one before the match, and one after the match, and one in the middle.
//                val newRanges = rangesMatched.flatMap { r ->
//                    val first = r.first
//                    val last = r.last
//
//                    val before = first.until(from)
//                    val after = (from + len)..last
//
//                    listOfNotNull(
//                        if (before.isEmpty()) null else before,
//                        if (after.isEmpty()) null else after,
//                    )
//                }
//
//                val unused = rangesNotMatched + newRanges
//
//                // now we add the middle values to the second list, after we modify them
//
//                val middle = rangesMatched.map { r ->
//                    val first = r.first
//                    val last = r.last
//
//                    max(first, from) .. min(last, from + len - 1)
//                }.map {
//                    val first = it.first
//                    val last = it.last
//
//                    (first + (to - from)).. (last + (to - from))
//                }
//
//                val newListOfRanges = listOfRanges + middle
//
//                unused to newListOfRanges
//            }.let { (range, listOfRanges) ->
//                range + listOfRanges
//            }
//        }.minOfOrNull { it.first }?.toInt() ?: 0
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 46)
//    check(part2(testInput) == 30)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
