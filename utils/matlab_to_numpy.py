from hdf5storage import loadmat
from numpy import savez
import numpy as np
import csv


# Gerar novamente a matriz Z corrigida.
def gen_new_z():
    path_to_sample = ""
    IDList = []
    pairs_graph = []
    with open(path_to_sample + "articlesSetID.csv") as file:
        csv_reader = csv.reader(file)
        for line in csv_reader:
            IDList.append(int(line[0]))
        file.close()
    print(f"Total ID's: {len(IDList)}")
    print("Generating new Z matrix...")
    with open(path_to_sample + "grafo.csv") as file:
        csv_reader = csv.reader(file)
        for line in csv_reader:
            pairs_graph.append([IDList.index(int(line[0])) + 1, IDList.index(int(line[1])) + 1, int(line[2])])
    return np.array(pairs_graph).T


# Lendo de um binario em .mat
f = loadmat("")

print(f.keys())

Dbin = f['Dbin']
F = f['F']
X = f['X']
Z = gen_new_z()
docs = f['docs']
# rates = f['rates']
titles = f['titles']
print(f"len(F): {np.array(F).shape}")
print(f"len(X): {np.array(X).shape}")
print(f"len(Z): {np.array(Z).shape}")
print(f"len(docs): {np.array(docs).shape}")
print(f"len(titles): {np.array(titles).shape}")

print("Salvando...")
# Salvando em .bin numpy
destin = ""
# savez(destin, docs=docs, titles=titles, Dbin=Dbin, X=X, F=F, Z=Z, rates=rates)
savez(destin + "/3000_loose_wws-wooki_vfinal.npz",
      docs=docs, titles=titles, Dbin=Dbin, X=X, F=F, Z=Z)

print("Salvo")
