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

def extractDescriptors (sift, imagePath):
	print('Extracting Features of', imagePath)
	img = cv2.imread(imagePath)
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	kp, des = sift.detectAndCompute(gray, None)
	return des

def generateTrainData (sourcePath, resultPath):
	sift = cv2.xfeatures2d.SIFT_create()
	imagePaths = util.readImagePaths(sourcePath)
	features = []

	for imagePath in imagePaths:
		des = extractDescriptors(sift, imagePath)
		features.append(des)
	flat = [item for sublist in features for item in sublist]
	util.writeFeatures(resultPath, flat)
	print('Features saved.')

def generateTestData (sourcePath, resultPath):
	sift = cv2.xfeatures2d.SIFT_create()
	data = util.readTests(sourcePath)
	tests = []

	for imagePair in data:
		features1 = extractDescriptors(sift, imagePair[0])
		features2 = extractDescriptors(sift, imagePair[1])
		test = [imagePair[0], features1, imagePair[1], features2, imagePair[2]]
		tests.append(test)
	util.writeTests(resultPath, tests)

'''
generateTrainData(
	'../uim_test-select/test3/train.txt',
	'../uim_bag-of-visual-words/data/3/train128.txt'
)
'''

generateTestData(
	'../uim_test-select/test1/test.txt',
	'../uim_bag-of-visual-words/data/1/test128.txt'
)