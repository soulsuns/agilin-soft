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
		
		// new �������� ������ü��� �� ���ϸ� �˾ƿ���
		/** @to-do fileList�� Map ���·� �� Ÿ���� �ִ°����� ��������
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
		
		System.out.println("\n���������������������������");
		System.out.println("�� ���󺯰��û�� �ۼ� ���α׷�(v"+confInstance.getCommonMapValue("VERSION")+") �Դϴ�.");
		System.out.println("���������������������������");
		System.out.println("�� �ֿ����� ������ �����ϴ�.");
		System.out.println("��  1. �������� ����Ʈ ����");
		System.out.println("��     - ������/�� ���ϵ��� ���Ͽ� ����� ���ϸ���Ʈ�� �������ݴϴ�.");
		System.out.println("��  2. �䱸���׺� OUT ���� ����");
		System.out.println("��     - �䱸���׺��� ������ ���� ����Ʈ�� �����Ͽ� ������ ��η� ");
		System.out.println("��       ���� ����� ������ �����մϴ�.");
		System.out.println("��  3. ���󺯰泻���� �ۼ�");
		System.out.println("��     - �䱸���׺� OUT������ ������� ������ ���ϰ� ���Ͽ� �񱳰����");
		System.out.println("��       �ѱ۹����� �ڵ� �ۼ��մϴ�.");
		System.out.println("���������������������������\n");

	}
	
	public int selectMenu() {
		int menuNo = 0;
		System.out.println("���������������������������");
		System.out.println("�� �޴��� �����ϼ���.");
		System.out.println("���������������������������");
		System.out.println("��  1. �������� ����Ʈ ����");
		System.out.println("��  2. �䱸���׺� OUT ���� ����");
		System.out.println("��  3. ���󺯰泻���� �ۼ�");
		System.out.println("���������������������������");
		System.out.println("��  0. ����");
		System.out.println("���������������������������");
		System.out.print("��  ���� : ");
		
		Scanner input = new Scanner(System.in);
		menuNo = input.nextInt();
		
		return menuNo;
	}
	
	public void printClosingView() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\n����������� ���󺯰��û�� �ۼ� ���α׷� ���� �����������");
			System.out.println("\n���͸� ������ â�� �����ϴ�.");
			reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
	}
}
