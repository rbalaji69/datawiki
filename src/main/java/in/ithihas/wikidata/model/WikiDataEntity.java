package in.ithihas.wikidata.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.Transient;

@Entity
@Table(name="EntityArticles", schema = "wikidata")
public class WikiDataEntity {
	public String getReignString() {
		return reignString;
	}

	public void setReignString(String reignString) {
		this.reignString = reignString;
	}

	public String getLabelEn() {
		return labelEn;
	}
	
	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn; 
	}

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(columnDefinition="nvarchar(50)")
	private String entityId; 
	
	@Column(columnDefinition="nvarchar(255)", nullable=false)
	private String labelEn; 
	
	@Column(columnDefinition="nvarchar(255)", nullable=false)
	private String articleName;
	
	@Column(columnDefinition="nvarchar(3000)")
	private String introSection; 
	
	@Column()
	private Byte reignPattern; 
	
	public Byte getReignPattern() {
		return reignPattern;
	}

	public void setReignPattern(Byte reignPattern) {
		this.reignPattern = reignPattern;
	}

	public String getIntroSection() {
		return introSection;
	}

	@Column(name="reign1", columnDefinition="nvarchar(255)")
	private String reign1; 
	
	@Column(columnDefinition="nvarchar(255)")
	private String reignStart; 
	
	@Column(columnDefinition="nvarchar(255)")
	private String reignEnd; 
	

	public String getReignStart() {
		return reignStart;
	}

	public void setReignStart(String reignStart) {
		this.reignStart = reignStart;
	}

	public String getReignEnd() {
		return reignEnd;
	}

	public void setReignEnd(String reignEnd) {
		this.reignEnd = reignEnd;
	}

	public String getReign1() {
		return reign1;
	}

	public void setReign1(String reign1) {
		this.reign1 = reign1;
	}

	public void setIntroSection(String introSection) {
		this.introSection = introSection;
	}

	@Column(columnDefinition="nvarchar(1000)")
	private String reignString; 
 
	public WikiDataEntity() {
	}
	
	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	} 
	
	public WikiDataEntity(String id, String label, String articleName) {
		this.entityId = id; 
		this.labelEn = label; 
		this.articleName = articleName; 
	}
}
