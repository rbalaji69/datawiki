package in.ithihas.wikidata;

public class ReignDto {
	private String start; 
	private String end; 
	private int startCount=0; 
	private int endCount=0; 
	
	ReignDto(String start, String end, int startCount, int endCount) {
		this.start = start; 
		this.end = end; 
		this.startCount = startCount; 
		this.endCount = endCount; 
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public int getStartCount() {
		return startCount;
	}

	public int getEndCount() {
		return endCount;
	}
}
