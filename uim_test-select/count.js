const fs = require('fs');
const path = require('path');

const printFile = (file, minChildCount) => {
    fs.readdir(file, (err, children) => {
        if (err) return console.log(err);

        if (children.length >= minChildCount) {
            console.log(file, '(', children.length, ')');
        }
    });
};

const printFiles = (parent, files, minChildCount) => {
    files.map(file => {
        fs.stat(file, (err, stats) => {
            if (err) return console.log(err);

            if (stats.isDirectory()) {
                printFile(path.join(parent, file), minChildCount)
            }
        });
    });
}

const countFiles = (dir, minChildCount) => {
    console.log('Files in', dir, 'that contain at least', minChildCount, 'children.');

    fs.readdir(dir, (err, files) => {
        if (err) return console.log(err);

        printFiles(dir, files, minChildCount);
    });
};

countFiles(process.cwd(), 100);