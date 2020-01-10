package validar;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.wikipedia.miner.db.WDatabase;
import org.wikipedia.miner.db.WIterator;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class ValidarPrograma1 {
	
	public static void main(String[] args) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
	    WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		WIterator<Integer, DbPage> PageMapIterator = PageMap.getIterator();
	    
	    ArrayList<String> colunas_list = new ArrayList<>();
	    
	    // STEP 1 -> Criando lista com os id's das pages do tipo Article
	    System.out.println("Criando lista com id dos articles");
	    colunas_list.add("0");
	    while(PageMapIterator.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator.next().getKey()) ;
	    	if(page.exists()) {
	    		if(page.getType() == PageType.article) {
	    			colunas_list.add(Integer.toString(page.getId()));
	    		}
	    	}
	    }// STEP 1 -> Criando lista com os id's das pages do tipo Article
	    PageMapIterator.close();
	    
	    Scanner csv_articles = new Scanner(new File("/home/thalisson/Documents/WikiMiner/csv_articles.csv"));
	    csv_articles.nextLine(); // Pulando a primeira linha (Ela contém a coluna de id dos articles).
	    int pageExistCount = 0;
	    int pageArticleCount = 0;
	    while(csv_articles.hasNextLine()) {
	    	String[] aux = csv_articles.nextLine().split(",");
	    	
	    	//STEP 1
	    	Page page = Page.createPage(wikipedia.getEnvironment(), Integer.parseInt(aux[0]));
	    	if(page.exists()) {
	    		pageExistCount++;
	    		if(page.getType() == PageType.article) {
	    			pageArticleCount++;
	    			ArrayList<String> linksOutDb = new ArrayList<>();
	    			ArrayList<String> linksOutCsv = new ArrayList<>();
	    			
	    			//Coletando links que saem do article a partir do DB do wikipedia
	    			Article[] linksOutArticle = ((Article) page).getLinksOut();
	    			for(int i = 0; i < linksOutArticle.length; i++) 
	    				if(Page.createPage(wikipedia.getEnvironment(), Integer.parseInt(linksOutArticle[i].toString().split(":")[0])).getType() == PageType.article)
	    					linksOutDb.add(linksOutArticle[i].toString().split(":")[0]);
	    			//Coletando links que saem do article a partir do DB do wikipedia
	    			
	    			for(int i = 1; i < aux.length; i++) {
	    				if(aux[i].equals("1"))
	    					linksOutCsv.add(colunas_list.get(i));
	    			}
	    			
	    			if(linksOutDb.equals(linksOutCsv)) {
	    				System.out.println(page.getId() + " | True");
	    			} else {
	    				System.err.println(page.getId() + " | False");
	    				System.exit(1);
	    			}

	    		} else {
	    			System.err.println("A page " + aux[0] + " não é Article!");
	    			System.exit(1);
	    		}
	    		
	    	} else {
	    		System.err.println("A page " + aux[0] + " não existe!");
	    		System.exit(1);
	    	}//STEP 1
	    	
	    }
	    System.out.println("Page Exist Count = " + pageExistCount);
	    System.out.println("Page Article Count = " + pageArticleCount);
	    csv_articles.close();
	}

}
