def printLine(file, line):
	with open(file, "r") as f:
		while (line > 1):
			f.readline()
			line -= 1
		print(f.readline().rstrip())

printLine("1/test128.txt", 2)