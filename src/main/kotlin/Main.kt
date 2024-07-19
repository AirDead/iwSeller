package ru.airdead.iwseller

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.iwseller.data.QuestStatus
import ru.airdead.iwseller.data.QuestType
import ru.airdead.iwseller.data.profile

class Main : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        openQuestMenu(player)
    }

    private fun openQuestMenu(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "§6Quests")
        player.profile.quests.forEachIndexed { index, quest ->
            val itemStack = quest.toItemStack()
            inventory.setItem(index, itemStack)
        }
        player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem ?: return
        val profile = player.profile

        val clickedQuest = profile.quests.find { it.name == item.itemMeta?.displayName }
        if (clickedQuest != null && clickedQuest.status == QuestStatus.NOT_STARTED && profile.assignNextQuest()) {
            player.sendMessage("Вы начали квест: ${clickedQuest.name}")
            openQuestMenu(player)
        }

        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val profile = killer.profile
        profile.updateQuestProgress(QuestType.KILL)

        val currentQuest = profile.getCurrentQuest()
        if (currentQuest != null && currentQuest.status == QuestStatus.COMPLETED) {
            profile.completeQuest(currentQuest.name)
            killer.sendMessage("Вы завершили квест: ${currentQuest.name}")
        }
    }
}
