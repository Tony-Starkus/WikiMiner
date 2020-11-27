% Codes by: Raoni Ferreira.
function [ auc, rmse, f1, prec, rec ] = evalModelByDoc3( predictions, truth, vp, varargin )
%Summary of this function goes here
%   This function computes the error predictions regarding
%   popular metrics widely used to evaluate machine learning algorithm
%   such as AUC (Area Under the Curve), RMSE (Root Mean Square Error),
%   Precision and Recall

% threshold default is 0.5
optarg = {0.5};
optarg(1:length(varargin)) = varargin;
epsilon = optarg{:};

offset =0 ;
n = size(vp,1);

mauc = [];
mf1 = [];
mprec = [];
mrec = [];
for i=1:n
    p = predictions( offset+1:offset+vp(i) );
    t = truth( offset+1:offset+vp(i) );
    
    [auc, rmse] = evalModel(p,t);
    mauc = [mauc auc];
    
    bPs = (p > epsilon);
    
    fp = sum( (bPs==1) & (t==0) );
    fn = sum( (bPs==0) & (t==1) );
    tp = sum( (bPs==1) & (t==1) );
    
    prec = tp/(tp+fp);
    rec = tp/(tp+fn);
    
    %f1 = 2*(prec*rec)/(prec+rec);
    %mf1 = [mf1 f1];
    mprec = [mprec prec];
    mrec = [mrec rec];
    
    offset = offset + vp(i);
end

auc = nanmean(mauc);
prec = nanmean(mprec);
rec = nanmean(mrec);

%binPredictions = (predictions > epsilon);

%fp = sum( (binPredictions==1) & (truth==0) );
%fn = sum( (binPredictions==0) & (truth==1) );
%tp = sum( (binPredictions==1) & (truth==1) );

%prec = tp/(tp+fp);
%rec = tp/(tp+fn);

%if isnan(prec)
%    prec = 0;
%end

%if isnan(rec)
%    rec = 0;
%end

f1 = 2*(prec*rec)/(prec+rec);

if isnan(f1)
    f1 = 0;
end

end

