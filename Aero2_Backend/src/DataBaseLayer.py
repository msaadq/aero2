__author__ = 'Saad'

import AzureSQLHandler as sql

'''
Database Layer allows functions for accessing the SQL Database at a higher abstraction level and easy-access
'''


class DataBaseLayer:
    DEFAULT_COLUMNS = "time, lat, long, alt, smog, airq"
    SAMPLE_TABLE_NAME = "aero2.SampleDataTable"
    RESULTS_TABLE_NAME = "aero2.ResultDataTable"
    SMOG_MAX = 100
    SMOG_MIN = 0
    AIRQ_MAX = 100
    AIRQ_MIN = 0

    _sql_handler = None

    '''
    Connects to the Azure SQL server using the AzureSQLHandler class
    Args: self
    Return: None
    '''

    def __init__(self):
        self._sql_handler = sql.AzureSQLHandler()

    '''
    Selects the complete table
    Args: self, Table Name (String)
	Return: Table (String[][])
    '''

    def select_all(self, table_name):
        data_table = []
        select_params = self.DEFAULT_COLUMNS

        rows_table = self._sql_handler.select_data(table_name, select_params)

        for row in rows_table:
            data_table.append([str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog), str(row.airq)])

        return data_table

    '''
    Selects the table with key-value in specified range
    Args: self, Table Name (String), Column Name (String), Min value (double), Max value (double)
	Return: Table (String[][])
    '''

    def select_key_range(self, table_name, column, min_value, max_value):
        data_table = []
        select_params = self.DEFAULT_COLUMNS
        where_params = column + " <= \'" + str(float(max_value)) + "\' AND " + column + " >= \'" + str(
            float(min_value)) + "\'"

        rows_table = self._sql_handler.select_data(table_name, select_params, where_params)

        for row in rows_table:
            data_table.append([str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog), str(row.airq)])

        return data_table

    '''
    Selects the table with lat and long within the specified range
    Args: self, Table Name (String), Latitude Position (double), Longitude Position (double), Range (double)
	Return: Table (String[][])
    '''

    def select_nearby(self, table_name, latitude_position, longitude_position, x_y_range):
        data_table = []
        select_params = self.DEFAULT_COLUMNS
        where_params = "lat <= \'" + str(float(latitude_position + x_y_range)) + "\' AND  lat >= \'" + str(
            float(latitude_position - x_y_range)) + "\' AND  long <= \'" + str(
            float(longitude_position + x_y_range)) + "\' AND  lat >= \'" + str(
            float(longitude_position - x_y_range)) + "\'"

        rows_table = self._sql_handler.select_data(table_name, select_params, where_params)

        for row in rows_table:
            data_table.append([str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog), str(row.airq)])

        return data_table

    '''
    Selects rows with values taken during the given date and time
    Args: self, Table Name (String), Formatted Date (String), Start Time (String), End Time (String)
	Return: Table (String[][])
    '''

    def select_when(self, table_name, date, start_time=None, end_time=None):
        if not start_time or not end_time:
            return self.select_key_range(table_name, 'time', float(date), float(date) + 1.0)

        return self.select_key_range(table_name, 'time', float(date + "." + start_time), float(date + "." + end_time))

    '''
    Inserts a new row into the DB table
    Args: self, Table Name (String), List of Data Values (String[]), List of Columns separated by ',' (String)
	Return: Int
    '''

    def insert_row(self, table_name, data_list, columns=DEFAULT_COLUMNS):
        columns = "(" + columns + ")"
        values = "(\'" + "\',\'".join(data_list) + "\')"

        if self._validate_data_values(data_list):
            return self._sql_handler.insert_data(table_name, columns, values)

        return 0

    '''
    Inserts multiple rows into the DB table
    Args: self, Table Name (String), Table of Data Values (String[][]), List of Columns separated by ',' (String)
	Return: Int
    '''

    def insert_multiple(self, table_name, data_table, columns=DEFAULT_COLUMNS):
        i = 0
        for data_list in data_table:
            i += self.insert_row(table_name, data_list, columns)

        return i

    '''
    Deletes rows with given key-value pair
    Args: self, Table Name (String), Column Name (String), Value (double)
	Return: Int
    '''

    def delete_key_value(self, table_name, column, value):
        where_params = column + " = \'" + str(float(value)) + "\'"

        return self._sql_handler.delete_data(table_name, where_params)

    '''
    Deletes rows with given key having values within provided range
    Args: self, Table Name (String), Column Name (String), Min Value (double), Max Value (double)
	Return: Int
    '''

    def delete_key_range(self, table_name, column, min_value, max_value):
        where_params = column + " <= \'" + str(float(max_value)) + "\' AND " + column + " >= \'" + str(
            float(min_value)) + "\'"

        return self._sql_handler.delete_data(table_name, where_params)

    '''
    Deletes rows with Latitudes and Longitudes having values within provided range
    Args: self, Table Name (String), Lat Value (double), Long Value (double), Range (double)
	Return: Int
    '''

    def delete_nearby(self, table_name, latitude_position, longitude_position, x_y_range):
        where_params = "lat <= \'" + str(float(latitude_position + x_y_range)) + "\' AND  lat >= \'" + str(
            float(latitude_position - x_y_range)) + "\' AND  long <= \'" + str(
            float(longitude_position + x_y_range)) + "\' AND  lat >= \'" + str(
            float(longitude_position - x_y_range)) + "\'"

        return self._sql_handler.delete_data(table_name, where_params)

    '''
    Deletes rows with values taken during the given date and time
    Args: self, Table Name (String), Formatted Date (String), Start Time (String), End Time (String)
	Return: Int
    '''

    def delete_when(self, table_name, date, start_time=None, end_time=None):
        if not start_time or not end_time:
            return self.delete_key_range(table_name, 'time', float(date), float(date) + 1.0)

        return self.delete_key_range(table_name, 'time', float(date + "." + start_time), float(date + "." + end_time))

    '''
    Validates values provided in the insert function
    Args: self, List of Data Values (String[])
	Return: Int
    '''

    def _validate_data_values(self, data_list):
        if len(data_list[0]) != 13 or data_list[0][6] != '.':
            return False

        try:
            int(data_list[0][0:6])
            int(data_list[0][7:13])
            float(data_list[1])
            float(data_list[2])
            float(data_list[3])
            float(data_list[4])
            float(data_list[5])
        except ValueError:
            return False

        if float(data_list[4]) not in range(self.SMOG_MIN, self.SMOG_MAX + 1):
            return False

        if float(data_list[5]) not in range(self.AIRQ_MIN, self.AIRQ_MAX + 1):
            return False

        return True

    '''
    Closes the SQL connection
    Args: self
	Return: None
    '''

    def close_connection(self):
        self._sql_handler.close_connection()
