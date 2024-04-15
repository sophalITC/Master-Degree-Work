# Master's Thesis on Osteoarthritis Grading Using Knee X-ray Imagery
## Focus: Texture-Based Approach for OA Classification

### Introduction
This repository contains the work and findings from my Master of Science in Information Technology thesis, which focused on developing and enhancing methods for grading Osteoarthritis (OA) using texture analysis of knee X-ray imagery. The goal of this project was to identify and classify stages of OA by analyzing various texture features extracted from regions of interest (ROI) in the X-ray images.

### Project Structure
- **Region of Interest (ROI) Segmentation and Enhancement**
  - **Region of Interest**: Techniques used to select and define areas in the X-ray images for further analysis.
  - **ROI Image Enhancement**: Methods applied to enhance the image quality of the selected ROI, improving the accuracy of subsequent texture analysis.
- **Summary**
  
- **Osteoarthritis Classification Using Knee X-ray Imagery: Texture-Based Approach**
  - **Introduction**: Overview of texture analysis techniques and their relevance to OA grading.
  - **Texture Analysis**: Detailed examination of various texture features used in this study, including:
    - **Histogram feature**
    - **Local Binary Pattern**
    - **Completed Local Binary Pattern**
    - **Rotated Local Binary Pattern**
    - **Local Binary Pattern Rotation Invariant**
    - **Local Binary Pattern Histogram Fourier**
    - **Local Configuration Pattern**
    - **Local Ternary Pattern**
    - **Haralick feature**
    - **Gabor filter feature**
  - **Feature Selection and Classification**: Methodology for selecting the most significant features and the classification techniques employed, using various ML algorithms:
    - **C4.5**
    - **Binary Split Tree**
    - **AODE (Averaged One-Dependence Estimators)**
    - **Bayesian Network**
    - **Naïve Bayes**
    - **SVM (Support Vector Machine)**
    - **SMO (Sequential Minimal Optimization)**
    - **Neural Network Back Propagation**
  - **Evaluation**: Discusses the evaluation methods used to assess the effectiveness of the texture-based classification system, including:
    - **Osteoarthritis Detection using texture analysis**
    - **Osteoarthritis Stage Classification texture analysis**

### Machine Learning Algorithms Used
- **C4.5**: A decision tree-based algorithm used for classification, which builds decision trees from a set of training data using the concept of information entropy.
- **Binary Split Tree**: A simple form of decision tree that uses binary decisions to split the data, typically used for problems with binary outcomes.
- **AODE (Averaged One-Dependence Estimators)**: Enhances the probability estimation of Naïve Bayes by averaging over all models that assume a single attribute dependency.
- **Bayesian Network**: A probabilistic graphical model that represents a set of variables and their conditional dependencies via a directed acyclic graph.
- **Naïve Bayes**: A simple probabilistic classifier based on applying Bayes' theorem with strong (naïve) independence assumptions between the features.
- **SVM (Support Vector Machine)**: A powerful classifier that works well in high-dimensional spaces, using a hyperplane to separate different classes.
- **SMO (Sequential Minimal Optimization)**: An algorithm for solving the optimization problem of SVMs by breaking it down into 2D sub-problems.
- **Neural Network Back Propagation**: A multilayer perceptron training algorithm that updates the weights of neurons via a backward pass of error correction.

### Software Used
- **MATLAB**: The primary environment used for implementing and testing the image processing and texture analysis algorithms.
- **Weka**: Utilized for applying various machine learning algorithms and conducting statistical analyses to evaluate model performances.

### Usage
To replicate the findings or further explore the techniques used in this project, ensure access to MATLAB and Weka software. Detailed scripts and dataset configurations are provided in the respective folders.

### Conclusion
This project demonstrates the potential of texture-based features in effectively classifying and grading Osteoarthritis from knee X-ray images. The methodologies and findings discussed herein contribute to the broader efforts of applying machine learning techniques to improve diagnostic processes in medical imaging.

### Contact
For any queries regarding this project, please contact:
- **[CHAN Sophal]**
- **Email**: [sophalcamchan38@gmil.com]

