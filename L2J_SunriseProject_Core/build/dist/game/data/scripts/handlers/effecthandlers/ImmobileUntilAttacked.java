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
package handlers.effecthandlers;

import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;

/**
 * ImmobileUntilAttacked effect implementation.
 * @author vGodFather
 */
public class ImmobileUntilAttacked extends L2Effect
{
	public ImmobileUntilAttacked(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BUFF;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected().isPlayer())
		{
			getEffected().setIsImmobilized(false);
			getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().abortAttack();
		getEffected().abortCast();
		getEffected().stopMove(null);
		getEffected().setIsImmobilized(true);
		getEffected().getAI().notifyEvent(CtrlEvent.EVT_SLEEPING);
		return super.onStart();
	}
}
