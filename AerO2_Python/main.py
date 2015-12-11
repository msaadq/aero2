

import azure_service as az
import maps as maps

import pandas as pd


#Callable Function
def saveDb (user, key, table_name, partition_key, df):

	'''
	Saves DataFrame on Azure.
	If the table does not already exist, it is created first.
	'''

	c_name = list(df.columns.values)

	#TO DO: Add timestamp here
	
	entities = [{'PartitionKey':partition_key,'RowKey':df[c_name[1]][i],c_name[2]:str(df[c_name[2]][i]),c_name[3]:str(df[c_name[3]][i])}
	for i in range(0,len(df[c_name[0]]))]

	if (az.checkTable(user,key,table_name) == False):
		az.createTable(user,key,table_name)
	
	#TO DO: Implement an exception here for if the data already exists.
	az.insertEntities(user, key, table_name, entities[5:10])

def getDb (user, key, table_name, partition_key):

	entities = az.getEntities(user,key,table_name,partition_key)
	for e in entities:
		print e.traffic

def main():

	azure_user = 'pythontestcloudservice1'
	azure_key = 'rBnrx1uqYGXY8tvAyXATjn2NLYiPC+p+jgmxty+ZbI39tDauPI3sDFnYXEy8y53Ei4We8WGDg52HILUoAx1OCA=='
	google_key1 = 'AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c'
	google_key2 = 'AIzaSyDKQ-7d3pejj3BxgdGusj3djD4ppwWtn2s'
	origin = ['33.6499632','72.9637728']
	destination = ['33.6499632','72.9737728']
	radius = '2000'
	ptype = 'hospital'
	table_name = 'smogTable'
	partition_key = 'smogValues'

	df = pd.read_csv('Data\data.csv')
	getDb(azure_user,azure_key,'smogTable','smogValues')


if __name__ == '__main__':
	main()