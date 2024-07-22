package ru.airdead.iwseller.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.airdead.iwseller.data.profile
import ru.airdead.iwseller.quest.Quest
import ru.airdead.iwseller.quest.QuestType
import ru.airdead.iwseller.quest.Reward

class TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command")
            return true
        }
        sender.profile.quests.forEach(::println)


        val quest = Quest(
            name = "Kill 10 zombies",
            description = "Kdd",
            reward = Reward(
                coins = 10,
                soulsCount = 1,
                experience = 10,
            ),
            type = QuestType.Kill(EntityType.ZOMBIE),
            target = 10
        )

        sender.profile.quests.add(quest)
        return true
    }
}