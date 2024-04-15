%LTP returns the local binary pattern image or LTP histogram of an image.
%  J = LTP(I,R,N,MAPPING,MODE,DELTA) returns either a local binary pattern
%  coded image or the local binary pattern histogram of an intensity
%  image I. The LTP codes are computed using N sampling points on a 
%  circle of radius R, using mapping table defined by MAPPING and a delta 
%  value defined by DELTA. 
%  See the getmapping function for different mappings and use 0 for
%  no mapping. Possible values for MODE are
%       'h' or 'hist'  to get a histogram of LTP codes
%       'nh'           to get a normalized histogram
%  Otherwise an LTP code image is returned.
%
%  J = LTP(I,SP,MAPPING,MODE,DELTA) computes the LTP codes using n sampling
%  points defined in (n * 2) matrix SP. The sampling points should be
%  defined around the origin (coordinates (0,0)).
%
%  Sample usage
%  --------
%       I=imread('rice.png');
%       mapping=getmapping(8,'u2'); 
%       delta = 4;
%       H1=LTP(I,1,8,mapping,'h', delta); %LTP histogram in (8,1) neighborhood
%                                         %using uniform patterns


function result = lbp(varargin) % image,radius,neighbors,mapping,mode)
% Version 0.1
% Author: Francesco Bianconi
%         This is a modified version of lbp.m (v 0.3.3) by Marko Heikkil? 
%         and Timo Ahonen



% Check number of input arguments.
error(nargchk(6,6,nargin));

Delta = varargin{6};

image=varargin{1};
d_image=double(image);

if nargin==1
    spoints=[-1 -1; -1 0; -1 1; 0 -1; -0 1; 1 -1; 1 0; 1 1];
    neighbors=8;
    mapping=0;
    mode='h';
end

if (nargin == 2) && (length(varargin{2}) == 1)
    error('Input arguments');
end

if (nargin > 2) && (length(varargin{2}) == 1)
    radius=varargin{2};
    neighbors=varargin{3};
    
    spoints=zeros(neighbors,2);

    % Angle step.
    a = 2*pi/neighbors;
    
    for i = 1:neighbors
        spoints(i,1) = -radius*sin((i-1)*a);
        spoints(i,2) = radius*cos((i-1)*a);
    end
    
    if(nargin >= 4)
        mapping=varargin{4};
        if(isstruct(mapping) && mapping.samples ~= neighbors)
            error('Incompatible mapping');
        end
    else
        mapping=0;
    end
    
    if(nargin >= 5)
        mode=varargin{5};
    else
        mode='h';
    end
end

if (nargin > 1) && (length(varargin{2}) > 1)
    spoints=varargin{2};
    neighbors=size(spoints,1);
    
    if(nargin >= 3)
        mapping=varargin{3};
        if(isstruct(mapping) && mapping.samples ~= neighbors)
            error('Incompatible mapping');
        end
    else
        mapping=0;
    end
    
    if(nargin >= 4)
        mode=varargin{4};
    else
        mode='h';
    end   
end

% Determine the dimensions of the input image.
[ysize xsize] = size(image);



miny=min(spoints(:,1));
maxy=max(spoints(:,1));
minx=min(spoints(:,2));
maxx=max(spoints(:,2));

% Block size, each LTP code is computed within a block of size bsizey*bsizex
bsizey=ceil(max(maxy,0))-floor(min(miny,0))+1;
bsizex=ceil(max(maxx,0))-floor(min(minx,0))+1;

% Coordinates of origin (0,0) in the block
origy=1-floor(min(miny,0));
origx=1-floor(min(minx,0));

% Minimum allowed size for the input image depends
% on the radius of the used LTP operator.
if(xsize < bsizex || ysize < bsizey)
  error('Too small input image. Should be at least (2*radius+1) x (2*radius+1)');
end

% Calculate dx and dy;
dx = xsize - bsizex;
dy = ysize - bsizey;

% Fill the center pixel matrix C.
C = image(origy:origy+dy,origx:origx+dx);
d_C = double(C);

bins = 2^neighbors;

% Initialize the result matrix with zeros.
result_L = zeros(dy+1,dx+1);
result_U = zeros(dy+1,dx+1);

%Compute the LTP code image
for i = 1:neighbors
  y = spoints(i,1)+origy;
  x = spoints(i,2)+origx;
  % Calculate floors, ceils and rounds for the x and y.
  fy = floor(y); cy = ceil(y); ry = round(y);
  fx = floor(x); cx = ceil(x); rx = round(x);
  % Check if interpolation is needed.
  if (abs(x - rx) < 1e-6) && (abs(y - ry) < 1e-6)
    % Interpolation is not needed, use original datatypes
    N = image(ry:ry+dy,rx:rx+dx);
    D_L = ((double(C) - double(N) - Delta) >= 0);       %LTP - lower
    D_U = ((double(N) - double(C) - Delta) >= 0);       %LTP - upper
  else
    % Interpolation needed, use double type images 
    ty = y - fy;
    tx = x - fx;

    % Calculate the interpolation weights.
    w1 = roundn((1 - tx) * (1 - ty),-6);
    w2 = roundn(tx * (1 - ty),-6);
    w3 = roundn((1 - tx) * ty,-6) ;
    % w4 = roundn(tx * ty,-6) ;
    w4 = roundn(1 - w1 - w2 - w3, -6);
            
    % Compute interpolated pixel values
    N = w1*d_image(fy:fy+dy,fx:fx+dx) + w2*d_image(fy:fy+dy,cx:cx+dx) + ...
w3*d_image(cy:cy+dy,fx:fx+dx) + w4*d_image(cy:cy+dy,cx:cx+dx);
    N = roundn(N,-4);
    
    D_L = ((d_C - double(N) - Delta) >= 0);
    D_U = ((double(N) - d_C - Delta) >= 0);
  end  
  % Update the result matrix.
  v = 2^(i-1);
  result_L = result_L + v*D_L;
  result_U = result_U + v*D_U;
end

%Apply mapping if it is defined
if isstruct(mapping)
    bins = mapping.num;
    for i = 1:size(result_L,1)
        for j = 1:size(result_L,2)
            result_L(i,j) = mapping.table(result_L(i,j)+1);
            result_U(i,j) = mapping.table(result_U(i,j)+1);
        end
    end
end

if (strcmp(mode,'h') || strcmp(mode,'hist') || strcmp(mode,'nh'))
    % Return with LTP histogram if mode equals 'hist'.
    result_L=hist(result_L(:),0:(bins-1));
    result_U=hist(result_U(:),0:(bins-1));
    if (strcmp(mode,'nh'))
        result_L=result_L/sum(result_L);
        result_U=result_U/sum(result_U);
    end
else
    %Otherwise return a matrix of unsigned integers
    if ((bins-1)<=intmax('uint8'))
        result_L=uint8(result_L);
        result_U=uint8(result_U);
    elseif ((bins-1)<=intmax('uint16'))
        result_L=uint16(result_L);
        result_U=uint16(result_U);
    else
        result_L=uint32(result_L);
        result_U=uint32(result_U);
    end
end
    
    %Return the concatenation of the lower and upper part
    result = [result_L, result_U];
end

function x = roundn(x, n)

error(nargchk(2, 2, nargin, 'struct'))
validateattributes(x, {'single', 'double'}, {}, 'ROUNDN', 'X')
validateattributes(n, ...
    {'numeric'}, {'scalar', 'real', 'integer'}, 'ROUNDN', 'N')

if n < 0
    p = 10 ^ -n;
    x = round(p * x) / p;
elseif n > 0
    p = 10 ^ n;
    x = p * round(x / p);
else
    x = round(x);
end


end