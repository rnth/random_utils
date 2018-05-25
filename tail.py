import time

def tail(file):
	file.seek(0, 2) #go to end of file
	while True:
		line = file.readline()
		if not line:
			time.sleep(0.4)
			continue
		yield line
		
#works like tail -f