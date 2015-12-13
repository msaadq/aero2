__author__ = 'Saad'

import pyodbc


'''
Azure SQL Database Handler uses SQL login credentials to connect and Interact with the Azure SQL DB
'''


class AzureSQLHandler:

	DEFAULT_SQL_SERVER = "kezq6jecop"
	DEFAULT_USERNAME = "aero2"
	DEFAULT_PASSWORD = "h1pa33w0rD"
	DEFAULT_DATABASE_NAME = "aero2_db"

	_sql_server = ""
	_username = ""
	_password = ""
	_database_name = ""
	_cursor = None

	'''

	'''

	def __init__(self, server_name=DEFAULT_SQL_SERVER, username=DEFAULT_USERNAME, password=DEFAULT_PASSWORD, database=DEFAULT_DATABASE_NAME):
		self._sql_server = server_name
		self._username = username
		self._password = password
		self._database_name = database

		server_url = self._sql_server + ".database.windows.net,1433"

		connection = pyodbc.connect("DRIVER={SQL Server};SERVER=" + server_url, user=self._username + "@" + self._sql_server, password=self._password, database=self._database_name)
		self._cursor = connection.cursor()


	def _sql_query(self, select_params, from_params, where_params = None):
		select_string = "SELECT " + select_params
		from_string = " FROM " + from_params

		if not where_params:
			self._cursor.execute(select_string + from_string)
		else:
			where_string = " WHERE " + where_params
			self._cursor.execute(select_string + from_string + where_string)


