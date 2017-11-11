import numpy as np

def featuresToArrays (features):
    arrays = []

    for feature in features:
        arrays.append(imageToArray(feature))
    return np.array(arrays)

def arraysToFeatures (arrays):
    features = []

    for arr in arrays:
        features.append(arrayToFeature(arr))
    return features

def imageToArray (feature):
    vector = []
    vector.extend(points for vec in feature[0] for points in vec)
    vector.extend(points for vec in feature[1] for points in vec)
    return np.array(vector)

def arrayToFeature (array):
    grad_x, grad_y = [], []
    half = int(len(array) / 2)

    for i in range(0, half, 39):
        grad_x.append(array[i:i+39])
        grad_y.append(array[half+i:half+i+39])
    return [grad_x, grad_y]

def readImagePaths (filePath):
    files = []
    file = open(filePath, "r")

    for line in file:
        files.append(line.replace("\\", "/").strip())
    return files

def writeFeatures (filePath, features):
    file = open(filePath, "w")
    for feature in features:
        file.write(" ".join(str(x) for x in feature))
        file.write("")
    
    file.close()