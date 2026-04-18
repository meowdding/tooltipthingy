package me.owdding.tooltipthingy.utils

import me.owdding.tooltipthingy.ComponentLike
import me.owdding.tooltipthingy.TooltipLine
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.math.floor

fun <T : Any> T?.otherwise(other: T): T = this ?: other

fun Float.floorToInt() = floor(this).toInt()
fun Double.floorToInt() = floor(this).toInt()

class ComponentLineListMerger(val originalMerger: ListMerger<TooltipLine>) : ListMerger<Component>(emptyList(), 0) {

    override var index: Int
        get() = originalMerger.index
        set(value) {
            originalMerger.index = value
        }
    override val original: List<Component> = originalMerger.original.map { it.asComponentOrNull() ?: CommonComponents.EMPTY }

    fun TooltipLine.asComponentOrNull() = when (this) {
        is ComponentLike -> this.component
        else -> null
    }

    fun TooltipLine.asComponent() = this.asComponentOrNull() ?: throw IllegalStateException("Expected next line to be a component but got ${this.toString()}")

    fun TooltipLine.isComponent() = this.asComponentOrNull() != null

    fun skipNonComponents() {
        originalMerger.addUntil { it.isComponent() }
    }

    override fun canRead(): Boolean {
        skipNonComponents()
        return super.canRead()
    }

    override fun peek(): Component {
        skipNonComponents()
        return originalMerger.peek().asComponent()
    }

    override fun read(): Component {
        skipNonComponents()
        return originalMerger.read().asComponent()
    }

    override fun copy(): Boolean {
        skipNonComponents()
        return originalMerger.copy()
    }

    override fun add(item: Component): Boolean {
        return originalMerger.add(ComponentLike(item))
    }

    override fun addAfterNext(predicate: (Component) -> Boolean, provider: MutableList<Component>.() -> Unit) {
        originalMerger.addAfterNext({
            skipNonComponents()
            canRead() && predicate(read())
        }, {
            addAll(buildList {
                provider()
            }.map(::ComponentLike))
        })
    }

    override fun addUntil(predicate: (Component) -> Boolean) {
        skipNonComponents()
        while (originalMerger.index + 1 < originalMerger.original.size && !predicate(originalMerger.peek().asComponent())) {
            copy()
            skipNonComponents()
        }
    }

    override fun addBeforeNext(predicate: (Component) -> Boolean, provider: MutableList<Component>.() -> Unit) {
        addUntil(predicate)
        originalMerger.destination.addAll(buildList { provider(this) }.map(::ComponentLike))
        copy()
    }

    override fun addRemaining() = originalMerger.addRemaining()

    override fun hasNext(predicate: (Component) -> Boolean): Boolean {
        return originalMerger.hasNext { it.asComponentOrNull()?.let(predicate) == true }
    }

    override fun readSafe(): Component? {
        skipNonComponents()
        return originalMerger.readSafe()?.asComponentOrNull()
    }


    fun skipUntilAfterSpace() {
        skipNonComponents()
        while (index + 1 < original.size && peek().stripped.isNotBlank()) {
            read()
            skipNonComponents()
        }
        if (index + 1 < original.size) read()
    }
    fun skipSpace() {
        skipNonComponents()
        while (index + 1 < original.size && peek().stripped.isBlank()) {
            read()
            skipNonComponents()
        }
    }

    fun addUntilRarityLine(rarity: SkyBlockRarity, includeRarity: Boolean = false): Boolean {
        val name = rarity.displayName.uppercase()
        val index = original.indexOfLast { it.stripped.contains(name) }
        if (index == -1) return false
        repeat(index + if (includeRarity) 1 else 0) { copy() }
        return true
    }

    override fun addUntilAfter(predicate: (Component) -> Boolean) {
        originalMerger.addUntilAfter {
            skipNonComponents()
            canRead() && predicate(it.asComponent())
        }
    }

    override fun copyAll() {
        originalMerger.copy()
    }
}

open class ListMerger<T>(open val original: List<T>, open var index: Int = 0) {
    val destination: MutableList<T> = mutableListOf()

    open fun peek() = original[index]
    open fun read() = original[index++]
    open fun copy() = destination.add(read())
    open fun add(item: T) = destination.add(item)


    open fun addAfterNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        copy()
        destination.provider()
    }

    open fun addUntil(predicate: (T) -> Boolean) {
        while (index + 1 < original.size && !predicate(peek())) copy()
    }

    open fun addBeforeNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        destination.provider()
        copy()
    }

    open fun addRemaining() {
        if (index >= original.size) return
        destination.addAll(original.subList(index, original.size))
        index = original.size
    }

    open fun hasNext(predicate: (T) -> Boolean): Boolean = this.original.subList(index, original.size).any(predicate)
    open fun canRead(): Boolean = index < original.size
    open fun readSafe(): T? = if (canRead()) read() else null

    open fun addUntilAfter(predicate: (T) -> Boolean) {
        addUntil(predicate)
        read()
    }

    open fun copyAll() {
        while (canRead()) copy()
    }
}

