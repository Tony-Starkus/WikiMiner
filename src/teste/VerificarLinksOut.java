package teste;

import java.io.File;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class VerificarLinksOut {
	
	public static void main(String[] args) throws Exception {
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		
		Page article = Page.createPage(wikipedia.getEnvironment(), 49);
		System.out.println("Article existe: " + article.exists());
		System.out.println("Links que sai do article: " + ((Article) article).getLinksOut().length);
	    Article[] linksOut = ((Article) article).getLinksOut();
	    for(int i = 0; i < 10; i++)
	    	System.out.println("linksOut["+ i +"] " + linksOut[i]);
		
	}

}
