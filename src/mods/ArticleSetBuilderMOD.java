package mods;

import java.util.Vector;
import java.util.regex.Pattern;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Wikipedia;

public class ArticleSetBuilderMOD {
	
	private Integer minInLinks = null ;
	private Integer minOutLinks = null ; 
	
	private Double minLinkProportion = null ; 
	private Double maxLinkProportion = null ; 
	
	private Integer minWordCount = null ; 
	private Integer maxWordCount = null ; 
	
	private Double maxListProportion = null ; 
	
	private Pattern mustMatch = null ; 
	private Pattern mustNotMatch = null ; 
	
	private ArticleSetMOD exclude = null ;

	public ArticleSetBuilderMOD setMinInLinks(Integer minInLinks) {
		this.minInLinks = minInLinks;
		return this ;
	}

	public ArticleSetBuilderMOD setMinOutLinks(Integer minOutLinks) {
		this.minOutLinks = minOutLinks;
		return this ;
	}

	public ArticleSetBuilderMOD setMinLinkProportion(Double minLinkProportion) {
		this.minLinkProportion = minLinkProportion;
		return this ;
	}

	public ArticleSetBuilderMOD setMaxLinkProportion(Double maxLinkProportion) {
		this.maxLinkProportion = maxLinkProportion;
		return this ;
	}

	public ArticleSetBuilderMOD setMinWordCount(Integer minWordCount) {
		this.minWordCount = minWordCount;
		return this ;
	}

	public ArticleSetBuilderMOD setMaxWordCount(Integer maxWordCount) {
		this.maxWordCount = maxWordCount;
		return this ;
	}

	public ArticleSetBuilderMOD setMaxListProportion(Double maxListProportion) {
		this.maxListProportion = maxListProportion;
		return this ;
	}

	public ArticleSetBuilderMOD setMustMatchPattern(Pattern mustMatch) {
		this.mustMatch = mustMatch;
		return this ;
	}

	public ArticleSetBuilderMOD setMustNotMatchPattern(Pattern mustNotMatch) {
		this.mustNotMatch = mustNotMatch;
		return this ;
	}

	public ArticleSetBuilderMOD setExclude(ArticleSetMOD exclude) {
		this.exclude = exclude;
		return this ;
	}
		
	public ArticleSetMOD build(int size, Wikipedia wikipedia) {
		return new ArticleSetMOD(wikipedia, size, minInLinks, minOutLinks, minLinkProportion, maxLinkProportion, minWordCount, maxWordCount, maxListProportion, mustMatch, mustNotMatch, null, exclude) ;
	}
	
	public ArticleSetMOD[] buildExclusiveSets(int[] sizes, Wikipedia wikipedia) {
		
		
		ArticleSetMOD sets[] = new ArticleSetMOD[sizes.length] ;
		
		ArticleSetMOD exclude = new ArticleSetMOD() ;
		
		if (this.exclude != null)
			exclude.addAll(this.exclude) ;
		
		Vector<Article> candidates = ArticleSetMOD.getRoughCandidates(wikipedia, minInLinks, minOutLinks) ;

		for (int i=0 ; i<sizes.length ; i++) {
			sets[i] = new ArticleSetMOD(wikipedia, sizes[i], minInLinks, minOutLinks, minLinkProportion, maxLinkProportion, minWordCount, maxWordCount, maxListProportion, mustMatch, mustNotMatch, candidates, exclude) ;			
			exclude.addAll(sets[i]) ;
		}
		
		return sets ;
	}
}
