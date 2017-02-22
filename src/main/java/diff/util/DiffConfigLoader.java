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
public class DiffConfigLoader {

	private ArrayList<Map> diffMapList = null;

	private Map<String, String> commonMap = null;			// 메시지ID별  컬럼맵
	
	private final String SYSTEM_PROPERTIES_PATH = "resource/conf/Diff.conf";
	
	/**
	 * 메인 인스턴스 객체 정의
	 */
	private static DiffConfigLoader instance = null;
	
	/**
	 * KMTFFormatLoader 인스턴스 반환
	 * @return KMTFFormatLoader
	 */
	public static DiffConfigLoader getInstance() throws Exception {
		if(instance == null) {
			instance = new DiffConfigLoader();
		}
		return instance;
	}
	
	public static DiffConfigLoader init() throws Exception {
		instance = new DiffConfigLoader();
		
		return instance;
	}
	
	public DiffConfigLoader() throws Exception {
		loadMessage();
	}
	
	/**
	 * KMTF.conf 를 로드하여 맵핑
	 */
	public void loadMessage() throws Exception {
		
		diffMapList =  new ArrayList<Map>();
		commonMap = new HashMap<String, String>();
		Map<String, String> diffMap = null;
		
		try
		{
			FileInputStream fi = new FileInputStream(SYSTEM_PROPERTIES_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, FileManager.getEncoding(SYSTEM_PROPERTIES_PATH)));
			String str = null;
			boolean isCommon = false;
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
						
						if(str.indexOf("[Diff-") != -1) {
							if(diffMap != null) {
								diffMapList.add(diffMap);
							}
							diffMap = new HashMap<String, String>();
						}
						
					} else {
						try
						{
							StringTokenizer st = new StringTokenizer(str, "=");	// st = {"UCAT0010", "S1_UC/S1_UNTELM_NM/S1_ACCGMT_UNIT_WHL_NM/S1_ASSGMT_HUCD"}
	
							String messageId = st.nextToken().trim();			// messageId = "UCAT0010"
							String path = st.nextToken().trim();				// columns = "S1_UC/S1_UNTELM_NM/S1_ACCGMT_UNIT_WHL_NM/S1_ASSGMT_HUCD"
							
							if(isCommon == true) {
								commonMap.put(messageId, path);
							} else {
								diffMap.put(messageId, path);
							}
						}
						catch (Exception e) {
							System.out.println(this.getClass().getName() + " : conf 메시지구조 오류\n오류부분 : "+str);
						}
					}
				}
			} while (str != null);
			
			if(diffMap != null) {
				diffMapList.add(diffMap);
			}
			
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " : conf 로드 실패 - "+SYSTEM_PROPERTIES_PATH+" 파일경로 확인");
			
			throw e;
		}

	}
	
	public int getDiffSize() {
		return diffMapList.size();
	}
	
	public String getCommonMapValue(String key) {
		return StringUtil.parseLastSlash(commonMap.get(key));
	}

	public String getOutFilePath(int index) {
		return StringUtil.parseLastSlash((String)(diffMapList.get(index)).get("OUT_FILE_PATH"));
	}
	
	public String getOldFilePath(int index) {
		return StringUtil.parseLastSlash((String)(diffMapList.get(index)).get("OLD_FILE_PATH"));
	}
	
	public String getNewFilePath(int index) {
		return StringUtil.parseLastSlash((String)(diffMapList.get(index)).get("NEW_FILE_PATH"));
	}
	
	
	public String getOutFileName(int index) {
		return StringUtil.parseLastSlash((String)(diffMapList.get(index)).get("OUT_FILE_NAME"));
	}
	
}
