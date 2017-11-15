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

def readTests (filePath):
    tests = []
    file = open(filePath, "r")

    for line in file:
        tests.append(line.replace("\\", "/").strip().split(" "))
    return tests

def writeFeatures (filePath, features):
    file = open(filePath, "w")

    for feature in features:
        file.write(" ".join(map(lambda x: '{0:.10f}'.format(x), feature)))
        file.write("\n")
    file.close()

def writeTests (filePath, tests):
    file = open(filePath, "w")

    for test in tests:
        file.write(test[0] + '\n')
        for feature in test[1]:
            file.write(",".join(map(lambda x: str(x), feature)))
            file.write("|")
        file.write('\n' + test[2] + '\n')
        for feature in test[3]:
            file.write(",".join(map(lambda x: str(x), feature)))
            file.write("|")
        file.write('\n' + test[4] + '\n')
    file.close()