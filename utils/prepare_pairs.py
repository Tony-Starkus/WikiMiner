import csv
import numpy as np
from scipy.sparse import coo_matrix, lil_matrix

"""
DUVIDAS
1. Os números extraídos dos arquivos devem permanecer em string ou converter para int/float? - Número
2. Valores booleanos serão números ou palavras? - Número
3- No pares-feature, o ID não está sendo coletado.
4- Confirmar nome das colunas do pares-features no algoritmo do mat-lab.
"""
# Diretório dos arquivos
folder = "/home/thalisson/Programas/PIBIC/Final/Amostra por média/Amosta 1/"
"""
PARTE 1 - Lista de ID's -  FEITO
- Pegar a quantidade de ID's - N
"""
IDList = []  # Lista com o ID dos artigos
print("Reading Articles ID's...")
with open(folder + "articlesSetID.csv") as file:
    csv_reader = csv.reader(file)
    for line in csv_reader:
        IDList.append(int(line[0]))
    file.close()
print(f"Total ID's: {len(IDList)}")
"""PARTE 1"""

"""
PARTE 2 - GRAFO - FEITO
Pegar cada coluna e armazenar em vetores diferentes
"""
# Grafo
print("Processing Grafo...")
g1 = []  # ID article 1
g2 = []  # ID article 2
g3 = []  # isLink
with open(folder + "grafo.csv") as file:
    csv_reader = csv.reader(file)
    for line in csv_reader:
        g1.append(int(line[0]))
        g2.append(int(line[1]))
        g3.append(int(line[2]))
    file.close()
print(f"g1: {len(g1)}")
print(f"g2: {len(g2)}")
print(f"g3: {len(g3)}")
"""
PARTE 3 - ARTIGOS-FEATURES
Pegar cada coluna e armazenar em vetores diferentes
- Etapa 1 -> Usar o grafo e segmentar em 3000
- Etapa 2 -> 
"""
print("Processing Node Features...")
# artigos-features
file = csv.reader(open(folder + "artigos-features.csv"))
# next(file)
a1 = []  # title
a2 = []  # inlink ratio
a3 = []  # outlink ratio
with open(folder + "artigos-features.csv") as file:
    csv_reader = csv.reader(file)
    next(csv_reader)  # Jumping column names
    for line in csv_reader:
        a1.append(line[1])
        a2.append(float(line[2]))
        a3.append(float(line[3]))
    file.close()
print(f"a1: {len(a1)}")
print(f"a2: {len(a2)}")
print(f"a3: {len(a3)}")

"""
PARTE 4 - PARES-FEATURES
"""
print("Processing Pairs Features...")
p1 = []  # id_article
p2 = []  # id_topic
p3 = []  # occurances
p4 = []  # maxDisambigConfidence
p5 = []  # avgDisambigConfidence
p6 = []  # relatednessToContext
p7 = []  # relatednessToOtherTopics
p8 = []  # maxLinkProbability
p9 = []  # avgLinkProbability
p10 = []  # firstOccurance
p11 = []  # lastOccurance
p12 = []  # spread
p13 = []  # isValidLink
p_features = []
with open(folder + "pares-features.csv") as file:
    csv_reader = csv.reader(file)
    next(csv_reader)  # Jumping column names
    for line in csv_reader:
        aux = []
        aux = [
            float(line[2]),  # occurance
            float(line[3]),  # maxDisambigConfidence
            float(line[4]),  # avgDisambigConfidence
            float(line[5]),  # relatednessToContext
            float(line[6]),  # relatednessToOtherTopics
            float(line[7]),  # maxLinkProbability
            float(line[8]),  # avgLinkProbability
            float(line[9]),  # firstOccurance
            float(line[10]),  # lastOccurance
            float(line[11]),  # spread
        ]
        p1.append(int(line[0]))  # id_article LIST
        p2.append(int(line[1]))  # id_topic LIST
        p_features.append(aux)  # FEATURES
        """p3.append(float(line[2]))
        p4.append(float(line[3]))
        p5.append(float(line[4]))
        p6.append(float(line[5]))
        p7.append(float(line[6]))
        p8.append(float(line[7]))
        p9.append(float(line[8]))
        p10.append(float(line[9]))
        p11.append(float(line[10]))
        p12.append(float(line[11]))
        p13.append(1 if line[12] == "TRUE" else 0)"""

print(f"{len(p1)}")
print(f"{len(p2)}")
"""print(f"{len(p3)}")
print(f"{len(p4)}")
print(f"{len(p5)}")
print(f"{len(p6)}")
print(f"{len(p7)}")
print(f"{len(p8)}")
print(f"{len(p9)}")
print(f"{len(p10)}")
print(f"{len(p11)}")
print(f"{len(p12)}")
print(f"{len(p13)}")"""
print(f"len(p_features): {len(p_features)}")

print(p_features[0])
print(p_features[1])
print(p_features[2])


# Matriz do Grafo
matrix_grafo = np.array([g1, g2, g3]).reshape(3, len(IDList) * len(IDList))  # Matriz pares
print("\nMatriz do Grafo:")
for i in range(3):
    for j in range(3):
        print(f"m[{j}][{i}] {matrix_grafo[j][i]}", end=" ")
    print()

# Matriz Artigos Features
matrix_artigos_features = np.array([a1, a2, a3]).reshape((3, len(IDList)))
print("\nMatriz do Artigos Features:")
for i in range(3):
    for j in range(3):
        print(f"m[{j}][{i}] {matrix_artigos_features[j][i]}", end=" ")
    print()

# matrix = np.zeros((matrix_linha, matrix_coluna, 13), dtype=int)

print("Done!")
