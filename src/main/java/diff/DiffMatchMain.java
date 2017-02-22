package diff;

import java.util.LinkedList;

import diff.core.Diff_match_patch;
import diff.core.Diff_match_patch.Diff;
import diff.file.FileManager;
import diff.process.DiffProcess;

public class DiffMatchMain {

	public static void main(String[] args) {
		
		DiffProcess process = new DiffProcess();
		process.start();
	}
	
}

/**
 * @to-do 한글에 넣을때 탭처리 필요!!
 * 줄번호가 이상하게 나옴!!
 * 한글.hml 파일을 생성할때 .hml 양식을 만들어 놓고, 항상 복사해와서 true 로 생성할것!!
 */
