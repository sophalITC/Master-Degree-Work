function csvRecord = TibiaROI(fileName)
fprintf('%s\n', fileName);
%clear;
%clc;
I=imread(fileName);
I2=imrotate(I,90);
mapping=getmaplbphf(8);
h=lbp(I,1,8,mapping,'h');
h=h/sum(h);
histograms(1,:)= h;
h=lbp(I2,1,8,mapping,'h');
h=h/sum(h);
histograms(2,:)= h;
lbp_hf_features=constructhf(histograms,mapping);
B=lbp_hf_features(1,:);

csvRecord = sprintf('%d', B(1));
for i = 2 :38
    csvRecord = sprintf('%s,%d',csvRecord, B(i));
end
