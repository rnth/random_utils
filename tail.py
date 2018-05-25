import time
import sys

def tail(file):
	file.seek(0, 2) #go to end of file
	while True:
		line = file.readline()
		if not line:
			time.sleep(0.4)
			continue
		yield line
		
#works like tail -f
with open(sys.argv[1]) as file:
	for line in tail(file):
		print line
		
#_______________________________________________________________
#we want to send the line to another output like a printer. we write a coroutine to consume each line and send it to a supposed printer
def coroutine(fn):
	def execute(*args, **kwargs):
		res = fn(*args, **kwargs)
		next(res)
		return res
	return execute
	
@coroutine
def printer():
	while True:
		line = yield
		print line #supposedly prints to a printer

def tail_target(file, target):
	file.seek(0, 2)
	while True:
		line = file.readline()
		if not line:
			time.sleep(0.4)
			continue
		target.send(line)
		

#tail -f > printer
with open(sys.argv[1]) as file:
	tail_target(file, printer())