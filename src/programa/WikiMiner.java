package programa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.wikipedia.miner.annotation.ArticleCleaner.SnippetLength;
import org.wikipedia.miner.db.WDatabase;
import org.wikipedia.miner.db.WIterator;
import org.wikipedia.miner.db.WDatabase.DatabaseType;
import org.wikipedia.miner.db.struct.DbPage;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.ArticleSet;
import org.wikipedia.miner.util.ArticleSetBuilder;
import org.wikipedia.miner.util.Result;
import org.wikipedia.miner.util.WikipediaConfiguration;

import annotationMOD.DisambiguatorMOD;
import annotationMOD.TopicDetector;
import mods.LinkDetectorMOD; //LinkDetector MODIFICADO
import weka.classifiers.Classifier;
import weka.core.Utils;
/*VERSÃO MOD - MODIFICADO - POSSÍVEL PROGRAMA 2*/
public class WikiMiner {
	
	/*MOD*/
	static String output_dir = "/home/thalisson/Documents/WikiMiner/";
	/*MOD*/
	
	private Wikipedia _wikipedia ;
	
	//directory in which files will be stored
	private File _dataDir ;
	
	//classes for performing annotation
	private DisambiguatorMOD _disambiguator ;
	private TopicDetector _topicDetector ;
	private LinkDetectorMOD _linkDetector ;
	
	//article set files
	private File _artsTrain, _artsTestDisambig, _artsTestDetect ;
	
	//feature data files
	private File _arffDisambig, _arffDetect ;
	
	//model files
	private File _modelDisambig, _modelDetect ;
	
	//stats file
	private File _statsCsv;
	
	//Important variables
	int _mediaInLinks; int _mediaOutLinks;
	
	//Article set id list
	static ArrayList<Integer> _articlesSet_list;
	
	public WikiMiner(File dataDir, Wikipedia wikipedia) throws Exception {
		
		_dataDir = dataDir ;
		_wikipedia = wikipedia ;
		
		_disambiguator = new DisambiguatorMOD(_wikipedia) ;
		_topicDetector = new TopicDetector(_wikipedia, _disambiguator) ;
		_linkDetector = new LinkDetectorMOD(_wikipedia, output_dir) ;
		
		_artsTrain = new File(_dataDir.getPath() + "/articlesSetID.csv") ;
		_artsTestDisambig = new File(_dataDir.getPath() + "/articlesTestDisambig.csv") ;
		_artsTestDetect = new File(_dataDir.getPath() + "/articlesTestDetect.csv") ;
		
		_arffDisambig = new File(_dataDir.getPath() + "/disambig.arff") ;
		_arffDetect = new File(_dataDir.getPath() + "/detect.arff") ;
		
		_modelDisambig = new File(_dataDir.getPath() + "/disambig.model") ;
		_modelDetect = new File(_dataDir.getPath() + "/detect.model") ;
		
		_statsCsv = new File(_dataDir.getPath() + "/stats.csv");
	}
	
	public void loadVariables() throws FileNotFoundException {
		Scanner file = new Scanner(new File(output_dir + "stats.csv"));
		while(file.hasNextLine()) {
			String[] line = file.nextLine().split(",");
			switch(line[0]) {
				case "mediaInLinks":
					_mediaInLinks = Integer.parseInt(line[1]);
					break;
				case "mediaOutLinks":
					_mediaOutLinks = Integer.parseInt(line[1]);
					break;
			}
		}
		file.close();
	}
	
	public void loadArticlesSetList() {
		Scanner file;
		try {
			file = new Scanner(new File(output_dir + "articlesSetID.csv"));
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
	}
	
	private void createArticlesSet(Wikipedia wikipedia, WDatabase<Integer, DbPage> pageMap) throws IOException{
		/* https://pt.khanacademy.org/math/probability/data-distributions-a1/summarizing-spread-distributions/a/calculating-standard-deviation-step-by-step */
		ArrayList<Integer> articles_id_list_temp = new ArrayList<>();
		WIterator<Integer, DbPage> IteratorMedia = pageMap.getIterator();
		PrintWriter stats_csv = new PrintWriter(output_dir + "stats.csv", "UTF-8");
		int totalInLinks = 0;
	    int totalOutLinks = 0;
	    int totalArticles = 0;
	    int totalPages = 0;
		while(IteratorMedia.hasNext()) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), IteratorMedia.next().getKey());
	    	if(page.exists()) {
	    		totalPages++;
	    		if(page.getType() == PageType.article) {
	    			articles_id_list_temp.add(page.getId());
		    		totalInLinks += ((Article) page).getDistinctLinksInCount();
		    		totalOutLinks += ((Article) page).getDistinctLinksOutCount();
		    		totalArticles++;
		    		System.out.println(totalArticles);
	    		}
	    	}
	    }
	    stats_csv.println("pageCount," + totalPages);
	    stats_csv.println("articleCount," + totalArticles);
	    stats_csv.println("totalInLinks," + totalInLinks);
	    stats_csv.println("totalOutLinks," + totalOutLinks);
	    
	    /* Média Artimética Simples de inlinks e outlinks */
	    _mediaInLinks = totalInLinks / totalArticles;
	    _mediaOutLinks = totalOutLinks / totalArticles;
	
	    stats_csv.println("mediaInLinks," + _mediaInLinks);
	    stats_csv.println("mediaOutLinks," + _mediaOutLinks);
	    stats_csv.close();
	    loadVariables();
	    
	    //Criando csv com o id dos articles para treinamento
	    System.out.println("Criando csv com o id dos articles: articlesSetID.csv");
	    PrintWriter pwArticleSet = new PrintWriter(_artsTrain);
	    int aux = 0;
	    for(int id : articles_id_list_temp) {
	    	Page page = Page.createPage(wikipedia.getEnvironment(), id);
	    	if(page.exists())  {
	    		if(page.getType() == PageType.article) {
	    			if(((Article) page).getDistinctLinksInCount() >= _mediaInLinks && ((Article) page).getDistinctLinksOutCount() >= _mediaOutLinks)
			    		pwArticleSet.println(page.getId());
	    		} else {
	    			System.err.println("Está página não é um article!");
	    			System.exit(1);
	    		}
	    	} else {
	    		System.err.println("Está página não existe!");
	    		System.exit(1);
	    	}
	    }
	    pwArticleSet.close();
	    loadArticlesSetList();//Criando csv com o id dos articles para treinamento
	    
    }
	
	private void createInOutLinksMatriz(Wikipedia wikipedia) throws FileNotFoundException {
		if(_articlesSet_list.size() < 1) {
			System.err.println("A lista de id de articles não está carregada!");
			return;
		}
		PrintWriter matriz_file = new PrintWriter(output_dir + "matriz.csv");
		
		//Criando Matriz
		int qtdIter = 0;
		int artUpMedia = 0;
		for(int id : _articlesSet_list) {
			qtdIter++;
			Page page = Page.createPage(wikipedia.getEnvironment(), id);
			if(page.exists()) {
				artUpMedia++;
				System.out.println(page.getId() + " | " + page.getTitle());
				ArrayList<String> linksOut = new ArrayList<>();
    			Article[] linksOutArticle = ((Article) page).getLinksOut();
    			for(int i = 0; i < linksOutArticle.length; i++) {
    		    	String[] ids = linksOutArticle[i].toString().split(":");
    		    	linksOut.add(ids[0]);
    		    }
    			for(int id_coluna: _articlesSet_list) {
					if(linksOut.contains(Integer.toString(id_coluna)))
    					matriz_file.println(page.getId() + "," + id_coluna + ",1");
    				else
    					matriz_file.println(page.getId() + "," + id_coluna + ",0");
    			}
			} else {
				System.err.println("Página não existe: " + page.getId());
			}
		}
		matriz_file.close();
		System.out.println("qtdIter: " + qtdIter);
		System.out.println("artUpMedia: " + artUpMedia);
    }
	
    
    private void createArffFiles(String datasetName) throws IOException, Exception {
    	
    	if (!_artsTrain.canRead()) 
            throw new Exception("Article sets have not yet been created") ;
    	if(_articlesSet_list.size() < 1) {
    		System.err.println("A lista de ID dos articles de treinamento não está carregado");
    		return;
    	}
    		
		
        ArticleSet trainingSet = new ArticleSet(_artsTrain, _wikipedia) ;
		
        System.out.println("_disambiguator.train:");
        _disambiguator.trainMOD(trainingSet, SnippetLength.full, datasetName + "_disambiguation", output_dir, null) ;
        System.out.println("_disambiguator.saveTrainingData:");
        _disambiguator.saveTrainingData(_arffDisambig) ;
        System.out.println("_disambiguator.buildDefaultClassifier:");
        _disambiguator.buildDefaultClassifier();
		
        System.out.println("_linkDetector.train:");
        _linkDetector.train(trainingSet, SnippetLength.full, datasetName + "_detection", _topicDetector, null) ;
        System.out.println("_linkDetector.saveTrainingData:");
        _linkDetector.saveTrainingData(_arffDetect) ;
    
    }
    
    
    
    private void createClassifiers(String configDisambig, String configDetect) throws Exception {
    	
    }

    private void evaluate() throws Exception {
    
    }

	public static void main(String[] args) throws Exception {
		
		File dataDir = new File(output_dir);
		if(!dataDir.exists())
			dataDir.mkdir();
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		conf.addDatabaseToCache(DatabaseType.label) ;
		conf.addDatabaseToCache(DatabaseType.pageLinksInNoSentences) ;
		Wikipedia wikipedia = new Wikipedia(conf, false) ;
		WDatabase<Integer, DbPage> PageMap = wikipedia.getEnvironment().getDbPage();
		
		WikiMiner trainer = new WikiMiner(dataDir, wikipedia) ;
		
		_articlesSet_list = new ArrayList<>();
		if(new File(output_dir + "articlesSetID.csv").exists())
			trainer.loadArticlesSetList();
		
		if(new File(output_dir + "/stats.csv").exists())
			trainer.loadVariables();
		
		//TESTES
		//TESTES//
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in)) ;
		
		while (true) {
			System.out.println("What would you like to do?") ;
			System.out.println(" - [1] Create Articles Train Set.") ;
			System.out.println(" - [2] Create in/out links matriz") ;
			System.out.println(" - [3] create classifiers.") ;
			System.out.println(" - [4] evaluate classifiers.") ;
			System.out.println(" - or ENTER to quit.") ;
			
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
				trainer.createArticlesSet(wikipedia, PageMap);
				break ;
			case 2:
				trainer.createInOutLinksMatriz(wikipedia);
				break;
			case 3:
				System.out.println("Dataset name:") ;
				String datasetName = input.readLine() ;
				trainer.createArffFiles(datasetName);;
				
				break ;
			case 4:
				System.out.println("Disambiguation classifer config (or ENTER to use default):") ;
				String configDisambig = input.readLine() ;
				
				System.out.println("Detection classifer config (or ENTER to use default):") ;
				String configDetect = input.readLine() ;
				
				trainer.createClassifiers(configDisambig, configDetect) ;
				break ;
			case 5:
				trainer.evaluate() ;
				break ;
			default:
				System.out.println("Invalid Input") ;
			}
		}

	}

}
