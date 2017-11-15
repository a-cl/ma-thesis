from autoencoder import StackedAutoEncoder
import numpy as np
import cv2
import extractor
import util

# Model for transformation

def makeModel ():
    return StackedAutoEncoder(
        dims=[3042, 1024, 512, 128, 36],
        activations=['sigmoid', 'sigmoid', 'sigmoid', 'sigmoid', 'sigmoid'],
        epoch=[500, 500, 500, 500, 500],
        loss='rmse',
        lr=0.03,
        noise='mask-0.3',
        batch_size=100,
        print_step=50
    )

# Training data generation

def getFeatures (path):
    paths = util.readImagePaths(path)
    gradients = extractor.extractAllFeatures(paths)
    return util.featuresToArrays(gradients)

def generateTrainData(train, test, target):
    print('Generating train data.')
    model = makeModel()
    model.fit(train)
    print('Transforming data.')
    result = model.transform(test)
    print('Writing results.')
    util.writeFeatures(target, result)

# Test data generation

def extractAndTransformFeatures (image, model):
    features = extractor.extractFeatures(image)
    converted = util.featuresToArrays(features)
    return model.transform(converted)

def generateTestData(train, imagePairs, target):
    print('Generating test data.')
    model = makeModel()
    model.fit(train)
    tests = []
    i = 0

    for imagePair in imagePairs:
        i = i + 1
        print('Transforming pair', i)
        features1 = extractAndTransformFeatures(imagePair[0], model)
        features2 = extractAndTransformFeatures(imagePair[1], model)
        test = [imagePair[0], features1, imagePair[1], features2, imagePair[2]]
        tests.append(test)
    print('Writing results.')
    util.writeTests(target, tests)

# train data
generateTrainData(
    getFeatures('../uim_test-select/test1/train.txt'),
    getFeatures('../uim_test-select/test1/train.txt'),
    '../uim_bag-of-visual-words/data/1/train36.txt'
)
generateTrainData(
    getFeatures('../uim_test-select/test2/train.txt'),
    getFeatures('../uim_test-select/test2/train.txt'),
    '../uim_bag-of-visual-words/data/2/train36.txt'
)
generateTrainData(
    getFeatures('../uim_test-select/test3/train.txt'),
    getFeatures('../uim_test-select/test3/train.txt'),
    '../uim_bag-of-visual-words/data/3/train36.txt'
)

# test data
'''
generateTestData(
    getFeatures('../uim_test-select/test1/train.txt'),
    util.readTests('../uim_test-select/test1/test.txt'),
    '../uim_bag-of-visual-words/data/1/test36.txt'
)
generateTestData(
    getFeatures('../uim_test-select/test2/train.txt'),
    util.readTests('../uim_test-select/test2/test.txt'),
    '../uim_bag-of-visual-words/data/2/test36.txt'
)
generateTestData(
    getFeatures('../uim_test-select/test3/train.txt'),
    util.readTests('../uim_test-select/test3/test.txt'),
    '../uim_bag-of-visual-words/data/3/test36.txt'
)
'''