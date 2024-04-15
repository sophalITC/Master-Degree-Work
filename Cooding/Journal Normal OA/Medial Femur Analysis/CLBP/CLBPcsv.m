csvFileName = 'CLBP Femur Medial ROI.csv';
sourceDir ='C:\Users\ASUS\Documents\MATLAB\Journal\Medial Femur Analysis\';
jpgFiles = dir(strcat(sourceDir, '*.JPG'));
jpgFilesSize = size(jpgFiles);

%header
cvsHeader = 'File';

%cvsHeader = sprintf('%s,%s', cvsHeader, 'CLBP_MH');

%cvsHeader = strcat(cvsHeader,'\n');
for count=1:59
     cvsHeader = sprintf('%s,"SH_%d"', cvsHeader, count);
     cvsHeader = sprintf('%s,"MH_%d"', cvsHeader, count);
end
cvsHeader = strcat(cvsHeader,'\n');
%record
cvsRecords = '';
for i=1:jpgFilesSize(1,1)
    jpgFile = strcat(sourceDir, jpgFiles(i).name);
    cvsRecords = sprintf('%s%s,%s\n',cvsRecords, jpgFiles(i).name, CLBPROI(jpgFile));
    %cvsRecords = sprintf('%s%s,%s\n',cvsRecords, jpgFiles(i).name, CLBPSROI(jpgFile));
end
csvFile = fopen(csvFileName,'w');
       fprintf(csvFile, cvsHeader);
       fprintf(csvFile, cvsRecords);
       fclose(csvFile);