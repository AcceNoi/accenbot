package org.accen.dmzj.util.setu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.FilePersistentUtil;
import org.accen.dmzj.web.vo.Qmessage;

public class CqSetuGreper implements SetuGreper {
	private List<String> imageCqs;
	private long minSize;
	private long maxSize;
	private Qmessage qmessage;
	private FilePersistentUtil filePersistentUtil = ApplicationContextUtil.getBean(FilePersistentUtil.class);
	private SetuCatcher setuCatcher = ApplicationContextUtil.getBean(SetuCatcher.class);
	public CqSetuGreper(long minSize,long maxSize,Qmessage qmessage,String... imageCqs) {
		this.qmessage =  qmessage;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.imageCqs = Arrays.asList(imageCqs);
	}
	public CqSetuGreper(long minSize,long maxSize,Qmessage qmessage,List<String> imageCqs) {
		this.qmessage =  qmessage;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.imageCqs = new ArrayList<String>(imageCqs);
	}
	@Override
	public int grep() {
		if(imageCqs == null ||imageCqs.isEmpty()) {
			return 0;
		}else {
			return (int)(this.imageCqs.parallelStream()
						.filter(cq->{long size = Long.parseLong(filePersistentUtil.getImageMetaInfo(cq)[3]); 
									return size>=minSize&&size<=maxSize&&setuCatcher.catchFromCqImage(cq, setuCatcher.uuSetuName(qmessage));})
						.count());
		}
	}

}
