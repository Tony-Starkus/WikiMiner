% Este código foi fornecido por Raoni S. Ferreira e implementa o método publicado no paper:
% Ferreira, R.S., Pimentel, M.d.G. and Cristo, M. (2018), A wikification prediction model based on the combination of latent, dyadic, and monadic features. Journal of
% the Association for Information Science and Technology, 69: 380-394. https://doi.org/10.1002/asi.23922

function [foldIds] = readFold(path_output, idFold,typeSet)

filename = strcat(path_output, 'codes-to-create-school\virtual_folds\fixed\',typeSet,'_',num2str(idFold),'.txt');

fileId = fopen(filename,'r');
in = textscan(fileId,'%d','Delimiter','');
fclose(fileId);

foldIds = in{1};
end
