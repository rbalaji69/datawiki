package in.ithihas.wikidata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PatternService {
	// looking for the string that looks like: ".... reign = {{circa 901 | 930 }} ... "
	private static Pattern reignPattern1 = Pattern.compile("reign\\s*=\\s*\\{\\{[a-zA-Z0-9\\|\\s\\\\]*\\}\\}"); 
	//private static Pattern reignPattern2 = Pattern.compile("reign\\s*=\\s*[a-zA-Z0-9\\s\\-\\\\&();]*\\|"); 
	private static Pattern reignPattern2 = Pattern.compile("reign\\s*=\\s*[^\\|]*\\|"); 
	private static Pattern innerPattern1 = Pattern.compile("\\{\\{[a-zA-Z0-9\\|\\s\\\\]*\\}\\}");
	
	private static Pattern datePattern = Pattern.compile("[0-9]{1,3}[\\s]+[a-zA-Z]+[\\s]+[0-9]{1,5}[BCE]{2,4}"); 
	
	//private Matcher matcher; 
	
	public Pattern getReignPattern1 () {
		return reignPattern1; 
	}
	
	public Pattern getReignPattern2() {
		return reignPattern2; 
	}
	
	public String cleanupReignString(String reignString) {
		String result = null; 
		/* Find first occurrence of '=' and extract substring after that first '=.'
		 * then remove leading and trailing spaces, replace occurrences of \u2013 with '-', and 
		 * '|', '\n' with ' '. 
		 */
		int equalsIndex = reignString.indexOf('='); 
		if(equalsIndex>=0) {
			result = reignString.substring(equalsIndex+1).trim(); 
			result = result.replace("\\u2013", "-").replace("&nbsp", " ").replace("\\n", " ").replace(';', ' '); 
		}
		
		return result; 
	}

	public Pattern getInnerReignPattern1() {
		// TODO Auto-generated method stub
		return innerPattern1;
	}
	
	
}
