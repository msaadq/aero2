from azure.storage.table import TableService, Entity
import numpy as np
import pandas as pd



#TO DO: Update function

#TO DO: Function to count values of all entities in a table

#TO DO: Delete function


def checkTable (user, key, table_name):

	'''
	Returns bool indicating if table exists in database.
	'''

	t_service = TableService(account_name=user, account_key=key)
	tables = t_service.query_tables()
	tables_names = [i.name for i in tables]

	if table_name in tables_names:
		return True
	else:
		return False

def createTable (user, key, table_name):

	'''
	Creates a table in database.
	'''

	t_service = TableService(account_name=user, account_key=key)
	t_service.create_table(table_name)


def insertEntities (user, key, table_name, entities):

	'''
	Stores entities (in form of a dictionary data structure) 
	in database.
	'''

	t_service = TableService(account_name=user, account_key=key)
	t_service.begin_batch()
	for e in entities:
		t_service.insert_entity(table_name,e)
		print "Saved: ", e 
	t_service.commit_batch()


def getEntities (user, key, table_name, partition_key):

	'''
	Retrieves entities from database.
	'''

	t_service = TableService(account_name=user, account_key=key)
	p = "PartitionKey eq "+"'"+partition_key+"'"
	entities = t_service.query_entities(table_name,p)
	return entities


def main ():

	user = 'pythontestcloudservice1'
	key = 'rBnrx1uqYGXY8tvAyXATjn2NLYiPC+p+jgmxty+ZbI39tDauPI3sDFnYXEy8y53Ei4We8WGDg52HILUoAx1OCA=='
	name = 'smogTable'

	entities = getEntities(user,key,'smogTable','smogValues')
	for e in entities:
		print e.time


if __name__ == '__main__':
	main()