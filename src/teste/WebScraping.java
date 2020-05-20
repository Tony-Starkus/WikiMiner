package teste;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.xml.sax.SAXException;

public class WebScraping {

	public static void main(String[] args) throws Exception {
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
	    String input = "/home/thalisson/PycharmProjects/webscraping-Wookipedia/";
	    String output = "/home/thalisson/Documents/";
	    PrintWriter all = new PrintWriter(new File(output + "all.csv"));
	    WebScraping program = new WebScraping();
	    program.ArticlesFA(input, output, wikipedia, all);
	    program.ArticlesGA(input, output, wikipedia, all);
	    program.ArticlesCA(input, output, wikipedia, all);
	    all.close();
	
	}
	
	public void ArticlesFA(String input, String output, Wikipedia wikipedia, PrintWriter all) throws Exception {
		Scanner file = new Scanner(new File(input + "fa_titles.txt"));
	    PrintWriter fa_id = new PrintWriter(new File(output + "fa_id.txt"));
	    ArrayList<Integer> lista = new ArrayList<Integer>();
	    
	    while(file.hasNextLine()) {
	    	String line = file.nextLine();
	    	Article article = wikipedia.getArticleByTitle(line);
	    	if(article != null) {
    			lista.add(article.getId());
	    	} 
	    }
	    
	    int existe = 0;
	    int naoExiste = 0;
	    int tipo = 0;
	    int naoTipo = 0;
	    int total = 0;
	    
	    System.out.println("Coletando 1000 FA articles");
	    for(int id: lista) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), id);
	    	if(page.exists()) {
	    		existe++;
	    		
	    		if(page.getType() == PageType.article) {
	    			if(page.getMarkup() != null) {
	    				fa_id.println(page.getId());
		    			all.println(page.getId());
		    			tipo++;
	    			}
		    		if(tipo == 1000)
		    			break;
		    	} else {
		    		naoTipo++;
		    	}
	    		
	    	} else {
	    		naoExiste++;
	    	}
	    	
	    }
	    
	    System.out.println("total: " + total);
	    System.out.println("Page existe: " + existe);
	    System.out.println("Page não existe: " + naoExiste);
	    System.out.println("Page article: " + tipo);
	    System.out.println("Page não article: " + naoTipo);
	    System.out.println("-----------------------------");
	    
	    file.close();
	    fa_id.close();
	}
	
	public void ArticlesGA(String input, String output, Wikipedia wikipedia, PrintWriter all) throws Exception {
		Scanner file = new Scanner(new File(input + "ga_titles.txt"));
	    PrintWriter ga_id = new PrintWriter(new File(output + "ga_id.txt"));
	    ArrayList<Integer> lista = new ArrayList<Integer>();
	    
	    while(file.hasNextLine()) {
	    	String line = file.nextLine();
	    	Article article = wikipedia.getArticleByTitle(line);
	    	if(article != null) {
    			lista.add(article.getId());
	    	} 
	    }
	    
	    int existe = 0;
	    int naoExiste = 0;
	    int tipo = 0;
	    int naoTipo = 0;
	    int total = 0;
	    System.out.println("Coletando 1000 GA articles");
	    for(int id: lista) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), id);
	    	if(page.exists()) {
	    		existe++;
	    		
	    		if(page.getType() == PageType.article) {
	    			if(page.getMarkup() != null) {
	    				ga_id.println(page.getId());
		    			all.println(page.getId());
		    			tipo++;
	    			}
		    		if(tipo == 1000)
		    			break;
		    	} else {
		    		naoTipo++;
		    	}
	    		
	    	} else {
	    		naoExiste++;
	    	}
	    	
	    }
	    
	    System.out.println("total: " + total);
	    System.out.println("Page existe: " + existe);
	    System.out.println("Page não existe: " + naoExiste);
	    System.out.println("Page article: " + tipo);
	    System.out.println("Page não article: " + naoTipo);
	    System.out.println("-----------------------------");
	    
	    file.close();
	    ga_id.close();
	}
	
	public void ArticlesCA(String input, String output, Wikipedia wikipedia, PrintWriter all) throws Exception {
		Scanner file = new Scanner(new File(input + "ca_titles.txt"));
	    PrintWriter ca_id = new PrintWriter(new File(output + "ca_id.txt"));
	    ArrayList<Integer> lista = new ArrayList<Integer>();
	    
	    while(file.hasNextLine()) {
	    	String line = file.nextLine();
	    	Article article = wikipedia.getArticleByTitle(line);
	    	if(article != null) {
    			lista.add(article.getId());
	    	} 
	    }
	    
	    int existe = 0;
	    int naoExiste = 0;
	    int tipo = 0;
	    int naoTipo = 0;
	    int total = 0;
	    System.out.println("Coletando 1000 CA articles");
	    for(int id: lista) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), id);
	    	if(page.exists()) {
	    		existe++;
	    		
	    		if(page.getType() == PageType.article) {
	    			if(page.getMarkup() != null) {
	    				ca_id.println(page.getId());
		    			all.println(page.getId());
			    		tipo++;
	    			}
		    		if(tipo == 1000)
		    			break;
		    	} else {
		    		naoTipo++;
		    	}
	    		
	    	} else {
	    		naoExiste++;
	    	}
	    	
	    }
	    
	    System.out.println("total: " + total);
	    System.out.println("Page existe: " + existe);
	    System.out.println("Page não existe: " + naoExiste);
	    System.out.println("Page article: " + tipo);
	    System.out.println("Page não article: " + naoTipo);
	    
	    file.close();
	    ca_id.close();
	}

}
