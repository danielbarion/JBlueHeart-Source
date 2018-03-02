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

import instances.CavernOfThePirateCaptain.CavernOfThePirateCaptain;
import instances.ChambersOfDelusion.ChamberOfDelusionEast;
import instances.ChambersOfDelusion.ChamberOfDelusionNorth;
import instances.ChambersOfDelusion.ChamberOfDelusionSouth;
import instances.ChambersOfDelusion.ChamberOfDelusionSquare;
import instances.ChambersOfDelusion.ChamberOfDelusionTower;
import instances.ChambersOfDelusion.ChamberOfDelusionWest;
import instances.CrystalCaverns.CrystalCaverns;
import instances.DarkCloudMansion.DarkCloudMansion;
import instances.DisciplesNecropolisPast.DisciplesNecropolisPast;
import instances.ElcadiaTent.ElcadiaTent;
import instances.FinalEmperialTomb.FinalEmperialTomb;
import instances.HideoutOfTheDawn.HideoutOfTheDawn;
import instances.IceQueensCastle.IceQueensCastle;
import instances.IceQueensCastleNormalBattle.IceQueensCastleNormalBattle;
import instances.IceQueensCastleUltimateBattle.IceQueensCastleUltimateBattle;
import instances.JiniaGuildHideout1.JiniaGuildHideout1;
import instances.JiniaGuildHideout2.JiniaGuildHideout2;
import instances.JiniaGuildHideout3.JiniaGuildHideout3;
import instances.JiniaGuildHideout4.JiniaGuildHideout4;
import instances.Kamaloka.Kamaloka;
import instances.LibraryOfSages.LibraryOfSages;
import instances.MithrilMine.MithrilMine;
import instances.NornilsGarden.NornilsGarden;
import instances.NornilsGardenQuest.NornilsGardenQuest;
import instances.PailakaDevilsLegacy.PailakaDevilsLegacy;
import instances.PailakaInjuredDragon.PailakaInjuredDragon;
import instances.PailakaSongOfIceAndFire.PailakaSongOfIceAndFire;
import instances.RimKamaloka.RimKamaloka;
import instances.SanctumOftheLordsOfDawn.SanctumOftheLordsOfDawn;
import instances.SecretAreaKeucereus.SecretAreaKeucereus;
import instances.ToTheMonastery.ToTheMonastery;
import instances.Zaken.Zaken;

/**
 * @author Nos
 */
public class InstanceLoader
{
	private static final Logger _log = LoggerFactory.getLogger(InstanceLoader.class);
	
	private static final Class<?>[] INSTANCES =
	{
		CavernOfThePirateCaptain.class,
		ChamberOfDelusionEast.class,
		ChamberOfDelusionNorth.class,
		ChamberOfDelusionSouth.class,
		ChamberOfDelusionSquare.class,
		ChamberOfDelusionTower.class,
		ChamberOfDelusionWest.class,
		CrystalCaverns.class,
		DarkCloudMansion.class,
		DisciplesNecropolisPast.class,
		ElcadiaTent.class,
		FinalEmperialTomb.class,
		HideoutOfTheDawn.class,
		IceQueensCastle.class,
		IceQueensCastleNormalBattle.class,
		IceQueensCastleUltimateBattle.class,
		JiniaGuildHideout1.class,
		JiniaGuildHideout2.class,
		JiniaGuildHideout3.class,
		JiniaGuildHideout4.class,
		Kamaloka.class,
		LibraryOfSages.class,
		MithrilMine.class,
		NornilsGarden.class,
		NornilsGardenQuest.class,
		PailakaDevilsLegacy.class,
		PailakaInjuredDragon.class,
		PailakaSongOfIceAndFire.class,
		RimKamaloka.class,
		SanctumOftheLordsOfDawn.class,
		SecretAreaKeucereus.class,
		ToTheMonastery.class,
		Zaken.class,
	};
	
	public InstanceLoader()
	{
		_log.info(InstanceLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> instance : INSTANCES)
		{
			try
			{
				instance.newInstance();
			}
			catch (Exception e)
			{
				_log.error(InstanceLoader.class.getSimpleName() + ": Failed loading " + instance.getSimpleName() + ":", e);
			}
		}
	}
}
