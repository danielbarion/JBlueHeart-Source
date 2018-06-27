package handlers.voicedcommandhandlers;

import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import gr.sr.configsEngine.configs.impl.CustomServerConfigs;

/**
 * @author Barion
 */
public class ExpVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"expon",
		"expoff",
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		switch (command)
		{
			case "expon":
//				if (CustomServerConfigs.ALLOW_EXP_GAIN_COMMAND)
//				{
					activeChar.setVar("noExp", "false");
					activeChar.sendMessage("Experience gain enabled.");
//				}
//				else
//				{
//					activeChar.sendMessage("Experience command disabled by a gm.");
//				}
				break;

			case "expoff":
//				if (CustomServerConfigs.ALLOW_EXP_GAIN_COMMAND)
//				{
					activeChar.setVar("noExp", "true");
					activeChar.sendMessage("Experience gain disabled.");
//				}
//				else
//				{
//					activeChar.sendMessage("Experience command disabled by a gm.");
//				}
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