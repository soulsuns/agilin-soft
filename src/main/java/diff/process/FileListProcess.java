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
 * @to-do 파일/폴더 필터처리
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
				System.out.println("****** " +(ii+1)+ "번째 파일리스트 작성 시작");
				fileList= new ArrayList<FileVO>();
	
				// out 폴더 생성(폴더가 없을시)
				FileManager.makeDirs(confInstance.getOutFilePath(ii));
			
				System.out.println("- 비교할 파일리스트 탐색 시작 (위치 : "+confInstance.getNewFilePath(ii)+" 폴더 이하)");
				LogWriter.info("\n\n================ 변경파일 리스트 생성 : 필터목록 ================");
				fm.createFolderFileListAddFilter(fileList, confInstance.getNewFilePath(ii));
				System.out.println("- 비교할 파일리스트 탐색 종료");
				
				System.out.println("- 변경된 파일리스트 생성 시작");
				this.makeDiffList(ii);
				System.out.println("- 변경된 파일리스트 생성 종료");
				
				System.out.println("- 변경된 파일리스트 작성 완료 (작성위치 : "+confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii)+")\n");
				
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
		System.out.println("■ 1. 변경파일 리스트 생성");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("· 현재 지정된 폴더경로와 파일명은 다음과 같습니다.");
		System.out.println("· (resource/conf/FileList.conf 에서 설정 가능)");
		
		for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
			System.out.println("· ■ " +(ii+1)+ "번째 내역");
			System.out.println("·   - 변경전 폴더 경로 위치 : " + confInstance.getOldFilePath(ii));
			System.out.println("·   - 변경후 푤더 경로 위치 : " + confInstance.getNewFilePath(ii));
			System.out.println("·   - 변경파일 리스트 경로/파일명 : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
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
			System.out.println("\n■■■■■■■■■■ 변경파일 리스트 생성 종료 ■■■■■■■■■■");
			
			for(int ii=0; ii<confInstance.getDiffSize(); ii++) {
				System.out.println("- " +(ii+1)+ "번째 변경파일 리스트 작성 위치 : " + confInstance.getOutFilePath(ii)+"/"+confInstance.getOutFileName(ii));
			}
			
			System.out.println("\n엔터를 누르면 메뉴로 돌아갑니다.");
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
			
			fw.write("***** 파일 리스트(fullPath) *****\n\n");
			for(int ii=0; ii<changedFileList.size(); ii++) {
				String filePath = changedFileList.get(ii);
				fw.write(filePath+"\n");
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
	
	
	public String getChangedFilePath(FileVO fVo, int index) {
		
		String newPath = fVo.getRelativeFilePath();
		String oldPath = newPath.replaceFirst(confInstance.getNewFilePath(index), confInstance.getOldFilePath(index));
		
		if(!fm.isFileExist(oldPath)) {
			return newPath;	// 신규파일
		}

/*		String filterResult = this.filterFile(oldPath, fVo.getFileName());
		
		if(!"".equals(filterResult)) {		// 필터링 확장자에 포함되어 있다면 해당 파일설명을 리턴
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
	 * @return 공백을 반환하면 필터링 제외, 내용을 반환하면 필터링 대상
	 */
	public String filterFile(String oldPath, String fileName) {
		
		if(!fm.isFileExist(oldPath)) {
			return "~신규 파일~";
		}
		
		HashMap<String, String> filterMap = fm.getFileText2("resource/excludeFiles.lst");

		String exValue = filterMap.get((fileName.substring(fileName.lastIndexOf(".")+1)).toUpperCase());	// null 이면 필터에 포함되지 않은 파일
		
//		System.out.println("exValue : "+exValue);
		
		if(exValue == null) {
			//바이너리 파일 체크
//			try {
//				if(FileManager.isBinaryFile(new File(oldPath)))
//					return "~바이너리 파일~";
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
