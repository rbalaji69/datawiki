package in.ithihas.wikidata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
	Logger log = LoggerFactory.getLogger(PatternService.class);
	
	// looking for the string that looks like: ".... reign = {{circa 901 | 930 }} ... "
	private static Pattern reignPattern1 = Pattern.compile("reign\\s*=\\s*\\{\\{[a-zA-Z0-9\\|\\s\\\\]*\\}\\}"); 
	//private static Pattern reignPattern2 = Pattern.compile("reign\\s*=\\s*[a-zA-Z0-9\\s\\-\\\\&();]*\\|"); 
	private static Pattern reignPattern2 = Pattern.compile("reign\\s*=\\s*[^\\|]*\\|"); 
	private static Pattern innerPattern1 = Pattern.compile("\\{\\{[a-zA-Z0-9\\|\\s\\\\]*\\}\\}");
	
	//pattern 2, which is something like: 10 August 1200
	private static Pattern dmyDatePattern = Pattern.compile("[0-9]{1,3}[\\s]+[a-zA-Z]+[\\s]+[0-9]{1,5}[\\s]*[BCE]*"); 
	
	//mdy pattern like: August 10, 1200
	private static Pattern mdyDatePattern = Pattern.compile("[a-zA-Z]+[\\s]+[0-9]{1,3}[,]*[\\s]+[0-9]{1,5}[\\s]*[BCE]*"); 
	private static Pattern myDatePattern = Pattern.compile("[a-zA-Z]+[\\s]+[0-9]{1,5}[\\s]*[BCE]*"); 
	private static Pattern yDatePattern = Pattern.compile("[0-9]{1,5}[\\s]*[BCE]*"); 
	
	//patter 3 has only the year, something like: 1200 - 1231
	private static Pattern datePattern3 = Pattern.compile("[0-9]{1,5}[\\s]*-[\\s]*[0-9]{1,5}[\\s]*[BCE]*"); 
	
	private static Pattern numberPattern = Pattern.compile("\\d+"); 
	
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
			result = result.replace("\\u2013", "-").replace("&ndash", "-")
							.replace("{{spaced ndash}}", "-").replace("{{snd}}", "-")
							.replace("&nbsp", " ").replace("\\n", " ").replace(';', ' '); 
		}
		
		return result; 
	}

	public Pattern getInnerReignPattern1() {
		// TODO Auto-generated method stub
		return innerPattern1;
	}

	public Pattern getDmyDatePattern() {
		return dmyDatePattern;
	}

	public Pattern getMyDatePattern() {
		return myDatePattern;
	}
	
	
	public Pattern getNumberPattern() {
		return numberPattern; 
	}

	public Pattern getyDatePattern() {
		return yDatePattern;
	}

	public Pattern getMdyDatePattern() {
		return mdyDatePattern;
	}
	
	
}
