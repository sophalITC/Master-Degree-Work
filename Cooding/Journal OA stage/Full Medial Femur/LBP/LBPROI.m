function csvRecord = TibiaROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I = imread(fileName);

%C_Gray = rgb2gray(I);
%Green = C(:,:,2);
%MidLine = Green<=50;
%Seed = Green<125;
%J = imfill(Seed,'holes');
%Edge = edge(J, 'canny');
%Total = Edge+MidLine;
%BW = uint8(255 * mat2gray(Total));
%stdf = stdfilt(C_Gray);
LBP = lbp(I,1,8,0);
%SP=[-1 -1; -1 0; -1 1; 0 -1; -0 1; 1 -1; 1 0; 1 1];
%I2= lbp(Green,SP,0,'i');
%imshow(I2);
%[featureVector,hogVisualization] = extractHOGFeatures(Total);

csvRecord = sprintf('%d', LBP(1));
for i = 2 :256
    csvRecord = sprintf('%s,%d', csvRecord, LBP(i));
end
