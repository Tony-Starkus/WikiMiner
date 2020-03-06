package programa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.wikipedia.miner.db.*;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;



public class Programa1Alternative  {
	
	//WikipediaConfiguration file
	static String _conf = "/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml";
	
	//Folder to output the program files
	static String _output = "/home/thalisson/Documents/WikiMiner/";
	
	//Files
	PrintWriter _articles_id_file;
	
	static ArrayList<String> _id_articles = new ArrayList<>();
	int _mediaInLinks; int _mediaOutLinks;
	
	public void calcularMedia(Wikipedia wikipedia, WDatabase<Integer, DbPage> pageMap) throws FileNotFoundException, UnsupportedEncodingException {
		WIterator<Integer, DbPage> IteratorMedia = pageMap.getIterator();
		ArrayList<Integer> articles_id_list = new ArrayList<>();
		_articles_id_file = new PrintWriter(_output + "articles_id_links.csv", "UTF-8");
		_articles_id_file.println("art_id,inlinks,outlinks");

	    int totalInLinks = 0;
	    int totalOutLinks = 0;
	    int totalArticles = 0;
	    /* https://pt.khanacademy.org/math/probability/data-distributions-a1/summarizing-spread-distributions/a/calculating-standard-deviation-step-by-step
	    Etapa 1: calcular a média.
		Etapa 2: calcular o quadrado da distância entre cada ponto e a média.
		Etapa 3: somar os valores da Etapa 2.
		Etapa 4: dividir pelo número de pontos.
		Etapa 5: calcular a raiz quadrada.
		*/
	    
	    //Etapa 1
	    /*Somando a quantidade de in/out links de todos os article | Contando o total de articles*/
	    while(IteratorMedia.hasNext()) {
	    	int inLinks = 0;
	    	int outLink = 0;
	    	Page page = Page.createPage(wikipedia.getEnvironment(), IteratorMedia.next().getKey());
	    	if(page.exists() && page.getType() == PageType.article) {
	    		articles_id_list.add(page.getId());
	    		inLinks += ((Article) page).getDistinctLinksInCount();
	    		outLink += ((Article) page).getDistinctLinksOutCount();
	    		totalInLinks += inLinks;
	    		totalOutLinks += outLink;
	    		_articles_id_file.println(page.getId() + "," + inLinks + "," + outLink);
	    		totalArticles++;
	    		System.out.println(totalArticles);
	    	}
	    }
	    _articles_id_file.close();
	    System.out.println("totalInLinks: " + totalInLinks);
	    System.out.println("totalOutLinks: " + totalOutLinks);
	    System.out.println("totalArticles: " + totalArticles);
	    
	    /* Média Artimética Simples de inlinks e outlinks */
	    _mediaInLinks = totalInLinks / totalArticles;
	    _mediaOutLinks = totalOutLinks / totalArticles;
	    
	    System.out.println("média totalInLinks: " + _mediaInLinks);
	    System.out.println("média totalOutLinks: " + _mediaOutLinks);
	    
	    int varianciaIn = 0;
	    int varianciaOut = 0;
	    //Etapa 2 e Etapa 3
	    for(Integer id_article: articles_id_list) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), id_article);
	    	int in = (int) Math.pow(((Article) page).getDistinctLinksInCount() - _mediaInLinks, 2) ;
	    	int out = (int) Math.pow(((Article) page).getDistinctLinksOutCount() - _mediaOutLinks, 2) ;
	    	varianciaIn += in;
	    	varianciaOut += out;
	    }
	    System.out.println(varianciaIn);
	    System.out.println(varianciaOut);
	    
	    //Etapa 4 e 5
	    double desvioPadraoIn = Math.sqrt(varianciaIn / totalArticles);
	    double desvioPadraoOut = Math.sqrt(varianciaOut / totalArticles);
	    System.out.println("desvio padrao in: " + desvioPadraoIn);
	    System.out.println("desvio padrao out " + desvioPadraoOut);
	}
	
	public void colunasArticle(Wikipedia wikipedia) throws FileNotFoundException {
		if(!new File(_output + "articles_id_links.csv").exists() || _mediaInLinks == 0) {
			System.err.println("Por favor, execute a opção 1");
			return;
		}
		Scanner id_articles_file_scanner = new Scanner(new File(_output + "articles_id_links.csv"));
		id_articles_file_scanner.nextLine(); // Pulando a primeira linha.
		int contador = 0;
		while(id_articles_file_scanner.hasNextLine()) {
			contador++;
			_id_articles.add(id_articles_file_scanner.nextLine().split(",")[0]);
			System.out.println(contador);
		}
		id_articles_file_scanner.close();
		System.out.println("contador: " + contador);
		System.out.println("Concluído - [2] Criar vetor de colunas.");
	}
	
	public void criarMatriz(Wikipedia wikipedia) {
		if(_id_articles.size() == 0) {
			System.err.println("O vetor de colunas não está criado!");
			return;
		}
			
		PrintWriter articles_file, articles_train_id;
		boolean createPrograma2Dir = new File(_output + "Programa2/").mkdir();
		try {
			articles_file = new PrintWriter(_output + "articles_in-out-links_info.csv", "UTF-8");
				if(createPrograma2Dir)
					articles_train_id = new PrintWriter(_output + "Programa2/articlesTrain_ID.csv", "UTF-8");
				else {
					System.err.println("Não foi possível criar o diretório para salvar arquivo que será utilizado pelo Programa2");
					articles_file.close();
					return;
				}
			articles_file.println("art_i,art_j,link");
			
			//Criando Matriz
			int qtdIter = 0;
			int artUpMedia = 0;
			ArrayList<String> artsIgnorados = new ArrayList<>();
			Scanner articles_id_links_file_done_scanner = new Scanner(new File(_output + "articles_id_links.csv"));
			articles_id_links_file_done_scanner.nextLine(); // PULANDO PRIMEIRA LINHA
			while(articles_id_links_file_done_scanner.hasNextLine()) {
				qtdIter++;
				String[] aux = articles_id_links_file_done_scanner.nextLine().split(",");
				if(Integer.parseInt(aux[1]) >= _mediaInLinks && Integer.parseInt(aux[2]) >= _mediaOutLinks) {
					artUpMedia++;
					Page page = Page.createPage(wikipedia.getEnvironment(), Integer.parseInt(aux[0]));
					if(page.getMarkup() != null) {
						System.out.println(page.getId() + " | " + page.getTitle());
						articles_train_id.println(page.getId());
		    			ArrayList<String> linksOut = new ArrayList<>();
		    			Article[] linksOutArticle = ((Article) page).getLinksOut();
		    			for(int i = 0; i < linksOutArticle.length; i++) {
		    		    	String[] ids = linksOutArticle[i].toString().split(":");
		    		    	linksOut.add(ids[0]);
		    		    }
		    			for(String id_coluna: _id_articles) {
		    				if(linksOut.contains(id_coluna))
		    					articles_file.println(page.getId() + "," + id_coluna + ",1");
		    				else
		    					articles_file.println(page.getId() + "," + id_coluna + ",0");
		    			}
					} else {
						artsIgnorados.add(page.toString());
					}
					
				}
			}
			articles_id_links_file_done_scanner.close();
		    articles_file.close();
		    articles_train_id.close();
		    System.out.println("Quantidade de iterações: " + qtdIter);
		    System.out.println("Articles dentro da média: " + artUpMedia);
		    System.out.println("Concluído - [3] Criar Matriz.");
		    if(artsIgnorados.size() != 0)
		    	for(String page: artsIgnorados)
		    		System.out.println(page);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	
	public static void main(String[] args) throws Exception {
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File(_conf)) ;
	    Wikipedia wikipedia = new Wikipedia(conf, false) ;
		WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
	    
	    Programa1Alternative programa1 = new Programa1Alternative();
	    
	    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	    while (true) {
	    	System.out.println("Escolha uma opção:") ;
			System.out.println(" - [1] Calcular média.") ;
			System.out.println(" - [2] Criar vetor de colunas.") ;
			System.out.println(" - [3] Criar Matriz.") ;
			System.out.println(" - pressione ENTER para sair.") ;
			
			String line = input.readLine() ;
			if (line.trim().length() == 0)
				break ;
			Integer choice = 0 ;
			try {
				choice = Integer.parseInt(line) ;
			} catch (Exception e) {
				System.out.println("Invalid Input") ;
				continue ;
			}
			
			switch(choice) {
				case 1:
					programa1.calcularMedia(wikipedia, PageMap);
					System.out.println("Concluído - [1] Calcular média.") ;
					break;
				
				case 2:
					programa1.colunasArticle(wikipedia);
					break;
					
				case 3:
					programa1.criarMatriz(wikipedia);
					break;
			}
			
	    }
	    
	    
	}
	
}
