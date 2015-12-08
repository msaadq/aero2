from azure.storage.table import TableService, Entity

def createTable (user, key, table_name):

	table_service = TableService(account_name=user, account_key=key)
	table_service.create_table(table_name)

def main ():

	user = 'pythontestcloudservice1'
	key = 'rBnrx1uqYGXY8tvAyXATjn2NLYiPC+p+jgmxty+ZbI39tDauPI3sDFnYXEy8y53Ei4We8WGDg52HILUoAx1OCA=='
	
	#createTable(user, key, name)

if __name__ == '__main__':
	main()