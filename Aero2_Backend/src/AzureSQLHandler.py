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
    _connection = None
    _cursor = None

    '''
	Connects to the Azure SQL server with either the default parameters or user-defined parameters
	and sets the cursor
	'''

    def __init__(self, server_name=DEFAULT_SQL_SERVER, username=DEFAULT_USERNAME, password=DEFAULT_PASSWORD,
                 database=DEFAULT_DATABASE_NAME):
        self._sql_server = server_name
        self._username = username
        self._password = password
        self._database_name = database

        server_url = self._sql_server + ".database.windows.net,1433"

        self._connection = pyodbc.connect("DRIVER={SQL Server};SERVER=" + server_url,
                                    user=self._username + "@" + self._sql_server,
                                    password=self._password,
                                    database=self._database_name)
        self._cursor = self._connection.cursor()

    '''
    Builds the SQL command from the string parameters, makes a SQL query and updates the cursor
    '''

    def sql_query(self, select_params, table_name, where_params=None):
        select_string = " SELECT " + select_params
        from_string = " FROM " + table_name

        if not where_params:
            self._cursor.execute(select_string + from_string)
        else:
            where_string = " WHERE " + where_params
            self._cursor.execute(select_string + from_string + where_string)

    '''
    Builds the SQL command from the string parameters and Inserts the row into the DB
    '''

    def insert_data(self, table_name, column_names, values):
        insert_string = " INSERT INTO " + table_name + column_names
        value_string = " VALUES " + values

        self._cursor.execute(insert_string + value_string)
        self._connection.commit()

    '''
    Builds the SQL command from the string parameters and Deletes the row(s)
    '''

    def delete_data(self, table_name, where_params):
        delete_string = " DELETE FROM " + table_name
        where_string = " WHERE " + where_params

        self._cursor.execute(delete_string + where_string)
        self._connection.commit()

    '''
    Closes the SQL connection
    '''

    def close_connection(self):
        self._connection.close()
 