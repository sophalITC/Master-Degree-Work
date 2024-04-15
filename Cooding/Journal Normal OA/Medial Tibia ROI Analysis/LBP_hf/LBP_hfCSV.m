csvFileName = 'LBP_hf Tibia Medial ROI .csv';
sourceDir = 'C:\Users\ASUS\Documents\MATLAB\Journal\Medial Tibia ROI Analysis\';
jpgFiles = dir(strcat(sourceDir, '*.JPG'));
jpgFilesSize = size(jpgFiles);

%header
cvsHeader = 'File';
for count=1:38
     cvsHeader = sprintf('%s,"LBP_hf_%d"',cvsHeader,count);
end
cvsHeader = strcat(cvsHeader,'\n');

%record
cvsRecords = '';
for i=1:jpgFilesSize(1,1)
    jpgFile = strcat(sourceDir, jpgFiles(i).name);
    cvsRecords = sprintf('%s%s,%s\n',cvsRecords, jpgFiles(i).name,LBP_hfROI(jpgFile));
end
csvFile = fopen(csvFileName,'w');
       fprintf(csvFile, cvsHeader); 
       fprintf(csvFile, cvsRecords);
       fclose(csvFile);