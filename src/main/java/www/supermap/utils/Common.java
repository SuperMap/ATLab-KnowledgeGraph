package www.supermap.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.processing.FilerException;

public class Common {
	
	public static void printTimeToFile(String filePath,String content){
		 FileOutputStream fos = null;
		 Date day =new Date();
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String time = df.format(day);
		 Long time2 = day.getTime();
		 String writeContent =time+"\t"+time2+"\t"+content;
	        try {
	            //true不覆盖已有内容
	            fos = new FileOutputStream(filePath, true);  
	            //写入
	            fos.write(writeContent.getBytes());
	            // 写入一个换行
	            fos.write("\r\n".getBytes());
	                       
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	            if(fos != null){
	                try {
	                    fos.flush();
	                    fos.close(); 
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	}

	
	
	/**
	 * 检查目录合法性-是否以\结尾，合法的话不存在则创建，不合法报错。
	 * @param dir
	 * @return 返回合法的目录名
	 */
	public static boolean checkDir(String dir) {
		// TODO Auto-generated method stub
		String checkedDir = dir;
		File originFile = new File(dir);
		//不存在则按照字符串进行判别
		if(!originFile.exists()) {
			//通过检查字符串中有没有"."来判断是不是目录 --可能存在bug
			if(!dir.contains(".")) {
				//是目录则创建,并返回
				try {
					originFile.mkdirs();				
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println("请输入正确的路径");
					System.exit(1);
				}
				return true;
			}
			else {
				//不是目录则抛异常
				try {
					throw new FileSystemException(dir);
				} catch (FileSystemException e) {
					// TODO: handle exception
					e.printStackTrace();
//					System.out.println(originFile.getAbsolutePath());
					System.out.println("\t"+"非法的路径名，请使用不含有.的合法目录路径");
					System.exit(1);
					return false;
				}
			}
		}
		//存在则继续判别
		else {
			//是目录则直接返回
			if(originFile.isDirectory()) {
				return true;
			}
			//不是目录抛异常
			else {
				try {
					throw new FileSystemException(dir);
				} catch (FileSystemException e) {
					// TODO: handle exception
					System.out.println("java.nio.file.FileSystemException:"+originFile.getAbsolutePath());
					System.out.println("\t"+"设置参数应该为目录");
					System.exit(1);
					return false;
				}		
			}
		}
	}
	
		

}
