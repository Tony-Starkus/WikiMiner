% Este código foi fornecido por Raoni S. Ferreira e implementa o método publicado no paper:
% Ferreira, R.S., Pimentel, M.d.G. and Cristo, M. (2018), A wikification prediction model based on the combination of latent, dyadic, and monadic features. Journal of
% the Association for Information Science and Technology, 69: 380-394. https://doi.org/10.1002/asi.23922

%
% prepare pairs for 
% wikification factorModel
clear all;
scaling=1;

path_to_files = 'C:\Users\thali\Documents\MATLAB\Amostra por media\Amostra 4\';

fprintf('Reading all ids sample...\n');
%file1=fopen('../5132_wss_wiki2014_sample-ids.txt');
file1=fopen(strcat(path_to_files, 'articlesSetID.csv'));

%file1=fopen('../wss_wiki2014_sample-ids_vfinal.txt');
%in = textscan(file1,'%d');
in = textscan(file1,'%d','Delimiter',',');
fprintf('articlesSetID shape: %s\n', mat2str(size(in)));
fclose(file1);

docs = in{1};
n = size(docs,1);

clear in;

% fprintf('Reading all rates sample...\n');
% file=fopen('../5132_wss_wiki2014_sample-rates.txt');
%in = textscan(file,'%d %s %s','Delimiter','\t'); 
% rates = in{3};
% clear in;

% feature label for each fold
% C = {sparse(n,n) sparse(n,n) sparse(n,n) sparse(n,n) sparse(n,n)};

% for i=1:5
%    C{i} = getlabelfeature(i,n);
% end

%c = regex(str(565996),'("[^"]*")|([^,"]+)', 'match');
%C = {in{1} in{2} in{3}};

clear in;

fprintf('Reading node features...\n');
% file=fopen('/home/raoni/workspace/wss-miner/data/factor_model/full_dataset/loose/nodes_features.csv');
file=fopen(strcat(path_to_files, 'artigos-features.csv'));

%in = textscan(file,'%d %s %f %f %f', 'HeaderLines',1,'Delimiter',';');
in = textscan(file,'%d %s %f %f', 'HeaderLines',1,'Delimiter','_'); % Um campo a menos, generality foi removido. Delimitador é virgula.
fprintf('artigos-features shape: %s\n', mat2str(size(in)));

x1 = in{3}; %inlink ratio (new attribute)
x2 = in{4}; %outlink ratio (new attribute)
titles=in{2}; %title names
% x3 = in{5};

fclose(file);

clear in;

fprintf('Reading pair features...\n');
% file2=fopen('/home/raoni/workspace/wss-miner/data/factor_model/full_dataset/loose/pairs_features.csv');
file2=fopen(strcat(path_to_files, '\pares-features.csv'));

%file2=fopen('arff/184854-pairs.csv');
% in = textscan(file2,'%d %d %d %d %f %f %f %f %f %f %f %f %f %f %f %d', 'HeaderLines',1,'Delimiter',',');
in = textscan(file2,'%d %d %f %f %f %f %f %f %f %f %f %f %s', 'HeaderLines',1,'Delimiter',','); % Três colunas removidas.
fprintf('pares-features shape: %s\n', mat2str(size(in)));
fclose(file2);

f0 = in{1}; %mwpair: feature used as tagger of MW pairs used by wikipediaminer)
f01 = in{2}; %haslabel: is there a label which represent the pair?
% id_articleA
% id_topicoB
f1 = double(in{3}); %occurances
f2 = double(in{4}); %maxDisambigConfidence
f3 = double(in{5}); %avgDisambigConfidence
f4 = double(in{6}); %relatednessToContext
f5 = double(in{7}); %relatednessToOtherTopics
f6 = double(in{8}); %maxLinkProbability
f7 = double(in{9}); %avgLinkProbability
f8 = double(in{10}); %firstOccurance numeric
f9 = double(in{11}); %lastOccurance
f10 = double(in{12}); %spread
aux1 = in{13};  % Getting isValidLink as string.
aux2 = [];  % Save the isValidLink as int.
for i = aux1
    aux2 = [aux2, strcmp(string(i), 'TRUE')]; 
end

f11 = aux2; %isValidLink
disp(size(f11));
fprintf('Obtaining topology information...\n');
A = zeros(n,n);
% obtain topology information
for i=1:n
    A(i,:) = f11(((n*i)-n)+1:n*i,1);
end

D = A;
% D=sparse(A);

clear A;
clear in;

% tag original pairs from MW
L = zeros(n,n);
%L = [f0, f01];

for i=1:n
    L(i,:) = f0((n*i)-n+1:n*i);
end
%break;

K = zeros(n,n);
for i=1:n
    K(i,:) = f01((n*i)-n+1:n*i);
end

% make normalization by standard deviation of each attribute
if scaling
    
    fprintf('Scaling node features...\n');
    % nodes attribute
    F = [x1 x2];
    mF = mean(F,1); %obtain mean of column f1
    sF = std(F,0,1); %std
    
    for j=1:size(F,2)
        F(:,j) = (F(:,j) - mF(j) / sF(j));
    end
    F = single(F'); %use generality as node feature
    
    
    % dyads attributes
    M = [f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11];
    
    %obtain mean and std (standard deviation) of each attribute (column)
    mM = mean(M,1);
    sM = std(M,0,1);
    
    %make normalization by std
    %subtract the mean of each attribute and divide by std
    for j=1:size(M,2)
        M(:,j) = (M(:,j) - mM(j)) / sM(j);
    end
    
    fprintf('Scaling pair features...\n');
    f = size(M,2);
    X = single(repmat(0.0,[f n n]));
    
    for k=1:f
        for i=1:n
            X(k,i,:) = M((n*i)-n+1:n*i,k);
        end
    end
    
    fprintf('Generating Dbin, Z and J pairs...\n');
    % [Dbin, Z] = create_pairs(D,L); % generate matrix Dbin 3 x pairs 
    %[Dbin, Z, J] = create_pairs(D,L,K);
    [Triplets, Dbin, Z, J] = create_pairs(D,L,K);
    
    covariance = {[]};
    % compute covariance matrix
%     fprintf('Computing covariance matrix...\n');
%     for t=1:size(Dbin,2)
%        i = Dbin(1,t);
%        j = Dbin(2,t);
%        truth = Dbin(3,t);
%        
%        covariance{i,j} = F(:,i)*F(:,j)';
%     end
    
    fprintf('Saving all variables required...\n');

    %save('../dataset/5132_loose_wss-wiki2014_vfinal.mat','-mat','-v7.3','docs','titles','Dbin','X','F','Z','J','rates')
    save(strcat(path_to_files, '3000_loose_wss-wooki_vfinal.mat'),'-mat','-v7.3','docs','titles','Dbin','X','F','Z','J')
    
    % save('../dataset/5132_loose_wss-wiki2014_triplets.mat','-mat','-v7.3','docs','titles','Triplets','X','F','Z','rates')
    
    fprintf('File saved with successful.\n');
   
end



