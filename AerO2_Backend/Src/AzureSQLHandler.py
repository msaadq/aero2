import pyodbc


class AzureSQLHandler:
    """
    Azure SQL Database Handler uses SQL login credentials to connect and Interact with the Azure SQL DB
    """

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

    def __init__(self, server_name=DEFAULT_SQL_SERVER, username=DEFAULT_USERNAME, password=DEFAULT_PASSWORD,
                 database=DEFAULT_DATABASE_NAME):
        """
        Connects to the Azure SQL server with either the default parameters or user-defined parameters
        and sets the cursor

        :param server_name: Server name (String)
        :param username: User name (String)
        :param password: Password (String)
        :param database: Database name (String)

        :return None
        """

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

    def select_data(self, table_name, select_params, where_params=None):
        """
        Builds the SQL command from the string parameters, makes a SQL query and updates the cursor

        :param table_name: Table Name (String)
        :param select_params: Parameters for SELECT (String)
        :param where_params: Parameters for WHERE (String)

        :return Table (String[][]):
        """

        output_table = []

        select_string = " SELECT " + select_params + " FROM " + table_name

        if where_params:
            select_string += " WHERE " + where_params

        self._cursor.execute(select_string)

        for row in self._cursor:
            output_table.append(row)

        return output_table

    def insert_data(self, table_name, column_names, values):
        """
        Builds the SQL command from the string parameters and Inserts the row into the DB

        :param table_name: Table Name (String)
        :param column_names: (Column Names separated by ', ') (String)
        :param values: (Values separated by ', ') (String)

        :return Int:
        """

        insert_string = " INSERT INTO " + table_name + " " + column_names + " VALUES " + values

        rowcount = self._cursor.execute(insert_string).rowcount
        self._connection.commit()

        return rowcount

    def update_data(self, table_name, set_params, where_params=None):
        """
        Builds the SQL command from the string parameters and updates the row into the DB

        :param table_name: Table Name (String)
        :param set_params: <Column Names> = <Values> separated by ', ' (String)
        :param where_params: Parameters for WHERE (String)

        :return Int:
        """

        update_string = " UPDATE " + table_name + " SET " + set_params

        if where_params:
            update_string += " WHERE " + where_params

        rowcount = self._cursor.execute(update_string).rowcount
        self._connection.commit()

        return rowcount

    def delete_data(self, table_name, where_params):
        """
        Builds the SQL command from the string parameters and Deletes the row(s)

        :param table_name: Table Name (String)
        :param where_params: Parameters for WHERE (String)

        :return List (String[])
        """

        delete_string = " DELETE FROM " + table_name
        where_string = " WHERE " + where_params

        rowcount = self._cursor.execute(delete_string + where_string).rowcount
        self._connection.commit()

        return rowcount

    def close_connection(self):
        """
        Closes the SQL connection

        :return None
        """

        self._connection.close()
