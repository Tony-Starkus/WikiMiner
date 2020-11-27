% Este código foi fornecido por Raoni S. Ferreira e implementa o método publicado no paper:
% Ferreira, R.S., Pimentel, M.d.G. and Cristo, M. (2018), A wikification prediction model based on the combination of latent, dyadic, and monadic features. Journal of % % the Association for Information Science and Technology, 69: 380-394. https://doi.org/10.1002/asi.23922

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
