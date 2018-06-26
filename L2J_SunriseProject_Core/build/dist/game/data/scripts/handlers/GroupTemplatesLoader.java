/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.group_template.BeastFarm;
import ai.group_template.DenOfEvil;
import ai.group_template.FeedableBeasts;
import ai.group_template.FleeMonsters;
import ai.group_template.FrozenLabyrinth;
import ai.group_template.GiantsCave;
import ai.group_template.HotSprings;
import ai.group_template.IsleOfPrayer;
import ai.group_template.MithrilMines;
import ai.group_template.MonasteryOfSilence;
import ai.group_template.PlainsOfDion;
import ai.group_template.PlainsOfLizardman;
import ai.group_template.PolymorphingAngel;
import ai.group_template.PolymorphingOnAttack;
import ai.group_template.PrisonGuards;
import ai.group_template.RandomSpawn;
import ai.group_template.Sandstorms;
import ai.group_template.SilentValley;
import ai.group_template.SummonMinions;
import ai.group_template.SummonPc;
import ai.group_template.TurekOrcs;
import ai.group_template.VarkaKetra;
import ai.group_template.WarriorFishingBlock;
import ai.group_template.extra.BrekaOrcOverlord;
import ai.group_template.extra.Chests;
import ai.group_template.extra.CryptsOfDisgrace;
import ai.group_template.extra.FieldOfWhispersSilence;
import ai.group_template.extra.KarulBugbear;
import ai.group_template.extra.LuckyPig;
import ai.group_template.extra.Mutation;
import ai.group_template.extra.OlMahumGeneral;
import ai.group_template.extra.TimakOrcOverlord;
import ai.group_template.extra.TimakOrcTroopLeader;
import ai.group_template.extra.TomlanKamos;
import ai.group_template.extra.WarriorMonk;
import ai.group_template.extra.ZombieGatekeepers;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class GroupTemplatesLoader
{
	private static final Logger _log = LoggerFactory.getLogger(GroupTemplatesLoader.class);
	
	private static final Class<?>[] templates =
	{
		BeastFarm.class,
		DenOfEvil.class,
		FeedableBeasts.class,
		FleeMonsters.class,
		FrozenLabyrinth.class,
		GiantsCave.class,
		HotSprings.class,
		IsleOfPrayer.class,
		MithrilMines.class,
		MonasteryOfSilence.class,
		PlainsOfDion.class,
		PlainsOfLizardman.class,
		PolymorphingAngel.class,
		PolymorphingOnAttack.class,
		PrisonGuards.class,
		RandomSpawn.class,
		Sandstorms.class,
		SilentValley.class,
		SummonMinions.class,
		SummonPc.class,
		TurekOrcs.class,
		VarkaKetra.class,
		WarriorFishingBlock.class,
		
		// Extras
		BrekaOrcOverlord.class,
		Chests.class,
		CryptsOfDisgrace.class,
		FieldOfWhispersSilence.class,
		KarulBugbear.class,
		LuckyPig.class,
		Mutation.class,
		OlMahumGeneral.class,
		TimakOrcOverlord.class,
		TimakOrcTroopLeader.class,
		TomlanKamos.class,
		WarriorMonk.class,
		ZombieGatekeepers.class,
	};
	
	public GroupTemplatesLoader()
	{
		_log.info(GroupTemplatesLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : templates)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(GroupTemplatesLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
