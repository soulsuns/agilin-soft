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
 * Config ���Ͽ��� ������ �������� Ŭ����
 * 
 * @author sskang
 *
 */
public class ConfigLoader {

	private ArrayList<Map> systemMapList = null;

	private Map<String, String> commonMap = null;
	
	private final String SYSTEM_PROPERTIES_PATH = "resource/conf/system.conf";
	
	/**
	 * ���� �ν��Ͻ� ��ü ����
	 */
	private static ConfigLoader instance = null;
	
	/**
	 * �ν��Ͻ� ��ȯ
	 * @return ConfigLoader
	 */
	public static ConfigLoader getInstance() throws Exception {
		if(instance == null) {
			instance = new ConfigLoader();
		}
		return instance;
	}
	
	public ConfigLoader() throws Exception {
		loadMessage();
	}
	
	/**
	 * conf ������ �ε��Ͽ� ����
	 */
	public void loadMessage() throws Exception {
		
		systemMapList =  new ArrayList<Map>();
		commonMap = new HashMap<String, String>();
		Map<String, String> diffMap = null;
		
		try
		{
			FileInputStream fi = new FileInputStream(SYSTEM_PROPERTIES_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, FileManager.getEncoding(SYSTEM_PROPERTIES_PATH)));
			String str = null;
			boolean isCommon = false;
			do {
				str = br.readLine();
				if ((str != null) && (str.trim().length() != 0)) {
					str = str.trim();

					if (str.charAt(0) == '#')
						continue;
					
					if((str.startsWith("[") && (str.endsWith("]")))) {
						
						if(str.indexOf("[Common") != -1)
							isCommon = true;
						else
							isCommon = false;
						
						if(str.indexOf("[Diff-") != -1) {
							if(diffMap != null) {
								systemMapList.add(diffMap);
							}
							diffMap = new HashMap<String, String>();
						}
						
					} else {
						try
						{
							StringTokenizer st = new StringTokenizer(str, "=");
							
							String messageId = st.nextToken().trim();
							String path = st.nextToken().trim();
							
							if(isCommon == true) {
								commonMap.put(messageId, path);
							} else {
								diffMap.put(messageId, path);
							}
						}
						catch (Exception e) {
							System.out.println(this.getClass().getName() + " : conf ���� ����\n�����κ� : "+str);
						}
					}
				}
			} while (str != null);
			
			if(diffMap != null) {
				systemMapList.add(diffMap);
			}
			
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " : conf �ε� ���� - "+SYSTEM_PROPERTIES_PATH+" ���ϰ�� Ȯ��");
			
			throw e;
		}

	}
	
	public int getDiffSize() {
		return systemMapList.size();
	}
	
	public String getCommonMapValue(String key) {
		return StringUtil.parseLastSlash(commonMap.get(key));
	}

	public String getOutFilePath(int index) {
		return StringUtil.parseLastSlash((String)(systemMapList.get(index)).get("OUT_FILE_PATH"));
	}
	
	public String getOldFilePath(int index) {
		return StringUtil.parseLastSlash((String)(systemMapList.get(index)).get("OLD_FILE_PATH"));
	}
	
	public String getNewFilePath(int index) {
		return StringUtil.parseLastSlash((String)(systemMapList.get(index)).get("NEW_FILE_PATH"));
	}
	
	
	public String getOutFileName(int index) {
		return StringUtil.parseLastSlash((String)(systemMapList.get(index)).get("OUT_FILE_NAME"));
	}
}
