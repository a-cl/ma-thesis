import tensorflow as tf
import numpy as np
import random
import cv2
import os

sift_width = 128
n_hidden = 100
corruption_level = 0.2

# DEFINITIONS

def extractFeaturesFromImage (imagePath):
    img = cv2.imread(imagePath)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    sift = cv2.xfeatures2d.SIFT_create()
    kp, des = sift.detectAndCompute(gray, None)
    return des

def extractFeaturesFromFolder (folderPath, limit = 20):
    files = os.listdir(folderPath)[:limit]
    features = []

    for filePath in files:
        imagePath = folderPath + '/' + filePath
        features.extend(extractFeaturesFromImage(imagePath))

    return np.array(features)

def model(X, mask, W, b, W_prime, b_prime):
    tilde_X = mask * X  # corrupted X

    Y = tf.nn.sigmoid(tf.matmul(tilde_X, W) + b)  # hidden state
    Z = tf.nn.sigmoid(tf.matmul(Y, W_prime) + b_prime)  # reconstructed input
    return Z

# VARIABLES

# create node for input data
X = tf.placeholder("float", [None, sift_width], name='X')

# create node for corruption mask
mask = tf.placeholder("float", [None, sift_width], name='mask')

# create nodes for hidden variables
W_init_max = 4 * np.sqrt(6. / (sift_width + n_hidden))
W_init = tf.random_uniform(shape=[sift_width, n_hidden], minval=-W_init_max, maxval=W_init_max)

W = tf.Variable(W_init, name='W')
b = tf.Variable(tf.zeros([n_hidden]), name='b')

W_prime = tf.transpose(W)  # tied weights between encoder and decoder
b_prime = tf.Variable(tf.zeros([sift_width]), name='b_prime')

# build model graph
Z = model(X, mask, W, b, W_prime, b_prime)

# create cost function
cost = tf.reduce_sum(tf.pow(X - Z, 2))  # minimize squared error
train_op = tf.train.GradientDescentOptimizer(0.02).minimize(cost)  # construct an optimizer

# Launch the graph in a session
with tf.Session() as sess:
    trX = extractFeaturesFromFolder("./img/guitar")

    teX1 = extractFeaturesFromImage("./img/guitar/image_0005.jpg")
    teX2 = extractFeaturesFromImage("./img/dragonfly/image_0004.jpg")
    teX = np.concatenate([teX1, teX2])

    sess.run(tf.global_variables_initializer())

    for i in range(1000):
        for input_ in trX:
            input_ = input_.reshape(1, sift_width)
            mask_np = np.random.binomial(1, 1 - corruption_level, input_.shape)
            sess.run(train_op, feed_dict={X: input_, mask: mask_np})

        mask_np = np.random.binomial(1, 1 - corruption_level, teX.shape)
        res = sess.run(cost, feed_dict={X: teX, mask: mask_np})
        print(i, '{0:.10f}'.format(res))
