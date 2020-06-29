package teste;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.wikipedia.miner.db.WDatabase;
import org.wikipedia.miner.db.WEnvironment.StatisticName;
import org.wikipedia.miner.db.WIterator;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class WikipediaDefiner {
	
	public static void main(String args[]) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
	    
	    Article article = wikipedia.getArticleByTitle("Wikipedia") ;
	    
        System.out.println(article.getSentenceMarkup(0)) ;
        
        
        
        
        
        
	    System.exit(1);
	    
	    
	    
	    
	   
	    Article article2 = new Article(wikipedia.getEnvironment(), 57042);
	    System.out.println("Generality: " + article2.getGenerality());
	    System.exit(1);
	    DbPage pd = wikipedia.getEnvironment().getDbPage().retrieve(article2.getId()) ;

	    ArrayList<Integer> _articlesSet_list = new ArrayList<>();
	    Scanner file;
		try {
			file = new Scanner(new File("/home/thalisson/Documents/WikiMiner/" + "articlesSetID.csv"));
			if(_articlesSet_list.size() > 0)
				_articlesSet_list.clear();
			while(file.hasNextLine()) {
				int id = Integer.parseInt(file.nextLine());
				_articlesSet_list.add(id);
			}
			file.close();
			System.out.println("Tamanho da lista de articles: " + _articlesSet_list.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
	    // Carregando todas as categorias
	    WIterator<Integer, DbPage> pageMap = wikipedia.getEnvironment().getDbPage().getIterator();
	    int auxContem = 0;
	    int auxNaoContem = 0;
	    int total = 0;
	    int totalArticles = 0;
	    int temGenelarity = 0;
	    
	    /*for(int id: _articlesSet_list) {
			Page page = Page.createPage(wikipedia.getEnvironment(), id);
			if(page.exists() && page.getType() == PageType.article) {
				Article article2 = new Article(wikipedia.getEnvironment(), page.getId());
				System.out.println(article2.getDepth());
			} else {
				System.err.println("Page exist: " + page.exists() + "| PageType: " + page.getType() +" | Page ID: " + page.getId());
			}
		}*/

	    while(pageMap.hasNext()) {
	    	total++;
	    	Page page = Page.createPage(wikipedia.getEnvironment(), pageMap.next().getKey());
	    	if(page.getType() == PageType.article) {
    			totalArticles += 1;
    			Article article1 = new Article(wikipedia.getEnvironment(), page.getId());
    			System.out.println("article1.getDepth(): " + article1.getDepth());
	    		System.out.println("article1.getGenerality(): " + article1.getGenerality());
	    		if(article1.getGenerality() != null)
	    			temGenelarity++;
	    		System.out.println();	
	    	}
	    }
	    System.out.println("total pages: " + total);
	    System.out.println("totalArticles: " + totalArticles);
	    System.out.println("Tem Generality: " + temGenelarity);
	    System.exit(1);
	    System.out.println("aux: " + auxContem);
	    System.out.println("auxNaoContem: " + auxNaoContem);
	    

	    System.out.println(wikipedia.getEnvironment().getDbPage().getDatabaseSize());
	    System.exit(1);
	    
	    
	    Page page = Page.createPage(wikipedia.getEnvironment(), 621478);
	    //Page page = wikipedia.getArticleByTitle("Luke Skywalker");
	    Article article1 = wikipedia.getArticleByTitle("Rahara Wick") ;
	    
	    //TESTES
	    System.out.println("Page existe: " + page.exists());
	    System.out.println("Page title: " + page.getTitle());
	    System.out.println("Page id: " + page.getId()) ;
	    System.out.println("Page type: " + page.getType());
	    System.out.println("Links que sai do article: " + ((Article) page).getDistinctLinksOutCount());
	    System.out.println(page.toString());
	    //TESTES//
	    
	    /*
	     * TESTE 1*/
	    WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		WIterator<Integer, DbPage> PageMapIterator = PageMap.getIterator();

		int totalPages = 0;
		while(PageMapIterator.hasNext()) {
			Page page1 = Page.createPage(wikipedia.getEnvironment(), PageMapIterator.next().getKey());
			if(page1.exists()) {
				totalPages++;
				if(page1.getType() == PageType.article) {
					if(((Article) page1).getDistinctLinksOutCount() >= 15 && ((Article) page1).getDistinctLinksInCount() >= 15) {
						totalArticles++;
						System.out.println(totalArticles);
					}
				}
			} else {
				System.err.println("Página não existe: " + page1.getId());
				System.exit(1);
			}
				
		}
	    System.out.println("Total Article: " + totalArticles);
	    System.out.println("Total Pages: " + totalPages);
	    System.exit(1);
	    System.exit(1);
	    //ARTICLE1
	    System.out.println("Page existe: " + page.exists());
	    System.out.println("Page title: " + page.getTitle());
	    System.out.println("Page id: " + page.getId()) ;
	    System.out.println("Page type: " + page.getType());
	    System.out.println(page.getFirstParagraphMarkup()) ;
	    System.out.println("Links que sai do article: " + ((Article) page).getDistinctLinksOutCount());
	    System.out.println("Verificar:\n\n");
	    Article[] linksOut = ((Article) page).getLinksOut();
	    for(int i = 0; i < 10; i++) {
	    	System.out.println(linksOut[i].getTitle() + " | " + linksOut[i].getId());
	    }
	    System.exit(1);
	    System.out.println("Links que sai do article: " + ((Article) page).getDistinctLinksOutCount());
	    System.out.println("linksOut[0] " + linksOut[0]);
	    System.out.println("linksOut[1] " + linksOut[1]);
	    System.out.println("linksOut[2] " + linksOut[2]);
	    System.out.println("linksOut[3] " + linksOut[3]);
	    System.out.println("linksOut[4] " + linksOut[4]);
	    System.out.println("linksOut[5] " + linksOut[5]);
	    //ARTICLE1
	    System.exit(1);
	    //ARTICLE2
	    System.out.println("Article2 existe: " + article2.exists());
	    System.out.println(article2.getId()) ;
	    System.out.println(article2.getSentenceMarkup(0)) ;
	    //ARTICLE2
	    
	    //System.out.println(article.getSentenceMarkup(100)) ;
	    
	    
	    wikipedia.close() ;
		
	}
	
	
    
    

}
