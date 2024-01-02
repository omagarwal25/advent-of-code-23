private enum class Attribute {
    X, M, A, S;

    companion object {
        fun fromString(c: String): Attribute {
            return when (c) {
                "x" -> X
                "m" -> M
                "a" -> A
                "s" -> S
                else -> throw IllegalArgumentException("Unknown attribute: $c")
            }
        }
    }
}

private data class Item(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun getValue(i: Attribute): Int {
        return when (i) {
            Attribute.X -> x
            Attribute.M -> m
            Attribute.A -> a
            Attribute.S -> s
        }
    }

    fun sum(): Int {
        return x + m + a + s
    }
}

private enum class Op {
    GT, LT;

    companion object {
        fun fromString(c: String): Op {
            return when (c) {
                ">" -> GT
                "<" -> LT
                else -> throw IllegalArgumentException("Unknown op: $c")
            }
        }
    }
}

private data class Condition(val attribute: Attribute, val op: Op, val value: Int) {
    fun eval(item: Item): Boolean {
        return when (op) {
            Op.GT -> item.getValue(attribute) > value
            Op.LT -> item.getValue(attribute) < value
        }
    }
}

private sealed class Result {
    companion object {
        fun fromString(input: String): Result {
            return when (input) {
                "A" -> Accept
                "R" -> Reject
                else -> Continue(input)
            }
        }
    }
}

private sealed class Final : Result()
private data object Accept : Final()
private data object Reject : Final()

private data class Continue(val workflow: String) : Result()

private data class Statement(val condition: Condition, val result: Result)

private data class Workflow(val name: String, val statements: List<Statement>, val final: Result) {
    companion object {
        fun fromString(input: String): Workflow {
            val name = input.takeWhile { it != '{' }
            val withoutName = input.drop(name.length)
            val withoutBrackets = withoutName.drop(1).dropLast(1)

            val split = withoutBrackets.split(",")

            val final = Result.fromString(split.last())

            val rest = split.dropLast(1).map {
                val (first, last) = it.split(":")
                val lastResult = Result.fromString(last)

                val attribute = Attribute.fromString(first[0].toString())
                val op = Op.fromString(first[1].toString())

                val value = first.drop(2).toInt()

                Statement(Condition(attribute, op, value), lastResult)
            }

            return Workflow(name, rest, final)
        }
    }

    fun parse(item: Item): Result {
        return statements.firstOrNull {
            it.condition.eval(item)
        }?.result ?: final
    }
}


fun main() {
    fun getWorkflow(input: List<Workflow>, name: String): Workflow {
        return input.first { it.name == name }
    }

    fun part1(input: List<String>): Int {
        val emptyLineIndex = input.indexOf("")

        val rules = input.subList(0, emptyLineIndex).map(Workflow::fromString)
        val items = input.subList(emptyLineIndex + 1, input.size).map {
            it.drop(1).dropLast(1)
        }.map {
            val processed = it.split(",").map { inn ->
                inn.drop(2).toInt()
            }

            Item(processed[0], processed[1], processed[2], processed[3])
        }

        return items.asSequence().map {
            it to generateSequence(getWorkflow(rules, "in").parse(it)) { result ->
                when (result) {
                    is Continue -> getWorkflow(rules, result.workflow).parse(it)
                    else -> null
                }
            }.first { res -> res is Final }
        }.filter {
            it.second == Accept
        }.map { it.first }.map(Item::sum).sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
//    check(part2(testInput) == 145)

    val input = readInput("Day19")
    timing { part1(input).println() }
//    timing { part2(input).println() }
}
