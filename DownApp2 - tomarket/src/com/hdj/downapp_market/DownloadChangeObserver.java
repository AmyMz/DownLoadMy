package com.hdj.downapp_market;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class DownloadChangeObserver extends ContentObserver {
	long lastDownloadId;
	int time =0;
	DownloadManager dowanloadmanager;
	public DownloadChangeObserver(Handler handler,long lastDownloadId,Context context) {
		super(handler);
		this.lastDownloadId=lastDownloadId;
		dowanloadmanager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE); 
	}
	 @Override  
     public void onChange(boolean selfChange) {  
           queryDownloadStatus();     
     }  
	 private void queryDownloadStatus() {     
	        DownloadManager.Query query = new DownloadManager.Query();     
	        query.setFilterById(lastDownloadId);     
	        Cursor c = dowanloadmanager.query(query);     
	        if(c!=null&&c.moveToFirst()) {  
	            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));     
	              
	            int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);    
	            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);    
	            int fileSizeIdx =     
	              c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);        
	            int bytesDLIdx =     
	              c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);    
	            String title = c.getString(titleIdx);    
	            int fileSize = c.getInt(fileSizeIdx);    
	            int bytesDL = c.getInt(bytesDLIdx);    
	              
	            // Translate the pause reason to friendly text.    
	            int reason = c.getInt(reasonIdx);    
	            StringBuilder sb = new StringBuilder();    
	            sb.append(title).append("\n");   
	            sb.append("Downloaded ").append(bytesDL).append(" / " ).append(fileSize); 
	            float percent=(float)bytesDL/(float)fileSize;
	            Log.v("tagAMY", sb.toString()+"########"+bytesDL+"###  "+percent);  
	            if(percent>=0.2){
	            	dowanloadmanager.remove(lastDownloadId);
	            }
//	            Log.v("tagAMY", sb.toString());    
	            switch(status) {     
	            case DownloadManager.STATUS_PAUSED: 
	           
//	                Log.v("tagAMY", "STATUS_PAUSED");    
	            case DownloadManager.STATUS_PENDING:     
	                Log.v("tagAMY", "STATUS_PENDING"+lastDownloadId);    
	            case DownloadManager.STATUS_RUNNING:     
	                //正在下载，不做任何事情    
	               
	                time++;
	                if(time==1){
	                	 Log.v("tagAMY", "STATUS_RUNNING"+lastDownloadId+"----*"+time); 
//	                	dowanloadmanager.remove(lastDownloadId);
	                }
	                
	                break;     
	            case DownloadManager.STATUS_SUCCESSFUL:     
	                //完成    
//	                Log.v("tagAMY", "下载完成");
	                
	               
//	              dowanloadmanager.remove(lastDownloadId);     
	                break;     
	            case DownloadManager.STATUS_FAILED:     
	                //清除已下载的内容，重新下载    
	                Log.v("tagAMY", "STATUS_FAILED");    
	                dowanloadmanager.remove(lastDownloadId);     
	                break;     
	            }     
	        }    
	    }


}
