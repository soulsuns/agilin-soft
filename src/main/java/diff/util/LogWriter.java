package diff.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import diff.file.FileManager;

public class LogWriter {
	
	private static boolean isInfoAppend = false;
	private static boolean isDebugAppend = false;
	
	public static void info(String str) {
		
		ConfigLoader confInstance = null;

		try {
			confInstance = ConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String logPath = confInstance.getCommonMapValue("INFO_LOG_FILE_PATH");
		
		FileManager.makeDirs(logPath);
		
		// sample.txt 파일을 File 객체로 가져온다.
//		File file = new File(logPath+"/info.log");
		
//		FileWriter fw = null;
		PrintWriter fw = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			fw = FileManager.writeFileLoad(logPath+"/info.log", "UTF-8", isInfoAppend);
//			fw = new FileWriter(file, isInfoAppend);
			
			fw.write(str+"\n");
			fw.flush();
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception : LogWriter.info - "+e.getMessage());
		}finally{
			
			// FileWriter를 닫아준다.
			if(fw != null) try{fw.close();}catch(Exception e){}
			
			isInfoAppend = true;
			
		}
	}
	
	public static void clear() {
		isInfoAppend = false;
		isDebugAppend = false;
		
		info("info.log - clear start");
		debug("debug.log - clear start");
	}
	
	public static void debug(String str) {
		
		ConfigLoader confInstance = null;

		try {
			confInstance = ConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String logPath = confInstance.getCommonMapValue("DEBUG_LOG_FILE_PATH");
		
		FileManager.makeDirs(logPath);
		
		// sample.txt 파일을 File 객체로 가져온다.
//		File file = new File(logPath+"/debug.log");
		
//		FileWriter fw = null;
		PrintWriter fw = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			fw = FileManager.writeFileLoad(logPath+"/debug.log", "UTF-8", isDebugAppend);
//			fw = new FileWriter(file, isDebugAppend);
			
			fw.write(str+"\n");
			fw.flush();
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception : LogWriter.debug - "+e.getMessage());
		}finally{
			
			// FileWriter를 닫아준다.
			if(fw != null) try{fw.close();}catch(Exception e){}
			
			isDebugAppend = true;
			
		}
	}
	
	public static void printStackTreace(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));				
			LogWriter.debug(sw.toString());
		} catch (Exception ex) {
			LogWriter.debug("LogWriter.printStackTreace(e) 출력 오류!!!");
		}
	}
}
