package programa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wikipedia.miner.annotation.*;
import org.wikipedia.miner.annotation.weighting.LinkDetector;
import org.wikipedia.miner.db.WDatabase.DatabaseType;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.ArticleSet;
import org.wikipedia.miner.util.ArticleSetBuilder;
import org.wikipedia.miner.util.WikipediaConfiguration;

public class Programa2 {

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

	public Programa2(File dataDir, Wikipedia wikipedia) throws Exception {

		_topicDetector = new TopicDetector(_wikipedia, _disambiguator) ;
		_linkDetector = new LinkDetector(_wikipedia) ;
		
		_artsTrain = new File(_dataDir.getPath() + "/articlesTrain.csv") ;
		_artsTestDisambig = new File(_dataDir.getPath() + "/articlesTestDisambig.csv") ;
		_artsTestDetect = new File(_dataDir.getPath() + "/articlesTestDetect.csv") ;

		_arffDisambig = new File(_dataDir.getPath() + "/disambig.arff") ;
		_arffDetect = new File(_dataDir.getPath() + "/detect.arff") ;

		_modelDisambig = new File(_dataDir.getPath() + "/disambig.model") ;
		_modelDetect = new File(_dataDir.getPath() + "/detect.model") ;
	}
	
	private void gatherArticleSets() throws IOException{
        int[] sizes = {200,100,100} ;

        ArticleSet[] articleSets = new ArticleSetBuilder()
            .setMinOutLinks(25)
            .setMinInLinks(50)
            .setMaxListProportion(0.1)
            .setMinWordCount(1000)
            .setMaxWordCount(2000)
            .buildExclusiveSets(sizes, _wikipedia) ;
		
        articleSets[0].save(_artsTrain) ;
        articleSets[1].save(_artsTestDisambig) ;
        articleSets[2].save(_artsTestDetect) ;
    }

	public static void main(String[] args) throws Exception {
		File dataDir = new File("/home/thalisson/Programas/PIBIC/Wikification/AnnotationWorkbench") ;

		WikipediaConfiguration conf = new WikipediaConfiguration(new File("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml")) ;
		conf.addDatabaseToCache(DatabaseType.label) ;
		conf.addDatabaseToCache(DatabaseType.pageLinksInNoSentences) ;

		Wikipedia wikipedia = new Wikipedia(conf, false) ;

		Programa2 trainer = new Programa2(dataDir, wikipedia) ;
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in)) ;

		if(new File(dataDir.getPath() + "/articlesTrain.csv").canRead()) {
			System.out.println("articlesTrain.csv: exist");
		} else {
			trainer.gatherArticleSets();
		}
		
		ArticleSet trainingSet = new ArticleSet(new File(dataDir.getPath() + "/articlesTrain.csv"), wikipedia) ;
		for(int i = 0; i < trainingSet.size(); i++) {
			System.out.println(trainingSet.get(i));
		}
		
		Disambiguator disambiguator = new Disambiguator(wikipedia);
		

	}

}
