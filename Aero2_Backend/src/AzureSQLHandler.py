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
	Args: self, Server Name (String), User Name (String), Password (String), Database Name (String)
	Return: None
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
    Args: self, Parameters for SELECT (String), Table Name (String), Parameters for WHERE (String)
	Return: Table (String[][])
    '''

    def select_data(self, table_name, select_params, where_params=None):
        output_table = []

        select_string = " SELECT " + select_params
        from_string = " FROM " + table_name

        if not where_params:
            self._cursor.execute(select_string + from_string)
        else:
            print("we have where_params = " + where_params)
            where_string = " WHERE " + where_params
            self._cursor.execute(select_string + from_string + where_string)

        for row in self._cursor:
            output_table.append(row)

        return output_table

    '''
    Builds the SQL command from the string parameters and Inserts the row into the DB
    Args: self, Table Name (String), Column Names separated by ', ' (String), Values separated by ', ' (String)
	Return: Int
    '''

    def insert_data(self, table_name, column_names, values):
        insert_string = " INSERT INTO " + table_name + column_names
        value_string = " VALUES " + values

        rowcount = self._cursor.execute(insert_string + value_string).rowcount
        self._connection.commit()

        if rowcount > 0:
            return 1
        return 0

    '''
    Builds the SQL command from the string parameters and Deletes the row(s)
    Args: self, Table Name (String), Parameters for WHERE (String)
	Return: List (String[])
    '''

    def delete_data(self, table_name, where_params):
        delete_string = " DELETE FROM " + table_name
        where_string = " WHERE " + where_params

        rowcount = self._cursor.execute(delete_string + where_string).rowcount
        self._connection.commit()

        return rowcount

    '''
    Closes the SQL connection
    Args: self
	Return: None
    '''

    def close_connection(self):
        self._connection.close()
