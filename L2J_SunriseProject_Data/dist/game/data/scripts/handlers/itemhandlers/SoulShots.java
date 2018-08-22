/*
 * Copyright (C) 2004-2013 L2J DataPack
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
package handlers.itemhandlers;

import l2r.gameserver.enums.ShotType;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ActionType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExAutoSoulShot;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Broadcast;
import l2r.util.Rnd;

public class SoulShots implements IItemHandler
{
	private static final int MANA_POT_CD = 3,
		HEALING_POT_CD = 11, // DO NOT PUT LESS THAN 10
		CP_POT_CD = 3;

	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		final SkillHolder[] skills = item.getItem().getSkills();
		
		int itemId = item.getId();

		//AutoPotion
		if (itemId == 728 || itemId == 1539 || itemId == 5592)
		{
			switch (itemId)
			{
				case 728: // mana potion
				{
					if (activeChar.isAutoPot(728))
					{
						activeChar.sendPacket(new ExAutoSoulShot(728, 0));
						activeChar.sendMessage("Deactivated auto mana potions.");
						activeChar.setAutoPot(728, null, false);
					}
					else
					{
						if (activeChar.getInventory().getItemByItemId(728) != null)
						{
							if (activeChar.getInventory().getItemByItemId(728).getCount() > 1)
							{
								activeChar.sendPacket(new ExAutoSoulShot(728, 1));
								activeChar.sendMessage("Activated auto mana potions.");
								activeChar.setAutoPot(728, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(728, activeChar), 1000, MANA_POT_CD*1000), true);
							}
							else
							{
								MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2279, 2, 0, 100);
								activeChar.broadcastPacket(msu);

								ItemSkills is = new ItemSkills();
								is.useItem(activeChar, activeChar.getInventory().getItemByItemId(728), true);
							}
						}
					}

					break;
				}
				case 1539: // greater healing potion
				{
					if (activeChar.isAutoPot(1539))
					{
						activeChar.sendPacket(new ExAutoSoulShot(1539, 0));
						activeChar.sendMessage("Deactivated auto healing potions.");
						activeChar.setAutoPot(1539, null, false);
					}
					else
					{
						if (activeChar.getInventory().getItemByItemId(1539) != null)
						{
							if (activeChar.getInventory().getItemByItemId(1539).getCount() > 1)
							{
								activeChar.sendPacket(new ExAutoSoulShot(1539, 1));
								activeChar.sendMessage("Activated auto healing potions.");
								activeChar.setAutoPot(1539, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(1539, activeChar), 1000, HEALING_POT_CD*1000), true);
							}
							else
							{
								MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2037, 1, 0, 100);
								activeChar.broadcastPacket(msu);

								ItemSkills is = new ItemSkills();
								is.useItem(activeChar, activeChar.getInventory().getItemByItemId(1539), true);
							}
						}
					}

					break;
				}
				case 5592: // greater cp potion
				{
					if (activeChar.isAutoPot(5592))
					{
						activeChar.sendPacket(new ExAutoSoulShot(5592, 0));
						activeChar.sendMessage("Deactivated auto cp potions.");
						activeChar.setAutoPot(5592, null, false);
					}
					else
					{
						if (activeChar.getInventory().getItemByItemId(5592) != null)
						{
							if (activeChar.getInventory().getItemByItemId(5592).getCount() > 1)
							{
								activeChar.sendPacket(new ExAutoSoulShot(5592, 1));
								activeChar.sendMessage("Activated auto cp potions.");
								activeChar.setAutoPot(5592, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(5592, activeChar), 1000, CP_POT_CD*1000), true);
							}
							else
							{
								MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2166, 2, 0, 100);
								activeChar.broadcastPacket(msu);

								ItemSkills is = new ItemSkills();
								is.useItem(activeChar, activeChar.getInventory().getItemByItemId(5592), true);
							}
						}
					}

					break;
				}
			}

			return;
		}


		if (skills == null)
		{
			_log.warn(getClass().getSimpleName() + ": is missing skills!");
			return false;
		}
		
		// Check if Soul shot can be used
		if ((weaponInst == null) || (weaponItem.getSoulShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
			}
			return false;
		}
		
		boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT) && (weaponInst.getItem().getItemGradeSPlus() == item.getItem().getItemGradeSPlus());
		
		if (!gradeCheck)
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
			}
			return false;
		}
		
		activeChar.soulShotLock.lock();
		try
		{
			// Check if Soul shot is already active
			if (activeChar.isChargedShot(ShotType.SOULSHOTS))
			{
				return false;
			}
			
			// Consume Soul shots if player has enough of them
			int SSCount = weaponItem.getSoulShotCount();
			if ((weaponItem.getReducedSoulShot() > 0) && (Rnd.get(100) < weaponItem.getReducedSoulShotChance()))
			{
				SSCount = weaponItem.getReducedSoulShot();
			}
			
			if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false))
			{
				if (!activeChar.disableAutoShot(itemId))
				{
					activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
				}
				return false;
			}
			// Charge soul shot
			weaponInst.setChargedShot(ShotType.SOULSHOTS, true);
		}
		finally
		{
			activeChar.soulShotLock.unlock();
		}
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_S1_);
		sm.addItemName(itemId);
		activeChar.sendPacket(sm);
		
		activeChar.sendPacket(SystemMessageId.ENABLED_SOULSHOT);
		if (!activeChar.getVarB("hideSSAnime"))
		{
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, skills[0].getSkillId(), skills[0].getSkillLvl(), 0, 0), 600);
		}
		return true;
	}


	private class AutoPot implements Runnable
	{
		private int id;
		private L2PcInstance activeChar;

		public AutoPot(int id, L2PcInstance activeChar)
		{
			this.id = id;
			this.activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if (activeChar.getInventory().getItemByItemId(id) == null)
			{
				activeChar.sendPacket(new ExAutoSoulShot(id, 0));
				activeChar.setAutoPot(id, null, false);
				return;
			}

			switch (id)
			{
				case 728:
				{
					if (activeChar.getCurrentMp() < 0.70*activeChar.getMaxMp())
					{
						MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2279, 2, 0, 100);
						activeChar.broadcastPacket(msu);

						ItemSkills is = new ItemSkills();
						is.useItem(activeChar, activeChar.getInventory().getItemByItemId(728), true);
					}

					break;
				}
				case 1539:
				{
					if (activeChar.getCurrentHp() < 0.95*activeChar.getMaxHp())
					{
						MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2037, 1, 0, 100);
						activeChar.broadcastPacket(msu);

						ItemSkills is = new ItemSkills();
						is.useItem(activeChar, activeChar.getInventory().getItemByItemId(1539), true);
					}

					break;
				}
				case 5592:
				{
					if (activeChar.getCurrentCp() < 0.95*activeChar.getMaxCp())
					{
						MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2166, 2, 0, 100);
						activeChar.broadcastPacket(msu);

						ItemSkills is = new ItemSkills();
						is.useItem(activeChar, activeChar.getInventory().getItemByItemId(5592), true);
					}

					break;
				}
			}

			if (activeChar.getInventory().getItemByItemId(id) == null)
			{
				activeChar.sendPacket(new ExAutoSoulShot(id, 0));
				activeChar.setAutoPot(id, null, false);
			}
		}
	}
}
