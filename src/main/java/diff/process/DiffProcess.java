package diff.process;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import diff.core.Diff_match_patch;
import diff.core.Diff_match_patch.Diff;
import diff.file.FileManager;
import diff.file.FileVO;
import diff.report.HmlReport;
import diff.util.ConfigLoader;

public class DiffProcess {
	
	private FileManager fm = null;
	private ArrayList<FileVO> fileList = null;
	private ArrayList<String> hmlReport = null;
	
	private ConfigLoader confInstance = null;
	
	private HmlReport hr = null;
	
	public DiffProcess() {
		
		fm = new FileManager();
		
		try {
			confInstance = ConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		hr = new HmlReport();
		
	}
	
	public void start() {
		
		// new �������� ������ü��� �� ���ϸ� �˾ƿ���
		/** @to-do fileList�� Map ���·� �� Ÿ���� �ִ°����� ��������
		 * 
		 */
		
		for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
			fileList= new ArrayList<FileVO>();
			FileManager.makeDirs(confInstance.getOutFilePath(ii));
		
			fm.createFolderFileList(fileList, confInstance.getNewFilePath(ii));
			
			// ���ϰ�� ������ ���� ����
			System.out.println("***** ���ϸ���Ʈ ���� ���� : "+confInstance.getNewFilePath(ii));
			this.createFileListReport(ii);
			System.out.println("***** ���ϸ���Ʈ ���� ����");
			
	//		for(int ii=0; ii<fileFullPaths.size(); ii++) {
	//			ArrayList<String> changedTextList = this.getChangedTextList(fileFullPaths.get(ii), fileFullPaths.get(ii));
	//			this.createHmlReport(changedTextList);
	//		}
			
			this.makeDiffList(ii);
			
			FileManager.deleteFile(confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			FileManager.copyFile("resource/template/template.hml", confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			this.createDiffResultReport(confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
		
		}	
		System.out.println("***** ���Ϻ� ���� *****");
		
	}
	
	public void makeDiffList(int index) {
		hmlReport = new ArrayList<String>();
		
		hmlReport.add(hr.getHeader());
		
		for(int ii=0; ii<fileList.size(); ii++) {
			FileVO fVo = fileList.get(ii);
			
			ArrayList<String> changedTextList = this.getChangedTextList(fVo, index);
			
			if(changedTextList == null) {
				changedTextList = new ArrayList<String>();
				changedTextList.add("~�ű�����~");
			} else if(changedTextList.size() == 0) {
				changedTextList.add("������");
//				continue;
			} else {
				String newPath = fVo.getRelativeFilePath();
				String oldPath = newPath.replaceFirst(confInstance.getNewFilePath(index), confInstance.getOldFilePath(index));
				String filterResult = this.filterFile(oldPath, fVo.getFileName());
				if(!"".equals(filterResult)) {		// ���͸� Ȯ���ڿ� ���ԵǾ� �ִٸ� �ش� ���ϼ����� ����
					changedTextList.clear();
					changedTextList = new ArrayList<String>();
					changedTextList.add(filterResult);
				} else {
					try {
						if(!FileManager.isAsciiText(newPath)) {
							changedTextList.clear();
							changedTextList = new ArrayList<String>();
							changedTextList.add("~���̳ʸ� ����~");
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			this.createHmlReport(changedTextList, fVo.getRelativeFilePath().replaceFirst(confInstance.getNewFilePath(index), ""), ii);
		}
		
		hmlReport.add(hr.getFooter());
		
//		for(int ii=0; ii<hmlReport.size(); ii++) {
//			System.out.println(hmlReport.get(ii));
//		}

	}
	
	public void createDiffResultReport(String outFilePath) {
		
//		PrintWriter fw = fm.writeFileLoad("out/diffReport.hml", "EUC-KR", false);
//		PrintWriter fw = fm.writeFileLoad(outFilePath, "UTF-8", true);
		
		PrintWriter fw = fm.writeFileLoad(outFilePath, "UTF-8", true);
		
		try {
			
			for(int ii=0; ii<hmlReport.size(); ii++) {
				fw.write(hmlReport.get(ii)+"\n");
			}
			
			fw.flush();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			// FileWriter�� �ݾ��ش�.
			if(fw != null) try{fw.close();}catch(Exception e){System.out.println("���ϴݱ� ����");}
		}
	}
	
	public void createFileListReport(int index) {
		
		PrintWriter fw = fm.writeFileLoad(confInstance.getOutFilePath(index)+"/fileList"+(index+1)+".csv", "EUC-KR", false);
		
		try {
			
			fw.write("***** ���� ����Ʈ(fullPath) *****\n\n");
			for(int ii=0; ii<fileList.size(); ii++) {
				FileVO fVo = fileList.get(ii);
				fw.write(fVo.getRelativeFilePath().replaceFirst(confInstance.getNewFilePath(index), "")+"\n");
			}
			
			fw.write("\n\n----------------------------------------------------------------------\n\n\n");
			fw.write("***** ���� ��ο� ���ϸ� *****\n\n");
			
			for(int ii=0; ii<fileList.size(); ii++) {
				FileVO fVo = fileList.get(ii);
				
				fw.write(fVo.getRelativePath().replaceFirst(confInstance.getNewFilePath(index), "")+","+fVo.getFileName()+"\n");
			}
			
			fw.write("\n\n----------------------------------------------------------------------\n\n\n");
			fw.write("***** ���ϸ� *****\n\n");
			
			for(int ii=0; ii<fileList.size(); ii++) {
				FileVO fVo = fileList.get(ii);
				
				fw.write(fVo.getFileName()+","+fVo.getFileName().replace(".java", ".class")+"\n");
			}
			
			fw.flush();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			// FileWriter�� �ݾ��ش�.
			if(fw != null) try{fw.close();}catch(Exception e){System.out.println("���ϴݱ� ����");}
		}
	}
	
	public void createHmlReport(ArrayList<String> changedTextList, String filePath, int index) {
		
//		System.out.println(HmlReport.getHeader());
		
		if(index == 0)
			hmlReport.add(hr.getFirstText(filePath, confInstance.getCommonMapValue("HML_PATH_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_PATH_VALUE").split(",")[1]));
		else
			hmlReport.add(hr.getText(filePath, confInstance.getCommonMapValue("HML_PATH_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_PATH_VALUE").split(",")[1]));
//		System.out.println(HmlReport.getFirstText("/path01/path01/file01.java", 14, 1));
		hmlReport.add(hr.getTableHeader());
//		System.out.println(HmlReport.getTableHeader());
		for(int ii=0; ii<changedTextList.size(); ii++) {
	    	String text = changedTextList.get(ii);
	    	if(text.indexOf("<Line>") != -1) {
	    		hmlReport.add(hr.getTableLine(confInstance.getCommonMapValue("HML_TABLE_BR_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_TABLE_BR_VALUE").split(",")[1]));
	    		hmlReport.add(hr.getText(text.replace("<Line>", "Line"), confInstance.getCommonMapValue("HML_LINENUM_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_LINENUM_VALUE").split(",")[1]));
//	    		System.out.println(HmlReport.getText(text.replace("<Line>", "Line"), 13, 6));
	    	} else {
	    		hmlReport.add(hr.getText(text, confInstance.getCommonMapValue("HML_TEXT_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_TEXT_VALUE").split(",")[1]));
//	    		System.out.println(HmlReport.getText(text, 13, 1));
//	    		hmlReport.add(HmlReport.getTableLine(15, 9));
//	    		System.out.println(HmlReport.getLine());
	    	}
	    }
		hmlReport.add(hr.getTableFooter());
//		System.out.println(HmlReport.getTableFooter());
		hmlReport.add(hr.getLine(confInstance.getCommonMapValue("HML_BR_VALUE").split(",")[0], confInstance.getCommonMapValue("HML_BR_VALUE").split(",")[1]));
//		System.out.println(HmlReport.getLine());
		
//		System.out.println(HmlReport.getFooter());
		
//		System.out.println(HmlReport.getHeader());
//		
//		System.out.println(HmlReport.getFirstText("/path01/path01/file01.java", 14, 1));
//		System.out.println(HmlReport.getTableHeader());
//		System.out.println(HmlReport.getText("Line 13 ~ 15", 13, 6));
//		System.out.println(HmlReport.getText("����01", 13, 1));
//		System.out.println(HmlReport.getTableFooter());
//		
//		System.out.println(HmlReport.getLine());
//		
//		System.out.println(HmlReport.getText("/path02/path02/file02.java", 14, 1));
//		System.out.println(HmlReport.getTableHeader());
//		System.out.println(HmlReport.getText("Line 17 ~ 18", 13, 6));
//		System.out.println(HmlReport.getText("����02", 13, 1));
//		System.out.println(HmlReport.getTableFooter());
//		
//		System.out.println(HmlReport.getLine());
//		
//		System.out.println(HmlReport.getFooter());

	}
	
	public ArrayList<String> getChangedTextList(FileVO fVo, int index) {
		
		String newPath = fVo.getRelativeFilePath();
		String oldPath = newPath.replaceFirst(confInstance.getNewFilePath(index), confInstance.getOldFilePath(index));
		
		if(!fm.isFileExist(oldPath)) {
			return null;	// �ű�����
		}
		
//		System.out.println("newPath : "+newPath+", oldPath : " + oldPath);

/*		String filterResult = this.filterFile(oldPath, fVo.getFileName());
		
		if(!"".equals(filterResult)) {		// ���͸� Ȯ���ڿ� ���ԵǾ� �ִٸ� �ش� ���ϼ����� ����
			ArrayList<String> filterTextList = new ArrayList<String>();
			filterTextList.add(filterResult);
			return filterTextList;
		}*/
		
		Diff_match_patch dmp = new Diff_match_patch();
		LinkedList<Diff> diffs = dmp.diff_main(fm.getFileText(oldPath), fm.getFileText(newPath), true);
//		dmp.diff_cleanupSemantic(diffs);
		dmp.diff_cleanupSemanticLossless(diffs);
		
		System.out.println("----------------------\n"+diffs+"\n----------------------\n");

		return dmp.diff_LineTextList(diffs);
	}
	
	
	/**
	 * 
	 * @param oldPath
	 * @param fileName
	 * @return ������ ��ȯ�ϸ� ���͸� ����, ������ ��ȯ�ϸ� ���͸� ���
	 */
	public String filterFile(String oldPath, String fileName) {
		
//		if(!fm.isFileExist(oldPath)) {
//			return "~�ű� ����~";
//		}
		
		HashMap<String, String> filterMap = fm.getFileText2("resource/excludeFiles.lst");

		String exValue = filterMap.get((fileName.substring(fileName.lastIndexOf(".")+1)).toUpperCase());	// null �̸� ���Ϳ� ���Ե��� ���� ����
		
//		System.out.println("exValue : "+exValue);
		
		if(exValue == null) {
			//���̳ʸ� ���� üũ
//			try {
//				if(FileManager.isBinaryFile(new File(oldPath)))
//					return "~���̳ʸ� ����~";
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return "";
		} else {
			return "~"+exValue+"~";
		}
	}
	
//	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		DiffProcess process = new DiffProcess();

//		try {
//			System.out.println(FileManager.getEncoding("in/new/cc/comDamage/biz/comDamageManager/test.java"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(process.filterFile("C:\\Settings.ini","Settings.exe"));
//	}

	public static void main(String[] args) {
		
		FileManager fm2 = new FileManager();
		Diff_match_patch dmp = new Diff_match_patch();
		LinkedList<Diff> diffs = dmp.diff_main(fm2.getFileText("D:\\Tools\\sts-agilin_home\\workspace\\Diff_1_1\\in\\old\\mapperUCAT0010.java"), fm2.getFileText("D:\\Tools\\sts-agilin_home\\workspace\\Diff_1_1\\in\\new\\mapperUCAT0010.java"), false);
//		dmp.diff_cleanupSemantic(diffs);
		dmp.diff_cleanupSemanticLossless(diffs);
//		dmp.diff_cleanupEfficiency(diffs);
//		dmp.diff_cleanupMerge(diffs);;
		
		System.out.println(dmp.diff_prettyHtml(diffs));
		
//		ArrayList<String> test = dmp.diff_LineTextList(diffs);
//		
//		for(int ii=0; ii<test.size(); ii++) {
//			System.out.println(test.get(ii)+"\n");
//		}
	}
}