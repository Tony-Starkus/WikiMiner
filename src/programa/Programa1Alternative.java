package programa;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.wikipedia.miner.db.*;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class Programa1Alternative  {
	
	//WikipediaConfiguration file
	static String _conf = "C:\\Thalisson\\Wikification\\wikipedia-miner-starwars\\configs\\wikipedia-template-starwars.xml";
	
	//Folder to output the program files
	static String _output = "C:\\Thalisson\\Documentos\\WikiMiner";
	
	
	public static void main(String[] args) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File(_conf)) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		WIterator<Integer, DbPage> PageMapIterator = PageMap.getIterator();
		WIterator<Integer, DbPage> PageMapIterator2 = PageMap.getIterator();
	    System.out.println("Iniciando...");

	    PrintWriter articles_file = new PrintWriter(_output + "articles_file.txt", "UTF-8");
	    
	    //CRIANDO COLUNAS
	    ArrayList<String> colunas_list = new ArrayList<>();
	    while(PageMapIterator.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator.next().getKey()) ;
	    	if(page.exists()) {
	    		if(page.getType() == PageType.article) {
	    			System.out.println(page.getId() + ". " + page.getTitle() + " | " + page.getType());
	    			colunas_list.add(Integer.toString(page.getId()));
	    		}
	    			
	    	}
	    }//CRIANDO COLUNAS
	    
	    articles_file.println("art_i,art_j,link");
	    //Criando Matriz
	    while(PageMapIterator2.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator2.next().getKey()) ;
	    	if(page.exists()) {
	    		if(page.getType() == PageType.article) {
	    			System.out.println(page.getId() + " | " + page.getTitle());
	    			ArrayList<String> linksOut = new ArrayList<>();
	    			Article[] linksOutArticle = ((Article) page).getLinksOut();
	    			for(int i = 0; i < linksOutArticle.length; i++) {
	    		    	String[] ids = linksOutArticle[i].toString().split(":");
	    		    	linksOut.add(ids[0]);
	    		    }
	    			
	    			//VERIFICANDO SAIDAS DE LINKS
	    			for(String id_coluna: colunas_list) {
	    				if(linksOut.contains(id_coluna))
	    					articles_file.println(page.getId() + "," + id_coluna + ",1");
	    				else
	    					articles_file.println(page.getId() + "," + id_coluna + ",0");
	    			}//VERIFICANDO SAIDAS DE LINKS
	    			
	    		}
	    			
	    	}

	    }//Criando Matriz
	    articles_file.close();
	    System.out.println("Concluído");
	    
	}
	
}
