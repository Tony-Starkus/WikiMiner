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

public class ValidarPrograma1Alternative {
	
	//WikipediaConfiguration file
	static String _conf = "C:\\Thalisson\\Wikification\\wikipedia-miner-starwars\\configs\\wikipedia-template-starwars.xml";
	
	//csv file folder
	static String dirFile = "C:\\Thalisson\\Documentos\\WikiMiner\\";
	
	public static void main(String[] args) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File(_conf)) ;
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
	    
	    Scanner csv_articles = new Scanner(new File(dirFile + "articles_file.txt"));
	    csv_articles.nextLine(); // Pulando a primeira linha (Ela contém informação das colunas de cada linha).
	    int pageExistCount = 0;
	    int pageArticleCount = 0;
	    while(csv_articles.hasNextLine()) {
	    	String[] line = csv_articles.nextLine().split(",");
	    	String id_article_i = line[0];
	    	//STEP 1
	    	Page page = Page.createPage(wikipedia.getEnvironment(), Integer.parseInt(id_article_i));
	    	if(page.exists()) {
	    		pageExistCount++;
	    		if(page.getType() == PageType.article) {
	    			pageArticleCount++;
	    			ArrayList<String> linksOutDb = new ArrayList<>();
	    			ArrayList<String> linksOutFile = new ArrayList<>();
	    			
	    			//Coletando links que saem do article a partir do DB do wikipedia
	    			Article[] linksOutArticle = ((Article) page).getLinksOut();
	    			for(int i = 0; i < linksOutArticle.length; i++) 
	    				if(Page.createPage(wikipedia.getEnvironment(), linksOutArticle[i].getId()).getType() == PageType.article)
	    					linksOutDb.add(Integer.toString(linksOutArticle[i].getId()));
	    			//Coletando links que saem do article a partir do DB do wikipedia
	    			
	    			if(line[2].equals("1"))
	    				linksOutFile.add(line[1]);
	    			
	    			for(int i = 2; i < colunas_list.size(); i++) {
	    				line = csv_articles.nextLine().split(",");
	    				if(line[2].equals("1"))
		    				linksOutFile.add(line[1]);
	    			}
	    			
	    			if(linksOutDb.equals(linksOutFile)) {
	    				System.out.println(page.getId() + " | True");
	    			} else {
	    				System.err.println(page.getId() + " | False");
	    				System.out.println("linksOutCsv: " );
	    				for(int i = 0; i < linksOutFile.size(); i++)
	    					System.out.print(linksOutFile.get(i) + " | ");
	    				System.exit(1);
	    			}

	    		} else {
	    			System.err.println("A page " + line[0] + " não é Article!");
	    			System.exit(1);
	    		}
	    		
	    	} else {
	    		System.err.println("A page " + line[0] + " não existe!");
	    		System.exit(1);
	    	}//STEP 1
	    	
	    }
	    System.out.println("Page Exist Count = " + pageExistCount);
	    System.out.println("Page Article Count = " + pageArticleCount);
	    csv_articles.close();
	}

}
