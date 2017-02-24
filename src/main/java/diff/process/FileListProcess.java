package diff.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import diff.core.Diff_match_patch;
import diff.core.Diff_match_patch.Diff;
import diff.file.FileManager;
import diff.file.FileVO;
import diff.util.FileListConfigLoader;
import diff.util.LogWriter;

/**
 * @to-do ����/���� ����ó��
 * @author C4IUSER
 *
 */
public class FileListProcess {
	
	private FileManager fm = null;
	private ArrayList<FileVO> fileList = null;
	
	private FileListConfigLoader confInstance = null;
	
	public FileListProcess() {
		
		fm = new FileManager();
		
		try {
			confInstance = FileListConfigLoader.init();
		} catch(Exception e) {
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
	}
	
	public void start() {
		
		
		try {
			this.printOpeningView();
			LogWriter.clear();
	
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
				System.out.println("****** " +(ii+1)+ "��° ���ϸ���Ʈ �ۼ� ����");
				fileList= new ArrayList<FileVO>();
	
				// out ���� ����(������ ������)
				FileManager.makeDirs(confInstance.getOutFilePath(ii));
			
				System.out.println("- ���� ���ϸ���Ʈ Ž�� ���� (��ġ : "+confInstance.getNewFilePath(ii)+" ���� ����)");
				LogWriter.info("\n\n================ �������� ����Ʈ ���� : ���͸�� ================");
				fm.createFolderFileListAddFilter(fileList, confInstance.getNewFilePath(ii));
				System.out.println("- ���� ���ϸ���Ʈ Ž�� ����");
				
				System.out.println("- ����� ���ϸ���Ʈ ���� ����");
				this.makeDiffList(ii);
				System.out.println("- ����� ���ϸ���Ʈ ���� ����");
				
				System.out.println("- ����� ���ϸ���Ʈ �ۼ� �Ϸ� (�ۼ���ġ : "+confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii)+")\n");
				
			}
	
			this.printClosingView();
		} catch(Exception e) {
			System.out.println("�ۼ� ����!!!! ( log/debug.log Ȯ�� )");
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
	
	
	public void printOpeningView() {
		
		System.out.println("\n\n���������������������������");
		System.out.println("�� 1. �������� ����Ʈ ����");
		System.out.println("���������������������������");
		System.out.println("�� ���� ������ ������ο� ���ϸ��� ������ �����ϴ�.");
		System.out.println("�� (resource/conf/FileList.conf ���� ���� ����)");
		
		for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
			System.out.println("�� �� " +(ii+1)+ "��° ����");
			System.out.println("��   - ������ ���� ��� ��ġ : " + confInstance.getOldFilePath(ii));
			System.out.println("��   - ������ ǧ�� ��� ��ġ : " + confInstance.getNewFilePath(ii));
			System.out.println("��   - �������� ����Ʈ ���/���ϸ� : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
		}
		
		System.out.println("���������������������������\n");
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("�غ� �Ǽ����� ���͸� �����ʽÿ�.");
			reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
			
		}
	}
	
	public void printClosingView() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\n����������� �������� ����Ʈ ���� ���� �����������");
			
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
				System.out.println("- " +(ii+1)+ "��° �������� ����Ʈ �ۼ� ��ġ : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			}
			
			System.out.println("\n���͸� ������ �޴��� ���ư��ϴ�.");
			reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
	
	public void makeDiffList(int index) {

		ArrayList<String> changedFileList = new ArrayList<String>();

		for(int ii=0; ii<fileList.size(); ii++) {
			FileVO fVo = fileList.get(ii);
			
			String filePath = this.getChangedFilePath(fVo, index);
			
			if(!"".equals(filePath)) {
				changedFileList.add(filePath);
			}
			
		}
		
		this.createFileListReport(index, changedFileList);

	}
	
	public void createFileListReport(int index, ArrayList<String> changedFileList) {
		
		PrintWriter fw = fm.writeFileLoad(confInstance.getOutFilePath(index)+"/"+confInstance.getOutFileName(index), "EUC-KR", false);
		
		try {
			
			fw.write("***** ���� ����Ʈ(fullPath) *****\n\n");
			for(int ii=0; ii<changedFileList.size(); ii++) {
				String filePath = changedFileList.get(ii);
				fw.write(filePath+"\n");
			}
			
			fw.flush();
			
		}catch(Exception e){
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}finally{
			
			// FileWriter�� �ݾ��ش�.
			if(fw != null) try{fw.close();}catch(Exception e){System.out.println("���ϴݱ� ����");}
		}
	}
	
	
	public String getChangedFilePath(FileVO fVo, int index) {
		
		String newPath = fVo.getRelativeFilePath();
		String oldPath = newPath.replaceFirst(confInstance.getNewFilePath(index), confInstance.getOldFilePath(index));
		
		if(!fm.isFileExist(oldPath)) {
			return newPath;	// �ű�����
		}

/*		String filterResult = this.filterFile(oldPath, fVo.getFileName());
		
		if(!"".equals(filterResult)) {		// ���͸� Ȯ���ڿ� ���ԵǾ� �ִٸ� �ش� ���ϼ����� ����
			ArrayList<String> filterTextList = new ArrayList<String>();
			filterTextList.add(filterResult);
			return filterTextList;
		}*/
		
		Diff_match_patch dmp = new Diff_match_patch();
		LinkedList<Diff> diffs = dmp.diff_main(fm.getFileText(oldPath), fm.getFileText(newPath), false);

		if(diffs.size() > 1)
			return newPath;
		
		return "";
	}
	
	
	/**
	 * 
	 * @param oldPath
	 * @param fileName
	 * @return ������ ��ȯ�ϸ� ���͸� ����, ������ ��ȯ�ϸ� ���͸� ���
	 */
	public String filterFile(String oldPath, String fileName) {
		
		if(!fm.isFileExist(oldPath)) {
			return "~�ű� ����~";
		}
		
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

//	public static void main(String[] args) {
//		
//		FileManager fm2 = new FileManager();
//		Diff_match_patch dmp = new Diff_match_patch();
//		LinkedList<Diff> diffs = dmp.diff_main(fm2.getFileText("E:\\diff\\index_old.html"), fm2.getFileText("E:\\diff\\index_new.html"), false);
//
//		ArrayList<String> test = dmp.diff_LineTextList(diffs);
//		
//		for(int ii=0; ii<test.size(); ii++) {
//			System.out.println(test.get(ii)+"\n");
//		}
//	}
}
