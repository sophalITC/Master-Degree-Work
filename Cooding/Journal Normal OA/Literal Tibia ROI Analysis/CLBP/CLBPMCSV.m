csvFileName = 'CLBPM CSV.csv';
sourceDir ='C:\Users\ASUS\Documents\MATLAB\Thesis\Feature\ROI of Femur\';
jpgFiles = dir(strcat(sourceDir, '*.JPG'));
jpgFilesSize = size(jpgFiles);

%header
cvsHeader = 'File';
%cvsHeader = sprintf('%s,%s', cvsHeader, 'CLBP_SH');
cvsHeader = sprintf('%s,%s', cvsHeader, 'CLBP_MH');

cvsHeader = strcat(cvsHeader,'\n');

%record
cvsRecords = '';
for i=1:jpgFilesSize(1,1)
    jpgFile = strcat(sourceDir, jpgFiles(i).name);
    cvsRecords = sprintf('%s%s,%s\n',cvsRecords, jpgFiles(i).name, clbpM(jpgFile));
end
csvFile = fopen(csvFileName,'w');
       fprintf(csvFile, cvsHeader);
       fprintf(csvFile, cvsRecords);
       fclose(csvFile);