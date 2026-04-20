package me.owdding.tooltipthingy.config

import com.teamresourceful.resourcefulconfigkt.api.builders.ColorBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.DraggableBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.EntriesBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.KeyBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.NumberBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.SelectBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.StringBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.TypeBuilder

@Suppress("unused")
interface AutoTranslated {
    val translationBase: String

    context(entryBuilder: EntriesBuilder)
    fun TypeBuilder.makeTranslations() {
        this.translation = listOf(translationBase, id).filter { it.isNotEmpty() }.joinToString(".")
    }

    context(entryBuilder: EntriesBuilder)
    fun <Type : TypeBuilder> wrap(builder: (Type) -> Unit): (Type) -> Unit = {
        it.makeTranslations()
        builder(it)
    }

    context(entryBuilder: EntriesBuilder)
    fun autoByte(value: Byte, builder: NumberBuilder<Byte>.() -> Unit = {}) = entryBuilder.byte(value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoByte(id: String, value: Byte, builder: NumberBuilder<Byte>.() -> Unit = {}) = entryBuilder.byte(id, value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoBytes(vararg value: Byte, builder: NumberBuilder<Byte>.() -> Unit = {}) = entryBuilder.bytes(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoBytes(id: String, vararg value: Byte, builder: NumberBuilder<Byte>.() -> Unit = {}) = entryBuilder.bytes(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoShort(value: Short, builder: NumberBuilder<Short>.() -> Unit = {}) = entryBuilder.short(value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoShort(id: String, value: Short, builder: NumberBuilder<Short>.() -> Unit = {}) = entryBuilder.short(id, value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoShorts(vararg value: Short, builder: NumberBuilder<Short>.() -> Unit = {}) = entryBuilder.shorts(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoShorts(id: String, vararg value: Short, builder: NumberBuilder<Short>.() -> Unit = {}) = entryBuilder.shorts(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoInt(value: Int, builder: NumberBuilder<Int>.() -> Unit = {}) = entryBuilder.int(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoInt(id: String, value: Int, builder: NumberBuilder<Int>.() -> Unit = {}) = entryBuilder.int(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoInts(vararg value: Int, builder: NumberBuilder<Int>.() -> Unit = {}) = entryBuilder.ints(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoInts(id: String, vararg value: Int, builder: NumberBuilder<Int>.() -> Unit = {}) = entryBuilder.ints(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoLong(value: Long, builder: NumberBuilder<Long>.() -> Unit = {}) = entryBuilder.long(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoLong(id: String, value: Long, builder: NumberBuilder<Long>.() -> Unit = {}) = entryBuilder.long(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoLongs(vararg value: Long, builder: NumberBuilder<Long>.() -> Unit = {}) = entryBuilder.longs(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoLongs(id: String, vararg value: Long, builder: NumberBuilder<Long>.() -> Unit = {}) = entryBuilder.longs(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoFloat(value: Float, builder: NumberBuilder<Float>.() -> Unit = {}) = entryBuilder.float(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoFloat(id: String, value: Float, builder: NumberBuilder<Float>.() -> Unit = {}) = entryBuilder.float(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoFloats(vararg value: Float, builder: NumberBuilder<Float>.() -> Unit = {}) = entryBuilder.floats(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoFloats(id: String, vararg value: Float, builder: NumberBuilder<Float>.() -> Unit = {}) = entryBuilder.floats(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoDouble(value: Double, builder: NumberBuilder<Double>.() -> Unit = {}) = entryBuilder.double(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoDouble(id: String, value: Double, builder: NumberBuilder<Double>.() -> Unit = {}) = entryBuilder.double(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoDoubles(vararg value: Double, builder: NumberBuilder<Double>.() -> Unit = {}) = entryBuilder.doubles(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoDoubles(id: String, vararg value: Double, builder: NumberBuilder<Double>.() -> Unit = {}) = entryBuilder.doubles(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoBoolean(value: Boolean, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.boolean(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoBoolean(id: String? = null, value: Boolean, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.boolean(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoBooleans(vararg value: Boolean, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.booleans(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoBooleans(id: String, vararg value: Boolean, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.booleans(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoString(value: String, builder: StringBuilder.() -> Unit = {}) = entryBuilder.string(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoString(id: String, value: String, builder: StringBuilder.() -> Unit = {}) = entryBuilder.string(id, value = value, wrap(builder))

    // Very hacky but sadly the varargs if its nullable makes it weird
    context(entryBuilder: EntriesBuilder)
    fun autoStrings(vararg value: String, builder: StringBuilder.() -> Unit = {}) = entryBuilder.strings(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoStrings(id: String, vararg value: String, builder: StringBuilder.() -> Unit = {}) = entryBuilder.stringsWithId(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoEnum(value: T, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.enum(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoEnum(id: String, value: T, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.enum(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoEnums(vararg value: T, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.enums(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoEnums(id: String, vararg value: T, builder: TypeBuilder.() -> Unit = {}) = entryBuilder.enums(id, value = value, wrap(builder))

    // special
    context(entryBuilder: EntriesBuilder)
    fun autoKey(value: Int, builder: KeyBuilder.() -> Unit = {}) = entryBuilder.key(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoKey(id: String, value: Int, builder: KeyBuilder.() -> Unit = {}) = entryBuilder.key(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun autoColor(value: Int, builder: ColorBuilder.() -> Unit = {}) = entryBuilder.color(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun autoColor(id: String, value: Int, builder: ColorBuilder.() -> Unit = {}) = entryBuilder.color(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoSelect(vararg value: T, builder: SelectBuilder<T>.() -> Unit = {}) = entryBuilder.select(value = value, wrap(builder))
    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoSelect(id: String, vararg value: T, builder: SelectBuilder<T>.() -> Unit = {}) = entryBuilder.select(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoDraggable(vararg value: T, builder: DraggableBuilder<T>.() -> Unit = {}) = entryBuilder.draggable(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    fun <T : Enum<T>> autoDraggable(id: String, vararg value: T, builder: DraggableBuilder<T>.() -> Unit = {}) = entryBuilder.draggable(id, value = value, wrap(builder))


}