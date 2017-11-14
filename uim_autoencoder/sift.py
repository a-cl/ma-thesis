import cv2
import numpy as np
import util

def extractAndDrawKeypoints (imagePath, resultPath):
    sift = cv2.xfeatures2d.SIFT_create()
    img = cv2.imread(imagePath)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    kp = sift.detect(gray, None)
    cv2.drawKeypoints(gray, kp, img, flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
    cv2.imwrite(resultPath, img)

#extractAndDrawKeypoints('sift_data/liberty1.png', 'sift_data/liberty1_kp.png')
#extractAndDrawKeypoints('sift_data/liberty2.jpg', 'sift_data/liberty2_kp.jpg')

def extractAndSaveFeatures (sourcePath, resultPath):
	sift = cv2.xfeatures2d.SIFT_create()
	imagePaths = util.readImagePaths(sourcePath)
	features = []

	for imagePath in imagePaths:
		print('Extracting Features of', imagePath)
		img = cv2.imread(imagePath)
		gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
		kp, des = sift.detectAndCompute(gray, None)
		features.append(des)
	flat = [item for sublist in features for item in sublist]
	util.writeFeatures(resultPath, flat)
	print('Features saved.')

extractAndSaveFeatures(
	'../uim_test-select/test3/train.txt',
	'../uim_bag-of-visual-words/data/3/train128.txt'
)