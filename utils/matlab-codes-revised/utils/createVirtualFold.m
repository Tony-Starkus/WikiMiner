% Codes by: Raoni Ferreira.

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
