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

import ai.npc.Abercrombie.Abercrombie;
import ai.npc.Alarm.Alarm;
import ai.npc.Alexandria.Alexandria;
import ai.npc.ArenaManager.ArenaManager;
import ai.npc.Asamah.Asamah;
import ai.npc.AvantGarde.AvantGarde;
import ai.npc.BlackJudge.BlackJudge;
import ai.npc.BlackMarketeerOfMammon.BlackMarketeerOfMammon;
import ai.npc.CastleAmbassador.CastleAmbassador;
import ai.npc.CastleBlacksmith.CastleBlacksmith;
import ai.npc.CastleChamberlain.CastleChamberlain;
import ai.npc.CastleCourtMagician.CastleCourtMagician;
import ai.npc.CastleMercenaryManager.CastleMercenaryManager;
import ai.npc.CastleSiegeManager.CastleSiegeManager;
import ai.npc.CastleTeleporter.CastleTeleporter;
import ai.npc.CastleWarehouse.CastleWarehouse;
import ai.npc.ClanTrader.ClanTrader;
import ai.npc.Dorian.Dorian;
import ai.npc.DragonVortexRetail.DragonVortexRetail;
import ai.npc.EkimusMouth.EkimusMouth;
import ai.npc.FameManager.FameManager;
import ai.npc.ForgeOfTheGods.ForgeOfTheGods;
import ai.npc.ForgeOfTheGods.Rooney;
import ai.npc.ForgeOfTheGods.TarBeetle;
import ai.npc.FortressArcherCaptain.FortressArcherCaptain;
import ai.npc.FortressSiegeManager.FortressSiegeManager;
import ai.npc.FreyasSteward.FreyasSteward;
import ai.npc.Jinia.Jinia;
import ai.npc.Katenar.Katenar;
import ai.npc.KetraOrcSupport.KetraOrcSupport;
import ai.npc.ManorManager.ManorManager;
import ai.npc.MercenaryCaptain.MercenaryCaptain;
import ai.npc.Minigame.Minigame;
import ai.npc.MonumentOfHeroes.MonumentOfHeroes;
import ai.npc.NevitsHerald.NevitsHerald;
import ai.npc.NpcBuffers.NpcBuffers;
import ai.npc.NpcBuffers.impl.CabaleBuffer;
import ai.npc.PcBangPoint.PcBangPoint;
import ai.npc.PriestOfBlessing.PriestOfBlessing;
import ai.npc.Rafforty.Rafforty;
import ai.npc.Rignos.Rignos;
import ai.npc.Selina.Selina;
import ai.npc.Sirra.Sirra;
import ai.npc.Summons.MerchantGolem.GolemTrader;
import ai.npc.SupportUnitCaptain.SupportUnitCaptain;
import ai.npc.SymbolMaker.SymbolMaker;
import ai.npc.TerritoryManagers.TerritoryManagers;
import ai.npc.TownPets.TownPets;
import ai.npc.Trainers.HealerTrainer.HealerTrainer;
import ai.npc.Tunatun.Tunatun;
import ai.npc.VarkaSilenosSupport.VarkaSilenosSupport;
import ai.npc.WeaverOlf.WeaverOlf;
import ai.npc.WyvernManager.WyvernManager;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class NpcLoader
{
	private static final Logger _log = LoggerFactory.getLogger(NpcLoader.class);
	
	private static final Class<?>[] NPCs =
	{
		Abercrombie.class,
		Alarm.class,
		Alexandria.class,
		ArenaManager.class,
		Asamah.class,
		AvantGarde.class,
		BlackJudge.class,
		BlackMarketeerOfMammon.class,
		CastleAmbassador.class,
		CastleBlacksmith.class,
		CastleChamberlain.class,
		CastleCourtMagician.class,
		CastleMercenaryManager.class,
		CastleSiegeManager.class,
		CastleTeleporter.class,
		CastleWarehouse.class,
		ClanTrader.class,
		Dorian.class,
		// DragonVortex.class,
		DragonVortexRetail.class,
		EkimusMouth.class,
		FameManager.class,
		ForgeOfTheGods.class,
		Rooney.class,
		TarBeetle.class,
		FortressArcherCaptain.class,
		FortressSiegeManager.class,
		FreyasSteward.class,
		Jinia.class,
		Katenar.class,
		KetraOrcSupport.class,
		ManorManager.class,
		MercenaryCaptain.class,
		Minigame.class,
		MonumentOfHeroes.class,
		NevitsHerald.class,
		NpcBuffers.class,
		CabaleBuffer.class,
		PcBangPoint.class,
		PriestOfBlessing.class,
		Rafforty.class,
		Rignos.class,
		Selina.class,
		Sirra.class,
		GolemTrader.class,
		SupportUnitCaptain.class,
		SymbolMaker.class,
		TerritoryManagers.class,
		TownPets.class,
		HealerTrainer.class,
		Tunatun.class,
		VarkaSilenosSupport.class,
		WeaverOlf.class,
		WyvernManager.class,
	};
	
	public NpcLoader()
	{
		_log.info(NpcLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : NPCs)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(NpcLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
