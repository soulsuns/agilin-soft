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
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.universalchardet.UniversalDetector;

import diff.filter.FileFilter;
import diff.util.FileListConfigLoader;
import diff.util.LogWriter;

public class FileManager {
	
	private final static CopyOption[] options = new CopyOption[] {
		StandardCopyOption.REPLACE_EXISTING,
		StandardCopyOption.COPY_ATTRIBUTES
	};
	
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
					
					fileList.add(fVo);
				}catch(Exception e){
					e.printStackTrace();
					LogWriter.printStackTreace(e);
				}
			}
			else if(file.isDirectory())
			{
				try{
				}catch(Exception e){
					e.printStackTrace();
					LogWriter.printStackTreace(e);
				}
			}
		}
		
	}
	
	public void createFolderFileListAddFilter(ArrayList<FileVO> fileList, String folderPath) {
		
		FileFilter fFilter = null;
		FileListConfigLoader confInstance = null;
		
		try {
			fFilter = FileFilter.getInstance();
			confInstance = FileListConfigLoader.getInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File f= new File(folderPath);
		ArrayList<File> subFiles= new ArrayList<File>();
		FileVO fVo = null;
		
		if(!f.exists())
		{
			System.out.println("디렉토리가 존재하지 않습니다");
			return;
		}
		
		findSubFilesAddFilter(f, subFiles, confInstance.getFilterFolder(), fFilter);
		
		
		for(File file : subFiles)
		{
			
			boolean isFilterFile = false;
			
			if(file.isFile())
			{
				for(int ii=0; ii<confInstance.getFilterFile().size(); ii++) {
					
					if(fFilter.isFilterFile(confInstance.getFilterFile().get(ii), file)) {
						try {
							LogWriter.info(" - 파일 : " + file.getCanonicalPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							LogWriter.printStackTreace(e);
						}
						isFilterFile = true;
						break;
					}
				}
				
				if(!isFilterFile) {
				
					fVo = new FileVO();
					
					fVo.setFileName(file.getName());
					try{
						
						fVo.setRelativeFilePath(file.getPath().replace("\\", "/"));
						fVo.setAbsFilePath(file.getCanonicalPath().replace("\\", "/"));
						
						fVo.setRelativePath((file.getPath()).substring(0, (file.getPath()).lastIndexOf("\\")).replace("\\", "/"));
						fVo.setAbsPath((file.getCanonicalPath()).substring(0, (file.getCanonicalPath()).lastIndexOf("\\")).replace("\\", "/"));
						
						
						fileList.add(fVo);
					}catch(Exception e){
						e.printStackTrace();
						LogWriter.printStackTreace(e);
					}
				}
			}
			else if(file.isDirectory())
			{
				try{
				}catch(Exception e){
					e.printStackTrace();
					LogWriter.printStackTreace(e);
				}
			}
		}
		
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
			LogWriter.printStackTreace(e);
		}
		
		return fw;
		
	}
	
	public static PrintWriter writeFileLoad(String filePath, String encoding, boolean append) {
		// sample.txt 파일을 File 객체로 가져온다.
		PrintWriter outWriter = null;
		
		// sample.txt 에 출력할 FileWriter 객체를 생성한다.
		// true 이므로, 기존의 sample.txt 파일 뒤에 이어서 출력한다.
		try {
			outWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), encoding));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
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
			LogWriter.printStackTreace(e);
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
	
	public void findSubFilesAddFilter(File parentFile, ArrayList<File> subFiles, ArrayList<String> filterFolderList, FileFilter fFilter)
	{
		if(parentFile.isFile())
		{
			boolean isFilterFolder = false;
			for(int ii=0; ii<filterFolderList.size(); ii++) {
				if(fFilter.isFilterFolder(filterFolderList.get(ii), parentFile)) {
					try {
						LogWriter.info(" - 폴더 : " + parentFile.getCanonicalPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogWriter.printStackTreace(e);
					}
					isFilterFolder = true;
					break;
				}
			}
			
			if(!isFilterFolder)
				subFiles.add(parentFile);
		}
		else if(parentFile.isDirectory())
		{
			boolean isFilterFolder = false;
			for(int ii=0; ii<filterFolderList.size(); ii++) {
				if(fFilter.isFilterFolder(filterFolderList.get(ii), parentFile)) {
					try {
						LogWriter.info(" - 폴더 : " + parentFile.getCanonicalPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogWriter.printStackTreace(e);
					}
					isFilterFolder = true;
					break;
				}
			}
			
			if(!isFilterFolder) {
				subFiles.add(parentFile);
				File[] childFiles= parentFile.listFiles();
				for(File childFile : childFiles)
				{
					findSubFilesAddFilter(childFile, subFiles, filterFolderList, fFilter);
				}
			}
		}
	}
	
	public String getFileText(String fullPath) {
		StringBuffer text = new StringBuffer();
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath), FileManager.getEncoding(fullPath)));
			System.out.println(fullPath);
			String fileRead = br.readLine();
			while (fileRead != null) {
				text.append(fileRead+"\n");
				
				fileRead = br.readLine();
            }
		}catch(IOException e){
			e.printStackTrace();
			LogWriter.printStackTreace(e);
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
			LogWriter.printStackTreace(e);
		}finally{
			if(br != null) try{br.close();}catch(IOException e){}
		}
		
		return textMap;
	}
	
	public boolean isFileExist(String fileFullPath) {
		
		File f = new File(fileFullPath);
		
		return f.isFile();
	}
	
	public String getFileFullPath(String filePath) {
		
		File f = new File(filePath);
		
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		return "";
	}
	
	public void makeDir4List(ArrayList<String> fileList, String outFilePath, String startCopyPath) {
		
		String orgOutPath = "";
		String outPath = "";
		String startPath = "";
		
		String oldPath = "";
		
		try {
			File file = new File(outFilePath);
			
			orgOutPath = file.getCanonicalPath().replace("\\", "/");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		try {
			File file = new File(startCopyPath);
			
			startPath = file.getCanonicalPath().replace("\\", "/");
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		try {
			for(int ii=0; ii<fileList.size(); ii++) {
				File file = new File(fileList.get(ii));
				outPath = orgOutPath;
				
				oldPath = (file.getCanonicalPath()).substring(0, (file.getCanonicalPath()).lastIndexOf("\\")).replace("\\", "/");
				outPath = outPath + oldPath.replaceFirst(startPath, "");
				
				this.makeDirs(outPath);
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
	
	
	public static boolean isBinaryFile(File f) throws FileNotFoundException, IOException {
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
	
	public static boolean isAsciiFile(String fileName) {
		
		InputStream in;
		
		try {
			in = new FileInputStream(fileName);
		
			int maxByte = 0;
			File f = new File(fileName);
			if(f.length() > 1024)
				maxByte = 1024;
			else
				maxByte = (int)f.length();
			
			byte[] bytes = new byte[maxByte];
			
			in.read(bytes, 0, bytes.length);
			in.close();
			int x = 0;
			short bin = 0;
			
			for(byte thisByte : bytes) {
				char it = (char) thisByte;
				if(!Character.isWhitespace(it) && Character.isISOControl(it)) {
					bin++;
				}
				if(bin >= 5) {
					return false;
				}
				x++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		return true;
	}
	
	
	// jdk1.7 이상에서 사용
	public static void copyFile2(String sourceFilePath, String targetFilePath) {
		Path source = Paths.get(sourceFilePath);
		Path target = Paths.get(targetFilePath);
		try
		{
			
			if(!target.getParent().toFile().exists()) {
				target.getParent().toFile().mkdirs();
			}
			
		    Files.copy( source , target , options );

		    //Files.move( source , target , StandardCopyOption.ATOMIC_MOVE );
		}
		catch ( IOException e )
		{
		    e.printStackTrace( );
		}
	}
	
	// jdk1.6 이하에서 사용
	public static void copyFile(String sourceFilePath, String targetFilePath) {
		try {
			FileInputStream fis = new FileInputStream(sourceFilePath);
			FileOutputStream fos = new FileOutputStream(targetFilePath);
			FileChannel fic = fis.getChannel();
			FileChannel foc = fos.getChannel();
			fic.transferTo(0, fic.size(), foc);
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
	
}
