% Este código foi fornecido por Raoni S. Ferreira e implementa o método publicado no paper:
% Ferreira, R.S., Pimentel, M.d.G. and Cristo, M. (2018), A wikification prediction model based on the combination of latent, dyadic, and monadic features. Journal of
% the Association for Information Science and Technology, 69: 380-394. https://doi.org/10.1002/asi.23922

%%
% idFold: fold identifier
% typeSet: train or test
% docsMat: variable docs from mat file
function [] = createVirtualFold(idFold,typeSet,docsMat)

filenameSrc = strcat('/home/raoni/workspace/wss-miner/data/mw_model/folds/sample/',typeSet,'_',num2str(idFold),'.txt');
filenameDst = strcat('../dataset/virtual_folds/sample/',typeSet,'_',num2str(idFold),'.txt');

fileIdSrc = fopen(filenameSrc,'r');
fileIdDst = fopen(filenameDst,'w');

in = textscan(fileIdSrc,'%d','Delimiter','');

A = in{1};
for i=1:size(A,1)
    idv = find(ismember(docsMat,A(i))); %id virtual
    fprintf(fileIdDst,'%d\n',idv);
end

fclose('all');

end
