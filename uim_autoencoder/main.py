from autoencoder import StackedAutoEncoder
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
        lr=0.03,
        noise='mask-0.3',
        batch_size=100,
        print_step=50
    )

    print('Starting training of the network.')
    model.fit(train)
    print('Starting to transform data.', end='')
    test_ = model.transform(test)
    print(' Done.')
    print('Writing results.', end='')
    util.writeFeatures('../uim_bag-of-visual-words/data/test3.txt', test_)
    print(' Done.')

train = getFeatures('../uim_test-select/test3/train.txt')
test = getFeatures('../uim_test-select/test3/test_ae.txt')
run(train, test)