package me.owdding.iconographic.utils.extensions

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.utils.extentions.get

operator fun <Type> ItemStack.contains(type: DataType<Type>): Boolean = this[type] != null
