import re
import datetime
import json

logPatterns = r'(?P<timestamp>.+?(?:A|P)M)\s+(?P<class>\S+)\s+(?P<method>\S+)\s+(?P<level>\w+).+?\s(?P<message>.+)'

logPattern = re.compile(logPatterns, re.MULTILINE)

logLevels = set(['INFO:', 'FINE:', 'FINER:', 'FINEST:', 'CONFIG:'])

def readlines():
	aggregateLine = ''
	with open(r'wwwserver.log', 'r') as file:
		for line in file:
			aggregateLine += line
			aggregateLine = aggregateLine.replace('\n', ' ')
			if not len(set(aggregateLine.split(' ')).intersection(logLevels)):
				continue
			else:
				tmp = aggregateLine
				aggregateLine = ''
				yield tmp

loglines = readlines()

matches = (logPattern.match(line) for line in loglines)

groups = (match.groups() for match in matches)

colnames = ('timestamp', 'class', 'method', 'level', 'message')

log = (dict(zip(colnames, group)) for group in groups)

def mapper(dictGen, name, func):
	for dGen in dictGen:
		dGen[name] = func(dGen[name])
		yield dGen
		
def dateConverter(date):
	date = datetime.datetime.strptime(date, '%b %d, %Y %I:%M:%S %p')
	return datetime.date.strftime(date, "%m/%d/%y %H:%M:%S")
	
def serializable(generator):
	return [_ for _ in generator]

log = mapper(log, 'timestamp', dateConverter)
log = mapper(log, 'message', lambda line: line.strip(' '))

#log_level_config = (l for l in log if l['level'] == 'CONFIG')
#log_level_info = (l for l in log if l['level'] == 'INFO')
#log_level_fine = (l for l in log if l['level'] == 'FINE')
#log_level_finer = (l for l in log if l['level'] == 'FINER')
#log_level_finest = (l for l in log if l['level'] == 'FINEST')

#with open(r'wwwserver-level-INFO.json', 'w') as file:
#	file.write(json.dumps(serializable(log_level_info)))