package ru.airdead.iwseller.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import ru.airdead.iwseller.data.menu.MenuBuilder

object MenuListener : Listener {
    private val inventories = mutableMapOf<Inventory, MenuBuilder>()
    private var registered = false

    fun registerInventory(inventory: Inventory, builder: MenuBuilder) {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().plugins[0])
            registered = true
        }
        inventories[inventory] = builder
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val builder = inventories[event.inventory] ?: return
        builder.handleClick(event)
    }
}