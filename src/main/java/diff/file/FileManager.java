package diff.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.universalchardet.UniversalDetector;

import diff.util.ConfigLoader;

public class FileManager {
	
	public FileManager() {
		
	}
	
	public void createFolderFileList(ArrayList<FileVO> fileList, String folderPath) {
		File f= new File(folderPath);
		ArrayList<File> subFiles= new ArrayList<File>();
		FileVO fVo = null;
		
		if(!f.exists())
		{
			System.out.println("디렉토리가 존재하지 않습니다");
			return;
		}
		
		findSubFiles(f, subFiles);
		
//		System.out.println("———————————-");
		
		for(File file : subFiles)
		{
			if(file.isFile())
			{
				fVo = new FileVO();
				
				fVo.setFileName(file.getName());
//				fileNames.add(file.getName());
				try{
//					System.out.println("파일 경로 : "+file.getCanonicalPath());
					
					fVo.setRelativeFilePath(file.getPath().replace("\\", "/"));
					fVo.setAbsFilePath(file.getCanonicalPath().replace("\\", "/"));
					
					fVo.setRelativePath((file.getPath()).substring(0, (file.getPath()).lastIndexOf("\\")).replace("\\", "/"));
					fVo.setAbsPath((file.getCanonicalPath()).substring(0, (file.getCanonicalPath()).lastIndexOf("\\")).replace("\\", "/"));
					
//					System.out.println(fVo.toString());
//					filePaths.add(file.getPath());
//					filePaths.add(file.getCanonicalPath());
					
					fileList.add(fVo);
				}catch(Exception e){
					e.printStackTrace();
				}
//				System.out.println("파일 크기 : "+file.length());
//				System.out.println("———————————-");
			}
			else if(file.isDirectory())
			{
//				System.out.println("디렉토리 이름 : "+file.getName());
				try{
//					System.out.println("디렉토리 경로 : "+file.getCanonicalPath());
				}catch(Exception e){
					e.printStackTrace();
				}
//				System.out.println("———————————-");
			}
		}
		
//		parsingTableName(filePaths, fileNames);
//		System.out.println("------- 파싱 종료");
	}
	
	public FileWriter writeFileLoad(String path) {
		// sample.txt 파일을 File 객체로 가져온다.
		File file = new File(path);
		
		FileWriter fw = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			fw = new FileWriter(file, false);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fw;
		
	}
	
	public PrintWriter writeFileLoad(String filePath, String encoding, boolean append) {
		// sample.txt 파일을 File 객체로 가져온다.
		PrintWriter outWriter = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			outWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), encoding));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outWriter;
		
	}
	
	
	
	public FileWriter writeFileLoad(String filePath, boolean append) {
		// sample.txt 파일을 File 객체로 가져온다.
		File file = new File(filePath);
		
		FileWriter fw = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			fw = new FileWriter(file, append);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fw;
		
	}
	
	public void findSubFiles(File parentFile, ArrayList<File> subFiles)
	{
		if(parentFile.isFile())
		{
			subFiles.add(parentFile);
		}
		else if(parentFile.isDirectory())
		{
			subFiles.add(parentFile);
			File[] childFiles= parentFile.listFiles();
			for(File childFile : childFiles)
			{
				findSubFiles(childFile, subFiles);
			}
		}
	}
	
	public String getFileText(String fullPath) {
		StringBuffer text = new StringBuffer();
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath), FileManager.getEncoding(fullPath)));
			
			String fileRead = br.readLine();
			while (fileRead != null) {
				text.append(fileRead+"\n");
				
				fileRead = br.readLine();
            }
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(br != null) try{br.close();}catch(IOException e){}
		}
		
		return text.toString();
	}
	
	public HashMap<String, String> getFileText2(String fullPath) {
		HashMap<String, String> textMap = new HashMap<String, String>();
		BufferedReader br = null;
		
		try{
			br = new BufferedReader( new InputStreamReader(new FileInputStream(fullPath), FileManager.getEncoding(fullPath)) );
			
			String fileRead = br.readLine();
			while (fileRead != null) {
				String[] tmpArr = fileRead.split(":");
				try {
					textMap.put(tmpArr[0].toUpperCase(), tmpArr[1]);
				} catch (Exception e) {
					System.out.println("excludeFiles.lst 파일의 "+tmpArr[0]+" 확장자 내용없음");
					textMap.put(tmpArr[0], "미정");
				}
				
				fileRead = br.readLine();
            }
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(br != null) try{br.close();}catch(IOException e){}
		}
		
		return textMap;
	}
	
	public boolean isFileExist(String fileFullPath) {
		
		File f = new File(fileFullPath);
		
		return f.isFile();
	}
	
	public static boolean isBinaryFile(String path) throws FileNotFoundException, IOException {
		File f = new File(path);
	    FileInputStream in = new FileInputStream(f);
	    int size = in.available();
	    if(size > 1024) size = 1024;
	    byte[] data = new byte[size];
	    in.read(data);
	    in.close();

	    int ascii = 0;
	    int other = 0;

	    for(int i = 0; i < data.length; i++) {
	        byte b = data[i];
	        if( b < 0x09 ) return true;

	        if( b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D ) ascii++;
	        else if( b >= 0x20  &&  b <= 0x7E ) ascii++;
	        else other++;
	    }

	    if( other == 0 ) return false;

	    return 100 * other / (ascii + other) > 95;
	}
	
	public static boolean isAsciiText(String fileName) throws IOException {

	    InputStream in = new FileInputStream(fileName);
	    
	    long fileSize = (new File(fileName)).length();
	    
	    if(fileSize > 500)
	    	fileSize = 500;
	    
	    byte[] bytes = new byte[(int)fileSize];

	    in.read(bytes, 0, bytes.length);
	    int x = 0;
	    short bin = 0;

	    for (byte thisByte : bytes) {
	        char it = (char) thisByte;
	        if (!Character.isWhitespace(it) && Character.isISOControl(it)) {

	            bin++;
	        }
	        if (bin >= 5) {
	            return false;
	        }
	        x++;
	    }
	    in.close();
	    return true;
	}
	
	public static void copyFile(String sourceFilePath, String targetFilePath) {
		Path source = Paths.get(sourceFilePath);
		Path target = Paths.get(targetFilePath);
		try
		{
		    Files.copy( source , target , StandardCopyOption.COPY_ATTRIBUTES );

		    //Files.move( source , target , StandardCopyOption.ATOMIC_MOVE );
		}
		catch ( IOException e )
		{
		    e.printStackTrace( );
		}
	}
	
	public static void deleteFile(String deleteFilePath) {
		
		File f = new File(deleteFilePath);
		
		try {
			if(f.exists()) {
				f.delete();
			}
		} catch ( Exception e ) {
		    e.printStackTrace( );
		}
	}
	
	public static void makeDirs(String path) {
		
		File f = new File(path);
		
		try {
			if(!f.exists()) {
				f.mkdirs();
			}
		} catch ( Exception e ) {
		    e.printStackTrace( );
		}
	}
	
	public static String getEncoding(String filePath) throws IOException {
		byte[] buf = new byte[4096];
	    java.io.FileInputStream fis = new java.io.FileInputStream(filePath);

	    UniversalDetector detector = new UniversalDetector(null);

	    int nread;
	    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
	      detector.handleData(buf, 0, nread);
	    }
	    detector.dataEnd();

	    String encoding = detector.getDetectedCharset();
	    if (encoding != null) {
//	      System.out.println("Detected encoding = " + encoding);
	    	if(!encoding.startsWith("UTF"))
	    		encoding = "EUC-KR";
	    } else {
//	      System.out.println("No encoding detected.");
	      encoding = "EUC-KR";
	    }
	    
	    detector.reset();
	    
	    return encoding;
		
	}
