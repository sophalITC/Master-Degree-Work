function csvRecord = RLBPROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I = imread(fileName);
glcm = graycomatrix(I, 'offset', [0 1], 'Symmetric', true);

xFeatures = 1:14;
x = haralickTextureFeatures(glcm, 1:14);
a=x';

csvRecord = sprintf('%d', a(1));
for i = 2 :14
    csvRecord = sprintf('%s,%d', csvRecord, a(i));
end
