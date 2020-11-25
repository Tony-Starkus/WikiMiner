from numpy import *


def readFold(idFold, typeSet, out_path):
    filename = out_path + "codes-to-create-school/virtual_folds/fixed/" + typeSet + '_' + str(idFold) + '.txt'

    fileId = open(filename, 'r')
    In = loadtxt(fileId, delimiter=" ", dtype='int')
    fileId.close()

    foldIds = In

    return foldIds.reshape(-1,1)
# end
