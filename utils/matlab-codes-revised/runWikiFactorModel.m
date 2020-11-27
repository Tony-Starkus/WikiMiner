% Este código foi fornecido por Raoni S. Ferreira e implementa o método publicado no paper:
% Ferreira, R.S., Pimentel, M.d.G. and Cristo, M. (2018), A wikification prediction model based on the combination of latent, dyadic, and monadic features. Journal of
% the Association for Information Science and Technology, 69: 380-394. https://doi.org/10.1002/asi.23922

clear all;

%load('../colecao/school-wiki2014/5132_loose_wss-wiki2014_vfinal.mat');
load('C:\Users\thali\Documents\MATLAB\Amosta 1\colecoes\3000_loose_wss-wooki_vfinal.mat');

%Dbin contains all possible pairs between two articles. However, this is not true for wikipedia graph
%since an article does not mention all concepts (articles) in their
%content. As this is the case, I just remove Dbin and keep Z (pairs which
%represents the real possible pairs which can be mention in article)
clear Dbin;

% Z variable format
% 3 x pairs, where each column = (node id, node id, edge status - link (1) or not link (1)

%remove cases where i==j. I.e, case where an article points to itself
%(which are those that not make sense exist in wikipedia)
T = Z(:,Z(1,:)~=Z(2,:));
Z = T; % Here, Z contains real pairs except those which points to theirselves

clear T;

savepredictions = 1;

%% General parameters

% loss function
loss = 'logistic';
% link function
link = 'sigmoid';

symmetric = 0; % network is symmetric? Wikipedia School is asymmetric

k = 5; % number of latent features
EPOCHS = 100; % # of passes of SGD
epochFrac = 0.1; % fraction of +'ve and -'ve pairs to use in each pass
batchSize = 1; % # of examples in each update

% learning rates
eta = [];
eta.etaLatent = 1e-1; % learning rate for latent feature
eta.etaRowBias = 1e-1; % learning rate for node bias
eta.etaLatentScaler = 1e-1; % learning rate for scaler to latent features
eta.etaBias = 1e-1;

% penalization terms
lambda = [];
lambda.lambdaLatent = 1e-2; % regularization for node's latent vector U
lambda.lambdaRowBias = 1e-2; % regularization for node's bias UBias
lambda.lambdaLatentScaler = 1e-2; % regularization for scaling factors Lambda (in paper)

lambda.lambdaPair = 1e-5; % regularization for weights on pair features
lambda.lambdaBilinear = 1e-5; % regularization for weights on node features

lambda.lambdaScaler = 1; % scaling factor for regularization, can be set to 1 by default

m = size(titles,1); % number of nodes

sideBilinear = F; % article features
sidePair = X; % pair (link) features

dBilinear = size(sideBilinear,1); % number of article features
nodeFeaturesPresent = double(dBilinear>0);

dPair = size(sidePair,1); % number of pair features
linkFeaturesPresent = double(dPair>0);

%dir_results = strcat('adicional-jasist/only-undirected+directed/');

%% eta learning rates
eta.etaPair = linkFeaturesPresent * 1e-3; % learning rate for pairs, when their features are present
eta.etaBilinear = nodeFeaturesPresent * 1e-3; % learning rate for node, when their features are present
eta.etaBias = linkFeaturesPresent * 1e-3; % learning rate for global bias, used when features are present

trainFrac = 0.2; % training set fraction for learning

%% This script aims to evaluate the factor model to predict link through 5-fold cross validation methodology

ats_auc=0; ats_f1=0; ats_rmse=0; ats_prec=0; ats_rec=0;

maxFolds=1;
% parfor i=1:maxFolds % check the memory usage before uncomment this line to parallelize
% cross-validation
for i=1:maxFolds
   rng(0+i,'twister'); % to ensure reproducible experiments
   
     %% initialize weight (model) parameters
    
    weights = [];
    weights.U = 1/sqrt(k) * randn(k, m); % for k latent features and m nodes
    weights.P = 1/sqrt(k) * randn(k, m); % for k latent features and m nodes
    weights.Q = 1/sqrt(k) * randn(k, m); % for k latent features and m nodes
    
    weights.UBias = randn(m, 1);   
    
    weights.ULatentScaler = diag(randn(k, 1)); % for asymmetric use randn(k, k); for symmetric use diag(randn(k, 1))
    weights.GLatentScaler = diag(randn(k, 1));
    weights.ULatentScaler = diag(randn(k, 1)); % for asymmetric use randn(k, k); for symmetric use diag(randn(k, 1))
        
    weights.WPair = linkFeaturesPresent * randn(1, dPair); % for dPair features for each pair
    weights.WBias = linkFeaturesPresent * randn;
    weights.WBilinear = nodeFeaturesPresent * randn(dBilinear, dBilinear); % V
    
    Dtr = [];
    idsTr = read20perFold(i, 'train', trainFrac);
    
    ITr = find(ismember(Z(1,:),idsTr));
    Dtr = Z(:,ITr);
    insU = unique(Dtr(1,:)); % count # of nodes in training set
    
    % obtain # of pairs by articles in training set
    npairsTr = zeros(size(insU,2),1);
    for j=1:size(insU,2)
        npairsTr(j) = size(Dtr(:,Dtr(1,:)==insU(j)),2);
    end
    
    convergenceScoreTr = [];
    convergenceScoreTr.D = Dtr;
    convergenceScoreTr.npairs = npairsTr;
    
    idsTs = readFold(i, 'test');
    ITs = find(ismember(Z(1,:),idsTs));
    Dts = Z(:,ITs);
    
    insU = unique(Dts(1,:));
    
    npairsTs = zeros(size(insU,2),1);
    for j=1:size(insU,2)
        npairsTs(j) = size(Dts(:,Dts(1,:)==insU(j)),2);
    end
    
    convergenceScore = [];
    convergenceScore.D = Dts;
    convergenceScore.npairs = npairsTs;

    %% training the factorization algorithm
    % the factorization algorithm is trained on train set (Dtr) in order to create a model of link prediction
    % the model is characterized by the set of output weights such as U, P,
    % Wpair, etc...
     [U, P, Q, UBias, ULatentScaler, GLatentScaler, WPair, WBias, WBilinear] = ...
        fSInfoPSGDOresidual(Dtr, sidePair, sideBilinear, weights, lambda, eta, ...
        EPOCHS, epochFrac, convergenceScoreTr, convergenceScore, loss, link, symmetric, {[]});
    
    %% test the factorization model
    % the factorization model is used to perform predictions on test set
    % (Dts), i.e. data that still has been unseen before by the algorithm
    % (not train)
    SPred = bsxfun(@plus, U * ULatentScaler * U', bsxfun(@plus, UBias', UBias));
    SPred = bsxfun(@plus, SPred, P * GLatentScaler * Q');
   
    if numel(sideBilinear) > 0
        SPred = SPred + sideBilinear' * WBilinear * sideBilinear + WBias; % node features present
    end
    
    if numel(sidePair) > 0
        SPred = SPred + reshape(WPair * reshape(sidePair, [dPair m*m]), [m m]); % link features present
    end
    
    PPred = 1./(1 + exp(-SPred)); % predicted probability between 0 and 1
    
    %% evaluate test set performance
    % I select only the entries in PPred (i.e., predictions) which
    % representing pairs of articles on test set
    testLinks = sub2ind(size(PPred), Dts(1,:), Dts(2,:));
    predictions = PPred(testLinks);
    real = Dts(3,:); % I got the actual pairs status (link or not)
    
    npairsTs = convergenceScore.npairs;
    
    % Evaluate the prediction capacity based on some metrics such as AUC, RMSE, F1,
    % Precision and Recall
    [ts_auc, ts_rmse, ts_f1, ts_prec, ts_rec] = evalModelByDoc3(predictions, real, npairsTs);
    
    ats_auc = ats_auc + ts_auc;
    ats_rmse = ats_rmse + ts_rmse;
    ats_f1 = ats_f1 + ts_f1;
    ats_prec = ats_prec + ts_prec;
    ats_rec = ats_rec + ts_rec;
    
    % each train/test evaluation, call as holdout, is print to obtain
    % partial performance
    fprintf('holdout %d: avg: auc = %.4g f1 = %.4g prec = %.4g rec = %.4g rmse = %.4g\n', i, ts_auc, ts_f1, ts_prec, ts_rec, ts_rmse);
    
end

% the average performance of algorithm after execution of n-fold cross-validation 
fprintf('avg: auc: %.4g f1: %.4g rmse: %.4g\n', ats_auc/maxFolds, ats_f1/maxFolds, ats_rmse/maxFolds);
