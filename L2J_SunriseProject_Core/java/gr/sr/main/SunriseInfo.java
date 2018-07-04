//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.main;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SunriseInfo {
	private static final Logger _log = LoggerFactory.getLogger(SunriseInfo.class);
	private static final String _coreRev = "842 rev";
	private static final String _datapackRev = "760 rev";
	private static final String _chronicle = "High Five Part 5 (CT2.6)";
	private static final String _sunriseRev = "Final Edition";
	private static final String _website = "www.L2jSunrise.com";
	private static final String _copyRights = "Sunrise-Team 2015-2017";
	private static final String _owners = "vNeverMore";
	private static final String _devs = "vGodFather";
	private static final String _websiteDev = "vNeverMore";

	public SunriseInfo() {
	}

	public static void load() {
		_log.info("=====================================================");
		_log.info("Copyrights: .............: Sunrise-Team 2015-2017");
		_log.info("Project Owners: .........: vNeverMore");
		_log.info("Developers: .............: vGodFather");
		_log.info("Website: ................: www.L2jSunrise.com");
		_log.info("Website Owner-Dev: ......: vNeverMore");
		_log.info("Chronicle: ..............: High Five Part 5 (CT2.6)");
		_log.info("Sunrise Revision: .......: Final Edition");
		_log.info("Core Revision: ..........: 842 rev");
		_log.info("Data Revision: ..........: 760 rev");
		_log.info("BlueHeart Developer: ....: Barion");
		_log.info("BlueHeart Version: ......: 0.0.1 rev");
		printMemUsage();
		_log.info("=====================================================");
	}

	public static void printMemUsage() {
		String[] var0 = getMemoryUsageStatistics();
		int var1 = var0.length;

		for(int var2 = 0; var2 < var1; ++var2) {
			String line = var0[var2];
			_log.info(line);
		}

	}

	private static String[] getMemoryUsageStatistics() {
		double max = (double)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
		double allocated = (double)(Runtime.getRuntime().totalMemory() / 1024L / 1024L);
		double nonAllocated = max - allocated;
		double cached = (double)(Runtime.getRuntime().freeMemory() / 1024L / 1024L);
		double used = allocated - cached;
		double useable = max - used;
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
		DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		DecimalFormat df2 = new DecimalFormat(" # 'MB'");
		return new String[]{"+----", "| Global Memory Informations at " + sdf.format(new Date()) + ":", "|    |", "| Allowed Memory:" + df2.format(max), "|    |= Allocated Memory:" + df2.format(allocated) + df.format(allocated / max * 100.0D), "|    |= Non-Allocated Memory:" + df2.format(nonAllocated) + df.format(nonAllocated / max * 100.0D), "| Allocated Memory:" + df2.format(allocated), "|    |= Used Memory:" + df2.format(used) + df.format(used / max * 100.0D), "|    |= Unused (cached) Memory:" + df2.format(cached) + df.format(cached / max * 100.0D), "| Useable Memory:" + df2.format(useable) + df.format(useable / max * 100.0D), "+----"};
	}
}
