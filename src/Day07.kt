private data class Card(val value: Int, val jokerVariation: Boolean = false) : Comparable<Card> {
    override fun compareTo(other: Card): Int {
        if (value == other.value) return 0
        if (value == Card("J").value && jokerVariation) return -1
        if (other.value == Card("J").value && other.jokerVariation) return 1

        return value compareTo other.value
    }

    override fun toString(): String {
        return when (value) {
            14 -> "A"
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            10 -> "T"
            else -> value.toString()
        }
    }

    constructor(value: String, jokerVariation: Boolean = false) : this(
        when (value) {
            "J" -> 11
            "Q" -> 12
            "K" -> 13
            "A" -> 14
            "T" -> 10
            else -> value.toIntOrNull() ?: error("Invalid card value: $value")
        }, jokerVariation
    )
}

// hand of 5 cards
private data class Hand(val cards: List<Card>, val jokerVariation: Boolean = false) : Comparable<Hand> {
    override fun toString(): String {
        return cards.joinToString(" ")
    }

    private fun toMap(): Map<Int, Int> {
        return cards.groupBy { it.value }.mapValues { it.value.size }
    }

    private fun replaceJokers(): Hand {
        val jokers = cards.count { it.value == Card("J").value }

        if (jokers == 0) return this
        if (jokers == 5) return Hand(listOf(Card("2"), Card("2"), Card("2"), Card("2"), Card("2")), jokerVariation)

//        val max = cards.map { it.value }.filter { it != Card("J").value }.maxOrNull() ?: error("No max card")
        val maxNum =
            this.toMap().filter { (card, _) -> card != Card("J").value }.values.maxOrNull() ?: error("No max card")
        val max =
            this.toMap().filter { (card, _) -> card != Card("J").value }.filter { it.value == maxNum }.keys.first()

        val newCards = cards.map { card ->
            if (card.value == Card("J").value) {
                Card(max, true)
            } else {
                card
            }
        }

        return Hand(newCards, jokerVariation)
    }

    constructor(cards: String, jokerVariation: Boolean = false) : this(cards.map {
        Card(
            it.toString(),
            jokerVariation
        )
    }, jokerVariation)

    private fun regularCompareTo(other: Hand): Int {
        return this.cards.zip(other.cards).map { (a, b) -> a compareTo b }.firstOrNull { it != 0 } ?: 0
    }

    override fun compareTo(other: Hand): Int {
        // gets a bit complicated
        // 5 of a kind
        // 4 of a kind
        // Full house
        // 3 of a kind
        // 2 pair
        // 1 pair
        // high card

        val thisJokerBuff = if (jokerVariation) this.toMap()[Card("J").value] ?: 0 else 0
        val otherJokerBuff = if (jokerVariation) other.toMap()[Card("J").value] ?: 0 else 0

        val thisMap = if (thisJokerBuff > 0) this.replaceJokers().toMap() else this.toMap()
        val otherMap = if (otherJokerBuff > 0) other.replaceJokers().toMap() else other.toMap()

        if (thisMap.values.max() == 5) {
            return if (otherMap.values.max() == 5) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.max() == 5) {
            return -1
        }

        if (thisMap.values.max() == 4) {
            return if (otherMap.values.max() == 4) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.max() == 4) {
            return -1
        }

        if ((thisMap.values.max() == 3 && thisMap.values.contains(2))) {
            return if ((otherMap.values.max() == 3 && otherMap.values.contains(2))) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.max() == 3 && otherMap.values.contains(2)) {
            return -1
        }

        if (thisMap.values.max() == 3) {
            return if (otherMap.values.max() == 3) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.max() == 3) {
            return -1
        }

        if (thisMap.values.count { it == 2 } == 2) {
            return if (otherMap.values.count { it == 2 } == 2) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.count { it == 2 } == 2) {
            return -1
        }

        if (thisMap.values.max() == 2) {
            return if (otherMap.values.max() == 2) {
                regularCompareTo(other)
            } else {
                1
            }
        }

        if (otherMap.values.max() == 2) {
            return -1
        }

        return regularCompareTo(other)
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence().map {
            it.split(" ")
        }.map { (hand, id) ->
            Hand(hand) to id.trim().toInt()
        }.sortedBy { (hand, _) ->
            hand
        }.mapIndexed { index, (_, id) ->
            id * (index + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence().map {
            it.split(" ")
        }.map { (hand, id) ->
            Hand(hand, true) to id.trim().toInt()
        }
            .sortedBy { (hand, _) ->
                hand
            }.mapIndexed { index, (h, id) ->
                id * (index + 1)
            }.sum()
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    timing { part1(input).println() }
    timing { part2(input).println() }
}
