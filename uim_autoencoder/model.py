from autoencoder import StackedAutoEncoder
import autoencoder2
import numpy as np
import cv2
import extractor
import util

paths = [
    'img/electric_guitar/image_0011.jpg',
    'img/electric_guitar/image_0035.jpg',
    'img/electric_guitar/image_0044.jpg',
    'img/dragonfly/image_0002.jpg',
    'img/dragonfly/image_0020.jpg',
    'img/dragonfly/image_0035.jpg',
    'img/dragonfly/image_0056.jpg'
]

gradients = extractor.extractAllFeatures(paths)
normFeatures = util.featuresToArrays(gradients)
idx = np.random.rand(normFeatures.shape[0]) < 0.8
train_X = normFeatures[idx]
test_X = normFeatures[~idx]

def runAE1():
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

    model.fit(train_X)
    test_X_ = model.transform(test_X)
    print("Trans len", len(test_X_), test_X_.shape)
    result = util.arraysToFeatures(test_X_)
    #print(len(result), len(result[0]), len(result[0][0]), len(result[0][0][0]), result[0][0][0][0])

def runAE2():
    autoencoder2.run(train_X, test_X)

runAE1()