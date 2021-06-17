package in.ithihas.wikidata.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WikiDataEntityRepo extends JpaRepository<WikiDataEntity, String>{

	@Transactional
	@Modifying
	@Query(value = "update wikidata.entity_articles set intro_section = :intro_section where entity_id = :entity_id", nativeQuery = true)
	public int updateIntroSectionForEntityId(@Param("entity_id") String entityId, 
											 @Param("intro_section") String introSection); 
	
	
	@Transactional
	@Modifying
	@Query(value = "update wikidata.entity_articles set reign_string = :reign_string where entity_id = :entity_id", nativeQuery = true)
	public int updateReignStringForLabelEn(@Param("entity_id") String entityId, 
											 @Param("reign_string") String reignString); 
	
	@Transactional
	@Modifying
	@Query(value = "update wikidata.entity_articles set intro_section = :intro_section, reign_pattern=:reign_pattern, reign_string=:reign_string where entity_id = :entity_id", 
	       nativeQuery = true)
	public int updateIntroSection(@Param("entity_id") String entityId, 
								  @Param("intro_section") String introSection, 
								  @Param("reign_pattern") byte reignPattern, 
								  @Param("reign_string") String reignString); 
	
	@Transactional
	@Modifying
	@Query(value = "update wikidata.entity_articles set reign1 = :reign1 where entity_id = :entity_id", nativeQuery = true)
	public int updateReign1ForId(@Param("entity_id") String entityId, 
								 @Param("reign1") String reign1); 
	
	public Optional<WikiDataEntity> findByLabelEn(String labelEn); 
	
	@Query(value = "select * from wikidata.entity_articles where reign_string IS NULL", 
		   nativeQuery = true)
	public List<WikiDataEntity> findAllWithNullReignString();
	
	@Query(value = "select * from wikidata.entity_articles where intro_section IS NULL", 
			   nativeQuery = true)
	public List<WikiDataEntity> findAllWithNullIntroSection();


	@Transactional
	@Modifying
	@Query(value = "update wikidata.entity_articles set reign1 = :reign1, reign_start = :reign_start, reign_end = :reign_end where entity_id = :entity_id", nativeQuery = true)
	public int updateReignForEntityId(@Param("entity_id") String entityId, 
			  										  @Param("reign1") String reign1,
			  										  @Param("reign_start") String reignStart, 
			  										  @Param("reign_end") String reignEnd);
	

	//find all entities which have not been processed and reign_string was found. 
	@Query(value = "select * from wikidata.entity_articles where reign1 IS NULL and not (reign_string like 'Not found%') ", 
		   nativeQuery = true)
	public List<WikiDataEntity> findAllWithNullReign1(); 
	
}
