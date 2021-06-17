package in.ithihas.wikidata;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.ithihas.wikidata.model.WikiDataEntity;

@RestController
public class DataWikiController {
	
	@Autowired
	DataWikiService dataWikiService; 
	
	@GetMapping("/getIntroSection")
	@ResponseBody
	public WikiDataEntity getEntityWithDescription(@RequestParam(value="id", required=true) String id) throws IOException {
		if(id != null) {
			return dataWikiService.getEntityWithIntro(id);  
		}
		else {
			return null; 
		}
	}
	
	//@PutMapping(produces="application/json", path="/setReignStringForId")
	//@ResponseBody
	/*public ResponseEntity<?> setEntityWithReignString(@RequestParam(value="id", required=true) String id) throws Exception {
		if(id != null) {
			return dataWikiService.setReignStringForEntity(id); 
		}
		else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					 .body(new ApiResponse(false, "Null entity id.")); 
		}
	}*/
	
	@PutMapping(produces="application/json", path="/setReignString")
	@ResponseBody
	public ResponseEntity<?> setReignString(@RequestParam(value="count", required=true) int count) throws Exception {
		if(count > 0) {
			int entitiesUpdated = dataWikiService.setReignString(count); 
			String msg = String.format("%d entities updated.", entitiesUpdated); 
			return ResponseEntity.ok().body(new ApiResponse(true, msg)); 
		}
		else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					 .body(new ApiResponse(false, "Invalid count.")); 
		}
	}
	
	@PutMapping(produces="application/json", path="/setIntroSection")
	@ResponseBody
	public ResponseEntity<?> setIntroSection(@RequestParam(value="count", required=true) int count) throws Exception {
		if(count > 0) {
			int entitiesUpdated = dataWikiService.setIntroSection(count); 
			String msg = String.format("%d entities updated.", entitiesUpdated); 
			return ResponseEntity.ok().body(new ApiResponse(true, msg)); 
		}
		else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					 .body(new ApiResponse(false, "Invalid count.")); 
		}
	}
	
	@PutMapping(produces="application/json", path="/setReign")
	@ResponseBody
	public ResponseEntity<?> setReign(@RequestParam(value="count", required=true) int count) throws Exception {
		if(count > 0) {
			int entitiesUpdated = dataWikiService.processReignString(count); 
			String msg = String.format("%d entities updated.", entitiesUpdated); 
			return ResponseEntity.ok().body(new ApiResponse(true, msg)); 
		}
		else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					 .body(new ApiResponse(false, "Invalid count.")); 
		}
	}
}
