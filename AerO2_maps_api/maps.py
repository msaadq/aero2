import numpy as np
from bs4 import BeautifulSoup as bs
import urllib2


def findPlace(key, origin, radius, ptype):

	'''Calls Google Place API and returns the nearest place within 
	the radius provided by user'''

	web_link = 'https://maps.googleapis.com/maps/api/place/nearbysearch/xml?key='+key
	web_link += '&location='+origin[0]+','+origin[1]+'&radius='+radius+'&type='+ptype
	source = urllib2.urlopen(web_link)
	tree = bs(source)

	return tree.findAll('name')
	

def calDuration (key, origin, destination, departure_time='now',traffic_model='best_guess'):

	'''Calls Google Distance API and returns the duration/km'''

	web_link = 'https://maps.googleapis.com/maps/api/distancematrix/xml?key'+key
	web_link += '&origins='+origin[0]+','+origin[1]+'&destinations='+destination[0]+','
	web_link += destination[1]
	#web_link += '&departure_time='+departure_time+'&traffic_model='+traffic_model

	source = urllib2.urlopen(web_link)
	tree = bs(source)

	result = tree.findAll('distance')
	distance = [i.findChild('text').getText() for i in result]
	distance = float((distance[0].split())[0])

	result = tree.findAll('duration')
	duration = [i.findChild('text').getText() for i in result]
	duration = float((duration[0].split())[0])

	return duration/distance


def calTraffic (key, origin, increment=0.015):

	'''Estimates traffic by averaging duration over 4 different 
	directions from origin'''

	destination1 = [str(float(origin[0]) + increment), origin[1]]
	destination2 = [str(float(origin[0]) - increment), origin[1]]
	destination3 = [origin[0],str(float(origin[1]) + increment)]
	destination4 = [origin[0],str(float(origin[1]) - increment)]

	d1 = calDuration(key, origin, destination1)
	d2 = calDuration(key, origin, destination2)
	d3 = calDuration(key, origin, destination3)
	d4 = calDuration(key, origin, destination4)
	
	return (d1+d2+d3+d4)/4


def main():

	key1 = 'AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c'
	key2 = 'AIzaSyDKQ-7d3pejj3BxgdGusj3djD4ppwWtn2s'
	origin = ['33.6499632','72.9637728']
	destination = ['33.6499632','72.9737728']
	radius = '2000'
	ptype = 'hospital'

	'''
	#Test Code for findPlace function
	results_pl = findPlace(key, origin, radius, ptype)
	results_pl_text = [i.getText() for i in results]
	print results_text

	'''

	'''
	#Test code for calDuration function
	print calDuration(key2, origin, destination)
	'''

	print calTraffic(key2,origin)
# 

if __name__ == '__main__':
	main()


#convert latitude, longitude to distance
#implement place api
#function that saves data in pandas
#output pandas into csv