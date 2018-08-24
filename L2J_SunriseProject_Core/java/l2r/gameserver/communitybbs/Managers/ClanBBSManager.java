/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.communitybbs.Managers;

import java.util.StringTokenizer;

import l2r.gameserver.communitybbs.BoardsManager;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.util.StringUtil;

public class ClanBBSManager extends BaseBBSManager
{
	/**
	 * @param command
	 * @param activeChar
	 */
	@Override
	public void cbByPass(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbsclan"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan", command);
			
			if ((activeChar.getClan() == null) || (activeChar.getClan().getLevel() < 2))
			{
				clanlist(activeChar, 1);
			}
			else
			{
				clanhome(activeChar);
			}
		}
		else if (command.startsWith("_bbsclan_clanlist"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan List", command);
			
			if (command.equals("_bbsclan_clanlist"))
			{
				clanlist(activeChar, 1);
			}
			else if (command.startsWith("_bbsclan_clanlist;"))
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				int index = Integer.parseInt(st.nextToken());
				clanlist(activeChar, index);
			}
		}
		else if (command.startsWith("_bbsclan_clanhome"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan Home", command);
			
			if (command.equals("_bbsclan_clanhome"))
			{
				clanhome(activeChar);
			}
			else if (command.startsWith("_bbsclan_clanhome;"))
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				int index = Integer.parseInt(st.nextToken());
				clanhome(activeChar, index);
			}
		}
		else if (command.startsWith("_bbsclan_clannotice_edit;"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan Edit", command);
			clanNotice(activeChar, activeChar.getClanId());
		}
		else if (command.startsWith("_bbsclan_clannotice_enable"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan Notice Enable", command);
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().setNoticeEnabled(true);
			}
			clanNotice(activeChar, activeChar.getClanId());
		}
		else if (command.startsWith("_bbsclan_clannotice_disable"))
		{
			BoardsManager.getInstance().addBypass(activeChar, "Clan Notice Disable", command);
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().setNoticeEnabled(false);
			}
			clanNotice(activeChar, activeChar.getClanId());
		}
		else
		{
			separateAndSend("<html><body><br><br><center>Command : " + command + " needs core development</center><br><br></body></html>", activeChar);
		}
	}
	
	private void clanNotice(L2PcInstance activeChar, int clanId)
	{
		final L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if (cl != null)
		{
			if (cl.getLevel() < 2)
			{
				activeChar.sendPacket(SystemMessageId.NO_CB_IN_MY_CLAN);
				cbByPass("_bbsclan_clanlist", activeChar);
			}
			else
			{
				final StringBuilder html = StringUtil.startAppend(2000, "<html><body><br><br><br><center><table width=\"742\" cellpadding=\"0\" cellspacing=\"0\" background=\"L2UI_CT1.Windows_DF_Drawer_Bg_Darker\"><tr><td height=10></td></tr><tr><td align=center width=\"700\" valign=\"top\"><table width=\"680\"  height=\"60\" cellspacing=\"0\" cellpadding=\"7\"><tr><td width=\"32\" valign\"top\"><img src=\"L2UI_CT1.PVP_DF_TriggerBtn_Over\" width=\"32\" height=\"32\" align=\"top\" /></td><td width=\"545\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"26\" valign=\"top\"><font color=\"aa9977\">Clan notice</font></td></tr><tr><td>This function allows clan leader to send messages through a pop-up window to clan members at login!</td></tr></table></td><td width=\"128\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td><button value=\"Back\" action=\"bypass _bbsclan_clanhome;", String.valueOf((activeChar.getClan() != null) ? activeChar.getClan().getId() : 0), "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr></table><br><br>");
				if (activeChar.isClanLeader())
				{
					if (activeChar.getClan().isNoticeEnabled())
					{
						StringUtil.append(html, "<center><table><tr><td height=7></td></tr><tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;on&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan_clannotice_disable\">off</a>");
					}
					else
					{
						StringUtil.append(html, "<center><table><tr><td height=7></td></tr><tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan_clannotice_enable\">on</a>&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;off");
					}
					
					StringUtil.append(html, "</td></tr></table><br><br><table width=610 border=0 cellspacing=2 cellpadding=0><tr><td>Edit Notice: </td></tr><tr><td height=5></td></tr><tr><td><MultiEdit var =\"Content\" width=610 height=100></td></tr></table><br><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td height=5></td></tr><tr><td align=center FIXWIDTH=65><button value=\"Save\" action=\"Write Notice Set _ Content Content Content\" width=65 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td align=center FIXWIDTH=45></td><td align=center FIXWIDTH=500></td></tr></table><br><br><table border=0 cellspacing=0 cellpadding=0><tr><td width=755 height=20></td></tr></table><br><br></td></tr></table><br><table width=640><tr><td align=center>Community Board | <font color=aa9977>L2 Pegasus</font> 2013</td></tr></table><br></center></body></html></center></body></html>");
					send1001(html.toString(), activeChar);
					send1002(activeChar, activeChar.getClan().getNotice(), " ", "0");
				}
				else
				{
					StringUtil.append(html, "<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td>You are not your clan's leader, and therefore you cannot change the clan notice</td></tr></table>");
					if (activeChar.getClan().isNoticeEnabled())
					{
						StringUtil.append(html, "<br><br><table table border=0 background=\"L2UI_CT1.Windows_DF_Drawer_Bg_Darker\" cellspacing=0 cellpadding=0 width=540><tr><td height=20></td></tr><tr><td align=center><font color=\"aa9977\">The current clan notice:</font></td></tr><tr><td height=28></td></tr><tr><td align=center>" + activeChar.getClan().getNotice() + "</td><td fixqqwidth=5></td></tr><tr><td height=20></td></tr></table>");
					}
					StringUtil.append(html, "<br><br></td></tr></table>	<br><table width=640><tr><td align=center>Community Board | <font color=aa9977>L2 Pegasus</font> 2013</td></tr></table><br></center></body></html>");
					separateAndSend(html.toString(), activeChar);
				}
			}
		}
	}
	
	/**
	 * @param activeChar
	 * @param index
	 */
	private void clanlist(L2PcInstance activeChar, int index)
	{
		if (index < 1)
		{
			index = 1;
		}
		
		// header
		final StringBuilder html = StringUtil.startAppend(2000, "<html><body><br><br><center><table width=\"720\" background=\"L2UI_CT1.Windows_DF_Drawer_Bg_Darker\"><tr><td height=10></td></tr><tr><td align=center width=\"720\" valign=\"top\"><table width=\"700\"  height=\"60\" cellspacing=\"0\" cellpadding=\"7\"><tr><td width=\"32\" valign=\"top\"><img src=\"L2UI_CT1.PVP_DF_TriggerBtn_Over\" width=\"32\" height=\"32\" align=\"top\" /></td><td width=\"545\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"26\" valign=\"top\"><font color=\"aa9977\">Search clan manager</font></td></tr><tr><td>Here you can find all available clans and usefull information about them. No more searching! </td></tr></table></td><td width=\"128\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"18\"></td></tr><tr><td><button value=\"My clan\" action=\"bypass _bbsclan_clanhome;", String.valueOf((activeChar.getClan() != null) ? activeChar.getClan().getId() : 0), "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr></table><table border=0 cellspacing=0 cellpadding=2 bgcolor=A7A19A width=710><tr><td FIXWIDTH=315></td><td WIDTH=360>Clan Name</td><td FIXWIDTH=70></td><td WIDTH=360>Clan Leader</td><td FIXWIDTH=30></td><td WIDTH=215>Clan Level</td><td FIXWIDTH=1></td><td WIDTH=360>Clan Members</td></tr></table>");
		
		int i = 0;
		for (L2Clan cl : ClanTable.getInstance().getClans())
		{
			if (i > ((index + 1) * 7))
			{
				break;
			}
			
			if (i++ >= ((index - 1) * 7))
			{
				StringUtil.append(html, "<img src=\"L2UI.SquareBlank\" width=\"680\" height=\"3\"><table border=0 cellspacing=0 cellpadding=0 width=610><tr> <td FIXWIDTH=5></td><td FIXWIDTH=200 align=center><a action=\"bypass _bbsclan_clanhome;", String.valueOf(cl.getId()), "\">", cl.getName(), "</a></td><td FIXWIDTH=200 align=center>", cl.getLeaderName(), "</td><td FIXWIDTH=100 align=center>", String.valueOf(cl.getLevel()), "</td><td FIXWIDTH=100 align=center>", String.valueOf(cl.getMembersCount()), "</td><td FIXWIDTH=5></td></tr><tr><td height=5></td></tr></table><img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
			}
		}
		
		html.append("<img src=\"L2UI.SquareBlank\" width=\"680\" height=\"2\"><table cellpadding=0 cellspacing=2 border=0><tr>");
		
		if (index == 1)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		else
		{
			StringUtil.append(html, "<td><button action=\"_bbsclan_clanlist;", String.valueOf(index - 1), "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		
		i = 0;
		int nbp = ClanTable.getInstance().getClanCount() / 8;
		if ((nbp * 8) != ClanTable.getInstance().getClanCount())
		{
			nbp++;
		}
		for (i = 1; i <= nbp; i++)
		{
			if (i == index)
			{
				StringUtil.append(html, "<td> ", String.valueOf(i), " </td>");
			}
			else
			{
				StringUtil.append(html, "<td><a action=\"bypass _bbsclan_clanlist;", String.valueOf(i), "\"> ", String.valueOf(i), " </a></td>");
			}
			
		}
		if (index == nbp)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		else
		{
			StringUtil.append(html, "<td><button action=\"bypass _bbsclan_clanlist;", String.valueOf(index + 1), "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		html.append("</tr></table><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui141\" width=\"610\" height=\"1\"></td></tr></table><table border=0><tr><td><combobox width=65 var=keyword list=\"Name; \"></td><td><edit var = \"Search\" width=130 height=11 length=\"16\"></td>" +
		// TODO: search (Write in BBS)
		"<td><button value=\"&$420;\" action=\"Write 5 -1 0 Search keyword keyword\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\"> </td> </tr></table><br><br></td></tr></table><br><table width=640><tr><td align=center>Community Board | <font color=aa9977>L2 Pegasus</font> 2013</td></tr></table><br></center></body></html>");
		separateAndSend(html.toString(), activeChar);
	}
	
	/**
	 * @param activeChar
	 */
	private void clanhome(L2PcInstance activeChar)
	{
		clanhome(activeChar, activeChar.getClan().getId());
	}
	
	/**
	 * @param activeChar
	 * @param clanId
	 */
	private void clanhome(L2PcInstance activeChar, int clanId)
	{
		L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if (cl != null)
		{
			if (cl.getLevel() < 2)
			{
				activeChar.sendPacket(SystemMessageId.NO_CB_IN_MY_CLAN);
				cbByPass("_bbsclan_clanlist", activeChar);
			}
			else
			{
				final String html = StringUtil.concat("<html><body><br><br><br><center><table width=\"742\" cellpadding=\"0\" cellspacing=\"0\" background=\"L2UI_CT1.Windows_DF_Drawer_Bg_Darker\"><tr><td height=10></td></tr><tr><td align=center width=\"700\" valign=\"top\"><table width=\"680\"  height=\"60\" cellspacing=\"0\" cellpadding=\"7\"><tr><td width=\"32\" valign=\"top\"><img src=\"L2UI_CT1.PVP_DF_TriggerBtn_Over\" width=\"32\" height=\"32\" align=\"top\" /></td><td width=\"545\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"26\" valign=\"top\"><font color=\"aa9977\">Your clan</font></td></tr><tr><td>Here you can find usefull information about your clan. No more searching! </td></tr></table></td><td width=\"128\" valign=\"top\"><table cellspacing=\"0\" cellpadding=\"0\"><tr><td><button value=\"Search for clan\" action=\"bypass _bbsclan_clanlist\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr><tr><td><button value=\"Clan Notice\" action=\"bypass _bbsclan_clannotice_edit;", String.valueOf(clanId), ";cnotice\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr></table><br><br><table border=0 background=\"L2UI_CH3.refinewnd_back_Pattern\" cellspacing=0 cellpadding=0 width=610><tr><td height=40></td><</tr><tr><td valign=\"top\" align=\"center\"><table border=0 cellspacing=0 cellpadding=0 width=295><tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Clan Name:</font></td><td fixWIDTH=195 align=center>", cl.getName(), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Clan Level:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getLevel()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Clan Members:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getMembersCount()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Online Players</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getOnlineMembersCount()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Clan Leader:</font></td><td fixWIDTH=195 align=center height=16>", cl.getLeaderName(), "</td></tr><tr><td height=7></td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Alliance:</font></td><td fixWIDTH=195 align=center height=16>", (cl.getAllyName() != null) ? cl.getAllyName() : "N/A", "</td></tr></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Siege kills:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getSiegeKills()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Siege deaths:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getSiegeDeaths()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">Reputation:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.getReputationScore()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left><font color=\"aa9977\">At War:</font></td><td fixWIDTH=195 align=center height=16>", String.valueOf(cl.isAtWar()), "</td></tr></table></td></tr><tr><td height=40></td><</tr></table><br><br><table border=0 cellspacing=0 cellpadding=0><tr><td width=755 height=20></td></tr></table><br><br></td></tr></table><br><table width=640><tr><td align=center>Community Board | <font color=aa9977>L2 Pegasus</font> 2013</td></tr></table><br></center></body></html>");
				separateAndSend(html, activeChar);
			}
		}
	}
	
	@Override
	public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		if (ar1.equals("Set"))
		{
			activeChar.getClan().setNotice(ar4);
			cbByPass("_bbsclan_clanhome;" + activeChar.getClan().getId(), activeChar);
		}
	}
	
	public static ClanBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanBBSManager _instance = new ClanBBSManager();
	}
}