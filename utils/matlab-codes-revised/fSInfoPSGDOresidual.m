% Codes by: Raoni Ferreira.

% Stochastic gradient optimization of latent features + side-information
% - D = 3 x pairs, where each column = (node id, node id, edge status), edge
%   status = {-1, +1} -- Note {0,1} as in Menon!!!
% - sidePair = dPair x |V| x |V|, |V| = # of nodes; features for each pair of nodes
% - sideBilinear = dBilinear x |V|; features for each node
% - weights = structure with all the learned weights inside it
% - lambda = structure with all the regularization parameters inside it
% - eta = structure with all the learning rates inside it
% - EPOCHS = # of sweeps over D
% - epochFrac = used if we only want to do a partial sweep over D
% - loss = {'square', 'logistic'}
% - link = {'none', 'sigmoid'}
% - covariance = precomputed values of sideBilinear(:,i)*sideBilinear(:,j)'
function [U, P, Q, UBias, ULatentScaler, GLatentScaler, WPair, WBias, WBilinear] = ...
    fSInfoPSGDOresidual(D, sidePair, sideBilinear, weights, lambda, eta, ...
        EPOCHS, epochFrac, convergenceScoreTr, convergenceScore, loss, link, symmetric, covariance)
    
    
    pairs = size(D, 2);
    fprintf('processing %d pairs (1e%d)\n', pairs, round(log10(pairs)));

    square = strcmp(loss,'square');
    squareHinge = strcmp(loss,'squareHinge');
    sigmoid = strcmp(link,'sigmoid');
    
    % Extracting weights from the appropriate structures
    U0 = weights.U; P0 = weights.P; Q0 = weights.Q;
    U0Bias = weights.UBias; ULatentScaler = weights.ULatentScaler;
    GLatentScaler = weights.GLatentScaler;
    WPair = weights.WPair; WBias = weights.WBias; WBilinear = weights.WBilinear;
    
    if ~isstruct(lambda)
        lambdaLatent = lambda; lambdaRowBias = lambda; lambdaLatentScaler = lambda; lambdaPair = lambda; lambdaBilinear = lambda; lambdaScaler = ones(size(U0Bias));
    else
        lambdaLatent = lambda.lambdaLatent; lambdaRowBias = lambda.lambdaRowBias; lambdaLatentScaler = lambda.lambdaLatentScaler; lambdaPair = lambda.lambdaPair; lambdaBilinear = lambda.lambdaBilinear; lambdaScaler = lambda.lambdaScaler;
    end
    
    if numel(lambdaScaler) == 1
        lambdaScaler = lambdaScaler * ones(size(U0Bias));
    end    
    
    if ~isstruct(eta)
        etaLatent0 = eta; etaLatentScaler0 = eta; etaPair0 = eta*linkFeaturesPresent; etaBilinear0 = eta*nodeFeaturesPresent; etaRowBias0 = eta; etaBias0 = eta*nodeFeaturesPresent;
    else
        etaLatent0 = eta.etaLatent; etaLatentScaler0 = eta.etaLatentScaler; etaPair0 = eta.etaPair; etaBilinear0 = eta.etaBilinear; etaRowBias0 = eta.etaRowBias; etaBias0 = eta.etaBias;
    end

    lowRank = (size(WBilinear,1) ~= size(WBilinear,2));
    cachedCovariance = (numel(covariance{:}) > 0);
    hasDyadicSideInfo = numel(sidePair) > 0;
    

    U = U0; P = P0; Q = Q0; UBias = U0Bias;    
    UOld = U0; POld = P0; QOld = Q0; UBiasOld = U0Bias; 
    ULatentScalerOld = ULatentScaler; GLatentScalerOld = GLatentScaler;
    WPairOld = WPair; WBiasOld = WBias; WBilinearOld = WBilinear;

    lastScore = 0; bestScore = 0; badEpochs = 0;
    trainError = []; testError = [];

    %obj = computeObjective(U,UBias,ULatentScaler,WBilinear,WPair,WBias,sideBilinear,sidePair,D,square,sigmoid);
    %disp(sprintf('initial objective: %.8g', obj));
    
    %batchSize = 1;
    %% Main SGD body
    for e = 1 : EPOCHS
        % Dampening of the learning rates across epochs
        etaLatent = etaLatent0/((1 + etaLatent0*lambdaLatent)*e);
        etaRowBias = etaRowBias0/((1 + etaRowBias0*lambdaRowBias)*e);
        etaLatentScaler = etaLatentScaler0/((1 + etaLatentScaler0*lambdaLatentScaler)*e);
        etaPair = etaPair0/((1 + etaPair0*lambdaPair)*e);
        etaBilinear = etaBilinear0/((1 + etaBilinear0*lambdaBilinear)*e);        
        etaBias = etaBias0/((1 + etaBias0*lambdaLatent)*e);
        lossV = 0;

        % Random shuffle of the training set
        I = randperm(pairs);
        D = D(:,I);

        for t = 1 : round(epochFrac * pairs)
            %examples = (t-1)*1+1:min(pairs,t); % for varying batch size
            examples = t; % pairs

            i = D(1,examples);
            j = D(2,examples);
            truth = D(3,examples);
            
            %% Prediction
            prediction = (U(:,i)' * ULatentScaler *  U(:,j) + P(:,i)' * GLatentScaler *  Q(:,j) + UBias(i) + UBias(j))'; % E x 1
            
            if hasDyadicSideInfo
                prediction = prediction + WPair * sidePair(:,i,j) + WBias;
            end

            % Only update (potentially expensive) bilinear component when
            % required, viz. learning rate is non-zero
            if etaBilinear > 0
                if lowRank
                    % Much more efficient to multiply two components separately
                    % and then multiply the results, rather than forming
                    % W'W first and reducing to standard case...
                    prediction = prediction + (WBilinear * sideBilinear(:,i))' * (WBilinear * sideBilinear(:,j));
                else
                    prediction = prediction + sideBilinear(:,i)' * WBilinear * sideBilinear(:,j);
                end
            end

            if sigmoid % Link function
                prediction = 1./(1 + exp(-prediction));
            end
            
            %% Gradients
            % gradients were implemented (in pseudocode) between the lines 10 and 18

            % Common gradient scaler
            gradScaler = (prediction - truth); % code line 10
            if square
                gradScaler = 2 * gradScaler;
                if sigmoid
                    gradScaler = gradScaler * prediction * (1 - prediction);
                end
            end
            
            gradI = ULatentScaler*U(:,j); % line 11
            gradJ = ULatentScaler'*U(:,i); % line 12
            gradP = GLatentScaler*Q(:,j); % line 13
            gradQ = GLatentScaler'*P(:,i); % line 14
            gradRowBias = ones(1,numel(examples)); % 1 x 1
            gradBias = ones(1,numel(examples)); 
                
           
            if hasDyadicSideInfo,  gradPair = sidePair(:,i,j)'; end; % line 15
            
            if etaLatentScaler > 0             
                gradLatentScaler = diag(U(:,i).*U(:,j)); % line 16
                gradPQ = diag(P(:,i).*Q(:,j)); % line 17
                
                %if symmetric
                %    gradLatentScaler = diag(U(:,i).*U(:,j));
                %    gradPQ = diag(P(:,i).*Q(:,j));
                %else
                %    gradLatentScaler = U(:,i)*U(:,j)';
                %    gradPQ = P(:,i).*Q(:,j)';
                %end
            end

            if etaBilinear > 0
                if lowRank
                    % Again, more efficient to do one inner multiplication first
                    gradBilinear = (WBilinear * sideBilinear(:,i))*sideBilinear(:,j)' + (WBilinear * sideBilinear(:,j))*sideBilinear(:,i)'; % line 18
                else
                    % Check if d x d matrix has already been computed
                    if cachedCovariance
                        gradBilinear = covariance{i,j};
                    else
                        gradBilinear = sideBilinear(:,i)*sideBilinear(:,j)';
                    end
                end
            end
            
            % If relationship is symmetric, then update not only for (i,j) but for (j,i) also
            % Wikipedia graph is originally asymetric
            if symmetric
                % Actually, I think only the bilinear component need be
                % updated here, although updating the rest doesn't hurt...
                gradI = gradI + ULatentScaler'*U(:,j);
                gradJ = gradJ + ULatentScaler*U(:,i);
                gradRowBias = gradRowBias + gradRowBias;
                gradBias = gradBias + gradBias;

                if hasDyadicSideInfo, gradPair = gradPair + sidePair(:,j,i)'; end;

                if etaLatentScaler > 0, gradLatentScaler = gradLatentScaler + gradLatentScaler'; end;
                if hasNodeSideInfo && etaBilinear > 0, gradBilinear = gradBilinear + gradBilinear'; end;
            end
            
            %% Updates all model weights - lines 19-28
            U(:,[i j]) = U(:,[i j]) - etaLatent * (gradScaler * [gradI gradJ] + lambdaLatent * [lambdaScaler(i)*U(:,i) lambdaScaler(j)*U(:,j)]); % lines 19 and 20
            P(:,i) = P(:,i) - etaLatent * (gradScaler * gradP + lambdaLatent * lambdaScaler(i) * P(:,i)); % line 21
            Q(:,j) = Q(:,j) - etaLatent * (gradScaler * gradQ + lambdaLatent * lambdaScaler(j) * Q(:,j)); % line 22
            UBias([i j]) = UBias([i j]) - etaRowBias * (gradScaler * gradRowBias + lambdaRowBias*UBias([i j])); % lines 27 and 28
            WBias = WBias - etaBias * gradScaler * gradBias; % line 23
            
            if hasDyadicSideInfo, WPair = WPair - etaPair * (gradScaler * gradPair + lambdaPair * WPair); end; % line 23
            
            if etaLatentScaler > 0
                ULatentScaler = ULatentScaler - etaLatentScaler * (gradScaler * gradLatentScaler + lambdaLatentScaler * ULatentScaler); % line 24
                GLatentScaler = GLatentScaler - etaLatentScaler * (gradScaler * gradPQ + lambdaLatentScaler * GLatentScaler); % line 25
            end
            
            if etaBilinear > 0
                WBilinear = WBilinear - etaBilinear * (gradScaler * gradBilinear + lambdaBilinear * WBilinear); % line 26
            end

        end

        %% Optional code
        % Check periodic information (for each 10 epochs) about model prediction performance on test set
        % In order to do that, this code compute the objective function to eval how far the actual link status is from prediction status 
        if numel(D) > 0 && mod(e,10) == 0            
            fprintf('epoch %d ', e);
            %trainError = convergenceScoreTr(U',UBias,ULatentScaler,WPair,WBias,WBilinear); newScoreTr = trainError.auc;
            %testError = convergenceScore(U',UBias,ULatentScaler,WPair,WBias,WBilinear); newScore = testError.auc;
            
            %% Note that I'm using the same function above in order to evaluate the link prediction performance on test set
            SPred = bsxfun(@plus, U' * ULatentScaler * U, bsxfun(@plus, UBias', UBias)); % predicted score for (i,j) = U(i) * L * U(j) + UBias(i) + UBias(j) %jasist revision
            SPred = bsxfun(@plus, SPred, P' * GLatentScaler * Q); %jasist revision
            
            if numel(sideBilinear) > 0
                SPred = SPred + sideBilinear' * WBilinear * sideBilinear + WBias; % node features present
            end
            
            if numel(sidePair) > 0
                m = size(UBias',2);
                dPairs = size(sidePair,1);
                SPred = SPred + reshape(WPair * reshape(sidePair, [dPairs m*m ]), [m m]); % link features present
            end
            
            PPred = 1./(1 + exp(-SPred)); % predicted probability value between 0 and 1

            % I select only the entries in PPred (i.e., predictions) which
            % representing pairs of articles on test set
            testLinks = sub2ind(size(PPred), convergenceScore.D(1,:), convergenceScore.D(2,:)); 
            predictions = PPred(testLinks);

            % I got the actual pairs status (link or not) in order to
            % check the error predictions
            real = convergenceScore.D(3,:);
            npairsTs = convergenceScore.npairs;
            
            % Evaluation function outputs the performance of the model
            [newScore, newScoreRmse, f1Score, prec, rec] = evalModelByDoc3(predictions, real, npairsTs);
            
            fprintf('test auc: %.4g rmse: %.4g f1: %.4g\n ', newScore, newScoreRmse,f1Score);

            %obj = computeObjective(U,UBias,WBilinear,WPair,WBias,sideBilinear,sidePair,D,square,sigmoid);
            %paramDelta = mean(mean((U - UOld).^2));
            %fprintf('objective: %.8g, paramDelta = %.4g\n', obj, paramDelta);            
        else
            newScoreTr = 0; newScore = 0; trainError = []; testError = [];
        end

        % Keeps track of how many epochs in a row have led to limited improvement
        if newScore < lastScore + 1e-4
            badEpochs = badEpochs + 1;
            %fprintf('badEpochs: %d (%d)\n',badEpochs,e);
        else
            badEpochs = 0;
        end

        % Stop early if parameters blow up/we have many bad successive epochs
        if any(isnan(U(:))) || any(isnan(P(:))) || any(isnan(Q(:))) || any(isnan(WBilinear(:))) || isnan(newScore) || (0 && badEpochs > 3)
            U = UOld; P = POld; Q = QOld;
            UBias = UBiasOld;
            ULatentScaler = ULatentScalerOld;
            GLatentScaler = GLatentScalerOld;
            WPair = WPairOld;
            WBias = WBiasOld;
            WBilinear = WBilinearOld;

            fprintf('early stopping at epoch %d: auc = %.4g -> %.4g\n', e, lastScore, newScore);
            break;
        end

        if newScore > bestScore
            UOld = U; POld = P; QOld = Q;
            UBiasOld = UBias; ULatentScalerOld = ULatentScaler;
            GLatentScalerOld = GLatentScaler; 
            WPairOld = WPair; WBiasOld = WBias;
            WBilinearOld = WBilinear;            

            bestScore = newScore;
        end        

        lastScore = newScore;
    end

    U = U';
    P = P';
    Q = Q';
