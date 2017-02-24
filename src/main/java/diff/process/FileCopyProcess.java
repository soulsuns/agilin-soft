package diff.process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import diff.report.HmlReport;
import diff.util.FileCopyConfigLoader;
import diff.util.LogWriter;

public class FileCopyProcess {
	
	private FileManager fm = null;
	private ArrayList<FileVO> fileList = null;
	
	private FileCopyConfigLoader confInstance = null;
	
	public FileCopyProcess() {
		
		fm = new FileManager();
		
		try {
			confInstance = FileCopyConfigLoader.init();
		} catch(Exception e) {
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
	}
	
	public void start() {
		
		try {
			this.printOpeningView();
			LogWriter.clear();
	
			LogWriter.info("\n\n================ �䱸���׺� OUT ���� ���� ================");
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
				System.out.println("****** " +(ii+1)+ "��° �䱸���� ���� ����");
	
				FileManager.makeDirs(confInstance.getOutFilePath(ii));
				
				// out ���� ����(������ ������) jdk1.6 ���Ͽ��� ���
	//			fm.makeDir4List(confInstance.getFileCopyList(ii), confInstance.getOutFilePath(ii), confInstance.getStartCopyPath(ii));
	//			this.createBatchFile(ii);
				
				this.copyOutFile(ii);
				
				
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
		System.out.println("�� 2. �䱸���׺� OUT ���� ����");
		System.out.println("���������������������������");
		System.out.println("�� ���� ������ OUT ������δ� ������ �����ϴ�.");
		System.out.println("�� (resource/conf/FileCopy.conf ���� ���� ����)");
		
		for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
			System.out.println("�� �� " +(ii+1)+ "��° �䱸����");
			System.out.println("��   - ����� ������ ��� : " + confInstance.getOutFilePath(ii));
			System.out.println("��   - �������� ���� ������ ���� ��� : " + confInstance.getStartCopyPath(ii));
//			for(int jj=0; jj<(confInstance.getFileCopyList(ii)).size(); jj++) {
//				System.out.println("*   - "+fm.getFileFullPath((confInstance.getFileCopyList(ii)).get(jj)));
//			}
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
			System.out.println("\n����������� �䱸���׺� OUT ���� ���� ���� �����������");
			
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
//				System.out.println("- " +(ii+1)+ "��° ���󺯰泻���� �ۼ� ��ġ : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			}
			
			System.out.println("\n���͸� ������ �޴��� ���ư��ϴ�.");
			reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
	
	public void createBatchFile(int index) {
		
		PrintWriter fw = fm.writeFileLoad("resource/bat/req"+index+".bat", "EUC-KR", false);
		
		try {
			
//			for(int ii=0; ii<changedFileList.size(); ii++) {
//				String filePath = changedFileList.get(ii);
//				fw.write(filePath+"\n");
//			}
			
			ArrayList<String> fileCopyList = confInstance.getFileCopyList(index);
			
			String outPath = fm.getFileFullPath(confInstance.getOutFilePath(index)).replace("\\", "/");
			String startPath = fm.getFileFullPath(confInstance.getStartCopyPath(index)).replace("\\", "/");
			
			// outPath = outPath + oldPath.replaceFirst(startPath, "");
			
			for(int jj=0; jj<fileCopyList.size(); jj++) {
				String oldPath = fm.getFileFullPath(fileCopyList.get(jj));
				
				fw.write("copy "+ oldPath + " " + (outPath + (oldPath.replace("\\", "/")).replaceFirst(startPath, "")).replaceAll("/", "\\\\") + "\n");
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
	
	public void copyOutFile(int index) {
		
		ArrayList<String> fileCopyList = confInstance.getFileCopyList(index);
		
		LogWriter.info("\n"+(index+1)+"��° �䱸���� ���� ����Ʈ");
		String outPath = confInstance.getOutFilePath(index);
		String startPath = confInstance.getStartCopyPath(index);
		String allOutPath = confInstance.getCommonMapValue("ALL_OUT_FILE_PATH");
		
		// outPath = outPath + oldPath.replaceFirst(startPath, "");
		for(int jj=0; jj<fileCopyList.size(); jj++) {
			String oldPath = fileCopyList.get(jj);
			LogWriter.info(" - "+(outPath + oldPath.replaceFirst(startPath, "")));
			fm.copyFile2(oldPath, outPath + oldPath.replaceFirst(startPath, ""));
			
			if(allOutPath != null)
				fm.copyFile2(oldPath, allOutPath + oldPath.replaceFirst(startPath, ""));
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
