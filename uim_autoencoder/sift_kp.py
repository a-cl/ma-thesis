import cv2
import numpy as np

def extractAndSaveKeypoints (imagePath, resultPath):
    sift = cv2.xfeatures2d.SIFT_create()
    img = cv2.imread(imagePath)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    kp = sift.detect(gray, None)
    cv2.drawKeypoints(gray, kp, img, flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
    cv2.imwrite(resultPath, img)

extractAndSaveKeypoints('sift_data/liberty1.png', 'sift_data/liberty1_kp.png')
extractAndSaveKeypoints('sift_data/liberty2.jpg', 'sift_data/liberty2_kp.jpg')
