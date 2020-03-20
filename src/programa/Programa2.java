package programa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


import org.wikipedia.miner.annotation.ArticleCleaner.SnippetLength;


import org.wikipedia.miner.db.WDatabase.DatabaseType;
import org.wikipedia.miner.model.Wikipedia;
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
public class Programa2 {
	
	/*MOD*/
	static String output_dir = "/home/thalisson/Documents/WikiMiner/Programa2/";
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
	
public Programa2(File dataDir, Wikipedia wikipedia) throws Exception {
		
		_dataDir = dataDir ;
		_wikipedia = wikipedia ;
		
		_disambiguator = new DisambiguatorMOD(_wikipedia) ;
		_topicDetector = new TopicDetector(_wikipedia, _disambiguator) ;
		_linkDetector = new LinkDetectorMOD(_wikipedia, output_dir) ;
		
		_artsTrain = new File(_dataDir.getPath() + "/articlesTrain_ID.csv") ;
		_artsTestDisambig = new File(_dataDir.getPath() + "/articlesTestDisambig.csv") ;
		_artsTestDetect = new File(_dataDir.getPath() + "/articlesTestDetect.csv") ;
		
		_arffDisambig = new File(_dataDir.getPath() + "/disambig.arff") ;
		_arffDetect = new File(_dataDir.getPath() + "/detect.arff") ;
		
		_modelDisambig = new File(_dataDir.getPath() + "/disambig.model") ;
		_modelDetect = new File(_dataDir.getPath() + "/detect.model") ;
	}
	
	private void gatherArticleSets() throws IOException{
		/*int[] sizes = {200,100,100} ;

        ArticleSet[] articleSets = new ArticleSetBuilder()
            .setMinOutLinks(25)
            .setMinInLinks(50)
            .setMaxListProportion(0.1)
            .setMinWordCount(1000)
            .setMaxWordCount(2000)
            .buildExclusiveSets(sizes, _wikipedia) ;
		
        articleSets[0].save(_artsTrain) ;
        articleSets[1].save(_artsTestDisambig) ;
        articleSets[2].save(_artsTestDetect) ;*/
	    
    }
	
    
    private void createArffFiles(String datasetName) throws IOException, Exception {
    	
    	if (!_artsTrain.canRead()) 
            throw new Exception("Article sets have not yet been created") ;
		
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
		
		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		conf.addDatabaseToCache(DatabaseType.label) ;
		conf.addDatabaseToCache(DatabaseType.pageLinksInNoSentences) ;
		
		Wikipedia wikipedia = new Wikipedia(conf, false) ;
		
		Programa2 trainer = new Programa2(dataDir, wikipedia) ;
		
		//TESTES
		//TESTES//
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in)) ;
		
		while (true) {
			System.out.println("What would you like to do?") ;
			System.out.println(" - [1] createArffFiles.") ;
			System.out.println(" - [2] aff.") ;
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
				System.out.println("Dataset name:") ;
				String datasetName = input.readLine() ;
				trainer.createArffFiles(datasetName);
				break ;
			case 2:
				System.out.println("Dataset name:") ;
				String datasetName1 = input.readLine() ;
				
				break ;
			case 3:
				System.out.println("Disambiguation classifer config (or ENTER to use default):") ;
				String configDisambig = input.readLine() ;
				
				System.out.println("Detection classifer config (or ENTER to use default):") ;
				String configDetect = input.readLine() ;
				
				trainer.createClassifiers(configDisambig, configDetect) ;
				break ;
			case 4:
				trainer.evaluate() ;
				break ;
			default:
				System.out.println("Invalid Input") ;
			}
		}

	}

}