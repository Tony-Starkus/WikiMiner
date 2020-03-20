package pages;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.wikipedia.miner.db.*;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class Articles  {
	
	public static void main(String[] args) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		WIterator<Integer, DbPage> PageMapIterator = PageMap.getIterator();
		WIterator<Integer, DbPage> PageMapIterator2 = PageMap.getIterator();
	    System.out.println("Iniciando...");

	    PrintWriter csv_articles = new PrintWriter("/home/thalisson/Documents/WikiMiner/csv_articles.csv", "UTF-8");
	    
	    Map<Integer, String> artigos_dic = new HashMap<Integer, String>();
	    
	    //CRIANDO COLUNAS NO CSV
	    int last = 0;
	    ArrayList<String> colunas_list = new ArrayList<>();
	    while(PageMapIterator.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator.next().getKey()) ;
	    	if(page.exists()) {
	    		System.out.println(page.getId() + ". " + page.getTitle() + " | " + page.getType());
	    		if(page.getType() == PageType.article) {
	    			colunas_list.add(Integer.toString(page.getId()));
	    			artigos_dic.put(page.getId(), page.getTitle());
	    			csv_articles.print("," + page.getId());
	    			last = page.getId();
	    		}
	    			
	    	}
	    }//CRIANDO COLUNAS NO CSV

	    
	    //CRIANDO LINHAS NO CSV
	    while(PageMapIterator2.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator2.next().getKey()) ;
	    	if(page.exists()) {
	    		if(page.getType() == PageType.article) {
	    			System.out.println(page.getId() + " | " + page.getTitle());
	    		    csv_articles.println();
	    			ArrayList<String> linksOut = new ArrayList<>();
	    			csv_articles.print(page.getId() + ",");
	    			Article[] linksOutArticle = ((Article) page).getLinksOut();
	    			for(int i = 0; i < linksOutArticle.length; i++) {
	    		    	String[] ids = linksOutArticle[i].toString().split(":");
	    		    	linksOut.add(ids[0]);
	    		    }
	    			
	    			//VERIFICANDO SAIDAS DE LINKS
	    			for(String id_coluna: colunas_list) {
	    				boolean encontrado = false;
	    				for(String id_linkOut: linksOut) {
	    					if(id_coluna.equals(id_linkOut)) {
	    						encontrado = true;
	    						csv_articles.print("1,");
	    						break;
	    					}
	    					
	    				}
	    				if(!encontrado) {
	    					csv_articles.print("0,");
	    				}
	    			}//VERIFICANDO SAIDAS DE LINKS
	    			
	    		}
	    			
	    	}

	    }//CRIANDO LINHAS NO CSV
	    csv_articles.close();
	    System.out.println("Concluído");
	    
	}
	
}
