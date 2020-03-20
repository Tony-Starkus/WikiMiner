package pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.wikipedia.miner.db.WDatabase;
import org.wikipedia.miner.db.WIterator;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class finalizando {

	public static void main(String[] args) throws Exception {
		
		/*SETP 3 -> Verificar se todos os títulos tem valor diferente de zero
		 * Se tiver, a teoria falha.
		 * Se todos forem zero, a teoria é aceita*/
		System.out.println("Verificando se todos os valores são 0 (zero)");
		boolean todosArticles = true;
		String dadoLine;
		BufferedReader checagemReader = new BufferedReader(new FileReader("/home/thalisson/Documents/WikiMiner/checagem.txt"));
		while((dadoLine = checagemReader.readLine()) != null) {
			String[] aux = dadoLine.split(";");
			System.out.println(aux[0]);
			if(!aux[1].equals("0")) {
				System.out.println(aux[0] + " : " + aux[1]);
				System.out.println("Teoria falhou! Senta e chora :(");
				System.exit(1);
				todosArticles = false;
			}
		}
		
		if(todosArticles)
			System.out.println("Teoria válida!");
		else
			System.out.println("Teoria falhou!");
		
		/* STEP 4*/
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		WIterator<Integer, DbPage> PageMapIterator = PageMap.getIterator();
	    System.out.println("Iniciando...");
	    
	    int pageValido = 0;
	    int pageNulo = 0;
	    int pageArticle = 0;
	    int total = 0;
	    
	    while(PageMapIterator.hasNext()) {
	    	total++;
	    	Page page = Page.createPage(wikipedia.getEnvironment(), PageMapIterator.next().getKey()) ;
	    	if(page.exists()) {
	    		pageValido++;
	    		System.out.println(pageValido + ". " + page.getTitle() + " | " + page.getType());
	    		if(page.getType() == PageType.article)
	    			pageArticle++;
	    	} else {
	    		pageNulo++;
	    	}

	    }
	    
	    System.out.println("Total de page = " + PageMap.getDatabaseSize());
	    System.out.println("Page valido = " + pageValido);
	    System.out.println("Total Page Article = " + pageArticle);
	    System.out.println("Page nulo = " + pageNulo);
	    System.out.println("Total= " + total);
		System.out.println("Procurando e contando todos title com valor 0 (zero)");
		BufferedReader pagesOriginalReader = new BufferedReader(new FileReader("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/db/page (original).csv"));


	}

}
