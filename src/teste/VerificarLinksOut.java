package teste;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class VerificarLinksOut {
	
	public static void main(String[] args) throws Exception {
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		
		int id_article = 5;
		
		Page article = Page.createPage(wikipedia.getEnvironment(), id_article);
		System.out.println("Article existe: " + article.exists());
		System.out.println("Links que sai do article: " + ((Article) article).getLinksOut().length);
	    Article[] linksOut = ((Article) article).getLinksOut();
	    for(int i = 0; i < linksOut.length; i++)
	    	System.out.print(linksOut[i].getId() + " | ");
	    	//System.out.println("linksOut["+ i +"] " + linksOut[i].toString() + "| Type: " + Page.createPage(wikipedia.getEnvironment(), linksOut[i].getId()).getType()) ;
		
	    System.exit(1);
	    /*CHECAGEM*/
	    System.out.println("Vamos lá...");
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    String dirCsv = "/home/thalisson/Documents/WikiMiner/";
	    Scanner csv_articles = new Scanner(new File(dirCsv + "csv_articles.csv"));
	    System.out.println("Procurando...");
	    
	    
	    
	    String[] colunas = csv_articles.nextLine().split(",");
	    System.out.println("Tamanho da coluna: " + (colunas.length - 1));
	    while(csv_articles.hasNextLine()) {
	    	String[] aux = csv_articles.nextLine().split(",");
	    	System.out.println(aux[0]);
	    	if(aux[0].equals(Integer.toString(id_article))) {
	    		//ArrayList<String> csv_dados = new ArrayList<>();
	    		for(int i = 1; i < aux.length; i++) {
	    			if(aux[i].equals("1")) {
	    				System.out.print(colunas[i] + " | ");
	    			}
	    		}
	    		for(int i = 0; i < linksOut.length; i++)
	    	    	System.out.print(linksOut[i].getId() + " | ") ;
	    		csv_articles.close();
	    		System.out.println("Tamanho da coluna: " + (colunas.length - 1));
	    		System.exit(1);
	    	}
	    }
	    csv_articles.close();
	    
	    
	    
	    System.out.println("Tamanho da coluna: " + (colunas.length - 1));
	}

}
