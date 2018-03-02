package gr.sr.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class PacketsDebugger
{
	protected static final Logger _log = LoggerFactory.getLogger(PacketsDebugger.class);
	
	private final static boolean debugPacket = Config.DEBUG_PACKETS_COUNT;
	private final static boolean detailedDebugPackets = Config.DEBUG_PACKETS_NAMES;
	private final static int refreshTime = Config.DEBUG_PACKETS_INTERVAL;
	private static boolean started = false;
	private static int sendPacketTrace = 0;
	private static int maxPacketsInList = 200;
	private final static List<String> packetName = new ArrayList<>();
	private static ScheduledFuture<?> _packetsTask = null;
	
	public static void checkDebugger(L2GameServerPacket gsp)
	{
		if (debugPacket)
		{
			sendPacketTrace++;
			if (detailedDebugPackets && (packetName.size() < maxPacketsInList))
			{
				packetName.add(gsp.toString());
			}
			
			if (!started)
			{
				sendPacketsInfo(true, false);
			}
		}
		else if (!debugPacket && started)
		{
			sendPacketsInfo(false, false);
		}
	}
	
	private static void sendPacketsInfo(boolean firstStart, boolean stop)
	{
		if (stop)
		{
			if (_packetsTask != null)
			{
				_packetsTask.cancel(true);
				_packetsTask = null;
			}
			_log.info(PacketsDebugger.class.getSimpleName() + ": Packets debugging stopped.");
			return;
		}
		
		if (!firstStart)
		{
			if (detailedDebugPackets)
			{
				for (int i = 0; i < packetName.size(); i++)
				{
					try
					{
						_log.info(PacketsDebugger.class.getSimpleName() + ": Packet Name sent: " + packetName.get(i).replace("l2r.gameserver.network.serverpackets", ""));
					}
					catch (Exception e)
					{
						packetName.clear();
						startTask();
					}
				}
				packetName.clear();
			}
			
			_log.info(PacketsDebugger.class.getSimpleName() + ": Packets sent is this session: " + sendPacketTrace);
			sendPacketTrace = 0;
		}
		else
		{
			started = true;
			_log.info(PacketsDebugger.class.getSimpleName() + ": Packets debugging started.");
			startTask();
		}
	}
	
	private static void startTask()
	{
		if (_packetsTask != null)
		{
			_packetsTask.cancel(true);
			_packetsTask = null;
		}
		
		_packetsTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> sendPacketsInfo(false, false), 1000, refreshTime * 1000);
	}
}
