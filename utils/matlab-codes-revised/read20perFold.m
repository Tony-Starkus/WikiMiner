% Codes by: Raoni Ferreira.

function [docsIds] = read20perFold(path_output,idFold,typeSet, perc)

filename = strcat(path_output, 'codes-to-create-school\virtual_folds\fixed\',typeSet,'_',num2str(idFold),'.txt');

fileId = fopen(filename,'r');
in = textscan(fileId,'%d','Delimiter','');
fclose(fileId);

docsIds = randsample(in{1},round(size(in{1},1)*perc));

end
