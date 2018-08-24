package l2r.gameserver.communitybbs.SunriseBoards;

import gr.sr.configsEngine.configs.impl.SmartCommunityConfigs;
import gr.sr.dataHolder.PlayersTopData;
import gr.sr.main.TopListsLoader;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class TopOnlinePlayers extends AbstractSunriseBoards
{
	private int _counter = 1;
	private final StringBuilder _list = new StringBuilder();
	
	@Override
	public void load()
	{
		_list.setLength(0);
		_counter = 1;
		
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopOnlineTime())
		{
			if (_counter <= SmartCommunityConfigs.TOP_PLAYER_RESULTS)
			{
				addChar(playerData.getCharName(), playerData.getClanName(), getPlayerRunTime(playerData.getOnlineTime()));
				_counter++;
			}
		}
	}
	
	private void addChar(String name, String cname, String onTime)
	{
		_list.append("<tr>");
		_list.append("<td valign=\"top\" align=\"center\">" + _counter + "</td");
		_list.append("<td valign=\"top\" align=\"center\">" + name + "</td");
		_list.append("<td valign=\"top\" align=\"center\">" + cname + "</td>");
		_list.append("<td valign=\"top\" align=\"center\">" + onTime + "</td>");
		_list.append("</tr>");
	}
	
	public String getPlayerRunTime(int secs)
	{
		String timeResult = "";
		if (secs >= 86400)
		{
			timeResult = Integer.toString(secs / 86400) + " Days " + Integer.toString((secs % 86400) / 3600) + " hours";
		}
		else
		{
			timeResult = Integer.toString(secs / 3600) + " Hours " + Integer.toString((secs % 3600) / 60) + " mins";
		}
		return timeResult;
	}
	
	@Override
	public String getList()
	{
		return _list.toString();
	}
	
	public static TopOnlinePlayers getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TopOnlinePlayers _instance = new TopOnlinePlayers();
	}
}