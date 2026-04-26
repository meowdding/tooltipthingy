package me.owdding.tooltipthingy.system

import me.owdding.ktmodules.AutoCollect
import me.owdding.tooltipthingy.ComponentAlignment
import me.owdding.tooltipthingy.ComponentLike
import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.utils.ComponentLineListMerger
import me.owdding.tooltipthingy.utils.ListMerger
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.utils.extentions.get

@AutoCollect("TooltipFeatures")
annotation class RegisterFeature

abstract class TooltipFeatureWithContext<ContextType> {

    abstract val enabled: Boolean
    abstract fun createContext() : ContextType
    abstract val priority: Int
    abstract override fun toString(): String

    open fun ItemStack.applies(): Boolean = true

    context(context: ContextType)
    open fun ItemStack.nameOverride(): Component? = null

    context(context: ContextType)
    open fun ItemStack.nameReplacement(original: Component): Component? = null

    context(context: ContextType)
    open fun ItemStack.leftTags(): List<TooltipTag> = emptyList()

    context(context: ContextType)
    open fun ItemStack.rightTags(): List<TooltipTag> = emptyList()

    context(context: ContextType)
    open fun ItemStack.topRightIcon(): Identifier? = null

    context(context: ContextType)
    open fun ItemStack.isRarityUpgraded(): Boolean = false

    context(context: ContextType)
    open fun ItemStack.rarityOverride(): SkyBlockRarity? = null

    context(context: ContextType)
    open fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = Result.unmodified

    context(context: ContextType)
    open fun ItemStack.modify(): ItemStack? = null

    protected fun ListMerger<TooltipLine>.space() = this.add(ComponentLike(CommonComponents.EMPTY))
    protected fun ListMerger<TooltipLine>.add(alignment: ComponentAlignment, init: MutableComponent.() -> Unit) = this.add(ComponentLike(CommonComponents.EMPTY, alignment))
    protected fun ListMerger<TooltipLine>.add(alignment: Float, init: MutableComponent.() -> Unit) = this.add(ComponentLike(CommonComponents.EMPTY, ComponentAlignment.of(alignment)))
    protected fun ComponentLineListMerger.add(alignment: ComponentAlignment, init: MutableComponent.() -> Unit) = this.originalMerger.add(alignment, init)
    protected fun ComponentLineListMerger.add(alignment: Float, init: MutableComponent.() -> Unit) = this.originalMerger.add(alignment, init)

    @JvmName("space component list merger")
    protected fun ListMerger<Component>.space() = this.add(CommonComponents.EMPTY)

    @JvmName("add component list merger")
    protected fun ListMerger<Component>.add(init: MutableComponent.() -> Unit) = this.add(CommonComponents.EMPTY)

    protected fun withMerger(original: MutableList<TooltipLine>, init: ListMerger<TooltipLine>.() -> Result): Result {
        val merger = ListMerger(original)
        val result = merger.init()
        merger.addRemaining()
        original.clear()
        original.addAll(merger.destination)
        return result
    }

    protected fun withComponentMerger(original: MutableList<TooltipLine>, init: ComponentLineListMerger.() -> Result): Result {
        val merger = ListMerger(original)
        val componentMerger = ComponentLineListMerger(merger)
        val result = componentMerger.init()
        merger.addRemaining()
        original.clear()
        original.addAll(merger.destination)
        return result
    }

    context(stack: ItemStack)
    operator fun <Type> DataType<Type>.invoke() = stack[this]

    fun Boolean.asResult(propagateFurther: Boolean = true) = when {
        (this && propagateFurther) -> Result.modified
        this -> Result.consume
        propagateFurther -> Result.unmodified
        else -> Result.cancelled
    }
}

@Suppress("FunctionName", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
abstract class TooltipFeature : TooltipFeatureWithContext<Unit>() {

    final override fun createContext() = Unit

    context(_: Unit)
    final override fun ItemStack.nameOverride(): Component? = _nameOverride()
    context(_: Unit)
    final override fun ItemStack.nameReplacement(original: Component): Component? = _nameReplacement(original)
    context(_: Unit)
    final override fun ItemStack.leftTags(): List<TooltipTag> = _leftTags()
    context(_: Unit)
    final override fun ItemStack.rightTags(): List<TooltipTag> = _rightTags()
    context(_: Unit)
    final override fun ItemStack.topRightIcon(): Identifier? = _topRightIcon()

    context(_: Unit)
    final override fun ItemStack.isRarityUpgraded(): Boolean = _isRarityUpgraded()

    context(_: Unit)
    final override fun ItemStack.rarityOverride(): SkyBlockRarity? = _rarityOverride()

    context(_: Unit)
    final override fun ItemStack.modifyEntries(
        list: MutableList<TooltipLine>,
        previousResult: Result?,
    ): Result = _modifyEntries(list, previousResult)

    context(_: Unit)
    final override fun ItemStack.modify(): ItemStack? = this._modify()

    private fun ItemStack._nameOverride(): Component? = nameOverride()
    open fun ItemStack.nameOverride(): Component? = null
    private fun ItemStack._nameReplacement(original: Component): Component? = nameReplacement(original)
    open fun ItemStack.nameReplacement(original: Component): Component? = null
    private fun ItemStack._leftTags(): List<TooltipTag> = leftTags()
    open fun ItemStack.leftTags(): List<TooltipTag> = emptyList()
    private fun ItemStack._rightTags(): List<TooltipTag> = rightTags()
    open fun ItemStack.rightTags(): List<TooltipTag> = emptyList()
    private fun ItemStack._topRightIcon(): Identifier? = topRightIcon()
    open fun ItemStack.topRightIcon(): Identifier? = null
    private fun ItemStack._isRarityUpgraded(): Boolean = isRarityUpgraded()
    open fun ItemStack.isRarityUpgraded(): Boolean = false
    private fun ItemStack._rarityOverride(): SkyBlockRarity? = rarityOverride()
    open fun ItemStack.rarityOverride(): SkyBlockRarity? = null
    private fun ItemStack._modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = modifyEntries(list, previousResult)
    open fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = Result.unmodified
    private fun ItemStack._modify(): ItemStack? = modify()
    open fun ItemStack.modify(): ItemStack? = null
}

/**
 * @param modified Indicates whether there was any modification made
 * @param propagateFurther If true, lower priority modifiers will get a chance to modify as well, if false, no other modifier will be called.
 */
@ConsistentCopyVisibility
data class Result private constructor(
    val modified: Boolean,
    val propagateFurther: Boolean,
) {
    companion object Companion {
        val consume = Result(modified = true, propagateFurther = false)
        val modified = Result(modified = true, propagateFurther = true)
        val cancelled = Result(modified = false, propagateFurther = false)
        val unmodified = Result(modified = false, propagateFurther = true)
    }
}