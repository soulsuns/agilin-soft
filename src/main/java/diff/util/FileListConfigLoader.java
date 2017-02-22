package diff.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import diff.file.FileManager;

/**
 * 메시지ID별 컬럼정보를 가져오는 클래스
 * 
 * @author sskang
 *
 */
public class FileListConfigLoader {

	private ArrayList<Map> fileListMapList = null;

//	private Map<String, String> commonMap = null;			// 메시지ID별  컬럼맵
	private ArrayList<String> filterFileList = null;
	private ArrayList<String> filterFolderList = null;
	
	private final String SYSTEM_PROPERTIES_PATH = "resource/conf/FileList.conf";
	
	/**
	 * 메인 인스턴스 객체 정의
	 */
	private static FileListConfigLoader instance = null;
	
	/**
	 * KMTFFormatLoader 인스턴스 반환
	 * @return KMTFFormatLoader
	 */
	public static FileListConfigLoader getInstance() throws Exception {
		if(instance == null) {
			instance = new FileListConfigLoader();
		}
		return instance;
	}
	
	public static FileListConfigLoader init() throws Exception {
		instance = new FileListConfigLoader();
		
		return instance;
	}
	
	public FileListConfigLoader() throws Exception {
		loadMessage();
	}
	
	/**
	 * KMTF.conf 를 로드하여 맵핑
	 */
	public void loadMessage() throws Exception {
		
		fileListMapList = new ArrayList<Map>();
		filterFileList = new ArrayList<String>();
		filterFolderList = new ArrayList<String>();
		
		Map<String, String> fileListMap = null;
		
		try
		{
			FileInputStream fi = new FileInputStream(SYSTEM_PROPERTIES_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, FileManager.getEncoding(SYSTEM_PROPERTIES_PATH)));
			String str = null;
			boolean isFilterFile = false;
			boolean isFilterFolder = false;
			do {
				str = br.readLine();								
				if ((str != null) && (str.trim().length() != 0)) {	
					str = str.trim();								

					if (str.charAt(0) == '#')						
						continue;									
					
					if((str.startsWith("[") && (str.endsWith("]")))) {
						
						if(str.indexOf("[Filter-File") != -1)
							isFilterFile = true;
						else
							isFilterFile = false;
						
						if(str.indexOf("[Filter-Folder") != -1)
							isFilterFolder = true;
						else
							isFilterFolder = false;
						
						if(str.indexOf("[FileList-") != -1) {
							if(fileListMap != null) {
								fileListMapList.add(fileListMap);
							}
							fileListMap = new HashMap<String, String>();
						}
						
					} else {
						try
						{
							
							if(isFilterFile == true) {
								filterFileList.add(str);
							} else if(isFilterFolder == true) {
								filterFolderList.add(str);
							} else {
								StringTokenizer st = new StringTokenizer(str, "=");
								
								String messageId = st.nextToken().trim();
								String path = st.nextToken().trim();
								
								fileListMap.put(messageId, path);
							}
							
							
						}
						catch (Exception e) {
							System.out.println(this.getClass().getName() + " : conf 메시지구조 오류\n오류부분 : "+str);
						}
					}
				}
			} while (str != null);
			
			if(fileListMap != null) {
				fileListMapList.add(fileListMap);
			}
			
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " : conf 로드 실패 - "+SYSTEM_PROPERTIES_PATH+" 파일경로 확인");
			
			throw e;
		}

	}
	
	public int getDiffSize() {
		return fileListMapList.size();
	}
	
	public ArrayList<String> getFilterFile() {
		return filterFileList;
	}
	
	public ArrayList<String> getFilterFolder() {
		return filterFolderList;
	}

	public String getOutFilePath(int index) {
		return StringUtil.parseLastSlash((String)(fileListMapList.get(index)).get("OUT_FILE_PATH"));
	}
	
	public String getOldFilePath(int index) {
		return StringUtil.parseLastSlash((String)(fileListMapList.get(index)).get("OLD_FILE_PATH"));
	}
	
	public String getNewFilePath(int index) {
		return StringUtil.parseLastSlash((String)(fileListMapList.get(index)).get("NEW_FILE_PATH"));
	}
	
	
	public String getOutFileName(int index) {
		return StringUtil.parseLastSlash((String)(fileListMapList.get(index)).get("OUT_FILE_NAME"));
	}
	
}
