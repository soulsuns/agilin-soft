package diff.process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import diff.core.Diff_match_patch;
import diff.core.Diff_match_patch.Diff;
import diff.file.FileManager;
import diff.file.FileVO;
import diff.report.HmlReport;
import diff.util.ConfigLoader;
import diff.util.LogWriter;

public class ProcessFactory {
	
	
	private ConfigLoader confInstance = null;
	
	public ProcessFactory() {
		try {
			LogWriter.clear();
			confInstance = ConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
	}
	
	public void start() {
		
		// new 폴더에서 파일전체경로 및 파일명 알아오기
		/** @to-do fileList에 Map 형태로 각 타입을 넣는것으로 변경하자
		 */
		try {
			this.printOpeningView();
			
			int menuNo = 0;
			
			do {
				
				menuNo = this.selectMenu();
				
				switch(menuNo) {
					case 1:
						(new FileListProcess()).start();
						break;
					case 2:
						(new FileCopyProcess()).start();
						break;
					case 3:
						(new DiffProcess()).start();
						break;
					default:
						break;
				}
			} while(menuNo != 0);
			
			this.printClosingView();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void printOpeningView() {
		
		System.out.println("\n〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("■ 형상변경요청서 작성 프로그램(v"+confInstance.getCommonMapValue("VERSION")+") 입니다.");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("· 주요기능은 다음과 같습니다.");
		System.out.println("·  1. 변경파일 리스트 생성");
		System.out.println("·     - 변경전/후 파일들을 비교하여 변경된 파일리스트를 생성해줍니다.");
		System.out.println("·  2. 요구사항별 OUT 파일 생성");
		System.out.println("·     - 요구사항별로 정리된 파일 리스트를 참조하여 지정된 경로로 ");
		System.out.println("·       최종 변경된 파일을 복사합니다.");
		System.out.println("·  3. 형상변경내역서 작성");
		System.out.println("·     - 요구사항별 OUT파일을 대상으로 변경전 파일과 비교하여 비교결과를");
		System.out.println("·       한글문서로 자동 작성합니다.");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓\n");

	}
	
	public int selectMenu() {
		int menuNo = 0;
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("■ 메뉴를 선택하세요.");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("·  1. 변경파일 리스트 생성");
		System.out.println("·  2. 요구사항별 OUT 파일 생성");
		System.out.println("·  3. 형상변경내역서 작성");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.println("·  0. 종료");
		System.out.println("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
		System.out.print("■  선택 : ");
		
		Scanner input = new Scanner(System.in);
		menuNo = input.nextInt();
		
		return menuNo;
	}
	
	public void printClosingView() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\n■■■■■■■■■■ 형상변경요청서 작성 프로그램 종료 ■■■■■■■■■■");
			System.out.println("\n엔터를 누르면 창이 닫힙니다.");
			reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
}
