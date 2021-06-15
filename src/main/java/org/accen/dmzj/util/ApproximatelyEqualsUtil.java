package org.accen.dmzj.util;
/**
 * 近似相似
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class ApproximatelyEqualsUtil {
	public static final int IGNORE_CASE = 1;
	public static final int IGNORE_LINE = 2;
	public static final int IGNORE_BLANK = 4;
	public static boolean aequals(String s,String t,int ignoreLv) {
		if(s==null||t==null) {
			return false;
		}
		if((ignoreLv&IGNORE_CASE)==1) {
			s = s.toLowerCase();
			t = t.toLowerCase();
			if(s.equalsIgnoreCase(t)) {
				return true;
			}
		}
		if((ignoreLv&IGNORE_LINE)==1) {
			s = s.replaceAll("_", "");
			t = t.replaceAll("_", "");
			if(s.equalsIgnoreCase(t)) {
				return true;
			}
		}
		if((ignoreLv&IGNORE_BLANK)==1) {
			s = s.replace(" ", "");
			t = t.replaceAll(" ", "");
			if(s.equals(t)) {
				return true;
			}
		}
		return false;
	}
	public static boolean aequals(String s,String t) {
		return aequals(s, t, (1<<5)-1);
	}
}
