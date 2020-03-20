package pages;

import java.io.*;
import java.util.*;

public class ValidarTeoria {
	
	/* TEORIA -> Número 0 (zero) representa os Articles no page.csv
	 * */

	public static void main(String[] args) throws Exception {
		File artigos = new File("/home/thalisson/Documents/WikiMiner/articles.txt");
		BufferedReader pages_csv = new BufferedReader(new FileReader("/home/thalisson/Programas/PIBIC/Wikification/wikipedia-miner-starwars/db/page.csv"));
		BufferedReader br = new BufferedReader(new FileReader(artigos));
		File checagem_file = new File("/home/thalisson/Documents/WikiMiner/checagem.txt");
		FileWriter checagem = new FileWriter(checagem_file);
	
		/*STEP 1 -> pegar o título e o numero em cada linha do page.csv e guardar em um HashMap*/
		Map<String, String> dados_csv = new HashMap<String, String>();
		String linhaCsv;
		while((linhaCsv = pages_csv.readLine()) != null) {
			String[] aux = linhaCsv.split("¬");
			String aux1 = aux[1].replaceAll("^['\"]*", "");
			String aux2 = aux1.replaceAll("\"", "");
			dados_csv.put(aux2, aux[2]);
			System.out.println(aux2 + ":" + aux[2]);
		}
		for(String key : dados_csv.keySet()) {
			String value = dados_csv.get(key);
			System.out.println(key + " = " + value);
		}
		
		
		/*SETP 2 -> Percorrer cada linha do articles.txt e procurar no HashMap.
		 * Se encontrado, pegar o título e valor do número e salvar no arquivo checagem.txt */
		String linhaText;
		boolean existe = false;
		int falso = 0;
		int aff = 0;
		int totalLinhasText = 0;
		ArrayList<String> nao_existe = new ArrayList<String>();
		while((linhaText = br.readLine()) != null) {
			totalLinhasText++;
			String[] aux = linhaText.split(";");
			//1º Tentativa
			for(String key : dados_csv.keySet()) {
				if(aux[0].equals(key)) {
					if(dados_csv.get(key).equals("0")) {
						aff++;
						String value = dados_csv.get(key);
						checagem.write(key + ";" + value + "\n");
						System.out.println(aux[0] + " | Ok");
						existe = true;
					}
					
				}
				if(existe) {
					break;
				}	
			}
			
			//2º Tentativa
			if(!existe) {
				String result = aux[0].replaceAll("\"", "");
				System.out.println("result: " + result);
				for(String key : dados_csv.keySet()) {
					if(result.equals(key)) {
						if(dados_csv.get(key).equals("0")) {
							aff++;
							String value = dados_csv.get(key);
							checagem.write(key + ";" + value + "\n");
							System.out.println(result + " | Ok");
							existe = true;
						}
						
					}
					if(existe) {
						break;
					}
				}
				
				if(!existe) {
					nao_existe.add(aux[0]);
					falso++;
				}

			}
			existe = false;
		}
		
		for(String titulo : nao_existe) {
			System.out.println(titulo);
		}
		System.out.println("Não existe: " + falso);
		System.out.println("Tamanho do array: " + nao_existe.size());
		System.out.println("Total de articles_text: " + totalLinhasText);
		System.out.println("Total encontrado: " + aff);
		
		

	}

}
