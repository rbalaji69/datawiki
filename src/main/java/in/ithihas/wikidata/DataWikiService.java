package in.ithihas.wikidata;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.ithihas.wikidata.model.WikiDataEntity;
import in.ithihas.wikidata.model.WikiDataEntityRepo;

@Service
public class DataWikiService {
	// URL to get the entire Introduction section in Wikipedia page for given title
	private static String WIKIPEDIA_INTRO_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&rvsection=0&rvslots=main&titles=";

	Logger log = LoggerFactory.getLogger(DataWikiService.class);

	@Autowired
	PatternService patternService;

	@Autowired
	WikiDataEntityRepo wikiDataEntityRepo;

	public WikiDataEntity getEntityWithIntro(String entityId) throws IOException {
		String originalId = entityId.replace('_', ' ');
		Optional<WikiDataEntity> wdEntity = wikiDataEntityRepo.findByLabelEn(originalId);
		WikiDataEntity entity;
		if (wdEntity.isPresent()) {
			entity = wdEntity.get();
			String introSection = getIntroSection(entityId);
			entity.setIntroSection(introSection);
			Matcher matcher = patternService.getReignPattern1().matcher(introSection);
			if (matcher.find()) {
				String reignString = introSection.substring(matcher.start(), matcher.end());
				entity.setReignString(reignString);
			}
			return entity;
		} else {
			return null;
		}
	}

	@Transactional
	public int setReignStringForEntity(String entityId, String articleName) throws Exception {
		String reignString = null;
		String labelEnWithUnderscores = articleName.replace(' ', '_');

		try {
			String introSection = getIntroSection(labelEnWithUnderscores);

			// first try to find reign string using first pattern, if not found try 2nd
			// pattern
			reignString = extractStringWithPattern(patternService.getReignPattern1(), introSection);
			if (reignString == null) {
				reignString = extractStringWithPattern(patternService.getReignPattern2(), introSection);
			}
		} catch (IOException e) {
			reignString = "Not found - IOException"; // to handle exception from Wikipedia API from getIntroSection
			log.info("Got IOException for entity {}.", articleName);
		}
		if (reignString == null) {
			reignString = "Not found";
		}
		if (reignString != null) {
			if (reignString.length() > 128) {
				reignString = reignString.substring(0, 127);
			}
			int rows = wikiDataEntityRepo.updateReignStringForLabelEn(entityId, reignString);
			if (rows == 1) {
				return 1;
			}
			if (rows == 0) {
				log.info("No rows updated.");
				return 0;
			}
			throw new Exception("Unexpected update: more than 1 row updated. Rolling back transaction.");
		}
		return -1;
	}

	private String extractStringWithPattern(Pattern pattern, String introSection) {
		String reignString = null;
		Matcher matcher = pattern.matcher(introSection);
		if (matcher.find()) {
			reignString = introSection.substring(matcher.start(), matcher.end());
		}
		return reignString;
	}

	public String getIntroSection(String entityId) throws IOException {
		URL url = new URL(WIKIPEDIA_INTRO_URL + entityId);

		InputStream inputStream = url.openStream();

		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, "UTF-8");

		// we create a JSON object from the string we get from Wikipedia API
		// JSONObject json = new JSONObject(writer.toString());
		// JSONObject jsonPages = json.getJSONObject("query").getJSONObject("pages");
		// JSONObject jsonPage = jsonPages;

		inputStream.close();

		return writer.toString();
	}

	/*
	 * attempt to update reign-string of up to count entities whose reign-string is
	 * currently null. The reign string is found by fetching the Wikipedia Infobox
	 * and extracting the reign string in one of two possible formats.
	 */
	@Transactional
	public int setReignString(int count) throws Exception {
		int rowsUpdated = 0, i = 0;
		List<WikiDataEntity> entities = wikiDataEntityRepo.findAllWithNullReignString();

		for (WikiDataEntity entity : entities) {
			if (entity.getReignString() == null) {
				int updateCount = setReignStringForEntity(entity.getEntityId(), entity.getArticleName());
				if (updateCount == 1) {
					rowsUpdated++;
					// log.info("Reign string updated for {}.", entity.getLabelEn());
				} else {
					log.info("Reign string not found for {}.", entity.getArticleName());
				}
				i++;

				if (i % 10 == 0) {
					log.info("{} entities processed.", i);
				}
				if (i >= count) {
					return rowsUpdated;
				}
			}
		}

		return rowsUpdated;
	}

	@Transactional
	public int setIntroSection(int count) throws Exception {
		int rowsUpdated = 0, i = 0;
		List<WikiDataEntity> entities = wikiDataEntityRepo.findAllWithNullReignString();

		for (WikiDataEntity entity : entities) {
			if (entity.getIntroSection() == null) {
				String reignString = null;
				byte reignPattern = 0;
				String introSection = null;
				try {
					introSection = getIntroSection(entity.getArticleName().replace(' ', '_'));

					reignString = extractStringWithPattern(patternService.getReignPattern1(), introSection);
					if (reignString == null) {
						reignString = extractStringWithPattern(patternService.getReignPattern2(), introSection);
						if (reignString != null)
							reignPattern = 2;
					} else {
						reignPattern = 1;
					}
					if (reignString == null) {
						reignString = "Not found";
					}
					// truncate introSection and reignString if they are longer than DB column size.
					if (introSection.length() > 3000) {
						introSection = introSection.substring(0, 3000);
					}
					if (reignString.length() > 1000) {
						reignString = reignString.substring(0, 1000);
					}
				} catch (IOException e) {
					reignString = "Not found - IOException"; // to handle exception from Wikipedia API from
																// getIntroSection
					log.info("Got IOException for entity {}.", entity.getArticleName());
				}
				int updateCount = wikiDataEntityRepo.updateIntroSection(entity.getEntityId(), introSection,
						reignPattern, reignString);

				if (updateCount == 1) {
					rowsUpdated++;
					// log.info("Reign string updated for {}.", entity.getLabelEn());
				} else {
					log.info("Reign string not found for {}.", entity.getArticleName());
				}
				i++;

				if (i % 50 == 0) {
					log.info("{} entities processed.", i);
				}
				if (i >= count) {
					return rowsUpdated;
				}
			}
		}

		return rowsUpdated;
	}

	@Transactional
	int processReignString(int count) throws Exception {
		int rowsUpdated = 0, i = 0, updateCount = 0;
		List<WikiDataEntity> entities = wikiDataEntityRepo.findAllWithNullReign1();
		ReignDto reign;

		for (WikiDataEntity entity : entities) {
			if (entity.getReign1() == null) {
				String reign1 = patternService.cleanupReignString(entity.getReignString());
				if (reign1 != null) {
					// for now we are proceesing only pattern 2, which is something like: 10 August
					// 1200 - 31 November 1231 CE or 1200 - 1231
					if (entity.getReignPattern() == 2) {
						reign = this.extractReignFromReignString(2, reign1);
						if (reign == null) {
							reign = this.extractReignFromReignString(3, reign1);
						}
						if (reign != null && reign.getStartCount() == 1 && reign.getEndCount() == 1) {
							updateCount = wikiDataEntityRepo.updateReignForEntityId(entity.getEntityId(), reign1,
									reign.getStart(), reign.getEnd(), "Found");
						} else {
							log.info("Reign not found for: {}", reign1);
							updateCount = wikiDataEntityRepo.updateReignForEntityId(entity.getEntityId(), reign1, null,
									null, "Not found");
						}
					}

					if (updateCount == 1) {
						rowsUpdated++;
						// log.info("Reign string updated for {}.", entity.getLabelEn());
					} else {
						log.info("Could not update reign1 for {}.", entity.getArticleName());
					}
					i++;

					if (i % 50 == 0) {
						log.info("{} entities processed.", i);
					}
					if (i >= count) {
						return rowsUpdated;
					}
				}
			}
		}

		return rowsUpdated;

	}

	public ReignDto extractReignFromReignString(int patternType, String reignString) {
		Pattern pattern = null;
		if (patternType == 2) {
			pattern = patternService.getDmyDatePattern();
		}

		String reignStart = null, reignEnd = null;
		// if multiple pairs of reign start and end are found, the number is counted.
		int reignStartCount = 0, reignEndCount = 0;
		Matcher matcher = pattern.matcher(reignString);

		// First occurrence of inner pattern for date will be reignStart
		while (matcher.find()) {
			if (patternType == 2) {
				reignStartCount++;
				reignStart = reignString.substring(matcher.start(), matcher.end()).trim();
				// 2nd occurrence if found, will be reignEnd
				if (matcher.find()) {
					reignEndCount++;
					reignEnd = reignString.substring(matcher.start(), matcher.end()).trim();
				}
			} else if (patternType == 3) {
				reignStartCount++;
				reignEndCount++;
				String reignInYears = reignString.substring(matcher.start(), matcher.end()).trim();
				String[] reignYears = reignInYears.split("-");
				if (reignYears.length == 2) {
					log.info("reignYears: {}, {}", reignYears[0], reignYears[1]);
					reignStart = reignYears[0].split("\\s+")[0];
					Matcher numberMatcher = patternService.getNumberPattern().matcher(reignYears[1]);
					if (numberMatcher.find()) {
						reignEnd = numberMatcher.group();
					}
				}
			}
		}
		if (reignStartCount > 0 && reignEndCount > 0) {
			log.info("Start: {}, End: {}", reignStart, reignEnd);
			return new ReignDto(reignStart, reignEnd, reignStartCount, reignEndCount);
		}
		log.info("Pattern {} date not found.", patternType);
		return null;
	}

	@Transactional
	int setReign(int count) throws Exception {
		int rowsUpdated = 0, i = 0;
		List<WikiDataEntity> entities = wikiDataEntityRepo.findAllWithNullReign1();
		ReignDto reign;

		for (WikiDataEntity entity : entities) {
			if (entity.getReign1() == null) {
				int k = this.updateReignForEntity(entity);
				i++;
				rowsUpdated += k;
				if (i % 50 == 0) {
					log.info("{} entities processed.", i);
				}
				if (i >= count) {
					return rowsUpdated;
				}
			}
		}

		return rowsUpdated;
	}

	public int setReignForEntityId(String entityId) {
		Optional<WikiDataEntity> oEntity = wikiDataEntityRepo.findByEntityId(entityId);
		if (oEntity.isPresent()) {
			WikiDataEntity entity = oEntity.get();
			return updateReignForEntity(entity);
		} else
			return -1;
	}

	public int updateReignForEntity(WikiDataEntity entity) {
		int updateCount = 0;
		String reignStart = null, reignEnd = null;
		String reign1 = patternService.cleanupReignString(entity.getReignString());
		if (reign1 != null) {
			// for now we are proceesing only pattern 2, which is something like: 10 August
			// 1200 - 31 November 1231 CE or 1200 - 1231
			String[] segments = reign1.split("-");
			if (segments.length == 2) {
				reignStart = this.extractDateFromSegment(segments[0]);
				reignEnd = this.extractDateFromSegment(segments[1]);
			}

			if (reignStart != null && reignEnd != null) {
				if(reign1.length()>255) {
					reign1 = reign1.substring(0,255); 
				}
				if(reignStart.length()>255) {
					reignStart = reignStart.substring(0,255); 
				}
				if(reignEnd.length()>255) {
					reignEnd = reignEnd.substring(0,255); 
				}

				updateCount = wikiDataEntityRepo.updateReignForEntityId(entity.getEntityId(), reign1, reignStart,
																		reignEnd, "Found");
				return updateCount;
			}
		}
		log.info("Reign not found for: {}", reign1);
		if(reign1.length()>255) {
			reign1 = reign1.substring(0,255); 
		}
		updateCount = wikiDataEntityRepo.updateReignForEntityId(entity.getEntityId(), reign1, null, null, "Not found");
		return updateCount;
	}

	// look for full 'dmy' format, if not then 'mdy', then 'my' and if not just 'y' and return
	// matched date
	public String extractDateFromSegment(String input) {
		Matcher matcher = patternService.getDmyDatePattern().matcher(input);
		String date = null;
		if (matcher.find()) {
			date = matcher.group();
			return date;
		}

		matcher = patternService.getMdyDatePattern().matcher(input);
		date = null;
		if (matcher.find()) {
			date = matcher.group();
			return date;
		}
		
		matcher = patternService.getMyDatePattern().matcher(input);
		if (matcher.find()) {
			date = matcher.group();
			return date;
		}

		matcher = patternService.getyDatePattern().matcher(input);
		if (matcher.find()) {
			String tempDate = matcher.group();
			Matcher numberMatcher = patternService.getNumberPattern().matcher(tempDate);
			if (numberMatcher.find()) {
				date = numberMatcher.group();
			}
			return date;
		}

		return null;
	}
}
