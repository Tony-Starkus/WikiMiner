% Codes by: Raoni Ferreira.

%
% prepare pairs for 
% wikification factorModel

clear all;

scaling=1;


fprintf('Reading all ids sample...\n');
file1=fopen('../5132_wss_wiki2014_sample-ids.txt');
%file1=fopen('../wss_wiki2014_sample-ids_vfinal.txt');
%in = textscan(file1,'%d');
in = textscan(file1,'%d','Delimiter',',');
fclose(file1);

docs = in{1};
n = size(docs,1);

clear in;

fprintf('Reading all rates sample...\n');
file=fopen('../5132_wss_wiki2014_sample-rates.txt');
in = textscan(file,'%d %s %s','Delimiter','\t');
fclose(file);

rates = in{3};

clear in;

% feature label for each fold
% C = {sparse(n,n) sparse(n,n) sparse(n,n) sparse(n,n) sparse(n,n)};

% for i=1:5
%    C{i} = getlabelfeature(i,n);
% end

%c = regex(str(565996),'("[^"]*")|([^,"]+)', 'match');
%C = {in{1} in{2} in{3}};

clear in;

fprintf('Reading node features...\n');
file=fopen('/home/raoni/workspace/wss-miner/data/factor_model/full_dataset/loose/nodes_features.csv');
in = textscan(file,'%d %s %f %f %f', 'HeaderLines',1,'Delimiter',';');
x1 = in{3}; %inlink ratio (new attribute)
x2 = in{4}; %outlink ratio (new attribute)
x3 = in{5}; %generality

fclose(file);

titles=in{2}; %title names

clear in;

fprintf('Reading pair features...\n');
file2=fopen('/home/raoni/workspace/wss-miner/data/factor_model/full_dataset/loose/pairs_features.csv');
%file2=fopen('arff/184854-pairs.csv');
in = textscan(file2,'%d %d %d %d %f %f %f %f %f %f %f %f %f %f %f %d', 'HeaderLines',1,'Delimiter',',');
fclose(file2);

f0 = double(in{3}); %mwpair: feature used as tagger of MW pairs used by wikipediaminer)
f01 = double(in{4}); %haslabel: is there a label which represent the pair?
f1 = double(in{5}); %relatedness
f2 = double(in{6}); %relatednessToContext
f3 = double(in{7}); %relatednessToAllCandidates
f4 = double(in{8}); %occurance
f5 = double(in{9}); %avgLinkProb
f6 = double(in{10}); %maxLinkProb
f7 = double(in{11}); %firstOcurr
f8 = double(in{12}); %lastOcurr
f9 = double(in{13}); %spread
f10 = double(in{14}); %avgDisambigConf
f11 = double(in{15}); %maxDisambigConf
f12 = double(in{16}); %link status

fprintf('Obtaining topology information...\n');
A = zeros(n,n);
% obtain topology information
for i=1:n
    A(i,:) = f12(((n*i)-n)+1:n*i,1);
end

D = A;
%D=sparse(A);

clear A;
clear in;

% tag original pairs from MW
L = zeros(n,n);
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
    F = [x1 x2 x3];
    mF = mean(F,1); %obtain mean of column f1
    sF = std(F,0,1); %std
    
    for j=1:size(F,2)
        F(:,j) = (F(:,j) - mF(j) / sF(j));
    end
    F = single(F'); %use generality as node feature
    
    
    % dyads attributes
    M = [f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11 f01];
    
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

    save('../dataset/5132_loose_wss-wiki2014_vfinal.mat','-mat','-v7.3','docs','titles','Dbin','X','F','Z','J','rates')
    save('../dataset/5132_loose_wss-wiki2014_triplets.mat','-mat','-v7.3','docs','titles','Triplets','X','F','Z','rates')
    
    fprintf('File saved with successful.\n');
   
end



