package teste;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParesTrue {
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scnFile = new Scanner(new File("/home/thalisson/Documents/WikiMiner/grafo.csv"));
		int loop = 0;
		int verdadeiro = 0;
		while(scnFile.hasNextLine()) {
			loop++;
			String[] line = scnFile.nextLine().split(",");
			System.out.println(line[0]);
			if(line[2].equals("1"))
				verdadeiro++;
			
		}
		System.out.println("Loop: " + loop);
		System.out.println("True: " + verdadeiro);
		
	}

}
