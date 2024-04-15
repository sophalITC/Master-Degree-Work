function crop=cropimg(I)
final=uint8(255 * mat2gray(I));
figure,imshow(final);
%imshow(final);
[x,y] = ginput(1); 

% Get the x and y corner coordinates as integers
%sp(1) = min(floor(p(1)), floor(p(2))); %xmin
%sp(2) = min(floor(p(1)), floor(p(2))); %ymin
%x=sp(1);
%y=sp(2);

% Index into the original image to create the new image

% Display the subsetted image with appropriate axis ratio

hold on;
%rectangle('Position', [a1, b1, 150, 100],'EdgeColor','b','LineWidth',2);
rectangle('Position', [x,y,100,100],'EdgeColor','b','LineWidth',2);
MM = imcrop(final,[x,y,100,100]);
figure, imshow(MM),title('The ROI of G1 dataset');
% Write image to graphics file. 
imwrite(MM,'Medial Tibia of G3_44.jpg') 
%final=uint8(255 * mat2gray(Img));
%imtool(final);
%imwrite(final,'Tibia roi of OA_55.jpg');
end