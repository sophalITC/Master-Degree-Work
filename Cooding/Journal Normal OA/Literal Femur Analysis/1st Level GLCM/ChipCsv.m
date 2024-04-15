csvFileName = '1stGLCM Femur Literal ROI .csv';
sourceDir ='C:\Users\ASUS\Documents\MATLAB\Journal\Literal Femur Analysis\';
jpgFiles = dir(strcat(sourceDir, '*.JPG'));
jpgFilesSize = size(jpgFiles);

%header
cvsHeader = 'File';
for count=1:6
    if count==1
     cvsHeader = sprintf('%s,"Mean%d"',cvsHeader,count);
    end 
     if count==2
     cvsHeader = sprintf('%s,"Variance%d"',cvsHeader,count);
     end 
     if count==3
     cvsHeader = sprintf('%s,"Skewness%d"',cvsHeader,count);
     end 
     if count==4
     cvsHeader = sprintf('%s,"Kurtosis%d"',cvsHeader,count);
     end 
     if count==5
     cvsHeader = sprintf('%s,"Energy%d"',cvsHeader,count);
     end 
     if count==6
     cvsHeader = sprintf('%s,"Entropy%d"',cvsHeader,count);
     end 
    
    
end
cvsHeader = strcat(cvsHeader,'\n');

%record
cvsRecords = '';
for i=1:jpgFilesSize(1,1)
    jpgFile = strcat(sourceDir, jpgFiles(i).name);
    cvsRecords = sprintf('%s%s,%s\n',cvsRecords, jpgFiles(i).name,ChipROI(jpgFile));
end
csvFile = fopen(csvFileName,'w');
       fprintf(csvFile, cvsHeader); 
       fprintf(csvFile, cvsRecords);
       fclose(csvFile);