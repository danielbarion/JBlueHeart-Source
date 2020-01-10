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
package custom.AutoDeliveryItems;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import java.util.logging.Logger;

/**
 * @author PaiPlayer based on AutoReward from Debian @ L2jServer Forums
 *
 */
public class AutoDeliveryItems
{

	protected static final Logger _log = Logger.getLogger(AutoDeliveryItems.class.getName());

	static public void main(String[] args)
	{
		_log.info("AutoDelivering System Enabled");
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				AutoDeliveryItems();
			}

			private void AutoDeliveryItems()
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()){
					// GETTING THE ITEMS DELAYED
					PreparedStatement rss = con.prepareStatement("SELECT * FROM items_delayed WHERE delivered<1");
					ResultSet action = rss.executeQuery();

					for (L2PcInstance player : L2World.getInstance().getPlayers())
					{
						int PlayerObjectId = player.getObjectId();
						while (action.next())
						{
							if(PlayerObjectId==action.getInt("charId")){
								PreparedStatement rssb = con.prepareStatement("UPDATE items_delayed SET delivered=1 WHERE id=?");
								rssb.setInt(1, action.getInt("id"));
								rssb.executeUpdate();
								_log.info("=== ITEMS DELAYED SYSTEM: Player [ "+player.getName()+" ] received "+action.getInt("itemId")+" x"+action.getInt("itemCount"));
								player.addItem("IDS ", action.getInt("itemId"), action.getInt("itemCount"), player, true);
							}
						}

					}


				}
				catch (SQLException e)
				{
					print(e);
				}
			}
		}, 0, 120 * 1000);
	}

	private static void print(Exception e)
	{
		e.printStackTrace();
	}

}