package org.accen.dmzj.core.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.accen.dmzj.web.dao.SysQnumMapper;
import org.accen.dmzj.web.vo.SysQnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CqhttpSecurity {
	@Autowired
	private SysQnumMapper sysQnumMapper;
	
	public boolean checkCqhttp(String qnum,String sig,String body) {
		if(!StringUtils.isEmpty(qnum)) {
			SysQnum sysQnum = sysQnumMapper.getByQnum(qnum);
			if(sysQnum!=null&&null!=sysQnum.getSecret()) {
				String secret = sysQnum.getSecret();
				try {
					SecretKeySpec signinKey = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSHA1");
					Mac mac = Mac.getInstance("HmacSHA1");
		            mac.init(signinKey);
		            byte[] rawHmac = mac.doFinal(body.getBytes("utf-8"));
		            return byte2hex(rawHmac).equals(sig);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				}
				
			}
		}
		return true;
	}
	public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }
}
