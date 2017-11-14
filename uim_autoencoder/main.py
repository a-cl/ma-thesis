from autoencoder import StackedAutoEncoder
import numpy as np
import cv2
import extractor
import util

model = StackedAutoEncoder(
    dims=[3042, 1024, 512, 128, 36],
    activations=['sigmoid', 'sigmoid', 'sigmoid', 'sigmoid', 'sigmoid'],
    epoch=[500, 500, 500, 500, 500],
    loss='rmse',
    lr=0.03,
    noise='mask-0.3',
    batch_size=100,
    print_step=50
)

def getFeatures (path):
    paths = util.readImagePaths(path)
    gradients = extractor.extractAllFeatures(paths)
    return util.featuresToArrays(gradients)

def trainNet (train):
    print('Starting training of the network.')
    model.fit(train)

def runNet(data):
    print('Transform data.', end='')
    result = model.transform(data)
    print('Writing results.', end='')
    print(' Done.')

def generateTrainData(train, test):
    trainNet(train)
    result = runNet(test)
    util.writeFeatures('../uim_bag-of-visual-words/data/3/train36.txt', result)

def generateTestData(train, imagePairs):
    trainNet(train)
    tests = []

    for imagePair in imagePairs:
        features1 = runNet(extractor.extractFeatures(imagePair[0]))
        features2 = runNet(extractor.extractFeatures(imagePair[1]))
        test = [imagePair[0], features1, imagePair[1], features2, imagePair[2]]
        tests.append(test)
    util.writeTests(tests)

# Test 3 train data
generateTrainData(
    getFeatures('../uim_test-select/test3/train.txt'),
    getFeatures('../uim_test-select/test3/train.txt')
)

# Test 3 test data
generateTestData(
    getFeatures('../uim_test-select/test3/train.txt'),
    util.readTests('../uim_test-select/test3/test.txt')
)