% Codes by: Raoni Ferreira.

function [foldIds] = readFold(path_output, idFold,typeSet)

filename = strcat(path_output, 'codes-to-create-school\virtual_folds\fixed\',typeSet,'_',num2str(idFold),'.txt');

fileId = fopen(filename,'r');
in = textscan(fileId,'%d','Delimiter','');
fclose(fileId);

foldIds = in{1};
end
