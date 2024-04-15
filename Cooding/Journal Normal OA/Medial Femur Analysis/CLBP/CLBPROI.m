function csvRecord = CLBPSROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I = imread(fileName);
 %rows=size(I,1);
 %cols=size(I,2);
%C_Gray = rgb2gray(I);
%Green = C(:,:,2);
%MidLine = Green<=50;
%Seed = Green<125;
%J = imfill(Seed,'holes');
%Edge = edge(J, 'canny');
%Total = Edge+MidLine;
%BW = uint8(255 * mat2gray(Total));
%stdf = stdfilt(C_Gray);
mapping=getmapping(8,'u2'); 
%[CLBP_SH,CLBP_MH]=clbp(I,1,8,mapping,'h');
clbp_sh=clbpS(I,1,8,mapping,'h');
clbp_mh=clbpM(I,1,8,mapping,'h');
%clbp_mh=clbpM(I,1,8,mapping,'h');
%SP=[-1 -1; -1 0; -1 1; 0 -1; -0 1; 1 -1; 1 0; 1 1];
%I2= lbp(Green,SP,0,'i');
%imshow(I2);
%[featureVector,hogVisualization] = extractHOGFeatures(Total);

csvRecord = sprintf('%d',clbp_sh(1));
csvRecord = sprintf('%d',clbp_mh(1));
%csvRecord = sprintf('%d',clbp_sh(2));
for i = 2 :59
    csvRecord = sprintf('%s,%d', csvRecord,clbp_sh(i));
    csvRecord = sprintf('%s,%d', csvRecord, clbp_mh(i));
end
