package diff.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import diff.util.FileListConfigLoader;
import diff.util.LogWriter;

/**
 * 메시지ID별 컬럼정보를 가져오는 클래스
 * 
 * @author sskang
 *
 */
public class FileFilter {
	
	private ArrayList<String> testFilterFile = null;
	private ArrayList<String> testFilterFolder = null;

	private Map<String, String> fileMap = null;
	private Map<String, String> folderMap = null;
	
	private FileListConfigLoader confInstance = null;
	
	/**
	 * 메인 인스턴스 객체 정의
	 */
	private static FileFilter instance = null;
	
	/**
	 * KMTFFormatLoader 인스턴스 반환
	 * @return KMTFFormatLoader
	 */
	public static FileFilter getInstance() throws Exception {
		if(instance == null) {
			instance = new FileFilter();
		}
		return instance;
	}
	
	public FileFilter() throws Exception {
		
		try {
			confInstance = FileListConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		loadMessage();
	}
	
	/**
	 * KMTF.conf 를 로드하여 맵핑
	 */
	public void loadMessage() throws Exception {
		
		testFilterFile = confInstance.getFilterFile();
		testFilterFolder = confInstance.getFilterFolder();
		
		fileMap = new HashMap<String, String>();
		folderMap = new HashMap<String, String>();
		
		for(int ii=0; ii<testFilterFile.size(); ii++) {
			String filter = testFilterFile.get(ii);
			if(filter.indexOf(":") != -1) {	// 절대경로
				if(filter.indexOf("*") != -1)
					fileMap.put(filter, "AbsInxOf");
				else
					fileMap.put(filter, "AbsEqs");
			} else if(filter.indexOf("/") != -1) {	// 상대경로
				if(filter.indexOf("*") != -1)
					fileMap.put(filter, "RelInxOf");
				else
					fileMap.put(filter, "RelEqs");
			} else {	// 파일
				if(filter.indexOf("*") != -1)
					fileMap.put(filter, "InxOf");
				else
					fileMap.put(filter, "Eqs");
			}
		}
		
		for(int ii=0; ii<testFilterFolder.size(); ii++) {
			String filter = testFilterFolder.get(ii);
			if(filter.indexOf(":") != -1) {	// 절대경로
				if(filter.indexOf("*") != -1)
					folderMap.put(filter, "AbsInxOf");
				else
					folderMap.put(filter, "AbsEqs");
			} else if(filter.indexOf("/") != -1) {	// 상대경로
				if(filter.indexOf("*") != -1)
					folderMap.put(filter, "RelInxOf");
				else
					folderMap.put(filter, "RelEqs");
			} else {	// 폴더
				if(filter.indexOf("*") != -1)
					folderMap.put(filter, "InxOf");
				else
					folderMap.put(filter, "Eqs");
			}
		}

	}
	
	public boolean isFilterFile(String key, File file) {
		
		boolean result = false;
		
		try {
			
//			System.out.println("key" +key);
			
			String fileName = file.getName();
			String fileAbsPath = file.getCanonicalPath().replace("\\", "/");
			String filterCase = fileMap.get(key);
			
			if("InxOf".equals(filterCase)) {	// 파일에 * 포함
				result = this.isFileInxOf(key, fileName);
			} else if("Eqs".equals(filterCase)) {	// 정확한 파일명
				result = this.isFileEqs(key, fileName);
			} else if("RelEqs".equals(filterCase)) {	// 상대경로부터 정확한 파일명
				result = this.isFileRelEqs(key, fileAbsPath);
			} else if("RelInxOf".equals(filterCase)) {	// 상대경로부터 * 포함
				result = this.isFileRelInxOf(key, fileAbsPath);
			} else if("AbsEqs".equals(filterCase)) {	// 절대경로부터 정확한 파일명
				result = this.isFileAbsEqs(key, fileAbsPath);
			} else if("AbsInxOf".equals(filterCase)) {	// 절대경로부터 * 포함
				result = this.isFileAbsInxOf(key, fileAbsPath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		return result;
	}


	private boolean isFileAbsInxOf(String key, String absPath) throws IOException {
		
		// 절대경로는 마지막에 * 붙는걸로 생각한다.
		if(absPath.startsWith(key.replace("*", "")))
			return true;
		
		return false;
	}

	private boolean isFileAbsEqs(String key, String absPath) {
		if(key.equals(absPath))
			return true;
		
		return false;
	}

	private boolean isFileRelInxOf(String key, String absPath) throws IOException {
		
		int lastIndex = key.lastIndexOf("/");
		
		String folderPath = "";
		String inxOfFileName = "";
		if(lastIndex != -1) {
			folderPath = key.substring(0, lastIndex);
			inxOfFileName = key.substring(lastIndex, key.lastIndexOf("*"));
		}
		
		File file = new File(folderPath);
		String filterFilePath = file.getCanonicalPath().replace("\\", "/") + inxOfFileName;
		
		if(key.endsWith("*") && absPath.startsWith(filterFilePath) && (absPath.replaceAll(filterFilePath, "")).indexOf("/") == -1) {
			
			return true;
		}
			
		return false;
	}

	private boolean isFileRelEqs(String key, String absPath) throws IOException {
		File file = new File("");
		
		String fileFilePath = (file.getCanonicalPath().replace("\\", "/")) + "/" + key;
		
		if(fileFilePath.equals(absPath))
			return true;

		return false;
	}

	private boolean isFileEqs(String key, String fileName) {
		
		if(key.equals(fileName))
			return true;
		
		return false;
	}

	private boolean isFileInxOf(String key, String fileName) {
		if(key.startsWith("*")) {
			if(fileName.endsWith(key.replace("*", ""))) {
				return true;
			}
		} else if(key.endsWith("*")) {
			if(fileName.startsWith(key.replace("*", ""))) {
				return true;
			}
		}
		
		return false;
	}
////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isFilterFolder(String key, File file) {
		
		boolean result = false;
		
		try {
			String filePath = (file.getCanonicalPath()).substring(0, (file.getCanonicalPath()).lastIndexOf("\\")).replace("\\", "/");
			String filterCase = folderMap.get(key);
			
//			System.out.println(filterCase);
			
			if("InxOf".equals(filterCase)) {	// 파일에 * 포함
				result = this.isFolderInxOf(key, filePath);
			} else if("Eqs".equals(filterCase)) {	// 정확한 파일명
				result = this.isFolderEqs(key, filePath);
			} else if("RelEqs".equals(filterCase)) {	// 상대경로부터 정확한 파일명
				result = this.isFolderRelEqs(key, filePath);
			} else if("RelInxOf".equals(filterCase)) {	// 상대경로부터 * 포함
				result = this.isFolderRelInxOf(key, filePath);
			} else if("AbsEqs".equals(filterCase)) {	// 절대경로부터 정확한 파일명
				result = this.isFolderAbsEqs(key, filePath);
			} else if("AbsInxOf".equals(filterCase)) {	// 절대경로부터 * 포함
				result = this.isFolderAbsInxOf(key, filePath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		return result;
	}

	private boolean isFolderAbsInxOf(String key, String filePath) {
		if(filePath.startsWith(key.replace("*", "")))
			return true;
		
		return false;
	}

	private boolean isFolderAbsEqs(String key, String filePath) {
		if(key.equals(filePath))
			return true;
		
		return false;
	}

	private boolean isFolderRelInxOf(String key, String filePath) throws IOException {
		int lastIndex = key.lastIndexOf("/");
		
		String folderPath = "";
		String inxOfFolderName = "";
		if(lastIndex != -1) {
			folderPath = key.substring(0, lastIndex);
			inxOfFolderName = key.substring(lastIndex, key.lastIndexOf("*"));
		}
		
		File file = new File(folderPath);
		String filterFolderPath = file.getCanonicalPath().replace("\\", "/") + inxOfFolderName;
		
		if(key.endsWith("*") && (filePath.startsWith(filterFolderPath)))
			return true;
			
		return false;
	}

	private boolean isFolderRelEqs(String key, String filePath) throws IOException {
		File file = new File("");
		String fileFolderPath = (file.getCanonicalPath().replace("\\", "/")) + "/" + key;
		
		if(filePath.equals(fileFolderPath))
			return true;

		return false;
	}

	private boolean isFolderEqs(String key, String filePath) {
		if((filePath+"/").indexOf("/"+key+"/") != -1)
			return true;
		
		return false;
	}

	private boolean isFolderInxOf(String key, String filePath) {
		if(key.startsWith("*")) {
			if((filePath+"/").indexOf(key.replace("*", "")+"/") != -1) {
				return true;
			}
		} else if(key.endsWith("*")) {
			if((filePath+"/").indexOf("/"+key.replace("*", "")) != -1) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) throws IOException {
			
		FileFilter ff = null;
		try {
			ff = FileFilter.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		System.out.println("절대 정확 : "+ff.isFileAbsEqs("D:/aaa/bbbb/ccc/aabbcc.java", "D:/aaa/bbbb/ccc/aabbcc.java"));	// 절대경로 파일 정확
		System.out.println("절대 정확 : "+ff.isFileAbsEqs("D:/aaa/bbbb/ccc/aabb.java", "D:/aaa/bbbb/ccc/aabbcc.java"));	// 절대경로 파일 정확
		
		System.out.println("절대 * : "+ ff.isFileAbsInxOf("D:/aaa/bbbb/ccc/aabb*", "D:/aaa/bbbb/ccc/aabbcc.java"));	// 절대경로 파일 *
		System.out.println("절대 * : "+ ff.isFileAbsInxOf("D:/aaa/bbbb/ccc/bbcc", "D:/aaa/bbbb/ccc/aabbcc.java"));	// 절대경로 파일 *
		
		System.out.println("파일 정확 : "+ ff.isFileEqs("aabbcc.java", "aabbcc.java"));						// 파일 정확
		System.out.println("파일 정확 : "+ ff.isFileEqs("aabbc.java", "aabbcc.java"));						// 파일 정확
		
		System.out.println("파일 * : "+ ff.isFileInxOf("aabb*", "aabbcc.java"));						// 파일 *
		System.out.println("파일 * : "+ ff.isFileInxOf("*bbcc.java", "aabbcc.java"));						// 파일 *
		
		System.out.println("상대 정확 : "+ ff.isFileRelEqs("in/new/DEV/eng/vo/BattlePosSearchCondition.java", "E:/DEV/prv/Diff_1_3/in/new/DEV/eng/vo/BattlePosSearchCondition.java"));	// 상대경로 파일 정확
		System.out.println("상대 * : "+ ff.isFileRelInxOf("in/new/DEV/eng/vo/BattlePosSearchCondition*", "E:/DEV/prv/Diff_1_3/in/new/DEV/eng/vo/BattlePosSearchCondition.java"));	// 상대경로 *
//		ff.isFilterFile(key, file);
		/////////////////////////////////////////////////////////////////////////////////
		System.out.println("\n\n절대 정확 : "+ff.isFolderAbsEqs("D:/aaa/bbbb/ccc", "D:/aaa/bbbb/ccc"));	// 절대경로 파일 정확
		System.out.println("절대 정확 : "+ff.isFolderAbsEqs("D:/aaa/bbbb/cc", "D:/aaa/bbbb/ccc"));	// 절대경로 파일 정확
		
		System.out.println("절대 * : "+ ff.isFolderAbsInxOf("D:/aaa/bbbb/cc*", "D:/aaa/bbbb/ccc"));	// 절대경로 파일 *
		System.out.println("절대 * : "+ ff.isFolderAbsInxOf("D:/aaa/bbc*", "D:/aaa/bbbb/ccc"));	// 절대경로 파일 *
		
		System.out.println("파일 정확 : "+ ff.isFolderEqs("aaa", "D:/aaa/bbbb/ccc"));						// 파일 정확
		System.out.println("파일 정확 : "+ ff.isFolderEqs("bbbb", "D:/aaa/bbbb/ccc"));						// 파일 정확
		
		System.out.println("파일 * : "+ ff.isFolderInxOf("ce*", "D:/aaa/bbbb/cch"));						// 파일 *
		System.out.println("파일 * : "+ ff.isFolderInxOf("*ab", "D:/aaa/eebb/ccc"));						// 파일 *
		
		System.out.println("상대 정확 : "+ ff.isFolderRelEqs("in/new/DEV/eng", "E:/DEV/prv/Diff_1_3/in/new/DEV/eng"));	// 상대경로 파일 정확
		System.out.println("상대 * : "+ ff.isFolderRelInxOf("in/new/DEV/ed*", "E:/DEV/prv/Diff_1_3/in/new/DEV/eng"));	// 상대경로 *
			
//		System.out.println(("D:\\aaa\\bbbb\\ccc\\ddd.java").substring(0, ("D:\\aaa\\bbbb\\ccc\\ddd.java").lastIndexOf("\\")).replace("\\", "/"));
		
//			System.out.println("결과 : " + ff.isFilterFile("FileTransferEngine.java", new File("D:/Tools/sts-agilin_home/workspace/OutFileExtract/in/new/src/com/filetransfering/FileTransferEngine.java")));
	}
	
}
