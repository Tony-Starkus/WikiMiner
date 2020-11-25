from numpy import *


def readFold(idFold, typeSet, out_path):
    filename = out_path + typeSet + '_' + str(idFold) + '.txt'

    fileId = open(filename, 'r')
    In = loadtxt(fileId, delimiter=",", dtype='int')
    In = In[:, 0]  # Pegando apenas os valores de index quando o .txt está no formato "index, id"
    fileId.close()

    foldIds = In

    return foldIds.reshape(-1,1)
# end
