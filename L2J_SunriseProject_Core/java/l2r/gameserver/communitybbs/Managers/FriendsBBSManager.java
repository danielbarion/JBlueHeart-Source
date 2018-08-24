package l2r.gameserver.communitybbs.Managers;

import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.model.BlockList;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.FriendAddRequest;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SystemMessage;
import gr.sr.utils.Tools;

public class FriendsBBSManager extends BaseBBSManager
{
	@Override
	public void cbByPass(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("_bbs_friends:invite"))
		{
			String[] cm = command.split(" ");
			try
			{
				if ((cm[1] != null) && (cm[1].length() < 16))
				{
					TryFriendInvite(activeChar, cm[1]);
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Friend invite box cannot be empty");
			}
		}
		else if (command.startsWith("_bbs_friends:go"))
		{
			String[] cm = command.split(" ");
			if (cm[1].length() < 2)
			{
				pagr(activeChar, Integer.parseInt(cm[1]));
			}
		}
		else if (command.startsWith("_bbs_friends:block"))
		{
			String[] cm = command.split(" ");
			
			try
			{
				if (cm[1].length() < 16)
				{
					final int targetId = CharNameTable.getInstance().getIdByName(cm[1]);
					
					if (BlockList.isBlocked(activeChar, targetId))
					{
						removeFromBlockList(activeChar, targetId, Integer.parseInt(cm[2]));
					}
					else
					{
						addToBlockList(activeChar, targetId, 1);
					}
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Block player box cannot be empty");
			}
		}
		else if (command.startsWith("_bbs_friends:remove"))
		{
			String[] cm = command.split(" ");
			try
			{
				if (cm[1].length() < 16)
				{
					removeFriend(activeChar, cm[1], Integer.parseInt(cm[2]));
				}
			}
			catch (Exception e)
			{
				
			}
		}
		else
		{
			pagr(activeChar, 1);
		}
	}
	
	public void pagr(L2PcInstance pl, int page)
	{
		if (pl == null)
		{
			return;
		}
		
		String html = getHtml("data/html/CommunityBoard/friends/friends.htm");
		int friendforvisual = 0;
		int blockforvisual = 0;
		int all = 0;
		boolean pagereached = false;
		
		html = html.replaceAll("%fonline%", "" + pl.getOnlineFriendsCount());
		html = html.replaceAll("%fall%", "" + pl.getFriendsCount());
		html = html.replaceAll("%blocked%", "" + pl.getBlockList().getBlockList().size());
		
		int totalpages = 0;
		
		int maxfpages = (int) Math.round((pl.getFriendList().size() / 12.0) + 1);
		int maxbpages = (int) Math.round((pl.getBlockList().getBlockList().size() / 6.0) + 1);
		
		if (maxfpages > maxbpages)
		{
			totalpages = maxfpages;
		}
		else if (maxfpages < maxbpages)
		{
			totalpages = maxbpages;
		}
		else
		{
			totalpages = maxfpages;
		}
		
		if (page == 1)
		{
			html = html.replaceAll("%more%", "<button value=\"\" action=\"bypass _bbs_friends:go " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
			html = html.replaceAll("%back%", "&nbsp;");
		}
		else if (page > 1)
		{
			if (totalpages == page)
			{
				html = html.replaceAll("%back%", "<button value=\"\" action=\"bypass _bbs_friends:go " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
				html = html.replaceAll("%more%", "&nbsp;");
			}
			else
			{
				html = html.replaceAll("%more%", "<button value=\"\" action=\"bypass _bbs_friends:go " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
				html = html.replaceAll("%back%", "<button value=\"\" action=\"bypass _bbs_friends:go " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
			}
		}
		
		if (page <= maxfpages)
		{
			for (int id : pl.getFriendList())
			{
				String friend = CharNameTable.getInstance().getNameById(id);
				
				all++;
				if ((page == 1) && (friendforvisual > 12))
				{
					continue;
				}
				if (!pagereached && (all > (page * 12)))
				{
					continue;
				}
				if (!pagereached && (all <= ((page - 1) * 12)))
				{
					continue;
				}
				friendforvisual++;
				
				html = html.replaceAll("%charName" + friendforvisual + "%", friend);
				html = html.replaceAll("%charImage" + friendforvisual + "%", friend);// FIXME f.getImage());
				
				if (L2World.getInstance().getPlayer(id) != null)
				{
					html = html.replaceAll("%charLoginDate" + friendforvisual + "%", "Friend is <font color=\"00CC33\">Online</font>");
				}
				else
				{
					Long lastaccess = CharNameTable.getInstance().getLastAccessById(id);
					String date = Tools.convertDateToString(lastaccess);
					html = html.replaceAll("%charLoginDate" + friendforvisual + "%", "Friend was online at <font color=\"5b574c\">" + date + "</font>");
				}
				html = html.replaceAll("%charwidth" + friendforvisual + "%", "100");
				html = html.replaceAll("%btn" + friendforvisual + "%", "<button value=\"\" action=\"bypass _bbs_friends:remove " + friend + " " + page + "\" width=32 height=32 back=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red_Over\" fore=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red\">");
			}
		}
		
		if (page <= maxbpages)
		{
			all = 0;
			pagereached = false;
			
			for (Integer id : pl.getBlockList().getBlockList())
			{
				String blocked = CharNameTable.getInstance().getNameById(id);
				
				all++;
				if ((page == 1) && (blockforvisual > 6))
				{
					continue;
				}
				if (!pagereached && (all > (page * 6)))
				{
					continue;
				}
				if (!pagereached && (all <= ((page - 1) * 6)))
				{
					continue;
				}
				blockforvisual++;
				if (blocked != null)
				{
					html = html.replaceAll("%bcharName" + blockforvisual + "%", blocked);
				}
				else
				{
					html = html.replaceAll("%bcharName" + blockforvisual + "%", "N/A");
				}
				
				html = html.replaceAll("%bcharImage" + blockforvisual + "%", "icon.skill4269");
				html = html.replaceAll("%bcharwidth" + blockforvisual + "%", "100");
				html = html.replaceAll("%bchar" + blockforvisual + "%", "Blocked player.");
				if (blocked != null)
				{
					html = html.replaceAll("%bbtn" + blockforvisual + "%", "<button value=\"\" action=\"bypass _bbs_friends:block " + blocked + " " + page + "\" width=32 height=32 back=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red_Over\" fore=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red\">");
				}
				else
				{
					html = html.replaceAll("%bbtn" + blockforvisual + "%", "<button value=\"\" action=\"bypass _bbs_friends:block N/A " + page + "\" width=32 height=32 back=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red_Over\" fore=\"L2UI_CT1.MiniMap_DF_MinusBtn_Red\">");
				}
			}
		}
		
		if (friendforvisual < 12)
		{
			for (int d = friendforvisual + 1; d != 13; d++)
			{
				html = html.replaceAll("%charName" + d + "%", "&nbsp;");
				html = html.replaceAll("%charImage" + d + "%", "L2UI_CH3.multisell_plusicon");
				html = html.replaceAll("%charLoginDate" + d + "%", "&nbsp;");
				html = html.replaceAll("%charwidth" + d + "%", "121");
				html = html.replaceAll("%btn" + d + "%", "&nbsp;");
			}
		}
		if (blockforvisual < 6)
		{
			for (int d = blockforvisual + 1; d != 7; d++)
			{
				html = html.replaceAll("%bcharName" + d + "%", "&nbsp;");
				html = html.replaceAll("%bcharImage" + d + "%", "L2UI_CH3.multisell_plusicon");
				html = html.replaceAll("%bcharwidth" + d + "%", "121");
				html = html.replaceAll("%bchar" + d + "%", "&nbsp;");
				html = html.replaceAll("%bbtn" + d + "%", "&nbsp;");
			}
		}
		separateAndSend(html.toString(), pl);
	}
	
	public void removeFriend(L2PcInstance pl, String name, int page)
	{
		if (pl == null)
		{
			return;
		}
		
		final int targetId = CharNameTable.getInstance().getIdByName(name);
		
		if (pl.getFriendList().contains(targetId))
		{
			pl.removeFriend(name);
			pagr(pl, page);
		}
		else
		{
			pl.sendMessageS("Friend not found.", 4);
		}
	}
	
	public void addToBlockList(L2PcInstance player, int targetId, int page)
	{
		BlockList.addToBlockList(player, targetId);
		pagr(player, page);
	}
	
	public void removeFromBlockList(L2PcInstance activeChar, int targetId, int page)
	{
		BlockList.removeFromBlockList(activeChar, targetId);
		pagr(activeChar, page);
	}
	
	public boolean TryFriendInvite(L2PcInstance activeChar, String addFriend)
	{
		if ((activeChar == null) || (addFriend == null) || addFriend.isEmpty())
		{
			return false;
		}
		if (activeChar.isProcessingTransaction())
		{
			activeChar.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return false;
		}
		if (activeChar.getName().equalsIgnoreCase(addFriend))
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST);
			return false;
		}
		L2PcInstance friendChar = L2World.getInstance().getPlayer(addFriend);
		if (friendChar == null)
		{
			activeChar.sendPacket(SystemMessageId.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
			return false;
		}
		if (friendChar.getBlockList().isInBlockList(activeChar) || friendChar.getMessageRefusal())
		{
			activeChar.sendPacket(SystemMessageId.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			return false;
		}
		if (friendChar.isProcessingTransaction())
		{
			activeChar.sendPacket(SystemMessageId.PLEASE_TRY_AGAIN_LATER);
			return false;
		}
		if (activeChar.getFriendList().contains(addFriend.toLowerCase()))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_ALREADY_ON_FRIEND_LIST).addString(friendChar.getName()));
			return false;
		}
		
		final L2PcInstance friend = L2World.getInstance().getPlayer(addFriend);
		activeChar.onTransactionRequest(friend);
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_REQUESTED_C1_TO_BE_FRIEND);
		sm.addString(addFriend);
		FriendAddRequest ajf = new FriendAddRequest(activeChar.getName());
		friend.sendPacket(ajf);
		activeChar.sendMessageS("Friend invitation has beed sent.", 5);
		return true;
	}
	
	public String getHtml(String path)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		if (!html.setFile(null, path))
		{
			return null;
		}
		
		return html.getHtml();
	}
	
	@Override
	public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		
	}
	
	public static FriendsBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FriendsBBSManager _instance = new FriendsBBSManager();
	}
}