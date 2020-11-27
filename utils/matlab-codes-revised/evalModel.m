% Codes by: Raoni Ferreira.
function [auc, rmse] = evalModel(predictions, truth)
    n = length(predictions);
    rmse = sqrt(sum((predictions - truth).^2)/n);
    
    %% Count observations by class
    nTarget     = sum(double(truth == 1));
    nBackground = sum(double(truth ~= 1));
    %% Rank data
    R = tiedrank(predictions);  % 'tiedrank' from Statistics Toolbox
    %% Calculate AUC
    auc = (sum(R(truth == 1)) - (nTarget^2 + nTarget)/2) / (nTarget * nBackground);
    %[X,Y,~,auc] = perfcurve(truth, predictions, 1);
    %plot(X,Y)
    %xlabel('False positive rate'); ylabel('True positive rate')
    %title('ROC chart')
