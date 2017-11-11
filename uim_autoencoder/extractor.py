import numpy as np
import cv2
import matplotlib.pyplot as plt
import util

def DoG(image):
    #run a 5x5 gaussian blur then a 3x3 gaussian blr
    blur5 = cv2.GaussianBlur(image,(5,5),0)
    blur3 = cv2.GaussianBlur(image,(3,3),0)
    return blur5 - blur3

def getPatch (image, keypoint):
    x = int(keypoint.pt[0])
    y = int(keypoint.pt[1])
    return image[y-20:y+19, x-20:x+19]

def computeGradients (patch):
    #grad_x = cv2.Sobel(patch, cv2.CV_16S, 1, 0, ksize=7, scale=1, delta=0)
    #grad_y = cv2.Sobel(patch, cv2.CV_16S, 0, 1, ksize=7, scale=1, delta=0)
    grad_x = cv2.GaussianBlur(patch, (3, 1), 0.5)
    grad_y = cv2.GaussianBlur(patch, (1, 3), 0.5)
    #print("dat shape", grad_x.shape)
    return [grad_x, grad_y]

def computeDescriptors (image, keypoints):
    print("Computing descriptors for", len(keypoints), "keypoints")
    desc = []
    for keypoint in keypoints:
        patch = getPatch(image, keypoint)
        if (patch.shape[0] == 39 and patch.shape[1] == 39):
            desc.append(computeGradients(patch))
    return desc

def extractFeatures (imagePath):
    print("Extracting features of", imagePath)
    img = cv2.imread(imagePath)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    sift = cv2.xfeatures2d.SIFT_create()
    keypoints = sift.detect(gray, None)
    return computeDescriptors(gray, keypoints)

def extractAllFeatures (imagePaths):
    features = []
    for imagePath in imagePaths:
        imgFeatures = extractFeatures(imagePath)
        features.extend(imgFeatures)
    print("Extracted", len(features), "Features in total.")
    return features

# 'img/stop_sign/image_0001.jpg'
# hog.show(gradients, 50, 62)
def show(gradients, start, end):
    i = 0
    size = end - start

    def draw(img, index):
        plt.subplot(size, 2, index)
        plt.imshow(img, cmap='gray')
        plt.axis('off')

    while (i < size * 2):
        draw(gradients[i + start][0], i + 1)
        draw(gradients[i + start][1], i + 2)
        i = i + 2
    plt.show()

## Test

def test():
    paths = [
        'img/guitar/image_0001.jpg',
        'img/guitar/image_0011.jpg',
        'img/guitar/image_0035.jpg',
        'img/guitar/image_0044.jpg',
        'img/dragonfly/image_0002.jpg',
        'img/dragonfly/image_0020.jpg',
        'img/dragonfly/image_0035.jpg',
        'img/dragonfly/image_0056.jpg'
    ]

    features = extractAllFeatures(paths)
    p_features = util.imageToArray(features)
    u_features = util.arrayToImage(p_features)

    print("Feature 0, x0: [8, 9, 10, 11]")
    print('Original:', features[0][0][0][8], features[0][0][0][9], features[0][0][0][10], features[0][0][0][11])
    print('Packed:  ', p_features[0][8], p_features[0][9], p_features[0][10], p_features[0][11])
    print('Unpacked:', u_features[0][0][0][8], u_features[0][0][0][9], u_features[0][0][0][10], u_features[0][0][0][11])
    print('')

    max = 0
    min = 0

    for p_feature in p_features:
        for i in range(0, len(p_feature)):
            if (p_feature[i] > max):
                max = p_feature[i]
            if (p_feature[i] < min):
                min = p_feature[i]

    print('Max:', max, 'Min:', min)
    print('')

    print("Feature 110, y2: [35, 36, 37, 38]")
    print('Original:', features[110][1][2][35], features[110][1][2][36], features[110][1][2][37], features[110][1][2][38])
    print('Packed:  ', p_features[110][1634], p_features[110][1635], p_features[110][1636], p_features[110][1637])
    print('Unpacked:', u_features[110][1][2][35], u_features[110][1][2][36], u_features[110][1][2][37], u_features[110][1][2][38])
