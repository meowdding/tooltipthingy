# Mod Compatability

To have your custom rendered Items/Tooltips also affected by Iconographic, 
you need to add IMC as provided below to your mod.

<details>
<summary>Kotlin Code</summary>

```kotlin
package your.package

import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer

object IconographicCompat {
    private var withItemCallback: ((item: ItemStack, runnable: () -> Unit) -> Unit) = { _, runnable -> runnable() }
    
	fun withItem(item: ItemStack, runnable: () -> Unit) {
		withItemCallback(item, runnable)
	}
    
	fun setupItemCompat(consumer: BiConsumer<ItemStack, Runnable>) {
		this.withItemCallback = { item, runnable -> consumer.accept(item) { runnable() } }
	}
}
```
</details>

<details>
<summary>Java Code</summary>

```java
package your.package;

import net.minecraft.world.item.ItemStack;
import java.util.function.BiConsumer;

public class IconographicCompat {
    
    private static BiConsumer<ItemStack, Runnable> withItemCallback = (item, runnable) -> runnable.run();
    
    public static void withItem(ItemStack item, Runnable runnable) {
        withItemCallback.accept(item, runnable);
    }
    
    public static void setupItemCompat(BiConsumer<ItemStack, Runnable> consumer) {
        withItemCallback = consumer;
    }
}
```
</details>

Add the following entrypoint to your `fabric.mod.json`:

```json
  [
    ...
    "iconographic:imc/item": [
      {
        "adapter": "kotlin",
        "value": "your.package.IconographicCompat::setupItemCompat"
      }
    ],
    ...
  ]
```

And then whenever you call your `graphics.setComponentTooltipForNextFrame` when rendering the Tooltip, 
wrap it in the `withItem()` block.

Look at this Example from the [SkyBlock Item List](https://modrinth.com/mod/skyblock-item-list) Mod: https://github.com/OperationPotato/ItemList/commit/67a8a82f6d8e66396c20f54f5b3cdd386fe894b1