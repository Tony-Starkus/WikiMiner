from numpy import *
import os


# implementação do randSample do MatLab
def randSample(arr, perms):
    v = random.permutation(arr)
    r = v[0:perms]
    return r


# end


def read20perFold(idFold, typeSet, perc, out_path):
    filename = out_path + typeSet + '_' + str(idFold) + '.txt'
    fileId = open(filename, 'r')
    In = loadtxt(fileId, delimiter=",", dtype='int')
    In = In[:, 0]  # Pegando apenas os valores de index quando o .txt está no formato "index, id"
    fileId.close()

    k = round(In.shape[0] * perc)
    docsIds = randSample(In, k)

    return docsIds.reshape(-1, 1)
# end
