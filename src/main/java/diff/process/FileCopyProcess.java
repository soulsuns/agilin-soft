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
	
			LogWriter.info("\n\n================ 요구사항별 OUT 파일 생성 ================");
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
				System.out.println("****** " +(ii+1)+ "번째 요구사항 파일 복사");
	
				FileManager.makeDirs(confInstance.getOutFilePath(ii));
				
				// out 폴더 생성(폴더가 없을시) jdk1.6 이하에서 사용
	//			fm.makeDir4List(confInstance.getFileCopyList(ii), confInstance.getOutFilePath(ii), confInstance.getStartCopyPath(ii));
	//			this.createBatchFile(ii);
				
				this.copyOutFile(ii);
				
				
			}
			this.printClosingView();
		} catch(Exception e) {
			System.out.println("작성 실패!!!! ( log/debug.log 확인 )");
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
	
	public void printOpeningView() {
		
		System.out.println("\n\n〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("■ 2. 요구사항별 OUT 파일 생성");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("· 현재 지정된 OUT 폴더경로는 다음과 같습니다.");
		System.out.println("· (resource/conf/FileCopy.conf 에서 설정 가능)");
		
		for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
			System.out.println("· ■ " +(ii+1)+ "번째 요구사항");
			System.out.println("·   - 복사될 목적지 경로 : " + confInstance.getOutFilePath(ii));
			System.out.println("·   - 원본파일 기준 복사할 시작 경로 : " + confInstance.getStartCopyPath(ii));
//			for(int jj=0; jj<(confInstance.getFileCopyList(ii)).size(); jj++) {
//				System.out.println("*   - "+fm.getFileFullPath((confInstance.getFileCopyList(ii)).get(jj)));
//			}
		}
		
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓\n");
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("준비가 되셨으면 엔터를 누르십시오.");
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
			System.out.println("\n■■■■■■■■■■ 요구사항별 OUT 파일 생성 종료 ■■■■■■■■■■");
			
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
//				System.out.println("- " +(ii+1)+ "번째 형상변경내역서 작성 위치 : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			}
			
			System.out.println("\n엔터를 누르면 메뉴로 돌아갑니다.");
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
			
			// FileWriter를 닫아준다.
			if(fw != null) try{fw.close();}catch(Exception e){System.out.println("파일닫기 오류");}
		}
	}
	
	public void copyOutFile(int index) {
		
		ArrayList<String> fileCopyList = confInstance.getFileCopyList(index);
		
		LogWriter.info("\n"+(index+1)+"번째 요구사항 파일 리스트");
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
