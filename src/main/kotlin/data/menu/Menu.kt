package ru.airdead.iwseller.data.menu

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ru.airdead.iwseller.listener.MenuListener

fun menu(title: String, slots: Int, setup: MenuBuilder.() -> Unit): Inventory {
    val builder = MenuBuilder(title, slots)
    builder.setup()
    val inventory = builder.build()
    MenuListener.registerInventory(inventory, builder)
    return inventory
}

class MenuBuilder(private val title: String, private val slots: Int) {
    private val items = mutableMapOf<Int, MenuItem>()

    fun item(slot: Int, itemStack: ItemStack, onClick: InventoryClickEvent.() -> Unit) {
        items[slot] = MenuItem(itemStack, onClick)
    }

    fun build(): Inventory {
        val inventory = Bukkit.createInventory(null, slots, title)
        items.forEach { (slot, item) ->
            inventory.setItem(slot, item.itemStack)
        }
        return inventory
    }

    fun handleClick(event: InventoryClickEvent) {
        items[event.slot]?.onClick?.invoke(event)
        event.isCancelled = true
    }

    private data class MenuItem(val itemStack: ItemStack, val onClick: InventoryClickEvent.() -> Unit)
}
