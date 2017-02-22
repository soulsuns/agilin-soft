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
public class FileCopyConfigLoader {

	private ArrayList<ArrayList> fileCopyList = null;
	private ArrayList<Map> reqList = null;

	private Map<String, String> commonMap = null;			// 메시지ID별  컬럼맵
	
	private final String SYSTEM_PROPERTIES_PATH = "resource/conf/FileCopy.conf";
	
	/**
	 * 메인 인스턴스 객체 정의
	 */
	private static FileCopyConfigLoader instance = null;
	
	/**
	 * KMTFFormatLoader 인스턴스 반환
	 * @return KMTFFormatLoader
	 */
	public static FileCopyConfigLoader getInstance() throws Exception {
		if(instance == null) {
			instance = new FileCopyConfigLoader();
		}
		return instance;
	}
	
	public static FileCopyConfigLoader init() throws Exception {
		instance = new FileCopyConfigLoader();
		
		return instance;
	}
	
	public FileCopyConfigLoader() throws Exception {
		loadMessage();
	}
	
	/**
	 * KMTF.conf 를 로드하여 맵핑
	 */
	public void loadMessage() throws Exception {
		
		fileCopyList =  new ArrayList<ArrayList>();
		commonMap = new HashMap<String, String>();
		Map reqMap = null;
		reqList = new ArrayList<Map>();			// OUT_FILE_PATH
		ArrayList<String> copyList = null;
		
		try
		{
			FileInputStream fi = new FileInputStream(SYSTEM_PROPERTIES_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, FileManager.getEncoding(SYSTEM_PROPERTIES_PATH)));
			String str = null;
			boolean isCommon = false;
			boolean isReq = false;
			do {
				str = br.readLine();								// 파일을 한줄씩 읽음. str = "UCAT0010, S1_UC/S1_UNTELM_NM/S1_ACCGMT_UNIT_WHL_NM/S1_ASSGMT_HUCD"
				if ((str != null) && (str.trim().length() != 0)) {	// 파일에 문자가 포함된다면
					str = str.trim();								// 좌우 공백제거

					if (str.charAt(0) == '#')						// 첫문자가 #일경우 주석으로 처리됨.
						continue;									// (다음라인 진행)
					
					if((str.startsWith("[") && (str.endsWith("]")))) {
						
						if(str.indexOf("[Common") != -1)
							isCommon = true;
						else
							isCommon = false;
						
						if(str.indexOf("[Req-") != -1) {
							isReq = true;
							if(copyList != null) {
								fileCopyList.add(copyList);
							}
							copyList = new ArrayList<String>();
							
							if(reqMap != null) {
								reqList.add(reqMap);
							}
							reqMap = new HashMap<String, String>();
						} else {
							isReq = false;
						}
						
					} else {
						try
						{
							if(str.indexOf("=") != -1) {
								StringTokenizer st = new StringTokenizer(str, "=");
		
								String messageId = st.nextToken().trim();
								String path = st.nextToken().trim();
								
								if(isCommon == true) {
									commonMap.put(messageId, path);
								} else if(isReq == true) {
									reqMap.put(messageId, path);
								}
							} else {
							
								copyList.add(str);
							}
						}
						catch (Exception e) {
							System.out.println(this.getClass().getName() + " : conf 메시지구조 오류\n오류부분 : "+str);
						}
					}
				}
			} while (str != null);
			
			if(copyList != null) {
				fileCopyList.add(copyList);
			}
			
			if(reqMap != null) {
				reqList.add(reqMap);
			}
			
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " : conf 로드 실패 - "+SYSTEM_PROPERTIES_PATH+" 파일경로 확인");
			
			throw e;
		}

	}
	
	public int getDiffSize() {
		return fileCopyList.size();
	}
	
	public String getCommonMapValue(String key) {
		return StringUtil.parseLastSlash(commonMap.get(key));
	}

	public String getOutFilePath(int index) {
		return StringUtil.parseLastSlash((String)(reqList.get(index)).get("OUT_FILE_PATH"));
	}
	
	public String getStartCopyPath(int index) {
		return StringUtil.parseLastSlash((String)(reqList.get(index)).get("START_COPY_PATH"));
	}
	
	public ArrayList<String> getFileCopyList(int index) {
		return fileCopyList.get(index);
	}
	
}
