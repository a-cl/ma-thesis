from autoencoder import StackedAutoEncoder
import autoencoder2
import numpy as np
import cv2
import extractor
import util

def getFeatures (path):
    paths = util.readImagePaths(path)
    gradients = extractor.extractAllFeatures(paths)
    return util.featuresToArrays(gradients)

def run(train, test):
    model = StackedAutoEncoder(
        dims=[3042, 1024, 512, 128, 36],
        activations=['sigmoid', 'sigmoid', 'sigmoid', 'sigmoid', 'sigmoid'],
        epoch=[500, 500, 500, 500, 500],
        loss='rmse',
        lr=0.05,
        noise='mask-0.3',
        batch_size=100,
        print_step=50
    )

    model.fit(train)
    test_ = model.transform(test)
    print("Trans len", len(test_), test_.shape)
    result = util.arraysToFeatures(test_)
    util.writeFeatures("test1.txt", result)

train = getFeatures('../uim_test-select/test1/train.txt')
test = getFeatures('../uim_test-select/test1/test_ae.txt')
run(train, test)