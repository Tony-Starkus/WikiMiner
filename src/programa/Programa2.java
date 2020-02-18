package programa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.ArticleSet;
import org.wikipedia.miner.util.ArticleSetBuilder;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.xml.sax.SAXException;

public class Programa2 {
	
	static String _conf = "/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/configs/wikipedia-template-starwars.xml";
	static String _output_dir = "/home/thalisson/Documents/WikiMiner/";
	private static Wikipedia _wikipedia;
	
	//article set files
	static private File _artsTrain, _artsTestDisambig, _artsTestDetect ;
	
	private void getArticlesId() throws FileNotFoundException {
		Scanner article_id_csv_file_scanner = new Scanner(new File(_output_dir + "articles_id_links.csv"));
		article_id_csv_file_scanner.nextLine();
		while(article_id_csv_file_scanner.hasNextLine()) {
			String[] line = article_id_csv_file_scanner.nextLine().split(",");
		}
		article_id_csv_file_scanner.close();
	}
	
	private void gatherArticleSets() throws IOException{
		int[] sizes = {200,100,100} ;

        ArticleSet[] articleSets = new ArticleSetBuilder()
            .setMinOutLinks(25)
            .setMinInLinks(50)
            .setMaxListProportion(0.1)
            .setMinWordCount(1000)
            .setMaxWordCount(2000)
            .buildExclusiveSets(sizes, _wikipedia);
		
        articleSets[0].save(_artsTrain);
        articleSets[1].save(_artsTestDisambig);
        articleSets[2].save(_artsTestDetect);
	    
    }
	
	private void createDetectCsv() throws Exception {
		if (!_artsTrain.canRead()) {
			System.err.println("Article sets have not yet been created");
			return;
		}
		
		ArticleSet trainingSet = new ArticleSet(_artsTrain, _wikipedia);
           
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Este programa se utiliza de um modificação do LinkDetector para utilizar o csv usado no Programa1 em seu funcionamento */
		WikipediaConfiguration conf = new WikipediaConfiguration(new File(_conf)) ;
		_wikipedia = new Wikipedia(conf, false) ;
		
		_artsTrain = new File(_output_dir + "/articles_in-out-links_info.csv") ;
		_artsTestDisambig = new File(_output_dir) ;
		_artsTestDetect = new File(_output_dir) ;
		
		Programa2 programa2 = new Programa2();
		programa2.createDetectCsv();
		
		System.out.println("Oi mundo!");

	}

}
