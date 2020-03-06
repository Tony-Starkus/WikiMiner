package programa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wikipedia.miner.annotation.ArticleCleaner.SnippetLength;
import org.wikipedia.miner.annotation.Disambiguator;
import org.wikipedia.miner.annotation.TopicDetector;
import org.wikipedia.miner.annotation.weighting.LinkDetector;
import org.wikipedia.miner.db.WDatabase.DatabaseType;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.ArticleSet;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class Programa20 {

	private Wikipedia _wikipedia ;
	
	//directory in which files will be stored
	private File _dataDir ;
	
	//classes for performing annotation
	private Disambiguator _disambiguator ;
	private TopicDetector _topicDetector ;
	private LinkDetector _linkDetector ;
	
	//article set files
	private File _artsTrain, _artsTestDisambig, _artsTestDetect ;
	
	//feature data files
	private File _arffDisambig, _arffDetect ;
	
	//model files
	private File _modelDisambig, _modelDetect ;
	
	public Programa20(File dataDir, Wikipedia wikipedia) throws Exception {
		
		_dataDir = dataDir ;
		_wikipedia = wikipedia ;
		
		_disambiguator = new Disambiguator(_wikipedia) ;
		_topicDetector = new TopicDetector(_wikipedia, _disambiguator) ;
		_linkDetector = new LinkDetector(_wikipedia) ;
		
		_artsTrain = new File(_dataDir.getPath() + "/articlesTrain_ID.csv") ;
		_artsTestDisambig = new File(_dataDir.getPath() + "/articlesTestDisambig.csv") ;
		_artsTestDetect = new File(_dataDir.getPath() + "/articlesTestDetect.csv") ;
		
		_arffDisambig = new File(_dataDir.getPath() + "/disambig.arff") ;
		_arffDetect = new File(_dataDir.getPath() + "/detect.arff") ;
		
		_modelDisambig = new File(_dataDir.getPath() + "/disambig.model") ;
		_modelDetect = new File(_dataDir.getPath() + "/detect.model") ;
		
	}
	
	private void createArffFiles(String datasetName) throws IOException, Exception {
		 if (!_artsTrain.canRead()) 
	            throw new Exception("Article sets have not yet been created") ;
			
		 	System.out.println("1");
	        ArticleSet trainingSet = new ArticleSet(_artsTrain, _wikipedia) ;
			
	        System.out.println("2");
	        _disambiguator.train(trainingSet, SnippetLength.full, datasetName + "_disambiguation", null) ;
	        _disambiguator.saveTrainingData(_arffDisambig) ;
	        _disambiguator.buildDefaultClassifier();
			
	        System.out.println("3");
	        _linkDetector.train(trainingSet, SnippetLength.full, datasetName + "_detection", _topicDetector, null) ;
	        _linkDetector.saveTrainingData(_arffDetect) ;
    }
    
    private void createClassifiers(String configDisambig, String configDetect) throws Exception {
		
    }
    
    private void evaluate() throws Exception {
    
    }
    
    public static void main(String[] args) throws Exception {
    	
    	File dataDir = new File("/home/thalisson/Documents/WikiMiner/Programa2/") ;
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		conf.addDatabaseToCache(DatabaseType.label) ;
		conf.addDatabaseToCache(DatabaseType.pageLinksInNoSentences) ;
		
		Wikipedia wikipedia = new Wikipedia(conf, false) ;
		
		Programa20 trainer = new Programa20(dataDir, wikipedia) ;
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in)) ;
		
		while (true) {
			System.out.println("What would you like to do?") ;
			System.out.println(" - [1] create arff files.") ;
			System.out.println(" - [2] create classifiers.") ;
			System.out.println(" - [3] evaluate classifiers.") ;
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
				System.out.println("Dataset name:") ;
				String datasetName = input.readLine() ;
				
				trainer.createArffFiles(datasetName) ;
				break ;
			case 2:
				System.out.println("Disambiguation classifer config (or ENTER to use default):") ;
				String configDisambig = input.readLine() ;
				
				System.out.println("Detection classifer config (or ENTER to use default):") ;
				String configDetect = input.readLine() ;
				
				trainer.createClassifiers(configDisambig, configDetect) ;
				break ;
			case 3:
				trainer.evaluate() ;
				break ;
			default:
				System.out.println("Invalid Input") ;
			}
		}
    	
    	
    }

}
