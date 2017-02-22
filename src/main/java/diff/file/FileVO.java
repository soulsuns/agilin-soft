package diff.file;

public class FileVO {

	private String fileName;
	private String relativeFilePath;	// 상대파일경로
	private String absFilePath;	// 절대파일경로
	private String relativePath;		// 상대경로
	private String absPath;		// 절대경로

	public FileVO() {
		
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

	public String getAbsFilePath() {
		return absFilePath;
	}

	public void setAbsFilePath(String absFilePath) {
		this.absFilePath = absFilePath;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsPath() {
		return absPath;
	}

	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}
	
	public String toString() {
		
		return "fileName:" + fileName + ", relativeFilePath:" + relativeFilePath + ", absFilePath:" + absFilePath + ", relativePath:" + relativePath + ", absPath:" + absPath;
	}
	
}
