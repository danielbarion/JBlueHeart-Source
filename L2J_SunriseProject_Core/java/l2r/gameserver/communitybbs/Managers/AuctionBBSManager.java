/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.communitybbs.Managers;

import l2r.Config;
import l2r.features.auctionEngine.managers.AuctionHouseManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * Manager of Community Server custom his is to put all of the customs system bypasses without touching the other elemental
 * @author vGodFather
 */
public class AuctionBBSManager extends BaseBBSManager
{
	public String BBS_COMMAND = "_bbslink";
	
	@Override
	public void cbByPass(String command, L2PcInstance activeChar)
	{
		// Jailed players can not use Elemental BBs Manager
		if (activeChar.isJailed())
		{
			return;
		}
		
		// Processes the bypass for the auction house
		if (command.startsWith(BBS_COMMAND))
		{
			// You can only access the auction house from a peace zone (bypassed from gms)
			if (Config.AUCTION_HOUSE_ONLY_PEACE_ZONE && !activeChar.isGM() && !activeChar.isInsideZone(ZoneIdType.PEACE))
			{
				activeChar.sendMessage("The Auction House System cannot be used outside peace zone");
				return;
			}
			
			final String content = AuctionHouseManager.getInstance().processBypass(activeChar, command);
			
			if (command.contains(";search "))
			{
				String searchInput = command.substring(command.indexOf(";search ") + 8);
				if (searchInput.contains(";"))
				{
					searchInput = searchInput.substring(0, searchInput.indexOf(";"));
				}
				
				send1001(content, activeChar);
				send1002(activeChar, "", searchInput, "");
				return;
			}
			separateAndSend(content, activeChar);
		}
	}
	
	@Override
	public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		
	}
	
	public static AuctionBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionBBSManager _instance = new AuctionBBSManager();
	}
}
