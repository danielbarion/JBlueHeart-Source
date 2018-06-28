package handlers.voicedcommandhandlers;

import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import l2r.Config;

/**
 * @author Barion
 */
public class ExpVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"expon",
		"expoff",
		"exp",
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		switch (command)
		{
			case "exp":
				if (Config.EXP_SYSTEM_ENABLED)
				{
					if (!activeChar.getVarB("noExp"))
					{
						activeChar.setVar("noExp", "true");
						activeChar.sendMessage("Experience gain enabled.");
					}
					else
					{
						activeChar.setVar("noExp", "false");
						activeChar.sendMessage("Experience gain disabled.");
					}
				}
				else
				{
					activeChar.sendMessage("Experience command disabled by a gm.");
				}
				break;

			case "expon":
				if (Config.EXP_SYSTEM_ENABLED)
				{
					activeChar.setVar("noExp", "false");
					activeChar.sendMessage("Experience gain enabled.");
				}
				else
				{
					activeChar.sendMessage("Experience command disabled by a gm.");
				}
				break;

			case "expoff":
				if (Config.EXP_SYSTEM_ENABLED)
				{
					activeChar.setVar("noExp", "true");
					activeChar.sendMessage("Experience gain disabled.");
				}
				else
				{
					activeChar.sendMessage("Experience command disabled by a gm.");
				}
				break;
		}

		return true;

	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}