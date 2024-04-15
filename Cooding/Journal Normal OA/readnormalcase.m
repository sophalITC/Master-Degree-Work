imagefiles = dir('Normal*.dcm');      
nfiles = length(imagefiles);    % Number of files found
for ii=1:nfiles
   currentfilename = imagefiles(ii).name;
   currentimage = dicomread(currentfilename);
   nor{ii} = currentimage;
end
%imshow(normals{1})