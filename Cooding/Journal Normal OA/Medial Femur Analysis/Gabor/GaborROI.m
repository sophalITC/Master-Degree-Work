function csvRecord = RLBPROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I = imread(fileName);
gaborArray = gaborFilterBank(5,8,39,39);
%C_Gray = rgb2gray(I);
%Green = C(:,:,2);
%MidLine = Green<=50;
%Seed = Green<125;
%J = imfill(Seed,'holes');
%Edge = edge(J, 'canny');
%Total = Edge+MidLine;
%BW = uint8(255 * mat2gray(Total));
%stdf = stdfilt(C_Gray);
%featureVector = (gaborFeatures(I,gaborArray,4,4))';
featureVector = gaborFeatures(I,gaborArray,4,4);
gl=featureVector';
%SP=[-1 -1; -1 0; -1 1; 0 -1; -0 1; 1 -1; 1 0; 1 1];
%I2= lbp(Green,SP,0,'i');
%imshow(I2);
%[featureVector,hogVisualization] = extractHOGFeatures(Total);

csvRecord = sprintf('%d', gl(1));
for i = 2 :150
    csvRecord = sprintf('%s,%d', csvRecord, gl(i));
end
