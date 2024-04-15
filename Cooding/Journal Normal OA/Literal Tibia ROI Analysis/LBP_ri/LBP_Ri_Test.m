I=imread('ROI of TibiaNor1.jpg');
MAPPING=getmapping(8,'ri');
LBPHIST=lbp(I,1,8,MAPPING,'h');