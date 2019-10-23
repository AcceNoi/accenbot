package org.accen.dmzj.util;

import java.util.ArrayList;
import java.util.List;

public class GoogleUtil {
	public static String calculate_token(String text) {

		long b = 406644L;
		Long b1 = 3293161072L;
		String SALT_1 = "+-a^+6";
		String SALT_2 = "+-3^+b+-f";
		String d = text;
		List<Integer> e = new ArrayList<Integer>();
		for (int g = 0; g < d.length(); g++) {
			int m = charCodeAt(d, g);
			if (m < 128) {
				e.add(m); // 0{l[6-0]}
			} else if (m < 2048) {
				e.add(m >> 6 | 192); // 110{l[10-6]}
				e.add(m & 0x3F | 0x80); // 10{l[5-0]}
			} else if (0xD800 == (m & 0xFC00) && g + 1 < d.length() && 0xDC00 == (charCodeAt(d, g + 1) & 0xFC00)) {
				// that's pretty rare... (avoid ovf?)
				m = (byte) ((1 << 16) + ((m & 0x03FF) << 10) + (charCodeAt(d, ++g) & 0x03FF));
				e.add(m >> 18 | 0xF0); // 111100{l[9-8*]}
				e.add(m >> 12 & 0x3F | 0x80); // 10{l[7*-2]}
				e.add(m & 0x3F | 0x80); // 10{(l+1)[5-0]}
			} else {
				e.add(m >> 12 | 0xE0); // 1110{l[15-12]}
				e.add(m >> 6 & 0x3F | 0x80); // 10{l[11-6]}
				e.add(m & 0x3F | 0x80); // 10{l[5-0]}
			}
		}
		Long a1 = b;
		for (int f = 0; f < e.size(); f++) {
			a1 += e.get(f);
			a1 = RL(a1, SALT_1);

		}

		a1 = RL(a1, SALT_2);

		a1 ^= b1;
		if (0 > a1) {
			a1 = (a1 & 2147483647L) + 2147483648L;
		}

		a1 = a1 % 1000000;
		return a1.toString() + "." + (a1 ^ b);
	}

	private static long RL(long a, String seed) {
		for (int i = 0; i < seed.length() - 2; i += 3) {
			char c = seed.toCharArray()[i + 2];
			long d = (c >= 'a') ? ((int) c - 87) : Integer.parseInt(c + "");
			d = (seed.toCharArray()[i + 1] == '+') ? (_rshift(a, d)) : (a << d);
			a = (seed.toCharArray()[i] == '+') ? (a + d & 4294967295L) : (a ^ d);
		}
		return a;
	}

	private static long _rshift(long val, long n) {
		long l = (val >= 0) ? (val >> n) : (val + 0x100000000L) >> n;
		return l;
	}

	private static int charCodeAt(String string, int index) {
		return Character.codePointAt(string, index);
	}
}
