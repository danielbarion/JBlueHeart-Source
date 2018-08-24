package l2r.gameserver.communitybbs.SunriseBoards;

import gr.sr.configsEngine.configs.impl.SmartCommunityConfigs;
import gr.sr.dataHolder.PlayersTopData;
import gr.sr.main.TopListsLoader;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class TopPvpPlayers extends AbstractSunriseBoards
{
	private int _counter = 1;
	private final StringBuilder _list = new StringBuilder();
	
	@Override
	public void load()
	{
		_list.setLength(0);
		_counter = 1;
		
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopPvp())
		{
			if (_counter <= SmartCommunityConfigs.TOP_PLAYER_RESULTS)
			{
				addChar(playerData.getCharName(), playerData.getClanName(), playerData.getPvp());
				_counter++;
			}
		}
	}
	
	private void addChar(String name, String cname, int pvp)
	{
		_list.append("<tr>");
		_list.append("<td valign=\"top\" align=\"center\">" + _counter + "</td");
		_list.append("<td valign=\"top\" align=\"center\">" + name + "</td");
		_list.append("<td valign=\"top\" align=\"center\">" + cname + "</td>");
		_list.append("<td valign=\"top\" align=\"center\">" + pvp + "</td>");
		_list.append("</tr>");
	}
	
	@Override
	public String getList()
	{
		return _list.toString();
	}
	
	public static TopPvpPlayers getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TopPvpPlayers _instance = new TopPvpPlayers();
	}
}