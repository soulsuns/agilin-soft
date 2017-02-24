package diff.report;

import java.io.IOException;

import diff.file.FileManager;
import diff.util.DiffConfigLoader;
import diff.util.LogWriter;

public class HmlReport {
	
	private static FileManager fm = new FileManager();
	private DiffConfigLoader confInstance = null;
	
	private String header;
	private String footer;
	private String section1;
	private String tableHeader;
	private String tableFooter;

	
	public HmlReport() {
		
		try {
			confInstance = DiffConfigLoader.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
			LogWriter.printStackTreace(e);
		}
		
		header = fm.getFileText(confInstance.getCommonMapValue("HML_HEADER_PATH"));
		footer = fm.getFileText(confInstance.getCommonMapValue("HML_FOOTER_PATH"));
		section1 = fm.getFileText(confInstance.getCommonMapValue("HML_SECTION1_PATH"));
		tableHeader = fm.getFileText(confInstance.getCommonMapValue("HML_TABLE_HEADER_PATH"));
		tableFooter = fm.getFileText(confInstance.getCommonMapValue("HML_TABLE_FOOTER_PATH"));
	}
	
	public String getHeader() {
		return header;
	}
	
	public String getSection1() {
		return section1;
	}
	
	
	// paraShape 가 14는 경로, 15는 테이블 텍스트(바탕,9point)
	// charShape 가 1이면 일반, 10이면 파랑 진하게
	public String getText(String text, String paraShape, String charShape) {
		
		return "<P ParaShape=\""+paraShape+"\" Style=\"0\"><TEXT CharShape=\""+charShape+"\"><CHAR>"+text+"</CHAR></TEXT></P>";
	}
	
	// paraShape 은 14, charShape 은 1
	public String getFirstText(String text, String paraShape, String charShape) {
		
		return "<P ColumnBreak=\"false\" PageBreak=\"false\" ParaShape=\""+paraShape+"\" Style=\"0\"><TEXT CharShape=\""+charShape+"\">"+getSection1()+"<CHAR>"+text+"</CHAR></TEXT></P>";
	}
	
	public String getFooter() {
		return footer;
	}
	
	public String getTableHeader() {
		return tableHeader;
	}
	
	public String getTableFooter() {
		return tableFooter;
	}
	
	public String getTableLine(String paraShape, String charShape) {
		return "<P ParaShape=\""+paraShape+"\" Style=\"0\"><TEXT CharShape=\""+charShape+"\"/></P>";
	}
	
	public String getLine(String paraShape, String style) {
		return "<P ParaShape=\""+paraShape+"\" Style=\""+style+"\"/>";
	}
	
	
}
