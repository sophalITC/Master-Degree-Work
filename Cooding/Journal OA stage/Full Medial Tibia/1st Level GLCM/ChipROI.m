function csvRecord = HisRoi(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I = imread(fileName);
his=chip_histogram_features( I,'NumLevels',9,'G',[] );
csvRecord = sprintf('%d', his(1));
for i = 2 :6
    csvRecord = sprintf('%s,%d', csvRecord, his(i));
end
%=============SHAPE INFORMATION=================================

%stat = regionprops(Seed,'Area','MajorAxisLength','MinorAxisLength','Eccentricity');


% csvRecord = sprintf('%s,%f', csvRecord, stat.Area);
% csvRecord = sprintf('%s,%f', csvRecord, stat.MajorAxisLength);
% csvRecord = sprintf('%s,%f', csvRecord, stat.MinorAxisLength);
% csvRecord = sprintf('%s,%f', csvRecord, stat.Eccentricity);
%fprintf(fileName);
end


