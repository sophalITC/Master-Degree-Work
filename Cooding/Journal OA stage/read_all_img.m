imagefiles = dir('OA_*.dcm');      
nfiles = length(imagefiles);    % Number of files found
for ii=1:nfiles
   currentfilename = imagefiles(ii).name;
   currentimage = dicomread(currentfilename);
   oa{ii} = currentimage;
end
%imshow(images{1})