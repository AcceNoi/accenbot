package org.accen.dmzj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.accen.dmzj.web.vo.SysFile;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class FtpUtil {
	@Value("${ftp.host}")
	private String host;
	@Value("${ftp.port:21}")
	private int port;
	@Value("${ftp.username)")
	private String username;
	@Value("${ftp.password}")
	private String password;
	@Value("${ftp.timeout:30000}")
	private int timeout;
	@Value("${ftp.dataTimeout:30000}")
	private int dataTimeout;
	@Value("${ftp.basePath:/}")
	private String basePath;
	
	private FTPClient ftpClient = new FTPClient();
	
	private void connect() {
		if(ftpClient.isConnected()) {
			ftpClient.setControlEncoding("utf-8");
			ftpClient.setDefaultTimeout(timeout);
			ftpClient.setConnectTimeout(timeout);
			ftpClient.setDataTimeout(dataTimeout);
			try {
				ftpClient.connect(host, port);
				ftpClient.login(username, password);
				ftpClient.cwd(basePath);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private boolean changeWorkingDir(String dir) {
		if(!ftpClient.isConnected()) {
			return false;
		}
		try {
			return ftpClient.changeWorkingDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public File downloadFile(SysFile sysFile,String baseTempDir) {
		connect();
		if(changeWorkingDir(basePath)) {
			File localFile = new File(baseTempDir+"/"+sysFile.getFtpPath());
			try {
				ftpClient.retrieveFile(sysFile.getFtpPath()+"/"+sysFile.getFileName(), new FileOutputStream(localFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return localFile;
		}
		return null;
		
	}
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 上传文件
	 * @param file
	 * @return 注意没有持久化SysFile，只是保存了ftp上传所返回的信息
 	 */
	public SysFile uploadFile(File file) {
		connect();
		if(changeWorkingDir(basePath)) {
			String suffix = file.getName().substring(file.getName().lastIndexOf("."));
			String id = StringUtil.uuid();
			String date = sdf.format(new Date());
			String ftpFilePath = date+"/"+id+suffix;
			try {
				ftpClient.storeFile(ftpFilePath, new FileInputStream(file));
				
				SysFile sysFile = new SysFile();
				sysFile.setId(id);
				sysFile.setFileName(id+suffix);
				sysFile.setFileType(suffix);
				sysFile.setFtpPath(ftpFilePath);
				
				return sysFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
}
