package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class McTopCommandAsyncTask extends CancellableRunnable {
	private final CommandSender sender;
	private final PrimarySkillType skill;
	private final int page;
	private final boolean useBoard, useChat;

	public McTopCommandAsyncTask(int page, PrimarySkillType skill, CommandSender sender,
	                             boolean useBoard, boolean useChat) {
		Validate.isTrue(useBoard || useChat,
				"Attempted to start a rank retrieval with both board and chat off");
		Validate.notNull(sender, "Attempted to start a rank retrieval with no recipient");

		if (useBoard) {
			Validate.isTrue(sender instanceof Player,
					"Attempted to start a rank retrieval displaying scoreboard to a non-player");
		}

		this.page = page;
		this.skill = skill;
		this.sender = sender;
		this.useBoard = useBoard;
		this.useChat = useChat;
	}

	@Override
	public void run() {
		Set<String> hidden = mcMMO.p.getGeneralConfig().getHiddenFromLeaderboards()
				.stream()
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		int fetchAmount = 10 + hidden.size();

		List<PlayerStat> userStats = mcMMO.getDatabaseManager()
				.readLeaderboard(skill, page, fetchAmount);


		userStats = userStats.stream()
				.filter(stat -> !hidden.contains(stat.playerName().toLowerCase()))
				.limit(10)
				.collect(Collectors.toList());

		mcMMO.p.getFoliaLib().getScheduler().runNextTick(
				new MctopCommandDisplayTask(userStats, page, skill, sender, useBoard, useChat));
	}
}
