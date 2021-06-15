package in.ithihas.wikidata;

public class ReignDto {
	private String start; 
	private String end; 
	
	ReignDto(String start, String end) {
		this.start = start; 
		this.end = end; 
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}
}
