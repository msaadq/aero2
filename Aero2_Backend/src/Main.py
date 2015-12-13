import AzureSQLHandler as sql

sql_handler = sql.AzureSQLHandler()

sql_handler._sql_query("*", "aero2.SampleDataTable")

row = sql_handler._cursor.fetchone()

print(row.id)