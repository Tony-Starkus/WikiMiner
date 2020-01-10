package teste;

import java.io.File;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class WikipediaDefiner {
	
	public static void main(String args[]) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
	    
	    Page page = Page.createPage(wikipedia.getEnvironment(), 764);
	    Article article = wikipedia.getArticleByTitle("Episode V") ;
	    
	    //TESTES
	    System.out.println("Root Category: " + wikipedia.getRootCategory());
	    System.out.println("getPageById: " + wikipedia.getPageById(100));
	    //TESTES//
	    
	    //ARTICLE1
	    System.out.println("Page existe: " + page.exists());
	    System.out.println("Page id: " + page.getId()) ;
	    System.out.println("Page type: " + page.getType());
	    System.exit(1);
	    System.out.println(page.getSentenceMarkup(0)) ;
	    System.out.println("Links que sai do article: " + ((Article) page).getDistinctLinksOutCount());
	    Article[] linksOut = ((Article) page).getLinksOut();
	    System.out.println("linksOut[0] " + linksOut[0]);
	    System.out.println("linksOut[1] " + linksOut[1]);
	    System.out.println("linksOut[2] " + linksOut[2]);
	    System.out.println("linksOut[3] " + linksOut[3]);
	    System.out.println("linksOut[4] " + linksOut[4]);
	    System.out.println("linksOut[5] " + linksOut[5]);
	    //ARTICLE1
	    System.exit(1);
	    //ARTICLE2
	    System.out.println("Article2 existe: " + article.exists());
	    System.out.println(article.getId()) ;
	    System.out.println(article.getSentenceMarkup(0)) ;
	    //ARTICLE2
	    
	    //System.out.println(article.getSentenceMarkup(100)) ;
	    
	    
	    wikipedia.close() ;
		
	}
	
	
    
    

}
