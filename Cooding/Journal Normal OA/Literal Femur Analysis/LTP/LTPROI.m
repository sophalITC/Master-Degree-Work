function csvRecord = LTPROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I=imread(fileName);
mapping=getmapping(8,'u2'); 
delta = 4;
ltp =LTP(I,1,8,mapping,'h', delta);

csvRecord = sprintf('%d', ltp(1));
for i = 2 :118
    csvRecord = sprintf('%s,%d', csvRecord, ltp(i));
end
