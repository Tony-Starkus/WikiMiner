% Codes by: Raoni Ferreira.

%function [Dbin, Z, J] = create_pairs (D, L, K)
function [Triplets, Dbin, Z, J] = create_pairs (D, L, K)

% m = size(D,1);

% transform the squared matrix D of m-d to 3 x pairs matrix Dbin
[r0 c0 v0] = find(D == 0);
[r1 c1 v1] = find(D == 1);

temp = [r0' r1'; c0' c1'; zeros(1, size(r0,1)) ones(1, size(r1,1))];
[temp,~] = sortrows(temp');
Dbin = temp';

% [~,Is] = sort(temp(1,:));
% Dbin = temp(:,Is);

clear Is r0 c0 v0 r1 c1 v1 temp;

% define a 3 x pairs matrix of pairs used by MW2013 original method from L
I = find(L);
[r,c] = ind2sub(size(D),I);
Z = [r' ;c' ;D(I)'];
[~,Is] = sort(Z(1,:));
Z = Z(:,Is);

% define a 3 x pairs matrix of pairs which necessarily contains a label
[r0 c0 v0] = find(K == 0);
[r1 c1 v1] = find(K == 1);
temp = [r0' r1'; c0' c1'; zeros(1, size(r0,1)) ones(1, size(r1,1))];
[temp,~] = sortrows(temp');
temp = temp';

J = Dbin(:,find(temp(3,:)));

% obtain a subsample from (large) sample D
% sample = randsample(size(D,1), 500);
% I = find(ismember(temp(1,:),sample));
% Dbin = temp(:,I);

% I = find(ismember(Z(1,:),sample));
% Z = Z(:,I);

clear r c I Is temp r0 c0 v0 r1 c1 v1;

% I = find(C);
% [r,c] = ind2sub(size(C),I);
% K = [r'; c'; C(I)'];
% [~,Is] = sort(K(1,:));
% K = K(:,Is);

Triplets = [];

% obtain # of pairs per document (offset of pairs)
docs = unique(Z(1,:));
m = size(docs,2);
pairs_doc = zeros(m,1);
for i=1:m
    pairs_doc(i) = size(Z(:,Z(1,:)==docs(i)),2);
end


% create pairs on the following format
% 3 x pairs where rows (node1,node2,node3) such that (node1,node2) has
% positive link but (node1,node3) has negative link

offset=0;
for t = 1:m
    npairs = pairs_doc(t);
    A = Z(:,offset+1:offset+npairs);
    %size(A)
    %size(pairs)
    for pij = 1:npairs
        vij = A(3,pij);
        for pk = 1:npairs
            vk = A(3,pk);
            
            if (vij==1 && vk==0)
                Triplets = [ Triplets [A(1,pij);A(2,pij);A(2,pk)] ];
            end
        end
    end
    offset = offset + npairs;
end


end
