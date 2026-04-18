package me.owdding.tooltipthingy.utils.extensions

fun <Type> Iterable<Type>.extremesOf(converter: (Type) -> Int): Pair<Int, Int>? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null

    var min = converter(iterator.next())
    var max = min

    for (element in this) {
        val value = converter(element)
        if (value < min) min = value
        if (value > max) max = value
    }

    return min to max
}

fun <Type> Iterable<Type>.doubleSum(first: (Type) -> Int, second: (Type) -> Int) = this.doubleIterate(first, second, Integer::sum)

fun <Type, NumberType : Number> Iterable<Type>.doubleIterate(
    first: (Type) -> NumberType,
    second: (Type) -> NumberType,
    combiner: (NumberType, NumberType) -> NumberType
) : Pair<NumberType, NumberType>? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null

    val firstElement = iterator.next()
    var firstVar = first(firstElement)
    var secondVar = second(firstElement)

    for (element in this) {
        firstVar = combiner(firstVar, first(element))
        secondVar = combiner(secondVar, second(element))
    }

    return firstVar to secondVar
}