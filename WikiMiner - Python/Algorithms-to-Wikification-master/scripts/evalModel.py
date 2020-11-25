from numpy import *
from scipy.stats import rankdata


def evalModel (predictions, truth):
    # print(f"T - evalModel - predictions: {predictions} | truth: {truth}")
    # T - evalModel - predictions: [[0.1189938  0.1350244  0.13955219 ... 0.08457625 0.11921548 0.06401358]] | truth: [[0 0 0 ... 0 0 0]]
    # TESTE
    # seterr(all='ignore')

    n = predictions.shape[1] # num de colunas
    rmse = sqrt( sum( (predictions - truth) ** 2 ) / n )
    # print(f"T - evalModel - n: {n}")
    # print(f"T - evalModel - rmse: {rmse}")
    # Count observations by class
    nTarget = sum( double(truth == 1) )
    nBackground = sum( double(truth != 1) )
    # print(f"T - evalModel - nTarget: {nTarget} | truth: {nBackground}")
    # rank data
    R = rankdata(predictions) # tiedrank in matlab

    R = R.reshape((1,R.shape[0]))
    R = R.astype('int')
    # print(f"T - R: {R}")

    # calculate AUC
    a1 = sum(R[(truth==1)])
    # print(f"a1: {a1}")
    a2 = (nTarget ** 2 + nTarget)/2
    # print(f"a2: {a2}")
    a3 = nTarget * nBackground
    # print(f"a3: {a3}")
    auc = round(( round(sum(R[(truth==1)]),8) - round((nTarget ** 2 + nTarget)/2, 8) ) / round((nTarget * nBackground),8), 8)

    # print(a1)
    # print(a2)
    # print(a3)
    # print(auc)

    return (auc, rmse)