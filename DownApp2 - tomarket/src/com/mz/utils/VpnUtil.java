package com.mz.utils;

import android.text.TextUtils;

public class VpnUtil {

	public static boolean isVpnConnect() {
		String string = "netstat -anp | grep :1723";
		try {
			Process p = CmdUtil.run(string);
			p.waitFor();
			String out = CmdUtil.readResult(p).toUpperCase();
			if (!TextUtils.isEmpty(out) && out.contains("ESTABLISHED")) {
				return isVpnConnectFromIpRo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean isVpnConnectFromIpRo() {
		try {
			Process p = CmdUtil.run("ip ro");
			p.waitFor();
			String out = CmdUtil.readResult(p);
			if (!TextUtils.isEmpty(out) && out.contains("ppp")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
