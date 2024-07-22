package ru.airdead.iwseller.command

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.airdead.iwseller.data.menu.menu

class TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command")
            return true
        }

        val menu = menu("Test menu", 54) {
            item(0, ItemStack(Material.GOLDEN_PICKAXE)) {
                sender.sendMessage("Test item clicked")
            }
        }

        sender.openInventory(menu)
        return true
    }
}